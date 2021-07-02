package com.example.crudschedule_updatecronjob.service.impl;

import com.example.crudschedule_updatecronjob.config.SchedulerJobCreator;
import com.example.crudschedule_updatecronjob.entity.SchedulerInfo;
import com.example.crudschedule_updatecronjob.repo.ScheduleRepository;
import com.example.crudschedule_updatecronjob.service.SchedulerJobService;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SchedulerJobServiceImpl implements SchedulerJobService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerJobServiceImpl.class);
  @Autowired
  private ScheduleRepository schedulerJobRepo;

  @Autowired
  private SchedulerFactoryBean schedulerFactoryBean;

  @Autowired
  private ApplicationContext context;

  @Override
  public List<SchedulerInfo> listSchedulerJob() {
    return schedulerJobRepo.findAllByCronJob(true);
  }

  @Override
  public List<Map<String, Object>> getAllJobResponse() throws Exception {
    List<Map<String, Object>> list = new ArrayList<>();
    try {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();

      for (String groupName : scheduler.getJobGroupNames()) {
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

          String jobName = jobKey.getName();
          String jobGroup = jobKey.getGroup();

          //get job's trigger
          List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
          Date scheduleTime = triggers.get(0).getStartTime();
          Date nextFireTime = triggers.get(0).getNextFireTime();
          Date lastFiredTime = triggers.get(0).getPreviousFireTime();

          Map<String, Object> map = new HashMap<>();
          map.put("jobName", jobName);
          map.put("groupName", jobGroup);
          map.put("cronExpression", schedulerJobRepo.findByJobName(jobName).getCronExpression());
          map.put("scheduleTime", scheduleTime);
          map.put("lastFiredTime", lastFiredTime);
          map.put("nextFireTime", nextFireTime);

          if (isJobRunning(jobName)) {
            map.put("jobStatus", "RUNNING");
          } else {
            String jobState = getJobState(jobName);
            map.put("jobStatus", jobState);
          }

          Date currentDate = new Date();
          if (scheduleTime.compareTo(currentDate) > 0) {
            map.put("jobStatus", "scheduled");

          } else if (scheduleTime.compareTo(currentDate) < 0) {
            map.put("jobStatus", "Running");

          } else if (scheduleTime.compareTo(currentDate) == 0) {
            map.put("jobStatus", "Running");
          }

          list.add(map);
          LOGGER.info("Job details:");
          LOGGER.info("Job Name:" + jobName + ", Schedule Time:" + scheduleTime);
        }

      }
    } catch (SchedulerException e) {
      LOGGER.error("SchedulerException while fetching all jobs. error message :" + e.getMessage());
      e.printStackTrace();
    }
    return list;
  }

  @Override
  public void startAllSchedulers() {
    List<SchedulerInfo> jobInfoList = this.listSchedulerJob();
    if (jobInfoList != null) {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      jobInfoList.forEach(jobInfo -> {
        try {
          JobDetail jobDetail = JobBuilder.newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
            .withIdentity(jobInfo.getJobName()).build();
          if (!scheduler.checkExists(jobDetail.getKey())) {
            Trigger trigger = null;
            jobDetail = SchedulerJobCreator
              .createJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), false, context,
                jobInfo.getJobName());

            if (jobInfo.isCronJob() && CronExpression.isValidExpression(jobInfo.getCronExpression())) {
              trigger = SchedulerJobCreator
                .createCronTrigger(jobInfo.getJobName(), new Date(), jobInfo.getCronExpression(),
                  SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
            }
            scheduler.scheduleJob(jobDetail, trigger);
          }
        } catch (ClassNotFoundException e) {
//          LOGGER.error(LogUtils.printLogStackTrace(e));
          System.out.println(e.getMessage());
        } catch (SchedulerException e) {
//          LOGGER.error(LogUtils.printLogStackTrace(e));
          System.out.println(e.getMessage());
        }
      });
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public SchedulerInfo startScheduler(SchedulerInfo request) throws Exception {
    SchedulerInfo schedulerInfo = null;
    Trigger trigger = null;
    try {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();

      JobDetail jobDetail = JobBuilder.newJob((Class<? extends QuartzJobBean>) Class.forName(request.getJobClass()))
        .withIdentity(request.getJobName()).build();
      if (!scheduler.checkExists(jobDetail.getKey())) {

        jobDetail = SchedulerJobCreator
          .createJob((Class<? extends QuartzJobBean>) Class.forName(request.getJobClass()), true, context,
            request.getJobName());

        if (request.isCronJob()) {
          trigger = SchedulerJobCreator.createCronTrigger(request.getJobName(), new Date(), request.getCronExpression(),
            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
          scheduler.scheduleJob(jobDetail, trigger);
          //save info scheduler
          request.setJobName(request.getJobName().toUpperCase().trim());
          schedulerInfo = this.createSchedulerJob(request);
        }
      } else {
        throw new Exception("scheduleNewJobRequest.jobAlreadyExist");
      }

    } catch (ClassNotFoundException e) {
//      LOGGER.error(LogUtils.printLogStackTrace(e));
      System.out.println(e.getMessage());
    } catch (SchedulerException e) {
//      LOGGER.error(LogUtils.printLogStackTrace(e));
      System.out.println(e.getMessage());
    }
    return schedulerInfo;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public SchedulerInfo reScheduler(SchedulerInfo request) throws Exception {
    Trigger newTrigger = null;
    SchedulerInfo schedulerInfo = null;
    try {
      if (request.isCronJob()) {
        newTrigger = SchedulerJobCreator
          .createCronTrigger(request.getJobName().toUpperCase().trim(), new Date(), request.getCronExpression(),
            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        Date dt = schedulerFactoryBean.getScheduler()
          .rescheduleJob(TriggerKey.triggerKey(request.getJobName()), newTrigger);
        if (dt == null) {
          throw new Exception("Cannot reschedule with cron expression: " + request.getCronExpression());
        }
        LOGGER.info(
          "Trigger associated with jobKey :" + request.getJobName() + " rescheduled successfully for date :" + dt);
        schedulerInfo = this.updateSchedulerJob(request);
        if (schedulerInfo == null) {
          throw new Exception("UPDATE_SCHEDULER_JOB_FAILED");
        }
      }
    } catch (SchedulerException e) {
//      LOGGER.error(LogUtils.printLogStackTrace(e));
      System.out.println(e.getMessage());
    }
    return schedulerInfo;
  }

  @Override
  public boolean startNowScheduler(String jobName) throws Exception {
    SchedulerInfo schedulerInfo = this.getSchedulerJobByJobName(jobName);
    if (schedulerInfo == null) {
      throw new Exception("SCHEDULER_JOB_DOES_NOT_EXIST");
    }
    String jobKey = jobName;
    JobKey jKey = new JobKey(jobKey);
    LOGGER.info("Parameters received for starting job now : jobKey :" + jobKey);
    try {
      schedulerFactoryBean.getScheduler().triggerJob(jKey);
      LOGGER.info("Job with jobKey :" + jobKey + " started now successfully.");
      return true;
    } catch (Exception e) {
      LOGGER.error("SchedulerException while starting job now with key :" + jobKey + " message :" + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean stopNowScheduler(String jobName) {
    try {
      SchedulerInfo schedulerInfo = this.getSchedulerJobByJobName(jobName);
      if (schedulerInfo == null) {
        throw new Exception("SCHEDULER_JOB_DOES_NOT_EXIST");
      }
      String jobKey = schedulerInfo.getJobName();

      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      JobKey jKey = new JobKey(jobKey);
      return scheduler.interrupt(jKey);
    } catch (Exception e) {
      LOGGER.error("SchedulerException while stopping job. error message :" + e.getMessage());
    }
    return false;
  }

  @Override
  public boolean pauseScheduler(String jobName) throws Exception {
    SchedulerInfo schedulerInfo = this.getSchedulerJobByJobName(jobName);
    if (schedulerInfo == null) {
      throw new Exception("SCHEDULER_JOB_DOES_NOT_EXIST");
    }
    schedulerFactoryBean.getScheduler().pauseJob(new JobKey(schedulerInfo.getJobName()));
    return true;
  }

  @Override
  public boolean resumeScheduler(String jobName) throws Exception {
    SchedulerInfo schedulerInfo = this.getSchedulerJobByJobName(jobName);
    if (schedulerInfo == null) {
      throw new Exception("SCHEDULER_JOB_DOES_NOT_EXIST");
    }
    schedulerFactoryBean.getScheduler().resumeJob(new JobKey(schedulerInfo.getJobName()));
    return true;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteScheduler(String jobName) throws Exception {
    SchedulerInfo schedulerInfo = this.getSchedulerJobByJobName(jobName);
    if (schedulerInfo == null) {
      throw new Exception("SCHEDULER_JOB_DOES_NOT_EXIST");
    }
    schedulerFactoryBean.getScheduler().deleteJob(new JobKey(schedulerInfo.getJobName()));
    int deleted = this.deleteSchedulerJob(jobName.toUpperCase().trim());
    if (deleted <= 0) {
      return false;
    }
    return true;
  }

  @Override
  public boolean isJobWithNamePresent(String jobName) {
    try {
      SchedulerInfo schedulerInfo = this.getSchedulerJobByJobName(jobName);
      if (schedulerInfo == null) {
        throw new Exception("SCHEDULER_JOB_DOES_NOT_EXIST");
      }
      JobKey jobKey = new JobKey(schedulerInfo.getJobName());
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      if (scheduler.checkExists(jobKey)) {
        return true;
      }
    } catch (Exception e) {
      LOGGER.error("SchedulerException while checking job with name and group exist:" + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean isJobRunning(String jobName) throws Exception {
    LOGGER.info("Request received to check if job is running");
    SchedulerInfo schedulerInfo = this.getSchedulerJobByJobName(jobName);
    if (schedulerInfo == null) {
      throw new Exception("SCHEDULER_JOB_DOES_NOT_EXIST");
    }
    String jobKey = schedulerInfo.getJobName();
    LOGGER.info("Parameters received for checking job is running now : jobKey :" + jobKey);
    try {

      List<JobExecutionContext> currentJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
      if (currentJobs != null) {
        for (JobExecutionContext jobCtx : currentJobs) {
          String jobNameDB = jobCtx.getJobDetail().getKey().getName();
          if (jobKey.equalsIgnoreCase(jobNameDB)) {
            return true;
          }
        }
      }
    } catch (SchedulerException e) {
      LOGGER.error(
        "SchedulerException while checking job with key :" + jobKey + " is running. error message :" + e.getMessage());
      e.printStackTrace();
      return false;
    }
    return false;
  }

  @Override
  public String getJobState(String jobName) throws Exception {
    try {
      SchedulerInfo schedulerInfo = this.getSchedulerJobByJobName(jobName);
      if (schedulerInfo == null) {
        throw new Exception("SCHEDULER_JOB_DOES_NOT_EXIST");
      }
      JobKey jobKey = new JobKey(jobName);
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      JobDetail jobDetail = scheduler.getJobDetail(jobKey);

      List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
      if (triggers != null && triggers.size() > 0) {
        for (Trigger trigger : triggers) {
          TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

          if (TriggerState.PAUSED.equals(triggerState)) {
            return "PAUSED";
          } else if (TriggerState.BLOCKED.equals(triggerState)) {
            return "BLOCKED";
          } else if (TriggerState.COMPLETE.equals(triggerState)) {
            return "COMPLETE";
          } else if (TriggerState.ERROR.equals(triggerState)) {
            return "ERROR";
          } else if (TriggerState.NONE.equals(triggerState)) {
            return "NONE";
          } else if (TriggerState.NORMAL.equals(triggerState)) {
            return "SCHEDULED";
          }
        }
      }
    } catch (SchedulerException e) {
      LOGGER.error("SchedulerException while checking job with name and group exist:" + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public SchedulerInfo createSchedulerJob(SchedulerInfo request) throws Exception {
    SchedulerInfo schedulerInfo = schedulerJobRepo.findByJobName(request.getJobName());
    if (schedulerInfo != null) {
      throw new Exception(".SCHEDULER_JOB_ALREADY_EXIST");
    }
    return schedulerJobRepo.save(request);
  }

  @Override
  public SchedulerInfo updateSchedulerJob(SchedulerInfo request) throws Exception {
    SchedulerInfo schedulerInfo = schedulerJobRepo.findByJobName(request.getJobName().toUpperCase().trim());
    if (schedulerInfo == null) {
      throw new Exception(".SCHEDULER_JOB_DOES_NOT_EXIST");
    }
    schedulerInfo.setCronExpression(request.getCronExpression());
    schedulerInfo.setCronJob(request.isCronJob());
    schedulerInfo.setDescription(request.getDescription());
    schedulerInfo.setRepeatTime(request.getRepeatTime());
    schedulerInfo.setExpectDate(request.getExpectDate());
    SchedulerInfo schedulerInfoUpdated = schedulerJobRepo.save(schedulerInfo);
    return schedulerInfoUpdated;
  }

  @Override
  public SchedulerInfo getSchedulerJobByJobName(String jobName) {
    return schedulerJobRepo.findByJobName(jobName.toUpperCase().trim());
  }

  @Override
  public int deleteSchedulerJob(String jobName) {
    return schedulerJobRepo.deleteByJobName(jobName.toUpperCase().trim());
  }

  @Override
  public int getExpectTime(String jobName) {
    SchedulerInfo schedulerInfo = schedulerJobRepo.findByJobName(jobName);
    return schedulerInfo.getExpectDate();
  }
}