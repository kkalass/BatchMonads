package de.kalass.batchmonads.base.impl

private [base] class ReturnBatchHandler extends BatchProcessor {
  
  protected[base] def execute(operationsWithIndices: List[Tuple2[Operation[_], Int]]): BatchProcessorResult = {
    val (returns, remaining) = impl.Util.divideList(operationsWithIndices, {case r: Return[_] => r}: PartialFunction[Operation[_], Return[_]])
    BatchProcessorResult(this, remaining, returns.map(t => {val value = t._1.result; (Success(value), t._2)}))
  }
}