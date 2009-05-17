package de.kalass.batchmonads.base


trait AbstractService extends Service {
    /**
    * Process the given List of Monads, and return a list with the corresponding result objects,
    * in the same order as the input list.
    */
    private class TypeHandler[I, A](
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
    * @param canExecute a function that tests wether an operation can be executed by the given executor. 
    *                      Most importantly this means, that the type needs to be checked in this function
    * @param executeAll the function that "executes" a list of given operations, will be called by the framework
    *                      when a batchable execution is executed.
    */
    protected def registerOperation[M <: Operation[A], A](selectInput: PartialFunction[Operation[_], M])(executeAll: List[M] => List[Result[A]]) {
        handlers = new TypeHandler(selectInput, executeAll) :: handlers
    }

    //    protected def registerBatchedOperation[I, O](selectInput: PartialFunction[Operation[_], I], fkt: List[I] => List[O]) {
        //      
        //    }
}
