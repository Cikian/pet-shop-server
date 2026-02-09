package cn.cikian.system.sys.controller;


import cn.cikian.system.sys.entity.vo.Result;
import cn.cikian.system.sys.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-09 17:48
 */

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/getVerificationCode")
    public Result<?> sendVerificationCode(@RequestParam String to) {
        Map<String, Object> variables = new HashMap<>();
        String text = "123456";
        variables.put("verificationCode", text);
        emailService.sendTemplateEmail(to, "Verification Code", "Verification", variables);
        return Result.ok("Verification code sent successfully");
    }
}
