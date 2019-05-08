package cn.jiiiiiin;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import lombok.val;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * https://codecentric.github.io/spring-boot-admin/2.1.3/#getting-started
 *
 * https://github.com/codecentric/spring-boot-admin/blob/8b7584f83273db707bb72f24c35b09a7f56453b9/spring-boot-admin-samples/spring-boot-admin-sample-eureka/src/main/java/de/codecentric/boot/admin/SpringBootAdminEurekaApplication.java
 * @author jiiiiiin
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableAdminServer
public class SpringBootAdminApplication {

    public static void main(String[] args) {
        val app = new SpringApplication(SpringBootAdminApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Configuration
    public static class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
        private final String adminContextPath;

        public SecurityPermitAllConfig(AdminServerProperties adminServerProperties) {
            this.adminContextPath = adminServerProperties.getContextPath();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .anyRequest()
                    .permitAll()
                    .and()
                    // 允许嵌入iframe
                    .headers().frameOptions().disable()
                    .and()
                    .csrf().disable();
        }
    }
}
