package cn.jiiiiiin.module.common.mapper.mngauth;


import cn.jiiiiiin.ManagerApp;
import cn.jiiiiiin.module.mngauth.dto.Menu;
import cn.jiiiiiin.module.mngauth.dto.ResourceDto;
import cn.jiiiiiin.module.mngauth.entity.Interface;
import cn.jiiiiiin.module.mngauth.entity.Resource;
import cn.jiiiiiin.module.mngauth.entity.Role;
import cn.jiiiiiin.module.mngauth.enums.StatusEnum;
import cn.jiiiiiin.module.mngauth.enums.ChannelEnum;
import cn.jiiiiiin.module.mngauth.enums.ResourceTypeEnum;
import cn.jiiiiiin.module.mngauth.mapper.ResourceMapper;
import cn.jiiiiiin.module.mngauth.mapper.RoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static cn.jiiiiiin.security.core.dict.CommonConstants.GET;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ManagerApp.class)
@Slf4j
public class ResourceMapperTest {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Transactional
    @Rollback
    public void testInsert() {
        val home = new Resource()
                .setName("首页")
                .setIcon("home")
                .setPath("/index")
                .setLevels(1)
                .setNum(1);
        int homeRes = resourceMapper.insert(home);
        assertTrue(SqlHelper.retBool(homeRes));
        assertTrue(home.getId() > 0);

//        val resource = new Resource()
//                .setName("系统设置")
//                .setIcon("cog")
//                .setUrl("/sys")
//                .setLevels(1)
//                .setNum(2);
//        int res = resourceMapper.insert(resource);
//        Assert.assertTrue(SqlHelper.retBool(res));
//        Assert.assertTrue(resource.getId() > 0);
//
//        val resource2 = new Resource()
//                .setPid(resource.getId())
//                .setName("操作员管理")
//                .setIcon("users")
//                .setUrl("/mngauth/admin")
//                .setLevels(2)
//                .setNum(1);
//        int res2 = resourceMapper.insert(resource2);
//        Assert.assertTrue(SqlHelper.retBool(res2));
//
//        val resource3 = new Resource()
//                .setPid(resource.getId())
//                .setName("角色管理")
//                .setIcon("id-badge")
//                .setUrl("/mngauth/role")
//                .setLevels(2)
//                .setNum(2);
//        int res3 = resourceMapper.insert(resource3);
//        Assert.assertTrue(SqlHelper.retBool(res3));
//
//        val resource4 = new Resource()
//                .setPid(resource.getId())
//                .setName("资源管理")
//                .setIcon("treeAllChildrenNode")
//                .setUrl("/mngauth/resource")
//                .setLevels(2)
//                .setNum(3);
//        int res4 = resourceMapper.insert(resource4);
//        Assert.assertTrue(SqlHelper.retBool(res4));
//
//        val operator = roleMapper.selectOne(new QueryWrapper<Role>().eq(Role.AUTHORITY_NAME, "ADMIN"));
//        val set = new HashSet<Resource>();
//        set.add(resource);
//        set.add(resource2);
//        set.add(resource3);
//        set.add(resource4);
//        operator.setResources(set);
//        int temp = roleMapper.saveRelationResourceRecords(operator);
//        Assert.assertTrue(SqlHelper.retBool(temp));
    }

    @Test
//    @Transactional
//    @Rollback
    public void testAddBtnResource(){
//        val resourceMngMenu = resourceMapper.selectById(1061818318517747714L);
//        Assert.assertNotNull(resourceMngMenu);
//        val resourceAdd = new Resource()
//                .setPid(resourceMngMenu.getId())
//                .setName("新增资源")
//                .setUrl("resource")
//                .setMethod("POST")
//                .setLevels(3)
//                .setNum(1)
//                .setType(ResourceTypeEnum.BTN);
//        val updateSelfAndRelationRecords = new Resource()
//                .setPid(resourceMngMenu.getId())
//                .setName("修改资源")
//                .setLevels(3)
//                .setNum(2)
//                .setType(ResourceTypeEnum.BTN);
//        int updateRes = resourceMapper.insert(updateSelfAndRelationRecords);
//        Assert.assertTrue(SqlHelper.retBool(updateRes));

        val interfaceMng = new Resource()
                .setPid(1061818316563202049L)
                .setPids("0,1061818316563202049")
                .setName("接口管理")
                .setLevels(2)
                .setNum(4)
                .setType(ResourceTypeEnum.MENU);
        int interfaceMngRes = resourceMapper.insert(interfaceMng);
        assertTrue(SqlHelper.retBool(interfaceMngRes));

        val operator = roleMapper.selectOne(new QueryWrapper<Role>().eq(Role.AUTHORITY_NAME, "ADMIN"));
        val set = operator.getResources();
        set.add(interfaceMng);
        int temp = roleMapper.insertRelationResourceRecords(operator);
        assertTrue(SqlHelper.retBool(temp));
    }

    @Test
    public void testSelectByRoleId() {
        val role = roleMapper.selectOne(new QueryWrapper<Role>().eq(Role.AUTHORITY_NAME, "ADMIN"));
        Assert.assertNotNull(role);
        val res = resourceMapper.selectByRoleId(role.getId(), ChannelEnum.MNG);
        Assert.assertNotNull(res);
        assertTrue(res.size() > 0);
//        log.info("testSelectByRoleId {}", JSONObject.toJSON(res));
    }

    @Test
    public void testModelMapper() {
        val modelMapper = new ModelMapper();
        val resource = new Resource()
                .setName("控制台")
                .setIcon("home")
                .setLevels(1)
                .setNum(1);
        modelMapper.addMappings(new PropertyMap<Resource, Menu>() {

            @Override
            protected void configure() {
                map().setTitle(source.getName());
            }
        });
        val menu = modelMapper.map(resource, Menu.class);
        log.info("menu {}", menu);
    }

    @Test
    public void selectAllChildrenNode() {
        val res = resourceMapper.selectAllChildrenNode(0L, ChannelEnum.MNG, StatusEnum.ENABLE);
        Assert.assertNotNull(res);
//        log.debug("treeAllChildrenNode {}", JSONObject.toJSON(res));
    }

    @Test
    public void insertRelationInterfaceRecords() {
        val interfaces = new HashSet<Interface>();
        interfaces.add((Interface) new Interface().setId(1071771910230102017L));
        interfaces.add((Interface) new Interface().setId(1071944020369932289L));
        val resourceDto = new ResourceDto().setInterfaces(interfaces).setId(1062518178556526593L);
        val res = resourceMapper.insertRelationInterfaceRecords((ResourceDto) resourceDto);
        assertTrue(res);
    }

    @Test
    public void selectResourceAndRelationRecords() {
        val res = resourceMapper.selectResourceAndRelationRecords(1062518178556526593L);
        Assert.assertNotNull(res);
//        log.debug("selectResourceAndRelationRecords {}", JSONObject.toJSON(res));
    }

}
