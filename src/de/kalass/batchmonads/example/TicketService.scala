package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.AbstractService;
import de.kalass.batchmonads.base.TypeHandler;
import de.kalass.batchmonads.base.BatchMonadResult;
import de.kalass.batchmonads.base.BatchMonad;

case class RetrieveTicket(val id: Long) extends BatchMonad[Ticket]
case class RetrieveTicketsOfCustomer(customerId: Long) extends BatchMonad[List[Ticket]]

class TicketService extends AbstractService {
  
  class RetrieveTicketsOfCustomerHandler extends TypeHandler[RetrieveTicketsOfCustomer, List[Ticket]] {
    
    def canProcess(monad: BatchMonad[_]) = monad.isInstanceOf[RetrieveTicketsOfCustomer]
    
    def process(monads: List[RetrieveTicketsOfCustomer]) = {
      // dummy implementation, just for demonstration.
      monads.map(monad => {
        println("getTicketsOfCustomer(" + monad.customerId + ")")
        new BatchMonadResult(List(new Ticket(monad.customerId)))
      })
    }
  }
  
  class RetrieveTicketHandler extends TypeHandler[RetrieveTicket, Ticket] {
    
    def canProcess(monad: BatchMonad[_]) = monad.isInstanceOf[RetrieveTicket]
    
    def process(monads: List[RetrieveTicket]) = {
      // dummy implementation, just for demonstration.
      monads.map(monad => new BatchMonadResult(new Ticket(monad.id)))
    }
  }
  
  def getHandlers() = List(new RetrieveTicketsOfCustomerHandler, new RetrieveTicketHandler)
}
