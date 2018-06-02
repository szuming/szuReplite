import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @Author MING
* @Description 访问目标url返回html
* @Date 13:58 2018/6/2
* @Param
* @return
**/
public class HtmlGetter implements Callable {
    private String url=null;
    private String html=null;

    public HtmlGetter(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    /**
    * @Author MING
    * @Description 访问url取得html，返回html作为邮件实体
    * @Date 14:03 2018/6/2
    * @Param []
    * @return java.lang.Object 查询与加工过的html
    **/
    @Override
    public Object call() throws Exception {
        try {
            HttpClient client=HttpClients.createDefault();
            HttpGet get=new HttpGet(url);
            CloseableHttpResponse response=(CloseableHttpResponse) client.execute(get);
            //得到html
            HttpEntity enity=response.getEntity();
            html=EntityUtils.toString(enity, "GBK");           //校内网用的GBK
            //加工html，转换为绝对路径
            html=rToA(html);
        }catch (Exception e){
            e.printStackTrace();
        }
        return html;
    }

    /**
    * @Author MING
    * @Description  对访问得到的html进行加工，将url相对路径加上主域名组成绝对路径
    * @Date 14:01 2018/6/2
    * @Param [html] 访问得到的html
    * @return java.lang.String  加工完的htmls
    **/
    public String rToA(String html){
        //匹配 ="/
        Pattern pattern=Pattern.compile("=\\\"/");
        Matcher matcher=pattern.matcher(html);
        //将="/替代为="http://www1.szu.edu.cn/board/
        return matcher.replaceAll("=\\\"http://www1.szu.edu.cn/");
    }
}
