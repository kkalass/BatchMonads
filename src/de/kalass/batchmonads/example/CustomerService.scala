package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.AbstractService;
import de.kalass.batchmonads.base.Success;
import de.kalass.batchmonads.base.Operation;

case class RetrieveCustomer(val id: Long) extends Operation[Customer] 

class CustomerService extends AbstractService {
  
  
    /**
    * Retrieves all Customers with the requested Ids from the datasource.
    */
    registerOperation[RetrieveCustomer, Customer] {case c:RetrieveCustomer => c}
    { retrieveCustomers => {
        for (retrieveCustomer <- retrieveCustomers) yield {
            println("getCustomer(" + retrieveCustomer.id + ")")
            Success(new Customer(retrieveCustomer.id, "Customer " + retrieveCustomer.id, 1))
        }
    }}
}

