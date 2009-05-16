package de.kalass.batchmonads.example2
import de.kalass.batchmonads.base.Operation;


trait CustomerService {
  
    def retrieveCustomer(id: Long): Operation[Customer]
    
}

