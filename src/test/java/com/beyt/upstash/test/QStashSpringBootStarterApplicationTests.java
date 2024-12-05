package com.beyt.upstash.test;

import com.beyt.upstash.annotation.QStashListener;
import com.beyt.upstash.annotation.QStashSchedule;
import com.beyt.upstash.client.QStashClient;
import com.beyt.upstash.model.SendProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = QStashSpringBootStarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class QStashSpringBootStarterApplicationTests {
	private static final Logger log = LoggerFactory.getLogger(QStashSpringBootStarterApplicationTests.class);
	public static final String QUEUE_NAME = "testQueue";
	public static final String SCHEDULE_NAME = "testSchedule";
	private final BlockingQueue<Map<String, String>> messageQueue = new LinkedBlockingQueue<>();
	private final BlockingQueue<String> messageSchedule = new LinkedBlockingQueue<>();

	@Autowired
	private QStashClient qStashClient;

	@Test
	public void testEnqueueAndListenerAndSchedule() throws InterruptedException {
		// Generate a unique message for this test
		String testData = "TEST RESULT " + UUID.randomUUID().toString();
		Map<String, String> payload = Map.of("data", testData);

		// Publish the message
		String result = qStashClient.enqueueMessage(QUEUE_NAME, payload, new SendProperties());
		log.info("Message enqueued with result: " + result);

		// Wait for the listener to process the message
		Map<String, String> receivedMessage = messageQueue.poll(60, TimeUnit.SECONDS);
		log.info("Message received: " + receivedMessage);
		String receivedScheduleMessage = messageSchedule.poll(90, TimeUnit.SECONDS);
		log.info("Schedule received: " + receivedScheduleMessage);

		assertNotNull(receivedMessage, "Listener did not receive the message in time");
		assertEquals(testData, receivedMessage.get("data"), "Received message data does not match");

		assertNotNull(receivedScheduleMessage, "Listener did not receive the message in time");
		assertEquals(SCHEDULE_NAME, receivedScheduleMessage, "Received message data does not match");
	}

	@QStashListener(queueName = QUEUE_NAME)
	public void testQueueTest(Map<String, String> message) {
		log.info("Listener processed message: " + message);
		messageQueue.offer(message);
	}

	@QStashSchedule(scheduleName = SCHEDULE_NAME, cron = "* * * * *")
	public void updateScheduleTest() {
		log.info(SCHEDULE_NAME);
		messageSchedule.offer(SCHEDULE_NAME);
	}

	@AfterEach
	public void cleanup() {
		log.info("Cleaning up test data");
		qStashClient.deleteQueue(QUEUE_NAME);
		log.info("Queue deleted: " + QUEUE_NAME);
		qStashClient.deleteScheduleByName(SCHEDULE_NAME);
		log.info("Schedule deleted: " + SCHEDULE_NAME);
	}
}
