package cn.jiiiiiin.apollo;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 动态路由
 * <p>
 * https://github.com/ctripcorp/apollo-use-cases/issues/12
 * <p>
 * https://mp.weixin.qq.com/s/4d-epBiq5b69fZTCSkiOzA?
 * <p>
 * https://segmentfault.com/a/1190000009191419
 *
 * @author jiiiiiin
 */
@Component
@Slf4j
public class ZuulPropertiesRefresher implements ApplicationContextAware {


    private ApplicationContext applicationContext;

    private final RouteLocator routeLocator;

    @Autowired
    public ZuulPropertiesRefresher(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @ApolloConfigChangeListener
    public void onChange(ConfigChangeEvent changeEvent) {
        boolean zuulPropertiesChanged = false;
        for (String changedKey : changeEvent.changedKeys()) {
            if (changedKey.startsWith("zuul.")) {
                zuulPropertiesChanged = true;
                break;
            }
        }

        if (zuulPropertiesChanged) {
            refreshZuulProperties(changeEvent);
        }
    }

    private void refreshZuulProperties(ConfigChangeEvent changeEvent) {
        log.info("Refreshing zuul properties!");

        /**
         * rebind configuration beans, e.g. ZuulProperties
         * @see org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder#onApplicationEvent
         */
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));

        /**
         * refresh routes
         * @see org.springframework.cloud.netflix.zuul.ZuulServerAutoConfiguration.ZuulRefreshListener#onApplicationEvent
         */
        this.applicationContext.publishEvent(new RoutesRefreshedEvent(routeLocator));

        log.info("Zuul properties refreshed!");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
