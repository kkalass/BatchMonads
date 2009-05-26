package de.kalass.batchmonads.base.impl

private[base] class Sequence[A, B](val inputOperation: Operation[A], private val fkt: A => Operation[B]) extends Operation[B] {

  override def toString(): String = inputOperation + "~" + fkt

  private[base] def applyAnyResult(result: Any) = fkt(result.asInstanceOf[A])
}
