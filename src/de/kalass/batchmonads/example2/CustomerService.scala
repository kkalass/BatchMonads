package de.kalass.batchmonads.example2
import de.kalass.batchmonads.base.Operation;


trait CustomerService {
  
    def getCustomer(id: Long): Operation[Customer]
}

