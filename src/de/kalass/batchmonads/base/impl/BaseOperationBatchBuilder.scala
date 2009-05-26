package de.kalass.batchmonads.base.impl

object BaseOperationBatchBuilder {
    /**
     * @return a new BatchBuilder with the given single operation associated with its creating BatchOperation
     */
    private def addOperation[I, A](map: Map[BatchOperation[_,_], BaseOperationBatch[_,_]], op: BaseOperation[I, A], idx: Int) = {
        val newBatcher: BaseOperationBatch[I, A] = map.get(op.creator) match {
          case Some(oldBatcher) => oldBatcher.add(op, idx) 
          case None => new BaseOperationBatch[I, A](op, idx)
        }
        map + (op.creator -> newBatcher)
    }

    def apply(list : List[Tuple2[BaseOperation[_,_], Int]]) = {
        val map = list.foldLeft(Map[BatchOperation[_, _], BaseOperationBatch[_, _]]()) {
          (batchBuilder, operationWithIndex) => {
            val operation = operationWithIndex._1
            val index = operationWithIndex._2
            addOperation(batchBuilder, operation, index)
          }
        }
        new BaseOperationBatchBuilder(map)
  }
}
/**
 * Collects BaseOperation instances into the corresponding Batches. 
 */
private[base] class BaseOperationBatchBuilder private (
  map: Map[BatchOperation[_,_], BaseOperationBatch[_,_]]
) {
    
    def this() = this(Map())
    
    // TODO: maybe keep some reference of past operations, so that we can detect possibilities for caching, 
    // or detect batch-sequence errors? 
    def executeAllBatches() = (new BaseOperationBatchBuilder(), map.values.toList.flatMap(_.execute()))
}