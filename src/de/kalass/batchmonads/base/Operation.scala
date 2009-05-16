package de.kalass.batchmonads.base

/**
 * Base class for batchable operations
 */
abstract class Operation[A] {
  /**
   * "then" operator, builds a sequence
   */
  def ~[B](fkt: A => Operation[B]): Operation[B] = new Sequence[A, B](this, fkt)
  
}
