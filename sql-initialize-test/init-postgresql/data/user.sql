-- 插入初始用户数据
insert into "user"
values (1, 'a', ST_SetSRID(ST_MakePoint(139.7671, 35.6812), 4326)),
	   (2, 'b', ST_SetSRID(ST_MakePoint(139.7672, 35.6814), 4326)),
	   (3, 'c', ST_SetSRID(ST_MakePoint(139.7673, 35.6816), 4326));