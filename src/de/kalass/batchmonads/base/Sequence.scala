package de.kalass.batchmonads.base

private[base] class Sequence[A, B](val a: BatchMonad[A], private val fkt: A => BatchMonad[B]) extends BatchMonad[B] {

  override def toString(): String = a + "°" + fkt

  private[base] def applyAnyResult(result: Any) = fkt(result.asInstanceOf[A])
}
