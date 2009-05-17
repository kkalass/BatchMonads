package de.kalass.batchmonads.base


trait AbstractService extends Service {
    /**
    * Process the given List of Monads, and return a list with the corresponding result objects,
    * in the same order as the input list.
    */
    private class TypeHandler[I <:Operation[A], A](
            selector: PartialFunction[Operation[_], I], 
            process: List[I] => List[Result[A]],
            inputData: List[I],
            indices: Map[I,List[Int]]
    ) {
        assert(inputData.length == indices.size)

        def this(selector: PartialFunction[Operation[_], I], process: List[I] => List[Result[A]]) = this(selector, process, Nil, Map())

        private [AbstractService] def consume(op: Tuple2[Operation[_], Int]): Tuple2[TypeHandler[I, A], Boolean] = {
            if (selector.isDefinedAt(op._1)) {
                val value = selector(op._1)
                val index = op._2
                val (newInputData, newIndices) = indices.get(value) match {
                case Some(list) => (inputData, indices.update(value, index :: list))
                case None => (value :: inputData, indices.update(value, List(index)))
                }
                (new TypeHandler(selector, process, newInputData, newIndices), true)
            } else {
                (this, false)
            }
        }

        private[AbstractService] def execute(): List[Tuple2[Result[A], Int]] = {
                val reversedInput = inputData.reverse
                val result = process(reversedInput)
                assert(result.length == inputData.length)

                // go back to the full length, with one entry per input index
                result.zip(reversedInput).flatMap(t => {
                    val r = t._1
                    val d = t._2
                    val list = indices.getOrElse(d, throw new IllegalStateException)
                    list.map(idx => (r, idx))
                })
        }
    }

    private def consume(
            monadWithIndex: Tuple2[Operation[_], Int], 
            handlers : List[TypeHandler[_,_]] 
    ): Tuple2[List[TypeHandler[_,_]], Boolean] = {
            if (handlers.isEmpty) {
                (Nil, false)
            } else {
                val (handler, consumed) = handlers.head.consume(monadWithIndex)
                if (consumed) {
                    (handler :: handlers.tail, true)
                } else {
                    val (handlers2, foundConsumer) = consume(monadWithIndex, handlers.tail)
                    (handler :: handlers2, foundConsumer)
                }
            }
    }

    private def consumeMonads(
            monadsWithIndex: List[Tuple2[Operation[_], Int]],
            handlers: List[TypeHandler[_,_]]
    ): Tuple2[List[TypeHandler[_,_]], List[Tuple2[Operation[_], Int]]] = {
            if (monadsWithIndex.isEmpty) {
                (handlers, Nil)
            } else {
                val monadWithIndex = monadsWithIndex.head
                val (handlers2, consumed) = consume(monadWithIndex, handlers)
                val (handlers3, remaining) = consumeMonads(monadsWithIndex.tail, handlers2)
                if (consumed) {
                    (handlers3, remaining)
                } else {
                    (handlers3, monadWithIndex :: remaining)
                }
            }
    }

    protected[base] def derive(monads: List[Tuple2[Operation[_], Int]]): Tuple3[Service, List[Tuple2[Operation[_], Int]], List[Tuple2[Result[_], Int]]] = {
            val (handlers, remaining) = consumeMonads(monads, this.handlers)
            val results = handlers.flatMap(_.execute())
            (this, remaining.toList, results)
    }

    private var handlers = List[TypeHandler[_,_]]()

    /**
    * Registers a batchable operation.
    * 
    * @param selector a partial function that is defined for exactly the operations that should be executed in a batch by the given executeAll function
    * @param executeAll the function that "executes" a list of given operations, will be called by the framework
    *                      when a batchable execution is executed.
    */
    protected def registerOperation[M <: Operation[A], A](selector: PartialFunction[Operation[_], M])(executeAll: List[M] => List[Result[A]]) {
        handlers = new TypeHandler(selector, executeAll) :: handlers
    }

    /**
     * Registers a simple batchable operation.
     * 
     * <p>Executor functions registered with this method do not return the explicit Result object, but will return a 
     * valid instance of their result type for every input value instead, so either the entire operation must fail with 
     * an exception, or every input value will be afterwards regarded as been processed successfully.</p>
     * 
     * <p>Additionally, the input values given to the executor functions are not the operations themselves, but some input
     * values created by calling the "cvt" function.</p>
     */
    protected def registerSimpleOperation[M <: Operation[A], I, A](selector: PartialFunction[Operation[_], M]) (cvt: M => I) (fkt: List[I] => List[A]) {
        registerOperation[M,A](selector)(input => fkt(input.map(cvt)).map(Success(_)))
    }
}
