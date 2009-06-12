package de.kalass.batchmonads.base.impl


private[base] class BaseOperationBatchProcessor extends BatchProcessor {
  
    /**
     * Adds a base operation to the batch that contains all base operations that were created by the same batch operation
     * and returns the modified map.
     */
    def addOperationToBatchMap(map: Map[BatchOperation[_, _], BaseOperationBatch[_, _]], opWithIdx: (BaseOperation[_,_], Int)) = {
      val op = opWithIdx._1
      val idx = opWithIdx._2
      map.update(op.creator, map.get(op.creator) match {
        case Some(oldBatcher) => oldBatcher.add(op, idx) 
        case None => new BaseOperationBatch(op, idx)
      })  
    }
  
    protected[base] def execute(operationsWithIndices: List[Tuple2[Operation[_], Int]]): BatchProcessorResult = {
        val (selectedOperations, remaining) = Util.partition[Operation[_], BaseOperation[_,_]](operationsWithIndices, {case op: BaseOperation[_,_] => op})
        val batcherList = selectedOperations.foldLeft(Map[BatchOperation[_, _], BaseOperationBatch[_, _]]()) (addOperationToBatchMap _).values.toList
        
        // execute the batchers: each batch operation is called with all base operations that were previously created by it
        val results = batcherList.flatMap(_.execute())
        BatchProcessorResult(new BaseOperationBatchProcessor(), remaining, results)
    }
}

