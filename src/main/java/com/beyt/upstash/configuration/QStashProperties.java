package com.beyt.upstash.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.qstash")
public class QStashProperties {

    private String apiKey;
    private String endpoint;
    private String appEndpoint;

    // Getter ve Setter

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAppEndpoint() {
        return appEndpoint;
    }

    public void setAppEndpoint(String appEndpoint) {
        this.appEndpoint = appEndpoint;
    }
}
