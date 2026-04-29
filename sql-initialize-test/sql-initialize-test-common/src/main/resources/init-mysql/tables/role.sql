-- 先删除表
drop table if exists `role`;

-- 创建角色表
create table `role`
(
	`id`      int unsigned primary key, -- 角色id
	`name`    varchar(8) not null,      -- 名字
	`user_id` int unsigned              -- 关联用户id
) engine = InnoDB
  default charset = utf8mb4;