import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
public class MainJob implements Job{
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try{
            String body="";        //邮件内容
            HttpClient client=HttpClients.createDefault();
            HttpGet get = new HttpGet("http://www1.szu.edu.cn/board/");
            CloseableHttpResponse response = (CloseableHttpResponse) client.execute(get);
            // 得到html
            HttpEntity enity = response.getEntity();
            String html = EntityUtils.toString(enity, "gb2312");
            //正则表达式匹配得到urls
            //使用线程安全的map
            ConcurrentHashMap<String,String> map=UrlSelector.getUrls(html);
            //遍历urls，线程池开启访问线程
            ExecutorService pool = Executors.newFixedThreadPool(map.size());
            List<Future> list = new ArrayList<Future>();        //返回的结果链表
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()){
                //获取url
                Map.Entry<String, String> entry = iterator.next();
                String url=entry.getKey();
                String title=entry.getValue();
                //子进程再去访问url得到完整页面
                Callable temp=new HtmlGetter(url);
                //返回的结果
                Future f = pool.submit(temp);
                list.add(f);
            }
            //关闭线程池
            pool.shutdown();

            //获取子线程执行的结果
            for(Future f:list){
                body=body+(String) f.get();
            }
            System.out.println(body);
            Mail mail=Mail.getInstance();
            mail.send(body);
        }catch (Exception e){
            e.printStackTrace();;
        }

    }
}
