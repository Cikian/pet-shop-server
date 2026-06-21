package cn.cikian.system.core.exception;

import cn.cikian.system.sys.entity.enmu.BizCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import java.io.Serial;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 */
@Getter
@Setter
public class CikException extends InternalAuthenticationServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public CikException(String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }

    /**
     * 构造方法
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public CikException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 支持直接传入标准的 BizCode 枚举
     */
    public CikException(BizCode bizCode) {
        super(bizCode.getDescription());
        this.code = bizCode.getCode();
        this.message = bizCode.getDescription();
    }

    /**
     * 构造方法
     *
     * @param code    错误码
     * @param message 错误消息
     * @param cause   异常原因
     */
    public CikException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause   异常原因
     */
    public CikException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = 500;
    }
}
