<p align="center"><img src="https://github.com/MicroIoT/website/blob/master/src/statics/icons/favicon-96x96.png" alt="Logo"></p>
<h1 align="center">MicroIoT API for Java</h1>
MicroIoT API for Java方便用户将MicroIoT平台提供的物联网服务集成到自己的物联网应用和设备中。MicroIoT API for Java分为两端：应用端和设备端。应用端API可以帮助物联网应用建立与物联网设备之间的双向通道，监视控制物联网设备，查询物联网大数据，管理MicroIoT平台。设备端API为物联网设备提供接收监控命令编程接口，以及向平台上报事件和告警的编程接口。

## 开发准备

在安装和使用MicroIoT API for Java前，需要具备以下条件：

1. 安装Java开发环境，MicroIoT API for Java要求使用[JDK1.8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)或更高版本。
2. MicroIoT API for Java基于Spring Boot 2开发，推荐安装[Spring Tool Suite](https://spring.io/tools/sts/all)。
3. 注册MicroIoT物联网平台账号，详细内容参见[注册MicroIoT](https://www.microiot.top/site/start/start/#microiot)。
4. 使用MicroIoT Studio配置开发中使用的设备类型和场地类型，添加场地和设备。示例参见[配置共享单车](https://www.microiot.top/site/start/start/)。



## 添加maven依赖

无论开发MicroIoT物联网应用，还是开发MicroIoT物联网设备，都可以通过在pom.xml文件中添加Maven依赖安装MicroIoT API for Java。

```maven
<dependency>
	<groupId>top.microiot</groupId>
	<artifactId>iotapi</artifactId>
	<version>3.0.10</version>
</dependency>
```



## 文档

MicroIoT API for Java文档包括入门指南，开发指南，API参考，请访问MicroIoT网站：[microiot.top](https://www.microiot.top)

## 社区

如果有任何意见或建议，请使用官方QQ交流群：856599970。
