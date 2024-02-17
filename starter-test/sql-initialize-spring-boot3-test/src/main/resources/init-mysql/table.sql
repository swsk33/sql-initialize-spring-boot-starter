-- 先删除表
drop table if exists `user`;

-- 创建用户表
create table `user`
(
	`id`   int unsigned auto_increment, -- id
	`name` varchar(8) not null,         -- 名字
	primary key (`id`)
) engine = InnoDB
  default charset = utf8mb4;