select
info.id,
info.timestamp,
info.threads,
info.runCount,
info.rowCount,
info.`comment`,
info.tableName,
info.createTableSql,
info.insertPct,
info.selectPct,
info.updatePct,
info.initDataAmount
from testinfo info;