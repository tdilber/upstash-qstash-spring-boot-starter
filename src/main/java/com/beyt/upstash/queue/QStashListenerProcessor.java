package com.beyt.upstash.queue;

import com.beyt.upstash.annotation.QStashListener;
import com.beyt.upstash.util.AnnotationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class QStashListenerProcessor implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(QStashListenerProcessor.class);
    private final List<QueueListener> queueListeners = new CopyOnWriteArrayList<>();


    public record QueueListener(String queueName, Object bean, Method method) {
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        Map<QStashListener, Method>  listeners = AnnotationUtil.findAllMethodListenerAnnotations(targetClass, QStashListener.class);

        listeners.forEach((listener, method) -> {
            if (method.getParameterCount() != 1) {
                throw new IllegalArgumentException("Queue listener method should have only one parameter");
            }
            queueListeners.add(new QueueListener(listener.queueName(), bean, method));
            log.info("Queue listener registered: {}", listener);
        });

        return bean;
    }

    public void process(String queueName, Object body) {
        QueueListener queueListener = getQueueListener(queueName);
        try {
            log.info("Processing queue listener: {}", queueListener);
            queueListener.method().invoke(queueListener.bean(), body);
        } catch (Exception e) {
            throw new RuntimeException("Error while invoking queue listener method", e);
        }
    }

    protected QueueListener getQueueListener(String queueName) {
        return queueListeners.stream()
                .filter(queueListener -> queueListener.queueName().equals(queueName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Queue listener not found for queue name: " + queueName));
    }

}
