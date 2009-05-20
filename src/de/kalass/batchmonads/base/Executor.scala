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

    private val batchProcessors = new impl.ReturnBatchHandler() :: new impl.SingleOperationBatchProcessor() :: batchPrcssrs

    def this(batchProcessors: BatchProcessor*) = this(batchProcessors.toList)

    /**
    * Split the given list of operations into a tuple of two lists. The first list
    * will contain all sequences, and the second will contain all 
    * base operations
    */
    private def extractSequences(operations: List[Operation[_]]) : Tuple2[List[Tuple2[Sequence[_,_], Int]], List[Tuple2[Operation[_], Int]]] = {
        impl.Util.divideList2[Operation[_], Sequence[_,_]](operations.zipWithIndex, _ match {
        case s: Sequence[_,_] => Some(s)
        case _ => None
        })
    }

    /**
    * divide all operations into the approriate type batchProcessors
    */
    private def executeProcessors(operations: List[Tuple2[Operation[_], Int]], batchProcessors: List[BatchProcessor]) : List[Tuple2[BatchProcessor, List[Tuple2[Result[_], Int]]]] = {
        if (batchProcessors.isEmpty && !operations.isEmpty) {
            throw new IllegalStateException("No Handler registered for operations: " + operations)
        }
        if (batchProcessors.isEmpty) {
            List()
        } else {
            val BatchProcessorResult(batchProcessor, remaining, result) = batchProcessors.head.execute(operations)
            (batchProcessor, result) :: executeProcessors(remaining, batchProcessors.tail)
        }
    }

    /**
    * Process the given operations such that the operations are executed in a batch.
    * 
    * After each step of the operation, the partial operations will be collected and 
    * executed as a batch again, so that a minimum amount of calls to the underlying
    * BatchedFunctions are executed.
    */
    def process[A](operations: List[Operation[A]]) : List[Result[A]] = process(operations, batchProcessors)._2.map(_.asInstanceOf[Result[A]])

    private class SequenceResult{}
    private case class SuccessSequenceResult(operation: Operation[_], inputIdx: Int) extends SequenceResult {}
    private case class ErrorSequenceResult(error: Error, inputIdx: Int) extends SequenceResult {}

    private def applySequenceTuple(tuple: Tuple2[Tuple2[Sequence[_,_],Int], Result[_]]): SequenceResult = {
            val idx = tuple._1._2
            val sequence = tuple._1._1
            val inputResult = tuple._2

            inputResult match {
            case Success(result) => SuccessSequenceResult(sequence.applyAnyResult(result),idx)
            case e: Error => ErrorSequenceResult(e, idx)
            }
    }

    private def getErrors(sequenceResults: List[SequenceResult]): List[ErrorSequenceResult] = {
            if (sequenceResults.isEmpty) {
                Nil
            } else {
                sequenceResults.head match {
                case m: SuccessSequenceResult => getErrors(sequenceResults.tail)
                case e: ErrorSequenceResult => e :: getErrors(sequenceResults.tail)
                }
            }
    }

    private def getSuccesses(sequenceResults: List[SequenceResult]): List[SuccessSequenceResult] = {
            if (sequenceResults.isEmpty) {
                Nil
            } else {
                sequenceResults.head match {
                case m: SuccessSequenceResult => m :: getSuccesses(sequenceResults.tail)
                case e: ErrorSequenceResult => getSuccesses(sequenceResults.tail)
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
                val (batchProcessors1, sequenceInputResults) = process(indexedSequences.map(_._1.a), batchProcessors)

                val sequenceResults = indexedSequences.zip(sequenceInputResults).map(applySequenceTuple _)

                // recursion for the right side of the sequences
                val sequenceErrors = getErrors(sequenceResults)
                val sequenceOutputMonadWithInputIndex = getSuccesses(sequenceResults)
                val (batchProcessors2, recursionResults) = process(sequenceOutputMonadWithInputIndex.map(_.operation), batchProcessors1)

                val recursionResultsWithInputIndex = recursionResults.zip(sequenceOutputMonadWithInputIndex).map(t => (t._1, t._2.inputIdx))

                // now, let the batchProcessors do the (possibly slow and expensive) "real" work with side-effects, but keep track of the input index 
                val processorsWithResults: List[Tuple2[BatchProcessor, List[Tuple2[Result[_], Int]]]] = 
                    for (partialResult <- executeProcessors(indexedRemaining, batchProcessors2)) yield {partialResult}

                val results: List[Tuple2[Result[_], Int]] = sequenceErrors.map(e => (e.error, e.inputIdx)) ++ recursionResultsWithInputIndex ++ processorsWithResults.flatMap(_._2)

                // sort all results of the processing according to the corresponding input operations 
                (processorsWithResults.map(_._1), results.sort((t1, t2) => t1._2 < t2._2).map(_._1))
            }
    }
}
