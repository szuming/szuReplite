import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlSelector {
    /**
     * 正则表达式获取urls跟对应的titles
     * @param html  要解析的url页面
     * @return  要访问的urls
     */
    public static ConcurrentHashMap<String,String> getUrls(String html){
        ConcurrentHashMap<String,String> map=new ConcurrentHashMap<>(100);
        //String regex="^ <td[\\s\\S]*计算机与软件学院$</td>";
        String regex=" <td align=\"center\" style=\"font-size: 9pt\">.*计算机与软件学院[\\s\\S]{10,}?</tr>";        // [\s\S]代表匹配任意字符（包括换行）,{10，}代表至少匹配十次，?代表最短匹配,</tr>代表以</tr>结尾
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(html);
        while(matcher.find()){
            //匹配出计软学院的结果再用正则去匹配
            //group(1)是url，group[2]是title
            Matcher matcher1;
            matcher1=Pattern.compile(" <a target=_blank href=\\\"(.*)\\\".*>(.*)</").matcher(matcher.group());
            if(matcher1.find())
                map.put("http://www1.szu.edu.cn/board/"+matcher1.group(1),matcher1.group(2));

        }
        return map;
    }

}
