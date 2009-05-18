package de.kalass.batchmonads.example2

import de.kalass.batchmonads.base.AbstractService;
import de.kalass.batchmonads.base.BatchOperation;
import de.kalass.batchmonads.base.Return;
import de.kalass.batchmonads.base.Result;
import de.kalass.batchmonads.base.Operation;

// Define Batchable Operations offered by this Service

class TicketServiceImpl(customerService: CustomerService, siteService: SiteService) extends TicketService {
  
    /**
    * Retrieves all Tickets with the requested Ids from the datasource.
    * 
    * This is an example for registering a really simple operation that will produce a success value for each
    * and every input value, and that additionally automatically maps the operation objects to the
    * value wrapped by the operation object.
    */
    private val getTickets: BatchOperation[Long, Ticket] = BatchOperation.create(_.map(new Ticket(_)))
    def getTicket(id: Long): Operation[Ticket] = getTickets.singleOperation(id)

    /**
    * Retrieves the Tickets of the given Customers from the "datasource".
    */
    private val getTicketsOfCustomers: BatchOperation[Long, List[Ticket]] = BatchOperation.create {
        customerIds => {
            println("retrievTicketsOfCustomers(" + customerIds + ")")
            for (customerId <- customerIds) yield {
                // FIXME: this is wrong ;-)
                List(new Ticket(customerId))
            }
        }
    }

    /**
    * Demonstration of a function that uses the basic BatchMonads to build a new Operation instance.
    * 
    * What you see here is sequencing of several single operations. Each operation produces a result 
    * which will be used to create the next operation. Note how we use all retrieved data to build
    * the return value.
    */
    def getTicketsOfCustomer(customerId: Long) = {
        customerService.getCustomer(customerId) ~ (customer => 
        siteService.getSite(customer.siteId)  ~ (site => 
        // we do need to use the methods of a service - here we use the appropriate BatchOperation directly
        this.getTicketsOfCustomers.singleOperation(customer.id) ~ (tickets => 
        Return((customer, site, tickets))
        )))// end
    }


}
