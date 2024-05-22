package by.pavvel.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MeasurementAspect {

    private static final Logger logger = LogManager.getLogger(MeasurementAspect.class);

    @Pointcut("@annotation(by.pavvel.aspect.Timed)")
    public void anyMethod() {}

    @Around("anyMethod()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        logger.info("Execution time of {}.{} - {} ms", className, methodName, executionTime);
        return proceed;
    }
}
