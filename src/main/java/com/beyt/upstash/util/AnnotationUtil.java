package com.beyt.upstash.util;

import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class AnnotationUtil {

    public static <A extends Annotation> Map<A, Method> findAllMethodListenerAnnotations(Class<?> targetClass, Class<A> annotationClass) {
        Map<A, Method> allListenerAnnotations = new HashMap<>();
        ReflectionUtils.doWithMethods(targetClass, method -> {
            if (method.getDeclaringClass().getName().contains("$MockitoMock$")) {
                return;
            }
            List<A> listenerAnnotations = findListenerAnnotations(method, annotationClass);
            listenerAnnotations.forEach(listener -> allListenerAnnotations.put(listener, method));
        });
        return allListenerAnnotations;
    }

    public static <A extends Annotation> List<A> findListenerAnnotations(AnnotatedElement element, Class<A> annotationClass) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
                .stream(annotationClass)
                .filter(tma -> {
                    Object source = tma.getSource();
                    String name = "";
                    if (source instanceof Class<?>) {
                        name = ((Class<?>) source).getName();
                    }
                    else if (source instanceof Method) {
                        name = ((Method) source).getDeclaringClass().getName();
                    }
                    return !name.contains("$MockitoMock$");
                })
                .map(ann -> ann.synthesize())
                .collect(Collectors.toList());
    }
}
