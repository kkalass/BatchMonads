package de.kalass.batchmonads.base

class BatchExecutor(hdlers : List[Service]) {

    private val handlers = new ReturnHandler() :: hdlers

    def this(handlers: Service*) = this(handlers.toList)

    /**
    * Split the given list of monads into a tuple of two lists. The first list
    * will contain all sequences, and the second will contain all 
    * base monads
    */
    private def extractSequences(monads: List[BatchMonad[_]]) : Tuple2[List[Tuple2[Sequence[_,_], Int]], List[Tuple2[BatchMonad[_], Int]]] = {
        Util.divideList2[BatchMonad[_], Sequence[_,_]](monads.zipWithIndex, _ match {
        case s: Sequence[_,_] => Some(s)
        case _ => None
        })
    }

    /**
    * divide all monads into the approriate type handlers
    */
    private def deriveHandlers(monads: List[Tuple2[BatchMonad[_], Int]], handlers: List[Service]) : List[Tuple2[Service, List[Tuple2[BatchMonadResult[_], Int]]]] = {
        if (handlers.isEmpty && !monads.isEmpty) {
            throw new IllegalStateException("No Handler registered for monads: " + monads)
        }
        if (handlers.isEmpty) {
            List()
        } else {
            val (handler, remaining, result) = handlers.head.derive(monads)
            (handler, result) :: deriveHandlers(remaining, handlers.tail)
        }
    }

    /**
    * Process the given monads such that the corresponding operations are executed in a batched way.
    */
    def process[A](monads: List[BatchMonad[A]]) : List[BatchMonadResult[A]] = process(monads, handlers)._2.map(_.asInstanceOf[BatchMonadResult[A]])

    private class SequenceResult{}
    private case class MonadSequenceResult(monad: BatchMonad[_], inputIdx: Int) extends SequenceResult {}
    private case class ErrorSequenceResult(error: Error, inputIdx: Int) extends SequenceResult {}

    private def applySequenceTuple(tuple: Tuple2[Tuple2[Sequence[_,_],Int], BatchMonadResult[_]]): SequenceResult = {
            val idx = tuple._1._2
            val sequence = tuple._1._1
            val inputResult = tuple._2

            inputResult match {
            case Success(result) => MonadSequenceResult(sequence.applyAnyResult(result),idx)
            case e: Error => ErrorSequenceResult(e, idx)
            }
    }

    private def getErrors(sequenceResults: List[SequenceResult]): List[ErrorSequenceResult] = {
            if (sequenceResults.isEmpty) {
                Nil
            } else {
                sequenceResults.head match {
                case m: MonadSequenceResult => getErrors(sequenceResults.tail)
                case e: ErrorSequenceResult => e :: getErrors(sequenceResults.tail)
                }
            }
    }

    private def getMonads(sequenceResults: List[SequenceResult]): List[MonadSequenceResult] = {
            if (sequenceResults.isEmpty) {
                Nil
            } else {
                sequenceResults.head match {
                case m: MonadSequenceResult => m :: getMonads(sequenceResults.tail)
                case e: ErrorSequenceResult => getMonads(sequenceResults.tail)
                }
            }
    }

    private def process(monads: List[BatchMonad[_]], handlers: List[Service]) : Tuple2[List[Service], List[BatchMonadResult[_]]] = {
            if (monads.isEmpty) {
                (handlers, Nil)
            } else {
                // divide into sequences and other monads
                val (indexedSequences, indexedRemaining) = extractSequences(monads)

                // recursion for the left side of the sequences
                val (handlers1, sequenceInputResults) = process(indexedSequences.map(_._1.a), handlers)

                val sequenceResults = indexedSequences.zip(sequenceInputResults).map(applySequenceTuple _)

                // recursion for the right side of the sequences
                val sequenceErrors = getErrors(sequenceResults)
                val sequenceOutputMonadWithInputIndex = getMonads(sequenceResults)
                val (handlers2, recursionResults) = process(sequenceOutputMonadWithInputIndex.map(_.monad), handlers1)

                val recursionResultsWithInputIndex = recursionResults.zip(sequenceOutputMonadWithInputIndex).map(t => (t._1, t._2.inputIdx))

                // now, let the handlers do the (possibly slow and expensive) "real" work with side-effects, but keep track of the input index 
                val handlersWithResults: List[Tuple2[Service, List[Tuple2[BatchMonadResult[_], Int]]]] = 
                    for (partialResult <- deriveHandlers(indexedRemaining, handlers)) yield {partialResult}

                val results: List[Tuple2[BatchMonadResult[_], Int]] = sequenceErrors.map(e => (e.error, e.inputIdx)) ++ recursionResultsWithInputIndex ++ handlersWithResults.flatMap(_._2)

                // sort all results of the processing according to the corresponding input monads 
                (handlersWithResults.map(_._1), results.sort((t1, t2) => t1._2 < t2._2).map(_._1))
            }
    }
}
