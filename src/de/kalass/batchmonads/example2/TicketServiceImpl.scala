package de.kalass.batchmonads.example2

import de.kalass.batchmonads.base.AbstractService;
import de.kalass.batchmonads.base.BatchOperation;
import de.kalass.batchmonads.base.Success;
import de.kalass.batchmonads.base.Result;
import de.kalass.batchmonads.base.Operation;

// Define Batchable Operations offered by this Service

class TicketServiceImpl extends TicketService {

    /**
    * Retrieves all Tickets with the requested Ids from the datasource.
    * 
    * This is an example for registering a really simple operation that will produce a success value for each
    * and every input value, and that additionally automatically maps the operation objects to the
    * value wrapped by the operation object.
    */
    private val retrieveTickets: BatchOperation[Long, Ticket] = BatchOperation.create(_.map(new Ticket(_)))
    def retrieveTicket(id: Long): Operation[Ticket] = retrieveTickets.singleOperation(id)
    

    /**
    * Retrieves the Tickets of the given Customers from the datasource.
    * 
    * This example uses the default method for registering an operation by registering a selector for the command,
    * and a function that converts a list of those commands into a list of results.
    */
    private val retrieveTicketsOfCustomers: BatchOperation[Long, List[Ticket]] = BatchOperation.create {
        customerIds => {
            println("retrievTicketsOfCustomers(" + customerIds + ")")
            for (customerId <- customerIds) yield {
                List(new Ticket(customerId))
            }
        }
    }
    
    /**
     * Batchable Operation: Retrieve all tickets of the customer with the given Id.
     * @param customerId the Id of the customer 
     * @return the batchable (i.e. delayed, not yet executed) operation
     */
    def retrieveTicketsOfCustomer(customerId: Long): Operation[List[Ticket]] = retrieveTicketsOfCustomers.singleOperation(customerId)

}
