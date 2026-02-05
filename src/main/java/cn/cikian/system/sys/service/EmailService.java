package cn.cikian.system.sys.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-05 17:35
 */

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);

    void sendHtmlEmail(String to, String subject, String htmlContent);

    void sendEmailWithAttachment(String to, String subject, String text, MultipartFile attachment);

    void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables);
}
