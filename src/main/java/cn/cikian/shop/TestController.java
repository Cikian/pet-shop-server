package cn.cikian.shop;


import cn.cikian.system.sys.service.EmailService;
import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-05 17:43
 */

@RestController
@RequestMapping("/api/test/email")
public class TestController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/simple")
    public void sendSimpleEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String text) {
        emailService.sendSimpleEmail(to, subject, text);
    }

    @GetMapping("/html")
    public void sendHtmlEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String text) {
        String htmlText = "<html><body><h1>hello</h1></body></html>";
        emailService.sendHtmlEmail(to, subject, htmlText);
    }

    @GetMapping("/template")
    public void sendTemplateEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String text) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", to);
        variables.put("code", RandomUtil.randomString(6));
        emailService.sendTemplateEmail(to, subject, "EmailTemplate", variables);
    }
}
