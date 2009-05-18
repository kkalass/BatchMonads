package de.kalass.batchmonads.base

case class ExecutionResult(service: Service, remainingWithIndices: List[Tuple2[Operation[_], Int]], resultsWithIndices: List[Tuple2[Result[_], Int]]) {}


trait Service {
  /**
   * Execute all of the given operations that can be executed by this service.
   * 
   * The remaining operations will be returned, together with the results of the operations and a (possibly new) service.
   * @param operationsWithIndices the operations to execute, together with the indices they had in the original input
   */
  protected[base] def execute(operationsWithIndices: List[Tuple2[Operation[_], Int]]): ExecutionResult
}
