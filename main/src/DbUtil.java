import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;

/**
 * @ClassName DbUtil
 * @Description 数据库操作类
 * @Author MING
 * @Date 2018/6/2 13:22
 * @Update 2018/6/2 13:22
 **/
public class DbUtil {
    private static String classname = "com.mysql.jdbc.Driver";
    private static String datauser = "ming";
    private static String dataUrl = "jdbc:mysql://127.0.0.1/szureptile?useSSL=false&user=" + datauser + "&password=qq147741&characterEncoding=UTF-8&serverTimezone=GMT";
    private static Connection conn = null;

    /**
    * @Author MING
    * @Description  得到一个数据库连接
    * @Date 13:51 2018/6/2
    * @Param []
    * @return java.sql.Connection
    **/
    public static Connection getConn() {
        try {
            Class.forName(classname);
            conn = DriverManager.getConnection(dataUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
    * @Author MING
    * @Description  从数据库中查询最近10条url
    * @Date 13:52 2018/6/2
    * @Param []
    * @return java.util.HashSet<java.lang.String> 返回10条记录的HashSet
    **/
    public static HashSet<String> getUrl(){
        HashSet<String> urlSet=new HashSet<>(10);
        Connection conn = DbUtil.getConn();
        try {
            String sql = "select url from record  order by id Desc limit 10 ";
            PreparedStatement prst = conn.prepareStatement(sql);
            ResultSet rs = prst.executeQuery();
            while (rs.next()){
                urlSet.add(rs.getString("url"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlSet;
    }

    /**
    * @Author MING
    * @Description  把新的url插入到mysql中去
    * @Date 13:54 2018/6/2
    * @Param [newUrlSet]    新的url集合
    * @return void
    **/
    public static void insert(HashSet<String> newUrlSet) {
        Connection conn;
        try {
            conn = DbUtil.getConn();
            for(String url:newUrlSet){
                String sql = "insert into record (url) values(?)";
                PreparedStatement prst = conn.prepareStatement(sql);
                prst.setString(1, url);
                prst.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
