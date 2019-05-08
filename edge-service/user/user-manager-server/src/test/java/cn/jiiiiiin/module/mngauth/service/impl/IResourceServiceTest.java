package cn.jiiiiiin.module.mngauth.service.impl;

import cn.jiiiiiin.ManagerApp;
import cn.jiiiiiin.module.mngauth.dto.ResourceDto;
import cn.jiiiiiin.module.mngauth.entity.Resource;
import cn.jiiiiiin.module.mngauth.enums.ChannelEnum;
import cn.jiiiiiin.module.mngauth.service.IResourceService;
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

// 使用spring runner执行测试用例
@RunWith(SpringRunner.class)
// 声明为spring boot的测试用例
@SpringBootTest(classes = ManagerApp.class)
@Slf4j
public class IResourceServiceTest {

    @Autowired
    IResourceService resourceService;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Transactional
    public void saveAndSortNum() {
        val newResource = new Resource()
                .setPid(1061818316563202049L)
                .setNum(1)
                .setLevels(2)
                .setName("测试排序");
        val res = resourceService.saveAndSortNumAndRelationInterfaceRecords((ResourceDto) newResource);
        Assert.assertTrue(res);
    }

    @Test
    public void tree() {
        val res = resourceService.treeAllChildrenNode(0L, ChannelEnum.MNG);
        Assert.assertNotNull(res);
//        log.debug("treeAllChildrenNode {}", JSONObject.toJSON(res));
    }
}