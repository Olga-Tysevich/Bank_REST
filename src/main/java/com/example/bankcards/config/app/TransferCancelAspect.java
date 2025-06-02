package com.example.bankcards.config.app;

import com.example.bankcards.events.processors.TransferQueueProcessor;
import com.example.bankcards.service.impl.TransferServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Set;
/**
 * Aspect class for controlling the access to methods annotated with {@link DenyCancelTransfer}.
 * This aspect ensures that methods annotated with {@link DenyCancelTransfer} can only be executed
 * from specific allowed classes. If a method is called from a non-permitted class, an {@link IllegalAccessException}
 * will be thrown to prevent the cancellation operation from being executed.
 * <p>
 * The allowed classes are specified in the {@link #allowedClasses} set. Currently, only
 * {@link TransferQueueProcessor} and {@link TransferServiceImpl} are permitted to call methods annotated with
 * {@link DenyCancelTransfer}.
 * </p>
 *
 * @see DenyCancelTransfer
 * @see TransferQueueProcessor
 * @see TransferServiceImpl
 */
@Slf4j
@Aspect
@Component
public class TransferCancelAspect {
    /**
     * A set of allowed classes that are permitted to call methods annotated with {@link DenyCancelTransfer}.
     * Only methods invoked from these classes will be allowed to proceed.
     */
    private final Set<Class<?>> allowedClasses = Set.of(
            TransferQueueProcessor.class,
            TransferServiceImpl.class
    );

    /**
     * Around advice to check permission before allowing the execution of a method annotated with {@link DenyCancelTransfer}.
     * If the method is called from a class that is not in the {@link #allowedClasses} set, an {@link IllegalAccessException}
     * will be thrown to deny the cancellation process.
     *
     * @param joinPoint The join point representing the method execution.
     * @return The result of the method execution if allowed.
     * @throws Throwable if the method execution is denied or an exception occurs during execution.
     */
    @Around("@annotation(DenyCancelTransfer)")
    public Object checkPermissionForCancel(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean isValid = isValidContext(joinPoint);

        if (isValid) {
            log.info("Method {} from {} is allowed to proceed.", joinPoint.getSignature(), joinPoint.getTarget().getClass().getName());
            return joinPoint.proceed();
        } else {
            log.warn("Attempt to cancel transfer denied for method {} from {}. Invalid context.",
                    joinPoint.getSignature(), joinPoint.getTarget().getClass().getName());
            throw new IllegalAccessException("Cancellation is not allowed in this context");
        }
    }

    /**
     * Checks if the method is invoked from a valid context. Only methods invoked from classes in the
     * {@link #allowedClasses} set are considered valid.
     *
     * @param joinPoint The join point representing the method execution.
     * @return {@code true} if the method is called from an allowed class, {@code false} otherwise.
     */
    private boolean isValidContext(ProceedingJoinPoint joinPoint) {
        return allowedClasses.stream().anyMatch(allowedClass -> allowedClass.isAssignableFrom(joinPoint.getTarget().getClass()));
    }

}
