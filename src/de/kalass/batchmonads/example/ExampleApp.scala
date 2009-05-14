package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.Return
import de.kalass.batchmonads.base.BatchExecutor

/**
* Current Shortcomings:
	* <ul>
*   <li>If the sequence operator is forgotten and Monads are simply placed each on a line, 
*       there is no warning whatsoever that this code has no effects (apart from using processing power and memory ;-))</li>
*   <li>Transaction Handling and automatic Batch splitting needs to be introduced</li>
*   <li>Side effects by a modifying monad to other monads need to be considered (i.e. if the BMs retrieve a customer, 
		*       then do some other stuff and later on modify the customer, the code might behave differently if the two
		*       original monads are explicitely processed in sequence, or as a batch given to the executor)</li>
*   <li>What about connected monad types? How do I ensure that the ModifyTicketsHandler effectively evicts (or better: replaces) 
*       the results of the RetrieveTicktesHandler</li>
*   <li></li>
* </ul>
* 
*/
object BMExample {
  
	/**
	* Demonstration of a function that uses the basic BatchMonads to build a new BatchMonad instance 
	*/
	val getTicketsOfCustomer = (customerId: Long) => {
		RetrieveCustomer(customerId) ~ (customer => 
		RetrieveSite(customer.siteId)  ~ (site => {
			println("Demonstration of more complex function bodies")
			RetrieveTicket.allOfCustomer(customer.id) 
		} ~ (tickets => {
			println("about to return tickets of customer: " + customer)
			Return((customer, site, tickets))
		}
		)))// close all those stupid braces...
	}

	def main(args: Array[java.lang.String]) {

		val executor = new BatchExecutor(new CustomerHandler(Nil), new TicketHandler(Nil), new SiteHandler(Nil));

		for (bmr <- executor.process(getTicketsOfCustomer(1), getTicketsOfCustomer(2), getTicketsOfCustomer(3))) {
			val cst = bmr.result
			println("customer: " + cst._1 + ", site: " + cst._2 + ", tickets: " + cst._3)
		}

	}
}
