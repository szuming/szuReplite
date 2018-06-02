import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @ClassName QuartScheduler
 * @Description 主程序入口，启动定时器，定时执行MainJob
 * @Author MING
 * @Date 2018/6/2 14:54
 * @Update 2018/6/2 14:54
 **/
public class QuartScheduler {
    public static void main(String[] argv){
        //创建scheduler
        try {
            Scheduler scheduler=StdSchedulerFactory.getDefaultScheduler();
            //定义一个Trigger
            Trigger trigger = newTrigger().withIdentity("trigger1", "group1") //定义name/group
                    .startNow()//一旦加入scheduler，立即生效
                    .withSchedule(cronSchedule("0 57 14 * * ?")) // 每周一，9:30执行一次
                    .build();
            //定义一个JobDetail
            JobDetail job=newJob(MainJob.class) //定义Job类为HelloQuartz类，这是真正的执行逻辑所在
                    .withIdentity("访问公文通", "group1") //定义name/group
                    .usingJobData("name", "quartz") //定义属性
                    .build();
            //加入这个调度
            scheduler.scheduleJob(job, trigger);
            //启动调度
            scheduler.start();
            //等待输入让其阻塞
            System.in.read();
            scheduler.shutdown(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
