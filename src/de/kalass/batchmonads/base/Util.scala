package de.kalass.batchmonads.base

private[base] object Util {

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
}
