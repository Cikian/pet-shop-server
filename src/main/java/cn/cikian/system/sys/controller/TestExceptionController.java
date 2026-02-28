package cn.cikian.system.sys.controller;

import cn.cikian.system.sys.entity.vo.Result;
import cn.cikian.system.sys.exception.CikException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 测试异常控制器
 * 用于验证自定义异常和全局异常处理器的功能
 */
@Slf4j
@RestController
@Tag(name = "测试异常", description = "测试异常相关接口")
@RequestMapping("/api/v1/test/exception")
public class TestExceptionController {

    /**
     * 测试成功响应
     * @return 成功响应
     */
    @GetMapping("/success")
    public Result<?> testSuccess() {
        return Result.ok("测试成功");
    }

    /**
     * 测试业务异常
     * @return 业务异常响应
     */
    @GetMapping("/business")
    public Result<?> testBusinessException() {
        throw new CikException(400, "测试业务异常");
    }

    /**
     * 测试认证异常
     * @return 认证异常响应
     */
    @GetMapping("/auth")
    public Result<?> testAuthenticationException() {
        throw new org.springframework.security.core.AuthenticationException("测试认证异常") {
        };
    }

    /**
     * 测试权限异常
     * @return 权限异常响应
     */
    @GetMapping("/access")
    public Result<?> testAccessDeniedException() {
        throw new org.springframework.security.access.AccessDeniedException("测试权限不足异常");
    }

    /**
     * 测试IO异常
     * @return IO异常响应
     */
    @GetMapping("/io")
    public Result<?> testIOException() throws IOException {
        throw new IOException("测试IO异常");
    }

    /**
     * 测试系统异常
     * @return 系统异常响应
     */
    @GetMapping("/system")
    public Result<?> testSystemException() {
        throw new NullPointerException("测试空指针异常");
    }

    /**
     * 测试参数验证异常
     * @param id ID参数
     * @return 参数验证异常响应
     */
    @GetMapping("/param")
    public Result<?> testParamException(Integer id) {
        if (id == null) {
            throw new CikException(400, "ID参数不能为空");
        }
        return Result.ok(id, "参数验证成功");
    }
}
