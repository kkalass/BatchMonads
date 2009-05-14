package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.TypeHandler;
import de.kalass.batchmonads.base.BatchMonadResult;
import de.kalass.batchmonads.base.BatchMonad;

/**
 * FIXME: Handle RetrieveTicketsOfCustomer as well as RetrieveTicket!
 */
class TicketHandler(data:List[Tuple2[RetrieveTicketsOfCustomer, Int]]) extends TypeHandler[RetrieveTicketsOfCustomer] {

  override def derive(monads:List[Tuple2[BatchMonad[_], Int]]) = {
    val (typedMonads, remaining) = base.Util.divideList2(monads, (m:BatchMonad[_]) => m match {
      case s: RetrieveTicketsOfCustomer => Some(s)
      case _ => None
    })
    (new TicketHandler(typedMonads), remaining) 
  }
  def process[A]() = {
    data.map(t => (new BatchMonadResult(List(new Ticket(t._1.customerId)).asInstanceOf[A]), t._2))
  }
  
  
}
