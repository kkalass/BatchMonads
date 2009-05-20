package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.BatchOperation
import de.kalass.batchmonads.base.Operation


class CustomerServiceImpl extends CustomerService {

    private val getCustomers: BatchOperation[Long, Customer] = BatchOperation.create({
        ids => {
            println("getCustomers(" + ids + ")")
            for (id <- ids) yield {
                new Customer(id, "Customer " + id, 1)
            }
        }
    })

    def getCustomer(id: Long) = getCustomers.singleOperation(id)
}

