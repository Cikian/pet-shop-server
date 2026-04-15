package cn.cikian.system;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication(scanBasePackages = {"cn.cikian"})
@EnableTransactionManagement
@MapperScan("cn.cikian.**.mapper")
public class ShopServerApplication {

    public static void main(String[] args) {
        // 加载 .env 文件
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 将 .env 中的每一项放入系统属性，以便 Spring Boot 的 ${...} 可以读取
        for (DotenvEntry entry : dotenv.entries()) {
            System.out.println("环境变量["+ entry.getKey() + " = " +  entry.getValue() + "]");
            System.setProperty(entry.getKey(), entry.getValue());
        }

        SpringApplication.run(ShopServerApplication.class, args);

        log.info("启动成功...");
    }

}
