package com.example.crudschedule_updatecronjob.controller;

import com.example.crudschedule_updatecronjob.service.SchedulerJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scheduler")
public class SchedulerJobController {
  private static final Logger logger = LoggerFactory.getLogger(SchedulerJobController.class);

  @Autowired
  private SchedulerJobService schedulerJobService;

  @GetMapping("/get-all")
  public ResponseEntity getAllSchedulerJob() {
    try {
      List<Map<String, Object>> list = schedulerJobService.getAllJobResponse();
      return new ResponseEntity(list,HttpStatus.OK);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

//  @PostMapping("/create")
//  public ResponseData createSchedulerJob(@RequestBody SchedulerInfo request) {
//    try {
//      logger.info("createSchedulerJob with:" + request.toString());
//      if (request == null || StringUtils.isBlank(request.getJobName()) || StringUtils
//        .isBlank(request.getCronExpression())) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.DATA_INVALID);
//      }
//      request.setJobName(request.getJobName().toUpperCase().trim());
//      SchedulerInfo schedulerInfo = schedulerJobService.startScheduler(request);
//      if (schedulerInfo == null || !schedulerJobService.isJobWithNamePresent(schedulerInfo.getJobName())) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(),
//          ResponseMessageConstants.CREATE_SCHEDULER_JOB_FAILED);
//      }
//      return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
//        ResponseMessageConstants.CREATE_SCHEDULER_JOB_SUCCESS);
//    } catch (BusinessException e) {
//      logger.error(e.getMessage());
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//    } catch (Exception e) {
//      logger.error(LogUtils.printLogStackTrace(e));
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.MSG_ERR_SYSTEM);
//    }
//  }
//
//  @PostMapping("/update")
//  public ResponseData updateSchedulerJob(@RequestBody SchedulerInfo request) {
//    try {
//      logger.info("updateSchedulerJob with:" + request.toString());
//      if (request == null || StringUtils.isBlank(request.getJobName())) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.DATA_INVALID);
//      }
//      request.setJobName(request.getJobName().toUpperCase().trim());
//      schedulerJobService.reScheduler(request);
//      if (request == null || !schedulerJobService.isJobWithNamePresent(request.getJobName())) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(),
//          ResponseMessageConstants.UPDATE_SCHEDULER_JOB_FAILED);
//      }
//      return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
//        ResponseMessageConstants.UPDATE_SCHEDULER_JOB_SUCCESS);
//    } catch (BusinessException e) {
//      logger.error(e.getMessage());
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//    } catch (Exception e) {
//      logger.error(LogUtils.printLogStackTrace(e));
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.MSG_ERR_SYSTEM);
//    }
//  }
//
//  @PostMapping("/start-now/{jobName}")
//  public ResponseData startNowScheduler(@PathVariable("jobName") String jobName) {
//    try {
//      logger.info("startNowScheduler with:" + jobName);
//      if (StringUtils.isBlank(jobName)) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.DATA_INVALID);
//      }
//      if (schedulerJobService.isJobWithNamePresent(jobName)) {
//        if (!schedulerJobService.isJobRunning(jobName)) {
//          boolean status = schedulerJobService.startNowScheduler(jobName);
//          if (status) {
//            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
//              ResponseMessageConstants.START_NOW_SCHEDULER_SUCCESS);
//          } else {
//            //Server error
//            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(),
//              ResponseMessageConstants.START_NOW_SCHEDULER_FAILED);
//          }
//        } else {
//          //Job not in running state
//          return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(),
//            ResponseMessageConstants.JOB_ALREADY_IN_RUNNING_STATE);
//        }
//      } else {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(),
//          ResponseMessageConstants.SCHEDULER_JOB_DOES_NOT_EXIST);
//      }
//    } catch (BusinessException e) {
//      logger.error(e.getMessage());
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//    } catch (Exception e) {
//      logger.error(LogUtils.printLogStackTrace(e));
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.MSG_ERR_SYSTEM);
//    }
//  }
//
//  @PostMapping("/stop-now/{jobName}")
//  public ResponseData stopNowScheduler(@PathVariable("jobName") String jobName) {
//    try {
//      logger.info("stopNowScheduler with:" + jobName);
//      if (StringUtils.isBlank(jobName)) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.DATA_INVALID);
//      }
//      if (schedulerJobService.isJobWithNamePresent(jobName)) {
//        if (schedulerJobService.isJobRunning(jobName)) {
//          boolean status = schedulerJobService.stopNowScheduler(jobName);
//          if (status) {
//            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
//              ResponseMessageConstants.STOP_NOW_SCHEDULER_SUCCESS);
//          } else {
//            //Server error
//            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(),
//              ResponseMessageConstants.STOP_NOW_SCHEDULER_FAILED);
//          }
//        } else {
//          //Job not in running state
//          return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(),
//            ResponseMessageConstants.JOB_NOT_IN_RUNNING_STATE);
//        }
//      } else {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(),
//          ResponseMessageConstants.SCHEDULER_JOB_DOES_NOT_EXIST);
//      }
//    } catch (BusinessException e) {
//      logger.error(e.getMessage());
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//    } catch (Exception e) {
//      logger.error(LogUtils.printLogStackTrace(e));
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.MSG_ERR_SYSTEM);
//    }
//  }
//
//  @PostMapping("/pause/{jobName}")
//  public ResponseData pauseSchedulerJob(@PathVariable("jobName") String jobName) {
//    try {
//      logger.info("pauseSchedulerJob with:" + jobName);
//      if (StringUtils.isBlank(jobName)) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.DATA_INVALID);
//      }
//      schedulerJobService.pauseScheduler(jobName);
//      return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
//        ResponseMessageConstants.PAUSE_SCHEDULER_SUCCESS);
//    } catch (BusinessException e) {
//      logger.error(e.getMessage());
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//    } catch (Exception e) {
//      logger.error(LogUtils.printLogStackTrace(e));
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.MSG_ERR_SYSTEM);
//    }
//  }
//
//  @PostMapping("/resume/{jobName}")
//  public ResponseData resumeSchedulerJob(@PathVariable("jobName") String jobName) {
//    try {
//      logger.info("resumeSchedulerJob with:" + jobName);
//      if (StringUtils.isBlank(jobName)) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.DATA_INVALID);
//      }
//      schedulerJobService.resumeScheduler(jobName);
//      return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
//        ResponseMessageConstants.RESUME_SCHEDULER_SUCCESS);
//    } catch (BusinessException e) {
//      logger.error(e.getMessage());
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//    } catch (Exception e) {
//      logger.error(LogUtils.printLogStackTrace(e));
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.MSG_ERR_SYSTEM);
//    }
//  }
//
//  @PostMapping("/delete/{jobName}")
//  public ResponseData deleteSchedulerJob(@PathVariable("jobName") String jobName) {
//    try {
//      logger.info("deleteSchedulerJob with:" + jobName);
//      if (StringUtils.isBlank(jobName)) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.DATA_INVALID);
//      }
//      boolean rsDeleted = schedulerJobService.deleteScheduler(jobName);
//      if (!rsDeleted) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(),
//          ResponseMessageConstants.DELETE_SCHEDULER_JOB_FAILED);
//      }
//      return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
//        ResponseMessageConstants.DELETE_SCHEDULER_JOB_SUCCESS);
//    } catch (BusinessException e) {
//      logger.error(e.getMessage());
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//    } catch (Exception e) {
//      logger.error(LogUtils.printLogStackTrace(e));
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.MSG_ERR_SYSTEM);
//    }
//  }
//
//  @GetMapping("/check-job/{jobName}")
//  public ResponseData checkSchedulerJob(@PathVariable("jobName") String jobName) {
//    try {
//      logger.info("checkSchedulerJob with:" + jobName);
//      if (StringUtils.isBlank(jobName)) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.DATA_INVALID);
//      }
//      boolean isCheckStatus = schedulerJobService.isJobWithNamePresent(jobName);
//      if (!isCheckStatus) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(),
//          ResponseMessageConstants.DELETE_SCHEDULER_JOB_FAILED);
//      }
//      return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
//        ResponseMessageConstants.DELETE_SCHEDULER_JOB_SUCCESS);
//    } catch (BusinessException e) {
//      logger.error(e.getMessage());
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//    } catch (Exception e) {
//      logger.error(LogUtils.printLogStackTrace(e));
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.MSG_ERR_SYSTEM);
//    }
//  }
//
//  @GetMapping("/check-job-running/{jobName}")
//  public ResponseData checkSchedulerJobRunning(@PathVariable("jobName") String jobName) {
//    try {
//      logger.info("checkSchedulerJobRunning with:" + jobName);
//      if (StringUtils.isBlank(jobName)) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.DATA_INVALID);
//      }
//      boolean isCheckStatus = schedulerJobService.isJobRunning(jobName);
//      if (!isCheckStatus) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), false);
//      }
//      return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), true);
//    } catch (BusinessException e) {
//      logger.error(e.getMessage());
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//    } catch (Exception e) {
//      logger.error(LogUtils.printLogStackTrace(e));
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.MSG_ERR_SYSTEM);
//    }
//  }
//
//  @GetMapping("/get-job-state/{jobName}")
//  public ResponseData getSchedulerJobState(@PathVariable("jobName") String jobName) {
//    try {
//      logger.info("getSchedulerJobState with:" + jobName);
//      if (StringUtils.isBlank(jobName)) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.DATA_INVALID);
//      }
//      String jobState = schedulerJobService.getJobState(jobName);
//      if (StringUtils.isBlank(jobState)) {
//        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), null);
//      }
//      return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), jobState);
//    } catch (BusinessException e) {
//      logger.error(e.getMessage());
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//    } catch (Exception e) {
//      logger.error(LogUtils.printLogStackTrace(e));
//      return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.MSG_ERR_SYSTEM);
//    }
//  }
}
