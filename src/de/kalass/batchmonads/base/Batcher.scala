package de.kalass.batchmonads.base

private class Context(map: Map[BatchOperation[_,_], Container[_,_]] ) {
    def this() = this(Map())
    def addOperationToBatcher[I, A](op: BatchedOperationSingleOp[I, A], idx: Int) : Context = {
        val newContainer: Container[I, A] = map.get(op.batcher) match {
        case Some(container) => container.add(op, idx) 
        case None => new Container[I, A](op.batcher, List(op), Map(op-> List(idx)))
    }
    new Context(map + (op.batcher -> newContainer))
    }

    // TODO: maybe keep some reference of past operations, so that we can detect possibilities for caching, or detect batch-sequence errors? 
            def executeAllBatchers() = (new Context(), map.values.toList.flatMap(_.execute()))
}

private class Container[I, A](
        batcher: BatchOperation[I, A], 
        inputData: List[BatchedOperationSingleOp[I, A]],
        indices: Map[BatchedOperationSingleOp[I, A],List[Int]]
) {
    assert(inputData.length == indices.size)

    def add(op: BatchedOperationSingleOp[I, A], index: Int): Container[I, A] = {
        assert(op.batcher == batcher)
        val value = op
        val (newInputData, newIndices) = indices.get(value) match {
        case Some(list) => (inputData, indices.update(value, index :: list))
        case None => (value :: inputData, indices.update(value, List(index)))
        }
        new Container[I, A](op.batcher, newInputData, newIndices)
    }
    def execute(): List[Tuple2[Result[A], Int]] = {
            val input = inputData.reverse
            batcher.fkt(input.map(_.value)).map(Success(_)).zip(input).flatMap(t => {
                indices.getOrElse(t._2, throw new IllegalStateException).map((t._1, _))
            })
    }
}

object BatchOperation {
    def create[I, A](fkt: List[I] => List[A]) = new BatchOperation(fkt)
}
class BatchOperation[I, A](val fkt: List[I] => List[A]) {
    def singleOperation(value: I): Operation[A] = new BatchedOperationSingleOp[I, A](value, this)
    def apply(list: List[I]): List[A] = fkt(list)
}

// case class, so that we get a good equals implementation for free
private case class BatchedOperationSingleOp[I, A](value: I, batcher: BatchOperation[I, A]) extends Operation[A] {
    private[base] def add(c: Context, idx: Int) = {
        c.addOperationToBatcher(this, idx)
    }
}

private[base] class BatcherService private(ctxt: Context) extends Service {
    def this() = this(new Context())

    protected[base] def execute(operationsWithIndices: List[Tuple2[Operation[_], Int]]): ExecutionResult = {
        var (selectedOperations, remaining) = Util.divideList(operationsWithIndices, {case op: BatchedOperationSingleOp[_,_] => op}: PartialFunction[Operation[_], BatchedOperationSingleOp[_,_]])
        //Util.buildMap[Tuple2[BatchedOperationSingleOp[_,_],Int], List[_] => List[_], List[_]]((t: Tuple2[BatchedOperationSingleOp[_,_], Int]) => {val op = t._1; val idx = t._2; (op.fkt, op.value)})(selectedOperations)
        var context = selectedOperations.foldLeft(new Context())((ctx, a) => a._1.add(ctx, a._2))
        var (newContext, results) = context.executeAllBatchers()
        ExecutionResult(new BatcherService(newContext), remaining, results)
    }
}

