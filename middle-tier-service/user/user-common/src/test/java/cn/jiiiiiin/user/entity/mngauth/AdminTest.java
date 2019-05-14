package cn.jiiiiiin.user.entity.mngauth;
import cn.jiiiiiin.user.common.utils.View;
import cn.jiiiiiin.user.enums.ChannelEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * https://www.baeldung.com/jackson-json-view-annotation
 */
@Slf4j
public class AdminTest {

//    @Autowired
//    private ObjectMapper objectMapper;

//    @Test
//    public void test() throws IOException {
//        Admin user = new Admin();
//        user.setUsername("test001");
//        user.setChannel(ChannelEnum.MNG);
//        user.setPassword("123456");
////        user.setCreateTime(LocalDateTime.now());
//        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
//
//        ObjectMapper om = new ObjectMapper();
////        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
////        om.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT); //属性是默认时不进行序列化，例如当时List<Role> list = new ArrayList<>(0); 无权限时，可以进行自动忽略
////        om.setSerializationInclusion(JsonInclude.Include.NON_NULL); // null值不进行序列化
//        om.setSerializationInclusion(JsonInclude.Include.NON_EMPTY); // 集合为空、String的length()为0、时间为zero、其他null
//
//        String json = om.writeValueAsString(user);
//        log.debug("format json: {}", json);
//
////        om.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);//是一个双向开关，开启将输出没有JsonView注解的属性，false关闭将输出有JsonView注解的属性
//
//        json = om.writerWithView(View.SimpleView.class).writeValueAsString(user);
//        log.debug("format userview.info json: {}", json);
//
//        json = om.writerWithView(View.DetailView.class).writeValueAsString(user);
//        log.debug("format userview.info json: {}", json);
//
//        json = om.writerWithView(View.SecurityView.class).writeValueAsString(user);
//        log.debug("format userview.login json: {}", json);
//    }

//    @Test
//    public void testJsr303() throws JsonProcessingException {
//        val admin = new Admin();
//        ValidationResult result = ValidationUtils.validateEntity(admin);
//        log.debug("testJsr303 {} ", new ObjectMapper().writeValueAsString(result));
//    }

}