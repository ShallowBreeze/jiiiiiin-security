package cn.jiiiiiin.module.common.mapper.mngauth;


import cn.jiiiiiin.ManagerApp;
import cn.jiiiiiin.module.mngauth.dto.RoleDto;
import cn.jiiiiiin.module.mngauth.entity.Role;
import cn.jiiiiiin.module.mngauth.enums.ChannelEnum;
import cn.jiiiiiin.module.mngauth.mapper.RoleMapper;
import cn.jiiiiiin.security.rbac.component.dict.RbacDict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ManagerApp.class)
@Slf4j
public class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Rollback
    public void testInsert(){
        int res = roleMapper.insert(new Role().setAuthorityName(RbacDict.ROLE_ADMIN_AUTHORITY_NAME).setName("系统管理员"));
        Assert.assertTrue(SqlHelper.retBool(res));
        val adminRole = roleMapper.selectOne(new QueryWrapper<Role>().eq(Role.AUTHORITY_NAME, "ADMIN"));
        assertNotNull(adminRole);
        int res2 = roleMapper.insert(new Role().setAuthorityName("DB").setName("数据库管理员"));
        Assert.assertTrue(SqlHelper.retBool(res2));
        int res3 = roleMapper.insert(new Role().setAuthorityName("OPERATOR").setName("部门操作员"));
        Assert.assertTrue(SqlHelper.retBool(res3));
    }

    @Test
    public void selectPageDto() {
        val res = roleMapper.selectPageDto(new Page<RoleDto>(1, 5), ChannelEnum.MNG, null);
        assertNotNull(res);
        log.debug("selectPageDto {}", res);
    }

}
