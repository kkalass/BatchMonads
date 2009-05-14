package de.kalass.batchmonads.example

class Customer(idP: Long, name: String, siteIdP: Long) {
   val id: Long = idP
   val siteId: Long = siteIdP
   
   override def toString = "Customer[" + id + "]"
}
