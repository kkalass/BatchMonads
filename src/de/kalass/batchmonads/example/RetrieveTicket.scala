package de.kalass.batchmonads.example
import de.kalass.batchmonads.base.BatchMonad 

object RetrieveTicket {
  def apply(id: Long) = new RetrieveTicket(id)
  def allOfCustomer(customerId: Long) = new RetrieveTicketsOfCustomer(customerId) // FIXME: use correct monad
}
class RetrieveTicket(val id: Long) extends BatchMonad[Ticket]{
  override def toString = "getTicketById(" + id + ")"
}

class RetrieveTicketsOfCustomer(private[example] val customerId: Long) extends BatchMonad[List[Ticket]] {
  override def toString = "getTicketsByCustomerId(" + customerId + ")"
}