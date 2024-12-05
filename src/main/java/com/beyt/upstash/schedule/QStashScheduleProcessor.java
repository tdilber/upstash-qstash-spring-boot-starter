package com.beyt.upstash.schedule;

import com.beyt.upstash.annotation.QStashSchedule;
import com.beyt.upstash.client.QStashClient;
import com.beyt.upstash.configuration.QStashProperties;
import com.beyt.upstash.util.AnnotationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class QStashScheduleProcessor implements ApplicationRunner, BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(QStashScheduleProcessor.class);
    private final List<ScheduleListener> scheduleListeners = new CopyOnWriteArrayList<>();
    public Set<String> scheduleNames = new CopyOnWriteArraySet<>();
    List<String> existingSchedules = new ArrayList<>();

    @Autowired
    private QStashClient qStashClient;

    @Autowired
    private QStashProperties qStashProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Object> listSchedules = qStashClient.listSchedules();
        existingSchedules.addAll(listSchedules.stream().map(LinkedHashMap.class::cast).map(c -> (String)c.get("destination")).map(c -> c.replace(qStashProperties.getAppEndpoint() + "/listener/schedule/", "")).toList());
    }


    public record ScheduleListener(String scheduleName, String cron, Object bean, Method method, QStashSchedule annotation) {
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        Map<QStashSchedule, Method>  listeners = AnnotationUtil.findAllMethodListenerAnnotations(targetClass, QStashSchedule.class);

        listeners.forEach((schedule, method) -> {
            if (method.getParameterCount() != 0) {
                throw new IllegalArgumentException("Schedule listener method should not have any parameter");
            }
            ScheduleListener scheduleListener = new ScheduleListener(schedule.scheduleName(), schedule.cron(), bean, method, schedule);
            if (scheduleNames.contains(scheduleListener.scheduleName())) {
                throw new IllegalArgumentException("Duplicate schedule name: " + scheduleListener.scheduleName());
            }
            scheduleListeners.add(scheduleListener);
            updateSchedule(scheduleListener);
            log.info("Schedule listener registered: {}", schedule);
        });

        return bean;
    }

    protected void updateSchedule(ScheduleListener listener) {
        if (!existingSchedules.contains(listener.scheduleName())) {
            qStashClient.updateSchedule(listener.scheduleName(), listener.cron(), listener.annotation());
            log.info("Schedule new registered: {}", listener);
        } else {
            log.info("Schedule already exists: {}", listener);
        }
    }

    public void process(String jobName) {
        ScheduleListener scheduleListener = getScheduleListener(jobName);
        try {
            log.info("Invoking schedule listener method: {}", scheduleListener);
            scheduleListener.method().invoke(scheduleListener.bean());
        } catch (Exception e) {
            throw new RuntimeException("Error while invoking schedule listener method", e);
        }
    }

    protected ScheduleListener getScheduleListener(String scheduleName) {
        return scheduleListeners.stream()
                .filter(scheduleListener -> scheduleListener.scheduleName().equals(scheduleName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Schedule listener not found for scheduleName: " + scheduleName));
    }
}
