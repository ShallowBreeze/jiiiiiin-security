package cn.jiiiiiin.manager.component.social;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

/**
 * {@link org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository#findUserIdsWithConnection} 中发现有当前组件，就会调用并实现业务系统记录和social UserConnection表记录关联
 *
 * @author jiiiiiin
 */
public class AutoRegistConnectionSignUp implements ConnectionSignUp {
    @Override
    public String execute(Connection<?> connection) {
        //根据社交用户信息默认创建用户并返回用户唯一标识
        return null;
    }
}
