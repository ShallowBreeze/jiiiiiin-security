package cn.jiiiiiin.mvc.common.utils;

/**
 * https://github.com/sdeleuze/spring-jackson-demo/blob/HEAD/src/main/java/demo/View.java
 * {@link cn.jiiiiiin.mvc.common.config.JacksonConfiguration}
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
