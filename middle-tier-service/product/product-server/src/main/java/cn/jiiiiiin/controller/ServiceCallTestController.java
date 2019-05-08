package cn.jiiiiiin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * 提供给order服务针对`RestTemplate`的调用测试
 *
 * @author jiiiiiin
 */
@RestController
@Slf4j
public class ServiceCallTestController {

    @Value("${server.port}")
    private int port;

    @GetMapping("/msg")
    public String getMsg() throws InterruptedException {
        _randomlyRunLong();
        log.info("商品服务模拟正常返回");
        return "商品" + port;
    }

    private void _randomlyRunLong() {
        Random rand = new Random();

        // 1/3概率休眠
        // Random.nextInt()方法，是生成一个随机的int值，该值介于[0,n)的区间，也就是0到n之间的随机int值，包含0而不包含n。
        int randomNum = rand.nextInt(3) + 1;

        if (randomNum == 3) {
            log.info("商品服务模拟处理延迟");
            _sleep();
        } else if (randomNum == 2) {
            throw new RuntimeException("模拟商品服务模拟发生错误");
        }
    }

    private void _sleep() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
