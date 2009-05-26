package de.kalass.batchmonads.base.impl


private[base] class BaseOperationBatchProcessor extends BatchProcessor {
  
    protected[base] def execute(operationsWithIndices: List[Tuple2[Operation[_], Int]]): BatchProcessorResult = {
        val (selectedOperations, remaining) = Util.divideList[Operation[_], BaseOperation[_,_]](operationsWithIndices, {case op: BaseOperation[_,_] => op})
        val (batchCollector3, results) = BaseOperationBatchBuilder(selectedOperations).executeAllBatches()
        BatchProcessorResult(new BaseOperationBatchProcessor(), remaining, results)
    }
}

