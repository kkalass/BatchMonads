package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.AbstractService;
import de.kalass.batchmonads.base.Success;
import de.kalass.batchmonads.base.Operation;

// Define Batchable Operations offered by this Service
case class RetrieveTicket(val id: Long) extends Operation[Ticket]{}
case class RetrieveTicketsOfCustomer(customerId: Long) extends Operation[List[Ticket]]{}

class TicketService extends AbstractService {

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
