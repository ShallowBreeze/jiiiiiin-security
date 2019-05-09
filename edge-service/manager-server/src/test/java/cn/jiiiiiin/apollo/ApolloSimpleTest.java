package cn.jiiiiiin.apollo;

import cn.jiiiiiin.ManagerApp;
import cn.jiiiiiin.manager.entity.TestJavaConfigBean;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

/**
 * https://github.com/ctripcorp/apollo/wiki/Java客户端使用指南#323-spring-annotation支持
 * 参考：
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ManagerApp.class)
@Slf4j
public class ApolloSimpleTest {

    @Autowired
    public TestJavaConfigBean testJavaConfigBean;

    @ApolloConfig
    private Config config; //inject config for namespace application

    @Test
    public void springPlaceholderTest() {
        assertTrue(testJavaConfigBean.getTimeout() == 100);
    }

    @Test
    public void appApolloConfigTest() {
        assertTrue(config.getProperty("server.port", null).equals("7000"));
    }
}