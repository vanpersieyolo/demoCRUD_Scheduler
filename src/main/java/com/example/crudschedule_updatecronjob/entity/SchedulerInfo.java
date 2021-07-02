package com.example.crudschedule_updatecronjob.entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class SchedulerInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String jobName;
    private String jobClass;
    private String cronExpression;
    private Long repeatTime;
    private boolean cronJob;
    private String description;
    private int expectDate;

}
