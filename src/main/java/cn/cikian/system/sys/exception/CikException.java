package cn.cikian.system.sys.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 */
@Getter
@Setter
public class CikException extends RuntimeException {

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
     * @param message 错误消息
     */
    public CikException(String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }

    /**
     * 构造方法
     * @param code 错误码
     * @param message 错误消息
     */
    public CikException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法
     * @param code 错误码
     * @param message 错误消息
     * @param cause 异常原因
     */
    public CikException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法
     * @param message 错误消息
     * @param cause 异常原因
     */
    public CikException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = 500;
    }
}
