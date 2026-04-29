-- 创建角色表
create table "role"
(
	"id"      int primary key,     -- 角色id
	"name"    varchar(8) not null, -- 名字
	"user_id" int                  -- 关联用户id
);