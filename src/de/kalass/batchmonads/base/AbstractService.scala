package de.kalass.batchmonads.base

trait TypeHandler[M, A] {

    def canProcess(monad: BatchMonad[_]): Boolean

    /**
    * Process the given List of Monads, and return a list with the corresponding result objects,
    * in the same order as the input list.
    */
    def process(monads: List[M]): List[BatchMonadResult[A]]
    
    private[base] def processAny(monads: List[_]): List[BatchMonadResult[A]] = process(monads.map(_.asInstanceOf[M]))
}

trait AbstractService extends Service {

    private def appendToMap(
            monadWithIndex: Tuple2[BatchMonad[_], Int], 
            handlers : List[TypeHandler[_,_]], 
            monadsWithIndexByHandlerMap: scala.collection.mutable.Map[TypeHandler[_,_], List[Tuple2[BatchMonad[_], Int]]]
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
    
    def derive(monads: List[Tuple2[BatchMonad[_], Int]]): Tuple3[Service, List[Tuple2[BatchMonad[_], Int]], List[Tuple2[BatchMonadResult[_], Int]]] = {
            val monadsWithIndexByHandlerMap = scala.collection.mutable.Map[TypeHandler[_,_], List[Tuple2[BatchMonad[_], Int]]]()
            val handlers = getHandlers()
            val remaining = new scala.collection.mutable.ListBuffer[Tuple2[BatchMonad[_], Int]]()
            for (monadWithIndex <- monads) {
                if (!appendToMap(monadWithIndex, handlers, monadsWithIndexByHandlerMap)) {
                    remaining + monadWithIndex
                }
            }
            val results = for (monadsWithIndexByHandler <- monadsWithIndexByHandlerMap) yield {
                val handler: TypeHandler[_,_] = monadsWithIndexByHandler._1
                val monadsWithIndex = monadsWithIndexByHandler._2.reverse
                val monadsWithIndices = scala.collection.mutable.LinkedHashMap[BatchMonad[_], List[Int]]()
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

    def getHandlers(): List[TypeHandler[_,_]]
}
