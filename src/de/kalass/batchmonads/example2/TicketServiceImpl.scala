package de.kalass.batchmonads.example2

import de.kalass.batchmonads.base.AbstractService;
import de.kalass.batchmonads.base.Success;
import de.kalass.batchmonads.base.Operation;

// Define Batchable Operations offered by this Service

class TicketServiceImpl extends AbstractService with TicketService {

    private case class RetrieveTicket(val id: Long) extends Operation[Ticket]{}
    def retrieveTicket(id: Long): Operation[Ticket] = RetrieveTicket(id)

    private case class RetrieveTicketsOfCustomer(customerId: Long) extends Operation[List[Ticket]]{}
    def retrieveTicketsOfCustomer(customerId: Long): Operation[List[Ticket]] = RetrieveTicketsOfCustomer(customerId)

    /**
    * Retrieves all Tickets with the requested Ids from the datasource.
    */
    registerOperation[RetrieveTicket, Ticket](_.isInstanceOf[RetrieveTicket])
    { 
        for (retrieveTicket <- _) yield {
            println("getTicket(" + retrieveTicket.id + ")")
            Success(new Ticket(retrieveTicket.id))
        }
    }

    /**
    * Retrieves the Tickets of the given Customers from the datasource.
    */
    registerOperation[RetrieveTicketsOfCustomer, List[Ticket]](_.isInstanceOf[RetrieveTicketsOfCustomer]) 
    { 
        for (retrieveTicketsOfCustomer <- _) yield {
            println("getTicketsOfCustomer(" + retrieveTicketsOfCustomer.customerId + ")")
            Success(List(new Ticket(retrieveTicketsOfCustomer.customerId)))
        }
    }
}
