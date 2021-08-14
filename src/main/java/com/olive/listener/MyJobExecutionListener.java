package com.olive.listener;

import java.util.Date;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class MyJobExecutionListener implements JobExecutionListener{

	public void beforeJob(JobExecution je) {
		System.out.println("Before Starting Job Status ::> " + je.getStatus());
		System.out.println("Data And Time :: > " +new Date());
	}

	public void afterJob(JobExecution je) {
		System.out.println("After Completeting Job Status ::> " + je.getStatus());
		System.out.println("Data And Time :: > " +new Date());
		
	}

}
