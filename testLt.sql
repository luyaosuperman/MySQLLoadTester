drop table if exists testLt;
create table testLt (
id bigint unsigned auto_increment primary key,
runnerId int unsigned not null,
col1 int unsigned not null,
col2 bigint unsigned not null,
col3 varchar(255) not null,
col4 varchar(255) not null
)