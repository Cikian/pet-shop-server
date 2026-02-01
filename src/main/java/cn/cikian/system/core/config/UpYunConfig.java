package cn.cikian.system.core.config;


import com.upyun.RestManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cikian
 * @version 1.0
 * @implNote
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-01 03:45
 */

@Configuration
public class UpYunConfig {
    @Value("${oss.up-yun.bucket-name}")
    private String bucketName;
    @Value("${oss.up-yun.user-name}")
    private String userName;
    @Value("${oss.up-yun.pwd}")
    private String pwd;

    @Bean
    public RestManager restManager() {
        RestManager restManager = new RestManager(bucketName, userName, pwd);
        restManager.setApiDomain(RestManager.ED_AUTO);
        return restManager;
    }
}
