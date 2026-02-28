package cn.cikian.system.core.exception;

import cn.cikian.system.sys.entity.enmu.SysStatus;
import cn.cikian.system.sys.entity.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026/2/10 01:33
 */

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(CikException.class)
    public Result<?> handleBusinessException(CikException e) {
        logger.error("业务异常: {}", e.getMessage(), e);
        return Result.error(e.getCode() == null ? SysStatus.UNKNOW.code() : e.getCode(), e.getMessage(), e.getCause());
    }

    /**
     * 用户名或密码错误异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public Result<?> handleBadCredentialsException(BadCredentialsException e) {
        logger.error("用户名或密码错误: {}", e.getMessage());
        return Result.error(SysStatus.USER_BAD, e.getCause());
    }

    /**
     * 用户名不存在
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public Result<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        logger.error("用户名不存在: {}", e.getMessage());
        return Result.error(SysStatus.NO_USER, e.getCause());
    }

    /**
     * 处理参数校验异常（@Validated @Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        logger.error("参数校验异常: {}", message);
        return Result.error(400, "参数错误: " + message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        logger.error("参数绑定异常: {}", message);
        return Result.error(400, "参数绑定错误: " + message);
    }

    /**
     * 处理单个参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        logger.error("参数校验异常: {}", message);
        return Result.error(400, "参数错误: " + message);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<String> handleNotFoundException(NoHandlerFoundException e) {
        logger.error("404异常: {}", e.getRequestURL());
        return Result.error(404, "接口不存在: " + e.getRequestURL());
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<String> handleNullPointerException(NullPointerException e) {
        logger.error("空指针异常: ", e);
        return Result.error(500, "系统内部错误: 空指针异常");
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e, HttpServletRequest request) {
        logger.error("系统异常: URL={}, Exception: ", request.getRequestURL(), e);

        // 可以根据异常类型做不同的处理
        if (e instanceof IllegalArgumentException) {
            return Result.error(400, "请求参数错误: " + e.getMessage());
        }

        // 生产环境返回通用错误信息，开发环境返回详细信息
        String errorMsg = "系统异常";
        if ("dev".equals(System.getProperty("spring.profiles.active"))) {
            errorMsg = e.getMessage();
        }

        return Result.error(500, errorMsg);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        logger.error("运行时异常: ", e);
        return Result.error(500, "系统运行时异常: " + e.getMessage());
    }
}
