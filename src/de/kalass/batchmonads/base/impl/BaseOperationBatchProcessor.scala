package de.kalass.batchmonads.base.impl


private[base] class BaseOperationBatchProcessor extends BatchProcessor {
  

    protected[base] def execute(operationsWithIndices: List[Tuple2[Operation[_], Int]]): BatchProcessorResult = {
        def batchBuilder = new BaseOperationBatchBuilder()
        var (selectedOperations, remaining) = Util.divideList(operationsWithIndices, {case op: BaseOperation[_,_] => op}: PartialFunction[Operation[_], BaseOperation[_,_]])
        var batchCollector2 = selectedOperations.foldLeft(batchBuilder)((ctx, a) => a._1.addSelf(ctx, a._2))
        var (batchCollector3, results) = batchCollector2.executeAllBatches()
        BatchProcessorResult(new BaseOperationBatchProcessor(), remaining, results)
    }
}

