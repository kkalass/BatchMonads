package de.kalass.batchmonads.example2

import de.kalass.batchmonads.base.BatchOperation
import de.kalass.batchmonads.base.Operation


class CustomerServiceImpl extends CustomerService {

    private val retrieveCustomers: BatchOperation[Long, Customer] = BatchOperation.create({
        ids => {
            println("------------")
            for (id <- ids) yield {
                println("getCustomer(" + id + ")")
                new Customer(id, "Customer " + id, 1)
            }
        }
    })

    def retrieveCustomer(id: Long) = retrieveCustomers.singleOperation(id)
}

