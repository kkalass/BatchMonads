package de.kalass.batchmonads.base.impl

private[base] object Util {

    private[base] def divideList[A, B](monads: List[Tuple2[A, Int]], select: PartialFunction[A, B]) : Tuple2[List[Tuple2[B, Int]], List[Tuple2[A, Int]]] = {
      divideList2(monads, (m: A) => if (select.isDefinedAt(m)) Some(select(m)) else None)
    }
    
    private[base] def divideList2[A, B](indexedValues: List[Tuple2[A, Int]], convert: A => Option[B]) : Tuple2[List[Tuple2[B, Int]], List[Tuple2[A, Int]]] = indexedValues match {
      case List() => (Nil, Nil)
      case (value, index) :: ivs => {
        val (firstList, secondList) = divideList2(ivs, convert)
        convert(value) match {
          case Some(convertedValue) => ((convertedValue, index) :: firstList, secondList)
          case None => (firstList, (value, index) :: secondList)
        }
      }
    }

    private[base] def buildMap[I, K, V](fkt: I => (K, V))(list: List[I]): Map[K, List[V]] = list match {
      case List () => Map()
      case x :: xs => {
        val (key, value) = fkt(x)
        val m = buildMap(fkt)(xs)
        m.get(key) match {
          case Some(valueList) => m + (key -> (value :: valueList))
          case None => m + (key -> List(value))
        }
      }
    }

    private[base] def mapReduce[I, K, V, O](fkt: I => (K, V), reduce: (K, List[V]) => O)(list: List[I]) : List[O] = {
            buildMap(fkt)(list).transform((key , values) => reduce(key, values)).values.toList
    }
}
