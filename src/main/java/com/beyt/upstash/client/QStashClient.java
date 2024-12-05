package com.beyt.upstash.client;

import com.beyt.upstash.annotation.QStashSchedule;
import com.beyt.upstash.configuration.QStashProperties;
import com.beyt.upstash.model.SendProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

public class QStashClient {

    private final RestTemplate restTemplate;
    private final String endpoint;
    private final HttpHeaders headers;
    private final QStashProperties properties;

    public QStashClient(RestTemplate restTemplate, QStashProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.endpoint = properties.getEndpoint();
        this.headers = new HttpHeaders();
        this.headers.setBearerAuth(properties.getApiKey());
        this.headers.setContentType(MediaType.APPLICATION_JSON);
    }

    // Queue operations
    public String enqueueMessage(String queueName, Object body, SendProperties sendProperties) {

        String returnUrl = properties.getAppEndpoint() + "/listener/queue/" + queueName;

        if (Strings.isNotBlank(sendProperties.getUrlGroup())) {
            returnUrl = sendProperties.getUrlGroup();
        }

        String url = endpoint + "/v2/enqueue/" + queueName + "/" + returnUrl;

        HttpHeaders customHeaders = new HttpHeaders(headers);
        if (Objects.nonNull(sendProperties.getDelay())) {
            customHeaders.set("Upstash-Delay", sendProperties.getDelay().getSeconds() + "s");
        }
        if (Objects.nonNull(sendProperties.getNotBefore())) {
            customHeaders.set("Upstash-Not-Before", sendProperties.getNotBefore().toString());
        }
        if (Objects.nonNull(sendProperties.getRetryCount())) {
            customHeaders.set("Upstash-Retries", sendProperties.getRetryCount().toString());
        }
        if (StringUtils.isNotBlank(sendProperties.getCallbackUrl())) {
            customHeaders.set("Upstash-Callback", properties.getAppEndpoint() + sendProperties.getCallbackUrl());
        }
        if (StringUtils.isNotBlank(sendProperties.getFailureCallbackUrl())) {
            customHeaders.set("Upstash-Failure-Callback", properties.getAppEndpoint() + sendProperties.getFailureCallbackUrl());
        }
        if (StringUtils.isNotBlank(sendProperties.getDeduplicationId())) {
            customHeaders.set("Upstash-Deduplication-Id", properties.getAppEndpoint() + sendProperties.getFailureCallbackUrl());
        }

        if (sendProperties.getCustomHeaders() != null) {
            sendProperties.getCustomHeaders().forEach(customHeaders::set);
        }
        var request = new HttpEntity<>(body, customHeaders);
        return restTemplate.postForObject(URI.create(url), request, String.class);
    }


    // Background Jobs
    public String scheduleBackgroundJob(String jobName, String cronExpression, SendProperties sendProperties) {
        String returnUrl = properties.getAppEndpoint() + "/listener/queue/" + jobName;
        if (Strings.isNotBlank(sendProperties.getUrlGroup())) {
            returnUrl = sendProperties.getUrlGroup();
        }
        String url = endpoint + "/v2/schedules/" + returnUrl;

        HttpHeaders customHeaders = new HttpHeaders(headers);
        customHeaders.set("Upstash-Cron", cronExpression);

        if (Objects.nonNull(sendProperties.getRetryCount())) {
            customHeaders.set("Upstash-Retries", sendProperties.getRetryCount().toString());
        }
        if (StringUtils.isNotBlank(sendProperties.getCallbackUrl())) {
            customHeaders.set("Upstash-Callback", properties.getAppEndpoint() + sendProperties.getCallbackUrl());
        }
        if (StringUtils.isNotBlank(sendProperties.getFailureCallbackUrl())) {
            customHeaders.set("Upstash-Failure-Callback", properties.getAppEndpoint() + sendProperties.getFailureCallbackUrl());
        }

        if (sendProperties.getCustomHeaders() != null) {
            sendProperties.getCustomHeaders().forEach(customHeaders::set);
        }
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(customHeaders);
        return restTemplate.postForObject(URI.create(url), request, String.class);
    }


//    // URL Groups
//    public String configureURLGroup(String groupName, List<String> urls, SendProperties sendProperties) {
//        String url = endpoint + "/url-groups/" + groupName;
//        Map<String, Object> body = new HashMap<>();
//        body.put("urls", urls);
//        HttpHeaders customHeaders = new HttpHeaders(headers);
//        if (sendProperties.getCustomHeaders() != null) {
//            sendProperties.getCustomHeaders().forEach(customHeaders::set);
//        }
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, customHeaders);
//        return restTemplate.postForObject(url, request, String.class);
//    }

    public List<Object> listSchedules() {
        String url = endpoint + "/v2/schedules";
        return restTemplate.exchange(URI.create(url), HttpMethod.GET, new HttpEntity<>(headers), List.class).getBody();
    }

    public void updateSchedule(String scheduleName, String cron, QStashSchedule schedule) {
        String url = endpoint + "/v2/schedules/" + properties.getAppEndpoint() + "/listener/schedule/"+ scheduleName;

        HttpHeaders customHeaders = new HttpHeaders(headers);

        customHeaders.set("Upstash-Cron", cron);
        customHeaders.set("App-Schedule-Name", scheduleName);

        if (schedule.retryCount() > 0) {
            customHeaders.set("Upstash-Retries", schedule.retryCount() + "");
        }
        if (StringUtils.isNotBlank(schedule.callbackUrl())) {
            customHeaders.set("Upstash-Callback", properties.getAppEndpoint() + schedule.callbackUrl());
        }
        if (StringUtils.isNotBlank(schedule.failureCallbackUrl())) {
            customHeaders.set("Upstash-Failure-Callback", properties.getAppEndpoint() + schedule.failureCallbackUrl());
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(customHeaders);
        restTemplate.exchange(URI.create(url), HttpMethod.POST, request, String.class);
    }

    public void deleteQueue(String queueName) {
        String url = endpoint + "/v2/queues/" + queueName;
        restTemplate.exchange(URI.create(url), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
    }

    public void deleteScheduleByName(String scheduleName) {
        List<Object> listSchedules = listSchedules();
        Optional<String> scheduleId = listSchedules.stream().map(LinkedHashMap.class::cast).filter(c -> scheduleName.equalsIgnoreCase(((String) c.get("destination")).replace(properties.getAppEndpoint() + "/listener/schedule/", "")))
                .map(c -> (String) c.get("scheduleId")).findFirst();

        if (scheduleId.isEmpty()) {
            return;
        }
        String url = endpoint + "/v2/schedules/" + scheduleId.get();
        restTemplate.exchange(URI.create(url), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
    }
}
