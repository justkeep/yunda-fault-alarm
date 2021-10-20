package com.yunda.faultalarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.yunda.faultalarm"})
public class FaultAlarmApplication {

	public static void main(String[] args) {
		SpringApplication.run(FaultAlarmApplication.class, args);
	}

}
