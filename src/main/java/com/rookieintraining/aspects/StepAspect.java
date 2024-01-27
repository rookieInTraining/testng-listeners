package com.rookieintraining.aspects;

import com.rookieintraining.services.UIReportingService;
import com.rookieintraining.utils.AspectUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Aspect
public class StepAspect {

    private static final Collector<CharSequence, ?, String> JOINER = Collectors.joining(", ", "[", "]");

    @Pointcut("@annotation(Step) && execution(* *(..))")
    public void stepMethod() {

    }

    @Around("stepMethod()")
    public Object stepMethodAdvice(final ProceedingJoinPoint joinPoint) {
        Step stepInfo = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Step.class);
        Map<String, Object> params = AspectUtils.getParametersMap(joinPoint);
        Matcher matcher = Pattern.compile("\\{([^}]*)}").matcher(stepInfo.value());
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            final String pattern = matcher.group(1);
            final String replacement = processPattern(pattern, params).orElseGet(matcher::group);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        System.out.println("############## - Step Info : " + MessageFormat.format(stepInfo.value(), params.get("0")));
        UIReportingService.getScenarioThreadLocal().get().scenarioLog.add(MessageFormat.format(stepInfo.value(), params.get("0")));
        Object proceed;
        try {
            proceed = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return proceed;
    }

    private static Optional<String> processPattern(final String pattern, final Map<String, Object> params) {
        if (pattern.isEmpty()) {
            System.out.println("Could not process empty pattern");
            return Optional.empty();
        }
        final String[] parts = pattern.split("\\.");
        final String parameterName = parts[0];
        if (!params.containsKey(parameterName)) {
            System.out.println("Could not find parameter " + parameterName);
            return Optional.empty();
        }
        final Object param = params.get(parameterName);
        return Optional.ofNullable(extractProperties(param, parts, 1));
    }

    @SuppressWarnings("ReturnCount")
    private static String extractProperties(final Object object, final String[] parts, final int index) {
        if (Objects.isNull(object)) {
            return "null";
        }
        if (index < parts.length) {
            if (object instanceof Object[]) {
                return Stream.of((Object[]) object)
                        .map(child -> extractProperties(child, parts, index))
                        .collect(JOINER);
            }
            if (object instanceof Iterable) {
                final Spliterator<?> iterator = ((Iterable) object).spliterator();
                return StreamSupport.stream(iterator, false)
                        .map(child -> extractProperties(child, parts, index))
                        .collect(JOINER);
            }
            final Object child = extractChild(object, parts[index]);
            return extractProperties(child, parts, index + 1);
        }
        return Objects.toString(object);
    }

    private static Object extractChild(final Object object, final String part) {
        final Class<?> type = object == null ? Object.class : object.getClass();
        try {
            return extractField(object, part, type);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to extract " + part + " value from " + type.getName(), e);
        }
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private static Object extractField(final Object object, final String part, final Class<?> type)
            throws ReflectiveOperationException {
        try {
            final Field field = type.getField(part);
            return fieldValue(object, field);
        } catch (NoSuchFieldException e) {
            Class<?> t = type;
            while (t != null) {
                try {
                    final Field declaredField = t.getDeclaredField(part);
                    return fieldValue(object, declaredField);
                } catch (NoSuchFieldException ignore) {
                    // Ignore
                }
                t = t.getSuperclass();
            }
            throw e;
        }
    }

    private static Object fieldValue(final Object object, final Field field) throws IllegalAccessException {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            field.setAccessible(true);
            return field.get(object);
        }
    }

}
