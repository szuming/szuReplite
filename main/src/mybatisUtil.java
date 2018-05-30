import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class mybatisUtil {
    SqlSessionFactory sqlSessionFactory=null;
    mybatisUtil() throws IOException {
        String resource = "D:\\javaCode\\reptile\\main\\resources\\mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }



}
