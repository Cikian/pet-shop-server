package cn.cikian.system.core.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cikian
 * @version 1.0
 * @implNote 用于配置SpringDoc的OpenAPI文档
 * @see <a href="https://www.cikian.cn">https://www.cikian.cn</a>
 * @since 2026-02-10 00:34
 */
@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI selfOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Cikian");
        contact.setUrl("https://www.cikian.cn");
        contact.setEmail("support.cikian.cn");

        return new OpenAPI().info(new Info()
                        .title("PetShop API文档")
                        .description("PetShop 应用接口文档")
                        .contact(contact)
                        .version("v1.0.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("更多文档")
                        .url("https://springdoc.org"));
    }

}
