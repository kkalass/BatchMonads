package de.kalass.batchmonads.base

class ReturnHandler(data:List[Tuple2[Return[_], Int]]) extends TypeHandler[Return[_]] {

  override def derive(monads:List[Tuple2[BatchMonad[_], Int]]) = {
    val (typedMonads, remaining) = base.Util.divideList2[BatchMonad[_], Return[_]](monads, (m:BatchMonad[_]) => m match {
      case s: Return[_] => Some(s)
      case _ => None
    })
    (new ReturnHandler(typedMonads), remaining) 
  }
  def process[A]() = {
    data.map(t => (new BatchMonadResult(t._1.a.asInstanceOf[A]), t._2))
  }
  
  
}
