package com.beyt.upstash.test;

import com.beyt.upstash.annotation.EnableQStash;
import com.beyt.upstash.annotation.QStashListener;
import com.beyt.upstash.annotation.QStashSchedule;
import com.beyt.upstash.client.QStashClient;
import com.beyt.upstash.model.SendProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EnableQStash
@SpringBootApplication
public class QStashSpringBootStarterApplication {
	public static void main(String[] args) {
		SpringApplication.run(QStashSpringBootStarterApplication.class, args);
	}
}


