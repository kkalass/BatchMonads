package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.BatchMonad

object RetrieveCustomer {
  
  def apply(id: Long): BatchMonad[Customer] = new RetrieveCustomer(id);
}

class RetrieveCustomer(val id: Long) extends BatchMonad[Customer] {
  override def toString = "getCustomer(" + id + ")"
}
