use testreport;

drop table if exists testInfo;
create table testInfo
(
	id  int unsigned auto_increment primary key,
	timestamp  datetime not null,
	#testType  int unsigned not null,
	threads int unsigned not null,
	runCount  bigint unsigned not null,
	rowCount bigint unsigned not null,
	comment text,
	tableName varchar(255) not null,
	createTableSql text,
	insertPct int unsigned not null,
	selectPct int unsigned not null,
	updatePct int unsigned not null,
	initDataAmount bigint unsigned not null
);

drop table if exists testRuntimeInfo;

create table testRuntimeInfo
(
	id  int unsigned auto_increment primary key,
	systemNanoTime  bigint unsigned not null,
	testId  int unsigned not null,
	runCountCurrent  bigint unsigned not null,
	intervalInsertCount  bigint unsigned not null,
	intervalUpdateCount  bigint unsigned not null,
	intervalSelectCount  bigint unsigned not null
	#intervalExecution  int unsigned not null
);