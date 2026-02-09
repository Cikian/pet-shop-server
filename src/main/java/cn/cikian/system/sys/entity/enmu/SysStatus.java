package cn.cikian.system.sys.entity.enmu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP状态码字典
 * <p>
 * 1xx: 信息性状态码
 * 2xx: 成功状态码
 * 3xx: 重定向状态码
 * 4xx: 客户端错误状态码
 * 5xx: 服务器错误状态码
 */
@Getter
@Schema(name = "HTTP状态码枚举")
public enum HttpStatus {
    // 1xx 信息性状态码
    CONTINUE(100, "Continue", "服务器已收到请求的初始部分，客户端可以继续发送剩余部分"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols", "服务器同意切换协议，如从HTTP切换到WebSocket"),
    PROCESSING(102, "Processing", "服务器正在处理请求，但尚未完成"),

    // 2xx 成功状态码
    OK(200, "OK", "请求成功，服务器返回请求的资源"),
    CREATED(201, "Created", "请求成功并创建了新资源"),
    ACCEPTED(202, "Accepted", "请求已被接受，但尚未处理完成"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information", "服务器返回的信息来自第三方源"),
    NO_CONTENT(204, "No Content", "请求成功，但服务器没有返回内容"),
    RESET_CONTENT(205, "Reset Content", "请求成功，客户端应重置文档视图"),
    PARTIAL_CONTENT(206, "Partial Content", "服务器返回部分资源（用于断点续传）"),

    // 3xx 重定向状态码
    MULTIPLE_CHOICES(300, "Multiple Choices", "请求的资源有多个可能的位置"),
    MOVED_PERMANENTLY(301, "Moved Permanently", "资源已永久移动到新位置"),
    FOUND(302, "Found", "资源临时移动到新位置"),
    SEE_OTHER(303, "See Other", "客户端应使用GET方法请求另一个URL"),
    NOT_MODIFIED(304, "Not Modified", "资源未修改，客户端可使用缓存"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect", "临时重定向，保持原请求方法"),
    PERMANENT_REDIRECT(308, "Permanent Redirect", "永久重定向，保持原请求方法"),

    // 4xx 客户端错误状态码
    BAD_REQUEST(400, "Bad Request", "请求无效，服务器无法理解"),
    UNAUTHORIZED(401, "Unauthorized", "请求需要身份验证"),
    BAD_PWD(401, "Unauthorized", "密码错误"),
    NO_USER(401, "Unauthorized", "用户名不存在"),
    USER_BAD(401, "Unauthorized", "用户名或密码不正确"),
    NEED_LOGIN(401, "Unauthorized", "登录过期，请重新登录"),
    PAYMENT_REQUIRED(402, "Payment Required", "保留状态码，用于未来的支付系统"),
    FORBIDDEN(403, "Forbidden", "服务器拒绝访问请求的资源"),
    NOT_FOUND(404, "Not Found", "请求的资源不存在"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", "请求的HTTP方法不被允许"),
    NOT_ACCEPTABLE(406, "Not Acceptable", "服务器无法生成符合客户端Accept头的响应"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required", "需要代理服务器身份验证"),
    REQUEST_TIMEOUT(408, "Request Timeout", "服务器等待请求超时"),
    CONFLICT(409, "Conflict", "请求与服务器上的资源冲突"),
    GONE(410, "Gone", "请求的资源已永久删除"),
    LENGTH_REQUIRED(411, "Length Required", "服务器需要Content-Length头"),
    PRECONDITION_FAILED(412, "Precondition Failed", "请求的前提条件失败"),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large", "请求体过大"),
    URI_TOO_LONG(414, "URI Too Long", "请求的URI过长"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type", "请求的媒体类型不被支持"),
    RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable", "请求的范围无效"),
    EXPECTATION_FAILED(417, "Expectation Failed", "服务器无法满足Expect头的要求"),
    TOO_MANY_REQUESTS(429, "Too Many Requests", "客户端发送请求过于频繁"),

    // 5xx 服务器错误状态码
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "服务器内部错误"),
    NOT_IMPLEMENTED(501, "Not Implemented", "服务器不支持请求的功能"),
    BAD_GATEWAY(502, "Bad Gateway", "服务器作为网关或代理，收到无效响应"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable", "服务器暂时无法处理请求"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout", "服务器作为网关或代理，请求超时"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported", "服务器不支持请求的HTTP版本"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage", "服务器存储空间不足"),
    LOOP_DETECTED(508, "Loop Detected", "服务器检测到无限循环");

    private final int code;
    private final String name;
    private final String description;

    // 用于快速查找
    private static final Map<Integer, HttpStatus> CODE_MAP = new HashMap<>();

    static {
        for (HttpStatus status : values()) {
            CODE_MAP.put(status.code, status);
        }
    }

    HttpStatus(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /**
     * 根据状态码获取HttpStatus枚举
     * @param code 状态码
     * @return HttpStatus枚举，如果不存在返回null
     */
    public static HttpStatus getByCode(int code) {
        return CODE_MAP.get(code);
    }

    /**
     * 检查状态码是否为信息性状态码（1xx）
     * @return 是否为信息性状态码
     */
    public boolean isInformational() {
        return code >= 100 && code < 200;
    }

    /**
     * 检查状态码是否为成功状态码（2xx）
     * @return 是否为成功状态码
     */
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    /**
     * 检查状态码是否为重定向状态码（3xx）
     * @return 是否为重定向状态码
     */
    public boolean isRedirect() {
        return code >= 300 && code < 400;
    }

    /**
     * 检查状态码是否为客户端错误状态码（4xx）
     * @return 是否为客户端错误状态码
     */
    public boolean isClientError() {
        return code >= 400 && code < 500;
    }

    /**
     * 检查状态码是否为服务器错误状态码（5xx）
     * @return 是否为服务器错误状态码
     */
    public boolean isServerError() {
        return code >= 500 && code < 600;
    }

    public int code() {
        return this.code;
    }

    public String message() {
        return this.description;
    }
}
