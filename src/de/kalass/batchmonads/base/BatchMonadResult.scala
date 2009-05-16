package de.kalass.batchmonads.base

class BatchMonadResult[+A]()
case class Success[+A](val result: A) extends BatchMonadResult[A]
case class Error(msg: String) extends BatchMonadResult[Nothing]
    
