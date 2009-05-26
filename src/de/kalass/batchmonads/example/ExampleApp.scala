package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.Return
import de.kalass.batchmonads.base.Executor
import de.kalass.batchmonads.base.Success
import de.kalass.batchmonads.base.Error

object ExampleApp {


    def main(args: Array[java.lang.String]) {
      
      // create services - do what spring would do :-)
        val siteService: SiteService = new SiteServiceImpl()
        val customerService: CustomerService = new CustomerServiceImpl()
        val ticketService: TicketService = new TicketServiceImpl(customerService, siteService)

        val executor = new Executor();

        // simulate a client that simply wants to get the details of some customers
        // (Hint: have a look at the output: Each step of the Operation will be batched!)
        val operations = List(
          ticketService.getTicketsOfCustomer(1), 
          ticketService.getTicketsOfCustomer(2), 
          ticketService.getTicketsOfCustomer(3)
        )

        operations.foreach({
          operation => 
            println()
            executor.process(List(operation))
        })

        println()
        val results = executor.process(operations)
        println()

        // ok - lets examine the results
        results.foreach( _ match {
          case Success(result) => println("Success! Got " + result)
          case Error(msg) => println("Error: " + msg)
        })

    }
}
