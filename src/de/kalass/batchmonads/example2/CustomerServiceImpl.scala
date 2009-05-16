package de.kalass.batchmonads.example2

import de.kalass.batchmonads.base.AbstractService;
import de.kalass.batchmonads.base.Success;
import de.kalass.batchmonads.base.Operation;


class CustomerServiceImpl extends AbstractService with CustomerService {
    
    private case class RetrieveCustomer(val id: Long) extends Operation[Customer] 
    def retrieveCustomer(id: Long): Operation[Customer] = RetrieveCustomer(id)
  
    /**
    * Retrieves all Customers with the requested Ids from the datasource.
    */
    registerOperation[RetrieveCustomer, Customer](_.isInstanceOf[RetrieveCustomer])
    { retrieveCustomers => {
        for (retrieveCustomer <- retrieveCustomers) yield {
            println("getCustomer(" + retrieveCustomer.id + ")")
            Success(new Customer(retrieveCustomer.id, "Customer " + retrieveCustomer.id, 1))
        }
    }}
    
    
}

