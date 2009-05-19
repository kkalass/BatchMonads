package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.CustomBatchProcessor;
import de.kalass.batchmonads.base.Success;
import de.kalass.batchmonads.base.Operation;

// Define Batchable Operations offered by this Service
case class RetrieveTicket(val id: Long) extends Operation[Ticket]{}
case class RetrieveTicketsOfCustomer(customerId: Long) extends Operation[List[Ticket]]{}

class TicketService extends CustomBatchProcessor {

    /**
    * Retrieves all Tickets with the requested Ids from the datasource.
    */
    registerSimpleOperation[RetrieveTicket, Long, Ticket]{case t:RetrieveTicket => t} {_.id} { 
        _.map(id => {println("getTicket(" + id + ")");new Ticket(id)})
    }

    /**
    * Retrieves the Tickets of the given Customers from the datasource.
    */
    registerOperation[RetrieveTicketsOfCustomer, List[Ticket]]{case t: RetrieveTicketsOfCustomer => t} 
    { 
        commands => {
            println("getTicketsOfCustomers(" + commands + ")")
            for (retrieveTicketsOfCustomer <- commands) yield {
                Success(List(new Ticket(retrieveTicketsOfCustomer.customerId)))
            }
        }
    }
}
