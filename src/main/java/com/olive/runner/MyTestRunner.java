package com.olive.runner;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
public class MyTestRunner implements CommandLineRunner {
	@Autowired
    private Job jobA;
	@Autowired
	private JobLauncher launcher;
	
	public void run(String... args) throws Exception {
	 JobParameters jobParameters=new JobParametersBuilder()
			                 .addLong("Time :: ", System.currentTimeMillis())
			                 .addString("Launched By :: ", "Ravi Kumar Singh")
			                 .toJobParameters();
     launcher.run(jobA, jobParameters);
	}

}
