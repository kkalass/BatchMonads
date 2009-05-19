package de.kalass.batchmonads.base.impl


/**
 * A SingleOperationBatch groups a list of SingleOperations with the BatchOperation that was used 
 * to create the SingleOperation instances and thus will be used to execute them
 * together in one batch.
 * 
 * @param batchOperation the batch operation to use for executing the list of operations
 * @param singleOperations the currently accumulated list of SingleOperation instances
 * @param indices a map the indices under which the SingleOperation instances were included in the original input list.
 */
private[base] class SingleOperationBatch[I, A] private (
        batchOperation: BatchOperation[I, A], 
        singleOperations: List[SingleOperation[I, A]],
        indices: Map[SingleOperation[I, A],List[Int]]
) {
    assert(singleOperations.length == indices.size)
    
    def this(op: SingleOperation[I, A], idx: Int) = this(op.creator, List(op), Map(op-> List(idx)))
    
    /**
     * Adds a SingleOperation with its original index to the newly created and returned instance.
     * 
     * @param op the operation to add
     * @param index the index of this operation in the original input list
     * @return a new SingleOperationBatch that includes the given operation 
     * @throws AssertionError if the given operation reference a different BatchOperation than the one we were initialized with.
     */
    def add(op: SingleOperation[I, A], index: Int): SingleOperationBatch[I, A] = {
        assert(op.creator == batchOperation)
        
        val (newInputData, newIndices) = indices.get(op) match {
          case Some(list) => (singleOperations, indices.update(op, index :: list))
          case None => (op :: singleOperations, indices.update(op, List(index)))
        }
        new SingleOperationBatch[I, A](batchOperation, newInputData, newIndices)
    }
    
    /**
     * Executes all SingleOperation instances with the common BatchOperation and returns a list 
     * of tuples with the results from the BatchOperation and the original 
     * input-indices of the corresponding Operation. 
     */
    def execute(): List[Tuple2[Result[A], Int]] = {
        val input = singleOperations.reverse
        val result = batchOperation.fkt(input.map(_.value))
        assert(input.length == result.length)
        result.map(Success(_)).zip(input).flatMap(t => {
            indices.getOrElse(t._2, throw new IllegalStateException).map((t._1, _))
        })
    }
}
