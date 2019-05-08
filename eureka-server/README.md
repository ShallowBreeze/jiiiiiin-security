# 打包

```bash
mvn clean compile package -Dmaven.test.skip=true
```

# 运行

```bash
java -jar ./target/jiiiiiin-eureka-server.jar --server.port=8761

# 后台运行
nohup java -jar ./target/jiiiiiin-eureka-server.jar > /dev/null 2>&1 &

# 单机演示多实例
java -jar ./target/jiiiiiin-eureka-server.jar -Dspring.profiles.active=node2
```