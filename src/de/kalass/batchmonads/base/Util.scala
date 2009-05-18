package de.kalass.batchmonads.base

private[base] object Util {

    private[base] def divideList[A, B](monads: List[Tuple2[A, Int]], select: PartialFunction[A, B]) : Tuple2[List[Tuple2[B, Int]], List[Tuple2[A, Int]]] = {
            divideList2(monads, (m: A) => if (select.isDefinedAt(m)) Some(select(m)) else None)
    }
    
    private[base] def divideList2[A, B](monads: List[Tuple2[A, Int]], convert: A => Option[B]) : Tuple2[List[Tuple2[B, Int]], List[Tuple2[A, Int]]] = {
            if (monads.isEmpty) {
                (Nil, Nil)
            } else {
                val head = monads.head
                val (s, o) = divideList2(monads.tail, convert)
                convert(head._1) match {
                case Some(b) => ((b, head._2)::s, o)
                case None => (s, head::o)
                }
            }
    }

    private[base] def buildMap[I, K, V](fkt: I => (K, V))(list: List[I]): Map[K, List[V]] = {
            if (list.isEmpty) {
                Map()
            } else {
                val head = list.head
                val tail = list.tail
                val (k, value) = fkt(head)
                val m = buildMap(fkt)(tail)
                m.get(k) match {
                case Some(list) => m + (k -> (value :: list))
                case None => m + (k -> List(value))
                }
            }
    }

    private[base] def mapReduce[I, K, V, O](fkt: I => (K, V), reduce: (K, List[V]) => O)(list: List[I]) : List[O] = {
            buildMap(fkt)(list).transform((key , values) => reduce(key, values)).values.toList
    }
}
