import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @ClassName MainJob
 * @Description 供定时器运行的任务，负责url读写，爬取目标urls，访问url，获取html发邮件
 * @Author MING
 * @Date 2018/6/2 14:34
 * @Update 2018/6/2 14:34
 **/
public class MainJob implements Job {
    private static String initUrl="http://www1.szu.edu.cn/board/";                        //初始url,用于获取所有url
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try{
            //从mysql查询最近10条存入set中
            HashSet<String> urlSet= DbUtil.getUrl();
            HashSet<String> newUrlSet=new HashSet<>(10);
            String body="";     //邮件主体内容
            //访问http://www1.szu.edu.cn/board/得到包含所有url的html
            String urlsHtml=initQuery();
            //正则表达式匹配得到urls
            //从urlsHtml中获取urls
            ConcurrentHashMap<String,String> map=UrlSelector.getUrls(urlsHtml);
            //遍历urls，线程池开启访问线程
            ExecutorService pool = Executors.newFixedThreadPool(map.size());
            List<Future> list = new ArrayList<Future>();        //返回的结果链表
            Iterator<Map.Entry<String, String>> iterator =map.entrySet().iterator();
            while (iterator.hasNext()){
                //获取url
                Map.Entry<String, String> entry=iterator.next();
                String url=entry.getKey();
                //查看是否已有该url,无则查询获取html，加到set中
                if(!urlSet.contains(url)){
                    urlSet.add(url);
                    newUrlSet.add(url);
                    //子进程再去访问url得到完整页面
                    Callable temp=new HtmlGetter(url);
                    //返回的结果
                    Future f = pool.submit(temp);
                    list.add(f);
                }
            }
            //关闭线程池
            pool.shutdown();
            //获取子线程执行的结果
            for(Future f:list){
                body=body+(String) f.get();
            }
            //如果没有新内容则不发送邮件，直接返回
            if(body.equals(""))return;
            Mail mail=Mail.getInstance();
            mail.send(body);
            //将新的url写入mysql
            DbUtil.insert(newUrlSet);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * http访问获取包含所有url的html
     * @return  包含所有url的html
     * @throws IOException
     */
    private String initQuery() throws IOException {
        HttpClient client=HttpClients.createDefault();
        HttpGet get = new HttpGet(initUrl);
        CloseableHttpResponse response = (CloseableHttpResponse) client.execute(get);
        HttpEntity enity = response.getEntity();
        return EntityUtils.toString(enity, "gb2312");
    }
}
