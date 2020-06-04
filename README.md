# kaciras-blog/infra

[![Build Status](https://travis-ci.org/kaciras-blog/java-infra.svg?branch=master)](https://travis-ci.org/kaciras-blog/java-infra)
[![codecov](https://codecov.io/gh/kaciras-blog/java-infra/branch/master/graph/badge.svg)](https://codecov.io/gh/kaciras-blog/java-infra)

博客后端的基础层，包含与业务无关的通用组件。

此项目由 [kaciras-blog/content-server](https://github.com/kaciras-blog/content-server) 使用。

# 构建

```shell script
mvn install
```

如果要运行测试，则要指定Redis连接参数，可以参考 [application-EXAMPLE.yml](https://github.com/kaciras-blog/java-infra/blob/master/application-EXAMPLE.yml)
