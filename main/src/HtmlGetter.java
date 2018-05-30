import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    public Object call() throws Exception {
        try {
            HttpClient client=HttpClients.createDefault();
            HttpGet get = new HttpGet(url);
            CloseableHttpResponse response = (CloseableHttpResponse) client.execute(get);
            // 得到html
            HttpEntity enity = response.getEntity();
            html = EntityUtils.toString(enity, "GBK");           //校内网用的GBK
            html=rToA(html);
        }catch (Exception e){
            e.printStackTrace();
        }
        return html;
    }


    /**
     * 将src的相对路径转为绝对路径
     * @param html
     * @return
     */
    public String rToA(String html){
        Pattern pattern=Pattern.compile("=\\\"/");         //匹配 ="/
        Matcher matcher=pattern.matcher(html);
        //将="/替代为="http://www1.szu.edu.cn/board/
        return matcher.replaceAll("=\\\"http://www1.szu.edu.cn/");
    }
}
