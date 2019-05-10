package cn.jiiiiiin.manager.config;

import cn.jiiiiiin.manager.properties.ManagerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiiiiiin
 */
@Configuration
@EnableConfigurationProperties(ManagerProperties.class)
@Slf4j
public class MngConfig {

//    @Bean
//    public EhCacheManagerFactoryBean cacheManagerFactory() {
//        val factoryBean = new EhCacheManagerFactoryBean();
//        factoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
//        factoryBean.setShared(true);
//        return factoryBean;
//    }
}
