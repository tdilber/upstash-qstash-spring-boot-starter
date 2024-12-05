package com.beyt.upstash.configuration;

import com.beyt.upstash.client.QStashClient;
import com.beyt.upstash.queue.QStashListenerProcessor;
import com.beyt.upstash.queue.QStashQueueListenerController;
import com.beyt.upstash.schedule.QStashScheduleListenerController;
import com.beyt.upstash.schedule.QStashScheduleProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnClass(QStashClient.class)
@EnableConfigurationProperties(QStashProperties.class)
@Import(value = { QStashListenerProcessor.class, QStashQueueListenerController.class, QStashScheduleListenerController.class, QStashScheduleProcessor.class })
public class QStashAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public QStashClient qStashClient(QStashProperties properties, RestTemplate restTemplate) {
        return new QStashClient(restTemplate, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .build();
    }
}
