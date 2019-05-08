# 服务注册

运行Time微服务服务端
在IDE中，以定制配置方式运行微服务，

`Run As -> Run Configurations -> Spring Boot App`

配置服务启动端口Override Properties

第一次运行使用9090服务器端口

`-Dserver.port=9090`

第二次运行使用9091服务器端口

`-Dserver.port=9091`

UI校验：

`http://localhost:8761/`

API校验：

`http://localhost:8761/eureka/apps`