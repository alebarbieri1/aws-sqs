package br.com.demo.sqs.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sqs")
@Slf4j
public class SqsController {

	@Autowired
	private QueueMessagingTemplate queueMessagingTemplate;

	@Value("${cloud.aws.sqs.queue-url}")
	private String sqsQueueUrl;

	@GetMapping
	public void sendMessage() {
		Map<String, String> headers = new HashMap<>();
		headers.put("message-group-id", "1");
		headers.put("message-deduplication-id", UUID.randomUUID().toString());

		queueMessagingTemplate.send(sqsQueueUrl,
				MessageBuilder.withPayload("Hello from Spring Boot").copyHeaders(headers).build());
	}

	@SqsListener(value = "${cloud.aws.sqs.queue-name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void getMessage(String message) throws Exception {
		log.info("Message received from SQS: {}", message);
	}

}