package de.kalass.batchmonads.example2

import de.kalass.batchmonads.base.BatchOperation
import de.kalass.batchmonads.base.Operation


class CustomerServiceImpl extends CustomerService {

    private val getCustomers: BatchOperation[Long, Customer] = BatchOperation.create({
        ids => {
            println("------------")
            for (id <- ids) yield {
                println("getCustomer(" + id + ")")
                new Customer(id, "Customer " + id, 1)
            }
        }
    })

    def getCustomer(id: Long) = getCustomers.singleOperation(id)
}

