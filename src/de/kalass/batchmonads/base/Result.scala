package de.kalass.batchmonads.base

class Result[+A]()
case class Success[+A](val result: A) extends Result[A]
case class Error(msg: String) extends Result[Nothing]
    
