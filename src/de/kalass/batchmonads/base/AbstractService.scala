package de.kalass.batchmonads.base


trait AbstractService extends Service {
    /**
    * Process the given List of Monads, and return a list with the corresponding result objects,
    * in the same order as the input list.
    */
    private class TypeHandler[M, A](
            private[base] val canProcess: Operation[_] => Boolean, 
            private[base] val process: List[M] => List[Result[A]]
    ) {
        private[AbstractService] def processAny(monads: List[_]): List[Result[A]] = process(monads.map(_.asInstanceOf[M]))
    }

    private def appendToMap(
            monadWithIndex: Tuple2[Operation[_], Int], 
            handlers : List[TypeHandler[_,_]], 
            monadsWithIndexByHandlerMap: scala.collection.mutable.Map[TypeHandler[_,_], List[Tuple2[Operation[_], Int]]]
    ): Boolean = {
            if (handlers.isEmpty) {
                false
            } else {
                val handler = handlers.head
                if (handler.canProcess(monadWithIndex._1)) {
                    monadsWithIndexByHandlerMap.get(handler) match {
                    case Some(oldList) => monadsWithIndexByHandlerMap.put(handler, monadWithIndex :: oldList)
                    case None => monadsWithIndexByHandlerMap.put(handler, List(monadWithIndex))
                    }
                    true
                } else {
                    appendToMap(monadWithIndex, handlers.tail, monadsWithIndexByHandlerMap)
                }
            }
    }
    
    protected[base] def derive(monads: List[Tuple2[Operation[_], Int]]): Tuple3[Service, List[Tuple2[Operation[_], Int]], List[Tuple2[Result[_], Int]]] = {
            val monadsWithIndexByHandlerMap = scala.collection.mutable.Map[TypeHandler[_,_], List[Tuple2[Operation[_], Int]]]()
            val handlers = this.handlers
            val remaining = new scala.collection.mutable.ListBuffer[Tuple2[Operation[_], Int]]()
            for (monadWithIndex <- monads) {
                if (!appendToMap(monadWithIndex, handlers, monadsWithIndexByHandlerMap)) {
                    remaining + monadWithIndex
                }
            }
            val results = for (monadsWithIndexByHandler <- monadsWithIndexByHandlerMap) yield {
                val handler: TypeHandler[_,_] = monadsWithIndexByHandler._1
                val monadsWithIndex = monadsWithIndexByHandler._2.reverse
                val monadsWithIndices = scala.collection.mutable.LinkedHashMap[Operation[_], List[Int]]()
                for (monadWithIndex <- monadsWithIndex) {
                  val monad = monadWithIndex._1
                  val index = monadWithIndex._2
                  monadsWithIndices.get(monad) match {
                  case Some(oldList) => monadsWithIndices.put(monad, index :: oldList)
                  case None => monadsWithIndices.put(monad, List(index))
                  }
                }
                val inputList = monadsWithIndices.keys.toList
                val result = handler.processAny(inputList)
                val resultsWithIndicesListOfList = for ((monad, monadResult) <- inputList.zip(result)) yield {
                  monadsWithIndices.get(monad) match {
                    case Some(indices) => for (idx <- indices.reverse) yield (monadResult, idx)
                    case None => List()
                  }
                }
                resultsWithIndicesListOfList.flatMap(a => a)
            }
            (this, remaining.toList, results.flatMap(a => a).toList)
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
    protected def registerOperation[M <: Operation[A], A](canExecute: Operation[_] => Boolean)(executeAll: List[M] => List[Result[A]]) {
        handlers = new TypeHandler(canExecute, executeAll) :: handlers
    }
}
