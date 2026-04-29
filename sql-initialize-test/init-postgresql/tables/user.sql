-- 创建用户表
create table "user"
(
	"id"       int primary key,      -- id
	"name"     varchar(8) not null,  -- 名字
	"location" geometry(Point, 4326) -- 地理位置
);