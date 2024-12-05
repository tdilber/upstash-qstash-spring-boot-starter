package com.beyt.upstash.model;

import java.time.Duration;
import java.util.Map;

public class SendProperties {
    private Map<String, String> customHeaders;
    private Duration delay; // Upstash-Delay
    private Long notBefore; // Upstash-Not-Before
    private Integer retryCount; //Upstash-Retries
    private String callbackUrl; //Upstash-Callback
    private String failureCallbackUrl; //Upstash-Failure-Callback
    private String cron;  //Upstash-Cron
    private String deduplicationId;  //Upstash-Deduplication-Id
    private String urlGroup;
    private Integer parallelism;

    public SendProperties() {
    }

    private SendProperties(Builder builder) {
        this.customHeaders = builder.customHeaders;
        this.delay = builder.delay;
        this.notBefore = builder.notBefore;
        this.retryCount = builder.retryCount;
        this.callbackUrl = builder.callbackUrl;
        this.failureCallbackUrl = builder.failureCallbackUrl;
        this.cron = builder.cron;
        this.deduplicationId = builder.deduplicationId;
        this.urlGroup = builder.urlGroup;
        this.parallelism = builder.parallelism;
    }


    public Map<String, String> getCustomHeaders() {
        return customHeaders;
    }

    public void setCustomHeaders(Map<String, String> customHeaders) {
        this.customHeaders = customHeaders;
    }

    public Duration getDelay() {
        return delay;
    }

    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    public Long getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(Long notBefore) {
        this.notBefore = notBefore;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getFailureCallbackUrl() {
        return failureCallbackUrl;
    }

    public void setFailureCallbackUrl(String failureCallbackUrl) {
        this.failureCallbackUrl = failureCallbackUrl;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getUrlGroup() {
        return urlGroup;
    }

    public void setUrlGroup(String urlGroup) {
        this.urlGroup = urlGroup;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public String getDeduplicationId() {
        return deduplicationId;
    }

    public void setDeduplicationId(String deduplicationId) {
        this.deduplicationId = deduplicationId;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private Map<String, String> customHeaders;
        private Duration delay;
        private Long notBefore;
        private Integer retryCount;
        private String callbackUrl;
        private String failureCallbackUrl;
        private String cron;
        private String deduplicationId;
        private String urlGroup;
        private Integer parallelism;

        public Builder setCustomHeaders(Map<String, String> customHeaders) {
            this.customHeaders = customHeaders;
            return this;
        }

        public Builder setDelay(Duration delay) {
            this.delay = delay;
            return this;
        }

        public Builder setNotBefore(Long notBefore) {
            this.notBefore = notBefore;
            return this;
        }

        public Builder setRetryCount(Integer retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder setCallbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public Builder setFailureCallbackUrl(String failureCallbackUrl) {
            this.failureCallbackUrl = failureCallbackUrl;
            return this;
        }

        public Builder setCron(String cron) {
            this.cron = cron;
            return this;
        }

        public Builder setDeduplicationId(String deduplicationId) {
            this.deduplicationId = deduplicationId;
            return this;
        }

        public Builder setUrlGroup(String urlGroup) {
            this.urlGroup = urlGroup;
            return this;
        }

        public Builder setParallelism(Integer parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        public SendProperties build() {
            return new SendProperties(this);
        }
    }

}
