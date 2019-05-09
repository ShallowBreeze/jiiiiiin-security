package cn.jiiiiiin.manager.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * https://github.com/ctripcorp/apollo/wiki/Java客户端使用指南#3222-java-config使用方式
 * @author jiiiiiin
 */
@Component
public class TestJavaConfigBean {
    @Value("${timeout:100}")
    private int timeout;
    private int batch;

    @Value("${batch:200}")
    public void setBatch(int batch) {
        this.batch = batch;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getBatch() {
        return batch;
    }

}
