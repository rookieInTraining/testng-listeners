package com.rookieintraining.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AspectUtils {

    private static Map<String, Object> getParametersMap(final MethodSignature signature, final Object... args) {
        final String[] parameterNames = signature.getParameterNames();
        final Map<String, Object> params = new HashMap<>();
        params.put("method", signature.getName());
        for (int i = 0; i < Math.max(parameterNames.length, args.length); i++) {
            params.put(parameterNames[i], args[i]);
            params.put(Integer.toString(i), args[i]);
        }
        return params;
    }

    public static Map<String, Object> getParametersMap(final JoinPoint joinPoint) {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Map<String, Object> params = getParametersMap(methodSignature, joinPoint.getArgs());
        Optional.ofNullable(joinPoint.getThis()).ifPresent(objThis -> params.put("this", objThis));
        return params;
    }

}
