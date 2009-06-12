package de.kalass.batchmonads.base.impl


/**
 * A BaseOperationBatch groups a list of BaseOperations with the BatchOperation that was used 
 * to create the BaseOperation instances and thus will be used to execute them
 * together in one batch.
 * 
 * @param batchOperation the batch operation to use for executing the list of operations
 * @param singleOperations the currently accumulated list of BaseOperation instances
 * @param indices a map the indices under which the BaseOperation instances were included in the original input list.
 */
private[base] class BaseOperationBatch[I, A] private (
        batchOperation: BatchOperation[I, A], 
        singleOperations: List[BaseOperation[I, A]],
        indices: Map[BaseOperation[I, A],List[Int]]
) {
    assert(singleOperations.length == indices.size)
    
    def this(op: BaseOperation[I, A], idx: Int) = this(op.creator, List(op), Map(op-> List(idx)))
    
    /**
     * Adds a BaseOperation with its original index to the newly created and returned instance.
     * 
     * @param op the operation to add
     * @param index the index of this operation in the original input list
     * @return a new BaseOperationBatch that includes the given operation 
     * @throws AssertionError if the given operation reference a different BatchOperation than the one we were initialized with.
     */
    def add(op: BaseOperation[I, A], index: Int): BaseOperationBatch[I, A] = {
        assert(op.creator == batchOperation)
        
        val (newInputData, newIndices) = indices.get(op) match {
          case Some(list) => (singleOperations, indices.update(op, index :: list))
          case None => (op :: singleOperations, indices.update(op, List(index)))
        }
        new BaseOperationBatch[I, A](batchOperation, newInputData, newIndices)
    }
    
    /**
     * Executes all BaseOperation instances with the common BatchOperation and returns a list 
     * of tuples with the results from the BatchOperation and the original 
     * input-indices of the corresponding Operation. 
     */
    def execute(): List[(Result[A], Int)] = {
        val input = singleOperations.reverse
        val result = batchOperation.fkt(input.map(_.value))
        assert(input.length == result.length)
        result.zip(input).flatMap({ 
          case (res, operation) =>
            indices.getOrElse(operation, throw new IllegalStateException).map((Success(res), _))
        })
    }
}
