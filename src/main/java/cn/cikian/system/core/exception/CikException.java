package cn.cikian.system.core.exception;

import cn.cikian.system.sys.entity.enmu.SysStatus;
import lombok.Data;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-10 01:25
 */

public class CikException extends RuntimeException {

    private Integer code;

    public CikException(String message) {
        super(message);
        this.code = 500; // 默认错误码
    }

    public CikException(SysStatus sysStatus) {
        super(sysStatus.message());
        this.code = sysStatus.code(); // 默认错误码
    }

    public CikException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public CikException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public CikException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
