package de.kalass.batchmonads.example
import de.kalass.batchmonads.base.Operation;


trait CustomerService {
  
    def getCustomer(id: Long): Operation[Customer]
}

