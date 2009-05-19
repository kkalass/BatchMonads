package de.kalass.batchmonads.base

/**
 * Abstract base class for operations that are executed with an Executor.
 */
class Result[+A]()

/**
 * Result value for successful execution of an Operation.
 */
case class Success[+A](val result: A) extends Result[A]

/**
 * Result value if execution of an Operation was aborted without aborting the entire processing.
 */
case class Error(msg: String) extends Result[Nothing]
    
