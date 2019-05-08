package cn.jiiiiiin.module.mngauth.controller;

import cn.jiiiiiin.ManagerApp;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ManagerApp.class)
@Slf4j
public class AdminControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void list() {
    }

    @Test
    public void searchAdminDto() {
    }

    @Test
    public void search() {
    }

    @Test
    public void getAdminAndRelationRecords() {
    }

    @Test
    @Rollback
    public void create() throws Exception {
        String content = "{\"username\":\"\",\"password\":\"\",\"channel\":\"\"}";
        String reuslt = mockMvc.perform(post("/admin").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content))
                .andExpect(status().is5xxServerError())
                .andReturn().getResponse().getContentAsString();
        log.debug("whenCreateFail {}", reuslt);
    }

    @Test
    public void update() {
    }

    @Test
    public void updatePwd() {
    }

    @Test
    public void dels() {
    }

    @Test
    public void del() {
    }
}