package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.SingleTypeService;
import de.kalass.batchmonads.base.BatchMonadResult;
import de.kalass.batchmonads.base.BatchMonad;

case class RetrieveCustomer(val id: Long) extends BatchMonad[Customer] 

class CustomerService extends SingleTypeService[RetrieveCustomer, Customer](_.isInstanceOf[RetrieveCustomer]) {
  
    def process(monads: List[RetrieveCustomer]) = {
      monads.map(monad => {
        println("getCustomer(" + monad.id + ")")
        new BatchMonadResult(new Customer(monad.id, "Customer " + monad.id, 1))
      })
    }
}

