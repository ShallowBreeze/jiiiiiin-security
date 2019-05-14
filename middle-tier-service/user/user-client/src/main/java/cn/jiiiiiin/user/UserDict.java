package cn.jiiiiiin.user;

public interface UserDict {

    String SERVICE_NAME = "user-server";
    String SERVICE_FALLBACK_MSG = "暂时无法访问该服务[%s]，请稍后尝试";

    static String formatServiceFallbackMsg(String methodName){
        return String.format(SERVICE_FALLBACK_MSG, "signInByUsernameOrPhoneNumb");
    }

}
