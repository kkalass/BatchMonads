package de.kalass.batchmonads.base.impl

/**
 * Container class for the result of a BatchProcessor.
 * 
 * @param batchProcessor the processor which produced the result, perhaps enhanced with additional state
 * @param remainingWithIndices operations still not processed 
 * @param the results of the processing, together with the indices of the corresponding operations in the original input list
 */
private[base] case class BatchProcessorResult(batchProcessor: BatchProcessor, remainingWithIndices: List[Tuple2[Operation[_], Int]], resultsWithIndices: List[Tuple2[Result[_], Int]]) {}
