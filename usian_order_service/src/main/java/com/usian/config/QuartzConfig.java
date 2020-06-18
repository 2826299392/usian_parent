package com.usian.config;

import com.usian.factory.MyAdaptableJobFactory;
import com.usian.quartz.OrderQuartz;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * 定时配置类
 */
@Configuration
public class QuartzConfig  {

    /**
     * 定时的时间要做的事情
     * @return
     */
    @Bean
    public JobDetailFactoryBean getJobDetailFactoryBean(){
        //创建做事情的一个对象
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        //将我们实现任务的类放到工厂中
        jobDetailFactoryBean.setJobClass(OrderQuartz.class);
        return jobDetailFactoryBean;
    }

    /**
     * 设置做事情的时间
     */
    @Bean
    public CronTriggerFactoryBean getCronTriggerFactoryBean(JobDetailFactoryBean jobDetailFactoryBean){
        //创建定时工厂
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        //将我们做事情的对象分装到该对象里面
        cronTriggerFactoryBean.setJobDetail(jobDetailFactoryBean.getObject());
        //设置做事情的触发时间
        cronTriggerFactoryBean.setCronExpression("*/5 * * * * ?");
        return  cronTriggerFactoryBean;
    }

    //上面两个方法的结合在什么时间做什么事
    @Bean
    public SchedulerFactoryBean getSchedulerFactoryBean(CronTriggerFactoryBean cronTriggerFactoryBean,MyAdaptableJobFactory myAdaptableJobFactory){
        //创建实现任务对象
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setTriggers(cronTriggerFactoryBean.getObject());  //将设定的时间封装，时间里面分装了任务
        schedulerFactoryBean.setJobFactory(myAdaptableJobFactory);
        return schedulerFactoryBean;
    }
}
