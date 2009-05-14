package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.TypeHandler;
import de.kalass.batchmonads.base.BatchMonadResult;
import de.kalass.batchmonads.base.BatchMonad;

class CustomerHandler(data:List[Tuple2[RetrieveCustomer, Int]]) extends TypeHandler[RetrieveCustomer] {

  override def derive(monads:List[Tuple2[BatchMonad[_], Int]]) = {
    val (typedMonads, remaining) = base.Util.divideList2(monads, (m:BatchMonad[_]) => m match {
      case s: RetrieveCustomer => Some(s)
      case _ => None
    })
    (new CustomerHandler(typedMonads), remaining) 
  }
  def process[A]() = {
    data.map(t => (new BatchMonadResult(new Customer(t._1.id, "Customer Number " + t._1.id, 1).asInstanceOf[A]), t._2))
  }
  
  
}
