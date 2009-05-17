package de.kalass.batchmonads.example2

import de.kalass.batchmonads.base.AbstractService;
import de.kalass.batchmonads.base.Success;
import de.kalass.batchmonads.base.Result;
import de.kalass.batchmonads.base.Operation;

// Define Batchable Operations offered by this Service

class TicketServiceImpl extends AbstractService with TicketService {
    registerOperation[RetrieveTicketsOfCustomer, List[Ticket]]{case t: RetrieveTicketsOfCustomer => t} (retrieveTicketsOfCustomers)
    registerSimpleOperation[RetrieveTicket, Long, Ticket] {case t: RetrieveTicket => t} {_.id} (retrieveTickets) 

    /**
    * Retrieves all Tickets with the requested Ids from the datasource.
    * 
    * This is an example for registering a really simple operation that will produce a success value for each
    * and every input value, and that additionally automatically maps the operation objects to the
    * value wrapped by the operation object.
    */
    private def retrieveTickets(ticketIds: List[Long]) : List[Ticket] = ticketIds.map(new Ticket(_))
    private case class RetrieveTicket(val id: Long) extends Operation[Ticket]{}
    def retrieveTicket(id: Long): Operation[Ticket] = RetrieveTicket(id)
    

    /**
    * Retrieves the Tickets of the given Customers from the datasource.
    * 
    * This example uses the default method for registering an operation by registering a selector for the command,
    * and a function that converts a list of those commands into a list of results.
    */
    private def retrieveTicketsOfCustomers(commands: List[RetrieveTicketsOfCustomer]) : List[Result[List[Ticket]]] = {
            for (retrieveTicketsOfCustomer <- commands) yield {
                println("getTicketsOfCustomer(" + retrieveTicketsOfCustomer.customerId + ")")
                Success(List(new Ticket(retrieveTicketsOfCustomer.customerId)))
            }
    }
    private case class RetrieveTicketsOfCustomer(customerId: Long) extends Operation[List[Ticket]]{}
    
    /**
     * Batchable Operation: Retrieve all tickets of the customer with the given Id.
     * @param customerId the Id of the customer 
     * @return the batchable (i.e. delayed, not yet executed) operation
     */
    def retrieveTicketsOfCustomer(customerId: Long): Operation[List[Ticket]] = RetrieveTicketsOfCustomer(customerId)

}
