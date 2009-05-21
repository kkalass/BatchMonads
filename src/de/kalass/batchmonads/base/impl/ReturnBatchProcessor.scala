package de.kalass.batchmonads.base.impl

private [base] class ReturnBatchProcessor extends BatchProcessor {
  
  protected[base] def execute(operationsWithIndices: List[Tuple2[Operation[_], Int]]): BatchProcessorResult = {
    val (returnOperations, remaining) = impl.Util.divideList(operationsWithIndices, {case r: Return[_] => r}: PartialFunction[Operation[_], Return[_]])
    BatchProcessorResult(this, remaining, returnOperations.map(_ match {case (operation, index) => (Success(operation.result), index)}))
  }
}