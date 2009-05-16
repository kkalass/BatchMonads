package de.kalass.batchmonads.base

private[base] class Sequence[A, B](val a: Operation[A], private val fkt: A => Operation[B]) extends Operation[B] {

  override def toString(): String = a + "~" + fkt

  private[base] def applyAnyResult(result: Any) = fkt(result.asInstanceOf[A])
}
