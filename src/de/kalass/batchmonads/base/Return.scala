package de.kalass.batchmonads.base

/**
 * A very simply operation that simply returns the given value when executed in a batch.
 */
case class Return[A](private[base] result: A) extends Operation[A]
