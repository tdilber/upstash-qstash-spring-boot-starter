package com.beyt.upstash.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QStashSchedule {
    String scheduleName();
    String cron();
    int retryCount() default -1; //Upstash-Retries
    String callbackUrl() default ""; //Upstash-Callback
    String failureCallbackUrl() default ""; //Upstash-Failure-Callback
}
