package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.Operation;

trait TicketService {
    def getTicket(id: Long): Operation[Ticket]
    def getTicketsOfCustomer(customerId: Long): Operation[Tuple3[Customer, Site, List[Ticket]]]
}
