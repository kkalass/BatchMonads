package de.kalass.batchmonads.base.impl

private[base] object Util {

    private[base] def partition[A, B](valuesWithIndices: List[Tuple2[A, Int]], converter: PartialFunction[A, B]) : Tuple2[List[Tuple2[B, Int]], List[Tuple2[A, Int]]] = valuesWithIndices match {
      case List() => (Nil, Nil)
      case (value, index) :: ivs => {
        val (firstList, secondList) = partition(ivs, converter)
        if (converter.isDefinedAt(value))
          ((converter(value), index) :: firstList, secondList)
        else
          (firstList, (value, index) :: secondList)
      }
    }

    private[base] def buildMap[I, K, V, O](fkt: I => (K, V), reduce: (K, List[V]) => O)(list: List[I]): Map[K, O] = 
        buildMap(fkt)(list).transform((key, values) => reduce(key, values))
    
    private[base] def buildMap[I, K, V](fkt: I => (K, V))(list: List[I]): Map[K, List[V]] = (Map[K, List[V]]() /: list) {
      (map, x) => {
          val (key, value) = fkt(x)
          map.get(key) match {
            case Some(valueList) => map + (key -> (value :: valueList))
            case None => map + (key -> List(value))
          }
      }  
    }
}
