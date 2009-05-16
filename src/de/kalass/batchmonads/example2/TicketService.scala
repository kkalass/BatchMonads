package de.kalass.batchmonads.example2

import de.kalass.batchmonads.base.AbstractService;
import de.kalass.batchmonads.base.Success;
import de.kalass.batchmonads.base.Operation;

trait TicketService {
    def retrieveTicket(id: Long): Operation[Ticket]
    def retrieveTicketsOfCustomer(customerId: Long): Operation[List[Ticket]]
}
