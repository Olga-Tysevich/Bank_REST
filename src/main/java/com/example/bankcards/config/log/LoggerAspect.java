package com.example.bankcards.config.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * LoggerAspect is an aspect that provides logging functionality for method entry, exit, and exceptions
 * in Spring Beans annotated with {@link org.springframework.web.bind.annotation.RestController},
 * {@link org.springframework.stereotype.Service}, or {@link org.springframework.stereotype.Repository}.
 * <p>
 * This aspect logs method invocations with their arguments before execution, their results after successful execution,
 * and exceptions if thrown. It uses SLF4J for logging.
 */
@Slf4j
@Aspect
@Component
public class LoggerAspect {

    /**
     * Defines a pointcut that matches methods in Spring beans annotated with
     * {@link org.springframework.web.bind.annotation.RestController},
     * {@link org.springframework.stereotype.Service}, or
     * {@link org.springframework.stereotype.Repository}.
     * <p>
     * This pointcut is used to apply logging before and after methods are executed.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Service *) || " +
            "within(@org.springframework.stereotype.Repository *)")
    public void springBeanPointcut() {}

    /**
     * Logs method entry by capturing method name and arguments before the execution of any method
     * in Spring beans annotated with {@link org.springframework.web.bind.annotation.RestController},
     * {@link org.springframework.stereotype.Service}, or {@link org.springframework.stereotype.Repository}.
     * <p>
     * The log message includes the method name and the arguments passed to it.
     *
     * @param jp the {@link JoinPoint} representing the method being executed.
     */
    @Before("springBeanPointcut()")
    public void logMethodEntry(JoinPoint jp) {
        if (log.isDebugEnabled()) {
            String args = Arrays.stream(jp.getArgs())
                    .map(arg -> arg != null ? arg.toString() : "null")
                    .collect(Collectors.joining(", "));
            log.debug("→ {}.{}() with args = [{}]",
                    jp.getSignature().getDeclaringTypeName(),
                    jp.getSignature().getName(),
                    args);
        }
    }

    /**
     * Logs method exit by capturing the result after a successful method execution.
     * This is triggered after the method has successfully executed in Spring beans annotated with
     * {@link org.springframework.web.bind.annotation.RestController},
     * {@link org.springframework.stereotype.Service}, or {@link org.springframework.stereotype.Repository}.
     * <p>
     * The log message includes the method name and the result returned by the method.
     *
     * @param jp the {@link JoinPoint} representing the method that was executed.
     * @param result the result returned by the method.
     */
    @AfterReturning(pointcut = "springBeanPointcut()", returning = "result")
    public void logMethodExit(JoinPoint jp, Object result) {
        if (log.isDebugEnabled()) {
            log.debug("← {}.{}() with result = [{}]",
                    jp.getSignature().getDeclaringTypeName(),
                    jp.getSignature().getName(),
                    result != null ? result.toString() : "null");
        }
    }

    /**
     * Logs exception details when a method throws an exception during execution.
     * This is triggered after a method in Spring beans annotated with
     * {@link org.springframework.web.bind.annotation.RestController},
     * {@link org.springframework.stereotype.Service}, or
     * {@link org.springframework.stereotype.Repository} throws an exception.
     * <p>
     * The log message includes the method name, the cause of the exception (if available),
     * and the exception message.
     *
     * @param jp the {@link JoinPoint} representing the method that threw the exception.
     * @param e the exception thrown by the method.
     */
    @AfterThrowing(pointcut = "springBeanPointcut()", throwing = "e")
    public void logException(JoinPoint jp, Exception e) {
        log.error("Exception in {}.{}() with cause = {} and message = {}",
                jp.getSignature().getDeclaringTypeName(),
                jp.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL",
                e.getMessage() != null ? e.getMessage() : "NULL",
                e);
    }
}
