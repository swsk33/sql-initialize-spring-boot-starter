-- 先删除表
drop table if exists "user";

-- 创建用户表
create table "user"
(
	"id"   serial primary key, -- id
	"name" varchar(8) not null -- 名字
);