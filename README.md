# kaciras-blog/infra

[![Build Status](https://travis-ci.org/kaciras-blog/java-infra.svg?branch=master)](https://travis-ci.org/kaciras-blog/java-infra)
[![codecov](https://codecov.io/gh/kaciras-blog/java-infra/branch/master/graph/badge.svg)](https://codecov.io/gh/kaciras-blog/java-infra)

博客后端的基础层，包含与业务无关的通用组件。

**该项目已合并到 [kaciras-blog/content-server](https://github.com/kaciras-blog/content-server)**

# 构建

打包并安装到本地仓库，跳过测试。

```shell script
mvn install -Dmaven.test.skip=true
```

如果要运行测试，则要指定Redis连接参数，可以参考 [application.yml](https://github.com/kaciras-blog/java-infra/blob/master/application.yml)

```shell script
mvn test
```
