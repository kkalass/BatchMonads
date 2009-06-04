package de.kalass.batchmonads.base

import impl.Sequence
import impl.BatchProcessor
import impl.BatchProcessorResult

/**
 * Executor for executing a list of Operations together in one batch.
 * 
 * 
* Current Shortcomings:
    * <ul>
*   <li>If the sequence operator is forgotten and Monads are simply placed each on a line, 
*       there is no warning whatsoever that this code has no effects (apart from using processing power and memory ;-))</li>
*   <li>Transaction Handling and automatic Batch splitting needs to be introduced</li>
*   <li>Side effects by a modifying operation to other operations need to be considered (i.e. if the BMs retrieve a customer, 
        *       then do some other stuff and later on modify the customer, the code might behave differently if the two
        *       original operations are explicitely processed in sequence, or as a batch given to the executor)</li>
*   <li>Caching (once introduced): What about connected batch functions? How do I ensure that the ModifyTicketsFunction effectively evicts (or better: replaces) 
*       the cached results of the GetTicketsFunction</li>
*   <li></li>
* </ul>
* 
* 
* @param batchPrcssrs a list of BatchProcessor instances to use, additionally to the default instances
*/
class Executor(batchPrcssrs : List[BatchProcessor]) {

    private val batchProcessors = new impl.ReturnBatchProcessor() :: new impl.BaseOperationBatchProcessor() :: batchPrcssrs

    def this(batchProcessors: BatchProcessor*) = this(batchProcessors.toList)

    /**
    * Split the given list of operations into a tuple of two lists. The first list
    * will contain all sequences, and the second will contain all 
    * base operations
    */
    private def extractSequences(operations: List[Operation[_]]) : Tuple2[List[Tuple2[Sequence[_,_], Int]], List[Tuple2[Operation[_], Int]]] = {
      impl.Util.partition[Operation[_], Sequence[_,_]](operations.zipWithIndex, {case s: Sequence[_,_] => s})
    }

    /**
    * divide all operations into the approriate type batchProcessors
    */
    private def executeProcessors(operations: List[Tuple2[Operation[_], Int]], batchProcessors: List[BatchProcessor]) : List[Tuple2[BatchProcessor, List[Tuple2[Result[_], Int]]]] = {
        batchProcessors match {
          case List() => {
            if (!operations.isEmpty) {
                throw new IllegalStateException("No Handler registered for operations: " + operations)
            } else {
                List()
            }
          }
          case p :: ps => { 
              val BatchProcessorResult(batchProcessor, remaining, result) = p.execute(operations)
              (batchProcessor, result) :: executeProcessors(remaining, ps)
          }
        }
    }

    /**
    * Process the given operations such that the operations are executed in a batch.
    * 
    * After each step of the operation, the partial operations will be collected and 
    * executed as a batch again, so that a minimum amount of calls to the underlying
    * BatchedFunctions are executed.
    */
    def process[A](operations: List[Operation[A]]) : List[Result[A]] = {
      val (_, results) = process(operations, batchProcessors)
      results.map(_.asInstanceOf[Result[A]])
    }

    private case class SequenceOutputOperation(operation: Operation[_], inputIdx: Int) {}
    private case class SequenceError(error: Error, inputIdx: Int) {}

    private def getSequenceOutputOperations(
      indexedSequences: List[Tuple2[Sequence[_,_], Int]], 
      inputOperationResults: List[Result[_]]
    ) : Tuple2[List[SequenceOutputOperation], List[SequenceError]] = {
      assert(indexedSequences.size == inputOperationResults.size)
      
      ((List[SequenceOutputOperation](), List[SequenceError]()) /: indexedSequences.zip(inputOperationResults)) {
        (accumulated, inputValue: Tuple2[Tuple2[Sequence[_,_], Int], Result[_]]) => {
          val (outputOperations, sequenceErrors) = accumulated
          
          val sequence = inputValue._1._1
          val idx = inputValue._1._2
          val result = inputValue._2

          result match {
            case Success(r) => (SequenceOutputOperation(sequence.outputOperation(r), idx) :: outputOperations, sequenceErrors)
            case e: Error => (outputOperations, SequenceError(e, idx) :: sequenceErrors)
          }
        }
        
      }
    }

    private def process(operations: List[Operation[_]], batchProcessors: List[BatchProcessor]) : Tuple2[List[BatchProcessor], List[Result[_]]] = {
            if (operations.isEmpty) {
                (batchProcessors, Nil)
            } else {
                // divide into sequences and other operations
                val (indexedSequences, indexedRemaining) = extractSequences(operations)

                // recursion for the left side of the sequences
                val (batchProcessors1, sequenceInputOperationResults) = process(indexedSequences.map(_._1.inputOperation), batchProcessors)

                val (sequenceOutputOperationsWithInputIndex, sequenceErrors) = getSequenceOutputOperations(indexedSequences, sequenceInputOperationResults)

                // recursion for the right side of the sequences
                val (batchProcessors2, outputOperationResults) = process(sequenceOutputOperationsWithInputIndex.map(_.operation), batchProcessors1)

                val outputOperationResultsWithInputIndex = outputOperationResults.zip(sequenceOutputOperationsWithInputIndex).map(t => (t._1, t._2.inputIdx))

                // now, let the batchProcessors do the (possibly slow and expensive) "real" work with side-effects, but keep track of the input index 
                val (batchProcessors3, processorResults) = List.unzip(executeProcessors(indexedRemaining, batchProcessors2))

                val unsortedResults = sequenceErrors.map(e => (e.error, e.inputIdx)) ++ outputOperationResultsWithInputIndex ++ List.flatten(processorResults)

                val results = unsortedResults.sort((t1, t2) => t1._2 < t2._2).map(_._1)
                
                assert(results.length == operations.length)
                
                // sort all results of the processing according to the corresponding input operations and strip the index
                (batchProcessors3, results)
            }
    }
}
