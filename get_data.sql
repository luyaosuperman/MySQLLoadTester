select 
a.systemNanoTime,
@rowcount + a.insertCount as rowCount,
a.runCount - @lasttotalrunCount as intervalrunCount,
a.insertCount - @lasttotalinsertCount as intervalinsertCount,
a.updateCount - @lasttotalupdateCount as intervalupdateCount,
a.selectCount - @lasttotalselectCount as intervalselectCount,
@lasttotalrunCount := a.runCount,
@lasttotalinsertCount := a.insertCount,
@lasttotalupdateCount := a.updateCount,
@lasttotalselectCount := a.selectCount
from testreport.testruntimeinfo a,
(select 
@lasttotalrunCount := 0,
@lasttotalinsertCount := 0,
@lasttotalupdateCount := 0,
@lasttotalselectCount := 0,
@rowcount := initdataamount from testinfo where id = 22
) SQLVars
where testid = 22