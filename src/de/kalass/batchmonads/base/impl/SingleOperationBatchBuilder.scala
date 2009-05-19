package de.kalass.batchmonads.base.impl

/**
 * Collects SingleOperation instances into the corresponding Batches. 
 */
private[base] class SingleOperationBatchBuilder private (map: Map[BatchOperation[_,_], SingleOperationBatch[_,_]]) {
    
    def this() = this(Map())

    /**
     * @return a new BatchBuilder with the given single operation associated with its creating BatchOperation
     */
    def addOperation[I, A](op: SingleOperation[I, A], idx: Int) : SingleOperationBatchBuilder = {
        val newBatcher: SingleOperationBatch[I, A] = map.get(op.creator) match {
          case Some(oldBatcher) => oldBatcher.add(op, idx) 
          case None => new SingleOperationBatch[I, A](op, idx)
        }
        new SingleOperationBatchBuilder(map + (op.creator -> newBatcher))
    }

    // TODO: maybe keep some reference of past operations, so that we can detect possibilities for caching, 
    // or detect batch-sequence errors? 
    def executeAllBatches() = (new SingleOperationBatchBuilder(), map.values.toList.flatMap(_.execute()))
}