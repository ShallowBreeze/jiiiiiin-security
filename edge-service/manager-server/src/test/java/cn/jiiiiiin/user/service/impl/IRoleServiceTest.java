package cn.jiiiiiin.user.service.impl;

import cn.jiiiiiin.ManagerApp;
import cn.jiiiiiin.user.entity.Resource;
import cn.jiiiiiin.user.entity.Role;
import cn.jiiiiiin.user.enums.ChannelEnum;
import cn.jiiiiiin.user.controller.RoleController;
import cn.jiiiiiin.user.service.IResourceService;
import cn.jiiiiiin.user.service.IRoleService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ManagerApp.class)
@Slf4j
public class IRoleServiceTest {

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private RoleController roleController;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

//    @Test
//    public void parseResourceIds(){
////        assertTrue(new Resource().setNum(1).setId(1L).equals(new Resource().setNum(2).setId(1L)));
//        val role = roleService.getById(1061277221831905282L);
//        role.getResources().add(resourceService.getById(1061818318463221761L));
//        role.getResources().add(resourceService.getById(1061818318412890114L));
//        roleController._parseResourceIds(role);
//        log.debug("role.getResources() {} {}",role.getResources().size(), JSONObject.toJSON(role.getResources()));
//        assertTrue(role.getResources().size() == 3);
//    }

    @Transactional
    @Test
    public void save(){
        Role role = new Role().setAuthorityName("USER1").setName("用户1").setChannel(ChannelEnum.MNG);
        role.getResources()
                .add((Resource) new Resource().setId(1066671419766624257L));
        val res = roleService.save(role);
        Assert.assertTrue(res);
    }

    @Transactional
    @Test
    public void remove(){
        List<Long> idList = new ArrayList<>();
        idList.add(1069200931105271810L);
        boolean res = roleService.remove(idList);
        Assert.assertTrue(res);
    }

    @Test
    public void getRoleAndRelationRecords() {
        val res = roleService.getRoleAndRelationRecords(1069853648600694786L);
        assertNotNull(res);
        assertNotNull(res.getResources());
    }

}