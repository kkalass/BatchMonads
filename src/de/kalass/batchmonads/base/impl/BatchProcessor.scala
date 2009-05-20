package de.kalass.batchmonads.base.impl

/**
 * Interface for implementations of BatchProcessors which can take arbitrary operations from the input list
 * and process then in a batch.
 * 
 * Implementing this interface yourself and registering it with the Executor is the most powerfull
 * but also most complex way of using the transparent batching API.
 * 
 * Because the main function of this method is currently using a wildcard for the actual input and output 
 * values, it is not absolutely typesafe and implementors need to take care themselves that they return
 * the expected type. 
 * 
 * <em>Because of this unsafety, we currently keep this interface in the implementation.</em>
 * 
 */
private[base] trait BatchProcessor {
  /**
   * Execute all of the given operations that can be executed by this service.
   * 
   * The remaining operations must be returned (make sure to preserve the indices!), 
   * together with the results of the operations and a (possibly new) 
   * instance of the BatchProcessor for future usage within the same execution.
   * 
   * Furthermore you need to make sure that the results you create for the Operation instances corresponds 
   * exactly to the type expected at instantiation time of the operation.
   * 
   * @param operationsWithIndices the operations to execute, together with the indices they had in the original input
   * @return the result of processing
   */
  protected[base] def execute(operationsWithIndices: List[Tuple2[Operation[_], Int]]): BatchProcessorResult
}
