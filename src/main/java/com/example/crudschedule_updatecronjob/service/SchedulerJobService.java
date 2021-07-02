package com.example.crudschedule_updatecronjob.service;

import com.example.crudschedule_updatecronjob.entity.SchedulerInfo;

import java.util.List;
import java.util.Map;

public interface SchedulerJobService {
  List<SchedulerInfo> listSchedulerJob() throws Exception;

  List<Map<String, Object>> getAllJobResponse() throws Exception;

  void startAllSchedulers() throws Exception;

  SchedulerInfo startScheduler(SchedulerInfo request) throws Exception;

  SchedulerInfo reScheduler(SchedulerInfo request) throws Exception;

  boolean startNowScheduler(String jobName) throws Exception;

  boolean stopNowScheduler(String jobName) throws Exception;

  boolean pauseScheduler(String jobName) throws Exception;

  boolean resumeScheduler(String jobName) throws Exception;

  boolean deleteScheduler(String jobName) throws Exception;

  boolean isJobWithNamePresent(String jobName) throws Exception;

  boolean isJobRunning(String jobName) throws Exception;

  String getJobState(String jobName) throws Exception;

  SchedulerInfo createSchedulerJob(SchedulerInfo request) throws Exception;

  SchedulerInfo updateSchedulerJob(SchedulerInfo request) throws Exception;

  SchedulerInfo getSchedulerJobByJobName(String jobName) throws Exception;

  int deleteSchedulerJob(String jobName) throws Exception;

  int getExpectTime(String jobName);
}
