package de.kalass.batchmonads.base

object Util {
  def divideList[A, B](monads: List[A], convert: A => Option[B]) : Tuple2[List[B], List[A]] = {
    if (monads.isEmpty) {
    	(Nil, Nil)
    } else {
    	val head = monads.head
    	val (s, o) = divideList(monads.tail, convert)
        convert(head) match {
          case Some(b) => (b::s, o)
          case None => (s, head::o)
        }
    }
  }
  
  def divideList2[A, B](monads: List[Tuple2[A, Int]], convert: A => Option[B]) : Tuple2[List[Tuple2[B, Int]], List[Tuple2[A, Int]]] = {
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
