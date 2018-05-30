import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 用于处理redis
 */
public class jedisUtil {
    //默认设置生成的连接池
    JedisPool jedisPool=new JedisPool("127.0.0.1",6379);

    /**
     * 往redis插入新的记录
     * @param key 键名
     * @param content 要插入的内容
     */
    public void insert(String key,String content){
        //取得客户端连接
        Jedis jedis=jedisPool.getResource();
        //不在集合中则加入集合
        if(!jedis.sismember(key,content)){
            jedis.sadd(key,content);
        }
        //设置键20分钟过期,在7：15写入Mysql数据库
        jedis.expire(key,20*60);
    }
}
