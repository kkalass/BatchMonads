package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.TypeHandler;
import de.kalass.batchmonads.base.BatchMonadResult;
import de.kalass.batchmonads.base.BatchMonad;

class SiteHandler(data:List[Tuple2[RetrieveSite, Int]]) extends TypeHandler[RetrieveSite] {

  override def derive(monads:List[Tuple2[BatchMonad[_], Int]]) = {
    val (typedMonads, remaining) = base.Util.divideList2(monads, (m:BatchMonad[_]) => m match {
      case s: RetrieveSite => Some(s)
      case _ => None
    })
    (new SiteHandler(typedMonads), remaining) 
  }
  def process[A]() = {
    data.map(t => (new BatchMonadResult(new Site(t._1.id, "Site Number " + t._1.id).asInstanceOf[A]), t._2))
  }
  
  
}
