package de.kalass.batchmonads.base

/**
 * Interface for implementations of BatchProcessors which can take arbitrary operations from the input list
 * and process then in a batch.
 * 
 * Implementing this interface yourself and registering it with the Executor is the most powerfull
 * but also most complex way of using the transparent batching API.
 */
trait BatchProcessor {
  /**
   * Execute all of the given operations that can be executed by this service.
   * 
   * The remaining operations must be returned (make sure to preserve the indices!), 
   * together with the results of the operations and a (possibly new) 
   * instance of the BatchProcessor for future usage within the same execution.
   * 
   * @param operationsWithIndices the operations to execute, together with the indices they had in the original input
   * @return the result of processing
   */
  protected[base] def execute(operationsWithIndices: List[Tuple2[Operation[_], Int]]): BatchProcessorResult
}
