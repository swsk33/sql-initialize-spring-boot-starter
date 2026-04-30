# SQL数据库自动初始化Starter

<p align="center">
	<a target="_blank" href="https://central.sonatype.com/search?smo=true&q=io.github.swsk33.sql-initialize-spring-boot-starter">
		<img src="https://img.shields.io/maven-central/v/io.github.swsk33/sql-initialize-spring-boot-starter" />
	</a>
	<a target="_blank" href="https://www.apache.org/licenses/">
		<img alt="GitHub" src="https://img.shields.io/github/license/swsk33/sql-initialize-spring-boot-starter">
	</a>
	<a target="_blank" href="https://bell-sw.com/pages/downloads/#jdk-8-lts">
		<img alt="Static Badge" src="https://img.shields.io/badge/8%2B-blue?label=JDK">
	</a>
	<a href="https://zread.ai/swsk33/sql-initialize-spring-boot-starter" target="_blank">
		<img src="https://img.shields.io/badge/Ask_Zread-_.svg?style=flat&color=00b0aa&labelColor=000000&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iMTYiIGhlaWdodD0iMTYiIHZpZXdCb3g9IjAgMCAxNiAxNiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTQuOTYxNTYgMS42MDAxSDIuMjQxNTZDMS44ODgxIDEuNjAwMSAxLjYwMTU2IDEuODg2NjQgMS42MDE1NiAyLjI0MDFWNC45NjAxQzEuNjAxNTYgNS4zMTM1NiAxLjg4ODEgNS42MDAxIDIuMjQxNTYgNS42MDAxSDQuOTYxNTZDNS4zMTUwMiA1LjYwMDEgNS42MDE1NiA1LjMxMzU2IDUuNjAxNTYgNC45NjAxVjIuMjQwMUM1LjYwMTU2IDEuODg2NjQgNS4zMTUwMiAxLjYwMDEgNC45NjE1NiAxLjYwMDFaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik00Ljk2MTU2IDEwLjM5OTlIMi4yNDE1NkMxLjg4ODEgMTAuMzk5OSAxLjYwMTU2IDEwLjY4NjQgMS42MDE1NiAxMS4wMzk5VjEzLjc1OTlDMS42MDE1NiAxNC4xMTM0IDEuODg4MSAxNC4zOTk5IDIuMjQxNTYgMTQuMzk5OUg0Ljk2MTU2QzUuMzE1MDIgMTQuMzk5OSA1LjYwMTU2IDE0LjExMzQgNS42MDE1NiAxMy43NTk5VjExLjAzOTlDNS42MDE1NiAxMC42ODY0IDUuMzE1MDIgMTAuMzk5OSA0Ljk2MTU2IDEwLjM5OTlaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik0xMy43NTg0IDEuNjAwMUgxMS4wMzg0QzEwLjY4NSAxLjYwMDEgMTAuMzk4NCAxLjg4NjY0IDEwLjM5ODQgMi4yNDAxVjQuOTYwMUMxMC4zOTg0IDUuMzEzNTYgMTAuNjg1IDUuNjAwMSAxMS4wMzg0IDUuNjAwMUgxMy43NTg0QzE0LjExMTkgNS42MDAxIDE0LjM5ODQgNS4zMTM1NiAxNC4zOTg0IDQuOTYwMVYyLjI0MDFDMTQuMzk4NCAxLjg4NjY0IDE0LjExMTkgMS42MDAxIDEzLjc1ODQgMS42MDAxWiIgZmlsbD0iI2ZmZiIvPgo8cGF0aCBkPSJNNCAxMkwxMiA0TDQgMTJaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik00IDEyTDEyIDQiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSIxLjUiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIvPgo8L3N2Zz4K&logoColor=ffffff" alt="zread"/>
	</a>
</p>

## 1，介绍

这是一款简单的、适用于Spring Boot的数据库自动初始化工具。

### (1) 项目背景

在现在的后端开发中，只要是使用关系型数据库，相信SSM架构（Spring Boot + MyBatis）已经成为首选。

然而，在第一次运行或者部署项目的时候，通常要先手动连接数据库，执行一个SQL文件以创建数据库以及数据库表格完成**数据库的初始化工作**，这样我们的SSM应用程序才能够正常工作。这样也对实际部署或者是容器化造成了一些麻烦，必须先手动初始化数据库再启动应用程序。

那能否实现**SSM应用程序第一次启动时，自动地帮我们执行SQL文件以完成数据库初始化工作呢？**

鉴于上述问题，我开发了这个简单的Starter，只需引入并配置一下用于初始化的SQL脚本，在项目第一次启动时即可自动地连接数据库并完成数据库的初始化工作。

该项目的机制参考：[传送门](https://juejin.cn/post/7238522776055103544)

### (2) 环境要求

使用该Starter的项目需要满足下列要求：

- JDK 8及其以上版本
- Spring Boot 2.x及其以上版本，推荐`2.7.x`版本，支持Spring Boot 2.x - 4.x版本

目前支持的数据库：

- MySQL
- PostgreSQL

## 2，快速开始

### (1) 引入依赖

以Maven项目为例，加入下列依赖：

```xml
<!-- SQL 数据库自动初始化 -->
<dependency>
	<groupId>io.github.swsk33</groupId>
	<artifactId>sql-initialize-spring-boot-starter</artifactId>
	<version>3.0.0</version>
</dependency>
```

### (2) 配置文件

然后就需要在Spring Boot的配置文件`application.properties`中进行配置，这里以`yml`格式配置为例进行说明。

首先需要正确地配置数据源，例如：

```yaml
# Spring Boot 数据源配置
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
        database-check:
          # 若数据库不存在，则会创建数据库，并依次执行下列SQL文件
          # file:开头表示本地文件路径，classpath:表示类路径/jar包内资源路径
          sql-paths:
            - "classpath:setup-database.sql"
```

指定配置值`io.github.swsk33.sql-init.database-check.sql-paths`的值即可，这是一个数组，可以配置多个SQL文件的路径，对于路径配置有下列注意事项：

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

## 3，执行效果

按照上述说明完成配置之后，第一次启动，如果**数据库中无对应数据库或者数据库中没有任何表格**，程序会自动完成数据库的创建以及SQL初始化脚本的执行：

![image-20240116190703082](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/image-20240116190703082.png)

第二次启动项目，由于对应数据库已存在且数据库中已有表了，因此**不会**再次执行创建数据库或者执行SQL脚本的操作：

![image-20240116190740148](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/image-20240116190740148.png)

## 4，全部配置与进阶使用

SQL自动初始化的全部配置示例如下：

```yaml
# sql-initialize配置
io:
  github:
    swsk33:
      sql-init:
        # 是否启用数据库初始化检查
        # 当设为false时，则不会检查数据库、数据库中特定表是否存在（即不做任何操作）
        # 默认：true
        enabled: true
        # 数据库检查选项
        database-check:
          # 是否检查数据库是否存在，数据库从你配置的数据源jdbc url中自动解析
          # 如果设为false，则不会检查数据库是否存在，同时也不会执行下列数据库级初始化脚本
          # 默认：true
          check-database: true
          # 数据库级初始化脚本配置
          # 若数据库不存在，则会先创建数据库，然后依次执行下列SQL文件
          # 若数据库存在，则下面脚本不会执行
          # file:开头表示本地文件路径，classpath:表示类路径/jar包内资源路径，后面也一样
          sql-paths:
            - "classpath:setup-database.sql"
        # 数据表检查选项
        table-check:
          # 是否检查特定数据表是否存在
          # 默认：true
          check-table: true
          # 配置要检查的数据表列表
          table-list:
            # 检查用户表
              # 检查的表名
            - table-name: "user"
              # 表所属schema
              # PostgreSQL默认为：public
              # MySQL则建议省略该字段
              schema-name: "public"
              # 表级别初始化脚本
              # 若用户表user不存在，则会执行下列用户表相关初始化脚本
              sql-paths:
                - "classpath:init-mysql/tables/user.sql"
                - "classpath:init-mysql/data/user.sql"
            # 检查角色表
            - table-name: "role"
              schema-name: "public"
              # 角色表不存在则执行下列角色表相关初始化脚本
              sql-paths:
                - "classpath:init-mysql/tables/role.sql"
                - "classpath:init-mysql/data/role.sql"
```

如果想保持默认值，则可以省略对应的配置。

通过完整配置可以得知，`sql-initialize-spring-boot-starter`支持下列功能：

- **数据库级别检查与初始化**：检查数据库是否存在，不存在则自动创建并执行用户配置的数据库级初始化SQL脚本
- **表级别检查与初始化**：检查特定的某个表是否存在，不存在则会执行用户指定的表级别初始化SQL脚本

## 5，部分问题

### (1) 连接PostgreSQL时，报错`ERROR: database "xxx" already exists`

在项目连接PostgreSQL时，若数据库存在，Starter启动时报错：

```
2024-02-17T12:53:10.534+08:00 ERROR 6764 --- [main] i.g.s.s.util.SQLExecuteUtils: 创建数据库失败！
2024-02-17T12:53:10.534+08:00 ERROR 6764 --- [main] i.g.s.s.util.SQLExecuteUtils: ERROR: database "xxx" already exists
```

这是由于PostgreSQL驱动版本过低导致，将项目中PostgreSQL驱动手动设定为[最新版本](https://central.sonatype.com/artifact/org.postgresql/postgresql)即可，而不是继承Spring Boot依赖版本：

```xml
<!-- PostgreSQL驱动 -->
<dependency>
	<groupId>org.postgresql</groupId>
	<artifactId>postgresql</artifactId>
	<version>42.7.11</version>
	<scope>runtime</scope>
</dependency>
```

### (2) 连接到ShardingSphere-Proxy时，检测数据库步骤出现错误

SQL初始化Starter通过JDBC的接口检测连接元数据信息，从中获取数据库列表等等，这对于单节点数据库是不会出现错误的，但是对于连接到ShardingSphere-Proxy时该方法就不准确了。

所以设定配置项`io.github.swsk33.sql-init.database-check.check-database`为`false`即可，跳过数据库检查这个步骤，因为ShardingSphere-Proxy的逻辑数据库是一定存在的：

```yaml
io:
  github:
    swsk33:
      sql-init:
        database-check:
          # 跳过数据库检查
          check-database: false
          # 省略其它...
```

不过，数据库表格是可以正常创建的。
