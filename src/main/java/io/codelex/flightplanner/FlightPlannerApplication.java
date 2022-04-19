package io.codelex.flightplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FlightPlannerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlightPlannerApplication.class, args);
    }
}