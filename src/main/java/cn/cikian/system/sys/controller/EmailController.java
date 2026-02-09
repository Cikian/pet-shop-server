package cn.cikian.system.sys.controller;


import cn.cikian.system.core.utils.RedisCache;
import cn.cikian.system.sys.entity.vo.Result;
import cn.cikian.system.sys.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-09 17:48
 */

@RestController
@RequestMapping("/api/v1/email")
@Tag(name = "邮件", description = "邮件相关接口")
public class EmailController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private RedisCache redisCache;

    @Operation(summary = "发送验证码", description = "用户输入邮箱，发送验证码到邮箱")
    @GetMapping("/getVerificationCode")
    public Result<?> sendVerificationCode(@RequestParam String to) {
        Map<String, Object> variables = new HashMap<>();
        // 生成六位随机验证码
        String text = String.format("%06d", (int) (Math.random() * 1000000));
        redisCache.setCacheObject(to, text, 10 * 60, TimeUnit.SECONDS);
        variables.put("verificationCode", text);
        emailService.sendTemplateEmail(to, "Verification Code", "Verification", variables);
        return Result.ok("Verification code sent successfully");
    }
}
