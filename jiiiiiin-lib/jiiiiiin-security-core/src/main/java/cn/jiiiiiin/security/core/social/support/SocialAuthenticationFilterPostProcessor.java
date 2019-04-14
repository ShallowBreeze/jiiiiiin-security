/**
 *
 */
package cn.jiiiiiin.security.core.social.support;

import org.springframework.social.security.SocialAuthenticationFilter;

/**
 * SocialAuthenticationFilter后处理器，用于在不同环境下个性化社交登录的配置
 *
 * https://coding.imooc.com/lesson/134.html#mid=7234
 *
 * 目前默认的浏览器环境可以直接使用默认的处理逻辑
 *
 * app环境需要自己实现下发token
 *
 * @author zhailiang
 *
 */
public interface SocialAuthenticationFilterPostProcessor {

    /**
     * @param socialAuthenticationFilter 社交登录过滤器
     */
    void process(SocialAuthenticationFilter socialAuthenticationFilter);

}
