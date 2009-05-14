package de.kalass.batchmonads.base

/**
 * Base class for batch monads
 */
abstract class BatchMonad[A] {
  /**
   * "then" operator, builds a sequence
   */
  def ~[B](fkt: A => BatchMonad[B]): BatchMonad[B] = new Sequence[A, B](this, fkt)
  
}
