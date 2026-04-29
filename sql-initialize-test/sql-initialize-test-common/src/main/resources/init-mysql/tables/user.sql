-- 创建用户表
create table `user`
(
	`id`   int primary key,    -- id
	`name` varchar(8) not null -- 名字
) engine = InnoDB
  default charset = utf8mb4;