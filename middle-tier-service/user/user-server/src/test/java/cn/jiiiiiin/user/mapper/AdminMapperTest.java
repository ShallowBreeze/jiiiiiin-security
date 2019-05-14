package cn.jiiiiiin.user.mapper;


import cn.jiiiiiin.user.dto.AdminDto;
import cn.jiiiiiin.user.entity.Admin;
import cn.jiiiiiin.user.enums.ChannelEnum;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AdminMapperTest {

    @Autowired
    private AdminMapper adminMapper;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSelectByUsername() {
        val res = adminMapper.selectByUsername("admin", ChannelEnum.MNG);
        log.info("selectByUsername {}", res);
        Assert.assertNotNull(res);
        Assert.assertNotNull(res.getRoles());
    }

    @Test
    public void selectPageAdminDto() {
        val res = adminMapper.selectPageAdminDto(new Page<AdminDto>(1, 5), ChannelEnum.MNG, (AdminDto) new AdminDto().setUsername("admin"));
        Assert.assertNotNull(res);
    }

    @Test
    public void selectRoleRecordsById() {
        val res = adminMapper.selectRoleRecordsById(1L);
        log.info("selectRoleRecordsById res {}", res);
        Assert.assertNotNull(res);
    }

    @Test
    @Rollback
    public void testInsert() {
        int res = adminMapper.insert(new Admin().setUsername("admin").setPassword("$2a$10$XQi3SDI8aU8VL8PQkkyddOYk62OmDBtLwD9f9EEKf0AZBI0Y7pwPq").setEmail("15399999999@163.com").setPhone("15399999999"));
        Assert.assertTrue(SqlHelper.retBool(res));
    }

    @Test
    @Rollback
    public void testClearRelationRoleAdminRecord() {
        val res = SqlHelper.delBool(adminMapper.deleteRelationRoleAdminRecord(1L));
        Assert.assertTrue(res);
    }

}
