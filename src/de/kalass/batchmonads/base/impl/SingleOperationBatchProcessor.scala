package de.kalass.batchmonads.base.impl


private[base] class SingleOperationBatchProcessor extends BatchProcessor {
  

    protected[base] def execute(operationsWithIndices: List[Tuple2[Operation[_], Int]]): BatchProcessorResult = {
        def batchBuilder = new SingleOperationBatchBuilder()
        var (selectedOperations, remaining) = Util.divideList(operationsWithIndices, {case op: SingleOperation[_,_] => op}: PartialFunction[Operation[_], SingleOperation[_,_]])
        var batchCollector2 = selectedOperations.foldLeft(batchBuilder)((ctx, a) => a._1.addSelf(ctx, a._2))
        var (batchCollector3, results) = batchCollector2.executeAllBatches()
        BatchProcessorResult(new SingleOperationBatchProcessor(), remaining, results)
    }
}

