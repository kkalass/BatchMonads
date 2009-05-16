package de.kalass.batchmonads.example2

import de.kalass.batchmonads.base.Return
import de.kalass.batchmonads.base.BatchExecutor
import de.kalass.batchmonads.base.Success
import de.kalass.batchmonads.base.Error

object ExampleApp {

    def main(args: Array[java.lang.String]) {
        val siteService = new SiteServiceImpl();
        val ticketService = new TicketServiceImpl();
        val customerService = new CustomerServiceImpl();
        
        // create an executor that knows all our services
        val executor = new BatchExecutor(customerService, ticketService, siteService);

        /**
        * Demonstration of a function that uses the basic BatchMonads to build a new Operation instance 
        */
        val getTicketsOfCustomer = (customerId: Long) => {
            customerService.retrieveCustomer(customerId) ~ (customer => 
            siteService.retrieveSite(customer.siteId)  ~ (site => {
                println("Demonstration of more complex function bodies")
                ticketService.retrieveTicketsOfCustomer(customer.id) 
            } ~ (tickets => {
                println("about to return tickets of customer: " + customer)
                Return((customer, site, tickets))
            }
            )))// close all those stupid braces...
        }


        // create the items we want to execute
        val list = List(getTicketsOfCustomer(1), getTicketsOfCustomer(2), getTicketsOfCustomer(3))

        println("*****************************")
        println("Execute each item on its own:")
        println("*****************************")
        for (item <- list) {
            executor.process(List(item))    
            println
        }

        println
        println
        println("*****************************")
        println("Do the batching:")
        println("*****************************")
        val results = executor.process(list)

        // ok - lets examine the results
        results.foreach( _ match {
        case Success(result) => println("Success! Got " + result)
        case Error(msg) => println("Error: " + msg)
        })

    }
}
