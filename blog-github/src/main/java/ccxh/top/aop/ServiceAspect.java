package ccxh.top.aop;

import ccxh.top.pojo.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * service所有方法 切面 统一 回复对象
 * 以及检测运行时间
 * @author admin
 */
@Aspect
@Component
public class ServiceAspect {
    private final  static  Logger LOGGER=LoggerFactory.getLogger(ServiceAspect.class);
    private final static Integer TIME_OUT_WARN_VALUE=500;
    private final static Integer TIME_OUT_VALUE=700;
    @Around(value="execution(* ccxh.top.service.impl.*.*(..))")
    public Object aroundMethod(ProceedingJoinPoint jp){
        Long startTime=System.currentTimeMillis();
        Object proceed=null;
        try {
             proceed = jp.proceed();
            Long duration = System.currentTimeMillis() - startTime;
            if(duration<TIME_OUT_WARN_VALUE){
                return proceed;
            }else if (duration>TIME_OUT_WARN_VALUE){
                LOGGER.warn("{},调用时长:{}毫秒",jp.getSignature().getName(),duration);
            }else if (duration>TIME_OUT_VALUE){
               return Result.error("service time out");
            }
        }catch (Exception e){
            return Result.error(e.getMessage());
        }catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return proceed;
    }

}
