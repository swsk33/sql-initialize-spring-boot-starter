# SQL数据库自动初始化Starter

<p align="center">
	<a target="_blank" href="https://central.sonatype.com/search?smo=true&q=io.github.swsk33.sql-initialize-spring-boot-starter">
		<img src="https://img.shields.io/maven-central/v/io.github.swsk33/sql-initialize-spring-boot-starter" />
	</a>
	<a target="_blank" href="https://www.gnu.org/licenses/old-licenses/gpl-2.0.html">
		<img alt="GitHub" src="https://img.shields.io/github/license/swsk33/code-post">
	</a>
	<a target="_blank" href="https://www.azul.com/downloads/#downloads-table-zulu">
		<img alt="Static Badge" src="https://img.shields.io/badge/17%2B-blue?label=JDK">
	</a>
</p>

## 1，介绍

这是一款简单的、适用于Spring Boot + MyBatis工程的数据库自动初始化工具。

### (1) 项目背景

在现在的后端开发中，只要是使用关系型数据库，相信SSM架构（Spring Boot + MyBatis）已经成为首选。

不过在我们第一次运行或者部署项目的时候，通常要先手动连接数据库，执行一个SQL文件以创建数据库以及数据库表格完成**数据库的初始化工作**，这样我们的SSM应用程序才能够正常工作。

这样也对实际部署或者是容器化造成了一些麻烦，必须先手动初始化数据库再启动应用程序。

那能不能**让我们的SSM应用程序第一次启动时，自动地帮我们执行SQL文件以完成数据库初始化工作呢？**

鉴于上述问题，我开发了这个简单的starter，只需引入并配置一下用于初始化的SQL脚本，在项目第一次启动时即可自动地连接数据库并完成数据库的初始化工作。

该项目的机制参考：[传送门](https://juejin.cn/post/7238522776055103544)

### (2) 环境要求

使用该starter的项目需要满足下列要求：

- JDK 17及其以上版本
- Spring Boot 3.0.0及其以上版本

目前支持的数据库：

- MySQL
- PostgreSQL

## 2，使用方法

### (1) 引入依赖

以Maven项目为例，加入下列依赖：

```xml
<!-- SQL数据库自动初始化 -->
<dependency>
	<groupId>io.github.swsk33</groupId>
	<artifactId>sql-initialize-spring-boot-starter</artifactId>
	<version>1.1.0</version>
</dependency>
```

### (2) 配置文件

然后就需要在Spring Boot的配置文件`application.properties`中进行配置，这里以`yml`格式配置为例进行说明。

首先需要正确地配置数据源，例如：

```yaml
# Spring Boot数据源配置
spring:
  datasource:
    url: "jdbc:postgresql://127.0.0.1:5432/init_demo?TimeZone=Asia/Shanghai"
    username: "postgres"
    password: "123456"
```

这个是SSM项目都需要配置的，就不再赘述了。

然后配置你**用于初始化数据库表**的SQL脚本，并在配置文件中配置其路径，例如：

```yaml
# sql-initialize配置
io:
  github:
    swsk33:
      sql-init:
        sql-paths:
          - "classpath:/init.sql"
```

指定配置值`io.github.swsk33.sql-init.sql-paths`的值即可，这是一个数组，可以配置多个SQL文件的路径，对于路径配置有下列注意事项：

- 可以配置`classpath`路径或者文件系统路径（实际在电脑磁盘上的），`classpath`路径需要以`classpath:`开头，而文件系统路径需要以`file:`开头，上述例子中配置的是`classpath`路径
- 在Maven项目中，`classpath`的根路径对应着项目的`src/main/resources`目录
- 若使用文件系统路径，可以使用相对路径或者绝对路径，在IDEA开发Maven项目时相对路径是相对于项目中`src`目录所在的路径，编译完成后的项目相对路径则为运行项目时的运行路径
- 本项目在启动时会先创建数据库，然后再执行配置的SQL脚本，因此**不需要在SQL脚本中编写创建数据库的语句**，只需要写创建表或者插入初始数据的语句即可
- 配置多个SQL文件时，会按照配置的顺序依次执行（从上至下，也就是从数组的`0`下标开始）
- 如果仅仅是创建数据库，不需要创建表，则可以省略上述SQL路径配置

### (3) 假设有的Bean初始化时需要访问数据库

假设现在有一个类，在初始化为Bean的时候需要访问数据库，例如：

```java
// 省略package和import

/**
 * 启动时需要查询数据库的Bean
 */
@Slf4j
@Component
public class UserService {

	@Autowired
	private UserDAO userDAO;

	@PostConstruct
	private void init() {
		log.info("执行数据库测试访问...");
		userDAO.insert(new User(1));
		List<User> users = userDAO.selectList(null);
		for (User user : users) {
			System.out.println(user);
		}
	}

}
```

这个类在被初始化为Bean的时候，就需要访问数据库进行读写操作，那问题来了，如果这个类`UserService`在我们的starter完成自动配置**之前**就被初始化了怎么办呢？这会导致数据库还没有被初始化时，`UserService`就去访问数据库，导致初始化失败。

可以使用`@DependsOn`注解，控制这个Bean在Starter配置类完成配置之后再进行初始化：

```java
// 省略package和import

/**
 * 启动时需要查询数据库的Bean
 */
@Slf4j
@Component
@DependsOn("SQLInitializeAutoConfigure")
public class UserService {

	// 省略内容

}
```

在类上面加上`@DependsOn("SQLInitializeAutoConfigure")`注解即可！

## 3，效果

按照上述说明完成配置之后，第一次启动，数据库中无对应数据库，程序会自动完成数据库的创建以及SQL初始化脚本的执行：

![image-20230601231913305](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/image-20230601231913305.png)

第二次启动项目，由于对应数据库已存在，因此**不会**再次执行创建数据库或者执行SQL脚本的操作：

![image-20230601232111599](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/image-20230601232111599.png)
