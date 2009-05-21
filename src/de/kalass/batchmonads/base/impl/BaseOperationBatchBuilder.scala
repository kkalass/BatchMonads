package de.kalass.batchmonads.base.impl

/**
 * Collects BaseOperation instances into the corresponding Batches. 
 */
private[base] class BaseOperationBatchBuilder private (map: Map[BatchOperation[_,_], BaseOperationBatch[_,_]]) {
    
    def this() = this(Map())

    /**
     * @return a new BatchBuilder with the given single operation associated with its creating BatchOperation
     */
    def addOperation[I, A](op: BaseOperation[I, A], idx: Int) : BaseOperationBatchBuilder = {
        val newBatcher: BaseOperationBatch[I, A] = map.get(op.creator) match {
          case Some(oldBatcher) => oldBatcher.add(op, idx) 
          case None => new BaseOperationBatch[I, A](op, idx)
        }
        new BaseOperationBatchBuilder(map + (op.creator -> newBatcher))
    }

    // TODO: maybe keep some reference of past operations, so that we can detect possibilities for caching, 
    // or detect batch-sequence errors? 
    def executeAllBatches() = (new BaseOperationBatchBuilder(), map.values.toList.flatMap(_.execute()))
}