package com.example.crudschedule_updatecronjob.repo;

import com.example.crudschedule_updatecronjob.entity.SchedulerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<SchedulerInfo, Long> {
    List<SchedulerInfo> findAllByCronJob(boolean cronJob);

    SchedulerInfo findByJobName(String jobName);

    int deleteByJobName(String jobName);
}
