select 
a.systemNanoTime,
a.totalExecutionCount,
 a.totalExecutionCount - @lasttotalExecutionCount as intervalExecutionCount,
@lasttotalExecutionCount := a.totalExecutionCount
from testreport.testruntimeinfo a,
(select @lasttotalExecutionCount := 0) SQLVars
where testid = 7