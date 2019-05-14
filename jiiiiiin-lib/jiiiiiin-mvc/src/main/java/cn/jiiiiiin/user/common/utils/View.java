package cn.jiiiiiin.user.common.utils;

/**
 * https://github.com/sdeleuze/spring-jackson-demo/blob/HEAD/src/main/java/demo/View.java
 * {@link cn.jiiiiiin.user.common.config.JacksonConfiguration}
 * @author jiiiiiin
 */
public class View {

    public interface SimpleView {
    }

    public interface DetailView extends SimpleView {
    }

    public interface SecurityView extends DetailView {
    }
}
