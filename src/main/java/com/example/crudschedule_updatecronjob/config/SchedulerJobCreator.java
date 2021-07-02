package com.example.crudschedule_updatecronjob.config;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.text.ParseException;
import java.util.Date;

public class SchedulerJobCreator {
  private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerJobCreator.class);

  public static JobDetail createJob(Class<? extends QuartzJobBean> jobClass, boolean isDurable,
                                    ApplicationContext context, String jobName) {
    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
    factoryBean.setJobClass(jobClass);
    factoryBean.setDurability(isDurable);
    factoryBean.setApplicationContext(context);
    factoryBean.setName(jobName);

    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put(jobName, jobClass.getName());
    factoryBean.setJobDataMap(jobDataMap);

    factoryBean.afterPropertiesSet();

    return factoryBean.getObject();
  }

  public static CronTrigger createCronTrigger(String triggerName, Date startTime, String cronExpression,
                                              int misFireInstruction) {
    CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
    factoryBean.setName(triggerName);
    factoryBean.setStartTime(startTime);
    factoryBean.setCronExpression(cronExpression);
    factoryBean.setMisfireInstruction(misFireInstruction);
    try {
      factoryBean.afterPropertiesSet();
    } catch (ParseException e) {
//      LOGGER.error(LogUtils.printLogStackTrace(e));
    }
    return factoryBean.getObject();
  }

  public static SimpleTrigger createSimpleTrigger(String triggerName, Date startTime, Long repeatTime,
                                                  int misFireInstruction) {
    SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
    factoryBean.setName(triggerName);
    factoryBean.setStartTime(startTime);
    factoryBean.setRepeatInterval(repeatTime);
    factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
    factoryBean.setMisfireInstruction(misFireInstruction);
    factoryBean.afterPropertiesSet();
    return factoryBean.getObject();
  }
}
