package com.darkpixellabs.honeytrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HoneyTrapApplication {
    public static void main(String[] args) { SpringApplication.run(HoneyTrapApplication.class, args); }
}
