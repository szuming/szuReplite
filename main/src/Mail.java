import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * @ClassName Mail
 * @Description 邮件类，用于发送邮件
 * @Author MING
 * @Date 2018/6/2 14:11
 * @Update 2018/6/2 14:11
 **/
public class Mail {
    private static volatile Mail mail=null;                             //唯一的实例
    private static String senderAddress="13760254539@163.com";          //发件人地址
    private static String recipientAddress="609546389@qq.com";          //收件人地址
    private static String senderAccount="13760254539@163.com";          //发件人账户名
    private static String senderPassword="qq147741";                    //发件人账户密码

    private Mail(){}

    /**
    * @Author MING
    * @Description 用于得到唯一实例
    * @Date 14:13 2018/6/2
    * @Param []
    * @return Mail 唯一实例
    **/
    public static Mail getInstance(){
        //没有被实例化时将其实例化
        if(mail==null){
                mail=new Mail();
        }
        return mail;
    }

    /**
    * @Author MING
    * @Description 发送邮件
    * @Date 14:18 2018/6/2
    * @Param [html] 邮件实体内容
    * @return void
    **/
    public void send(String html) throws Exception {
        //连接邮件服务器的参数配置
        Properties props = new Properties();
        //设置用户的认证方式
        props.setProperty("mail.smtp.auth", "true");
        //设置传输协议
        props.setProperty("mail.transport.protocol", "smtp");
        //设置发件人的SMTP服务器地址
        props.setProperty("mail.smtp.host", "smtp.163.com");
        //创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getInstance(props);
        //设置调试信息在控制台打印出来
        session.setDebug(true);
        //创建邮件的实例对象
        Message msg=getMimeMessage(session,html);
        //根据session对象获取邮件传输对象Transport
        Transport transport=session.getTransport();
        //设置发件人的账户名和密码
        transport.connect(senderAccount, senderPassword);
        //发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(msg,msg.getAllRecipients());
        //如果只想发送给指定的人，可以如下写法
        //transport.sendMessage(msg, new Address[]{new InternetAddress("xxx@qq.com")});
        //关闭邮件连接
        transport.close();
    }

    /**
     * 获得创建一封邮件的实例对象
     * @param session
     * @param html      邮件实体
     * @return
     * @throws Exception
     */
    public static MimeMessage getMimeMessage(Session session,String html) throws Exception{
        //创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //设置发件人地址
        msg.setFrom(new InternetAddress(senderAddress));
        /**
         * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
         * MimeMessage.RecipientType.TO:发送
         * MimeMessage.RecipientType.CC：抄送
         * MimeMessage.RecipientType.BCC：密送
         */
        msg.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(recipientAddress));
        //设置邮件主题
        msg.setSubject("邮件主题","UTF-8");
        //设置邮件正文
        msg.setContent(html, "text/html;charset=UTF-8");
        //设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());
        return msg;
    }

}
