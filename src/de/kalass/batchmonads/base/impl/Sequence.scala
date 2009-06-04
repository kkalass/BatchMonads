package de.kalass.batchmonads.base.impl

private[base] class Sequence[A, B](val inputOperation: Operation[A], private val fkt: A => Operation[B]) extends Operation[B] {

  override def toString(): String = inputOperation + "~" + fkt

  /**
   * Apply the result of the input operation, but allow Any as type and use a cast 
   * to convert to the correct type. This way we can execute the sequence from
   * the outside without knowing correct type parametrization.
   * 
   * @param result the result of the input operation of this sequence
   */
  private[base] def outputOperation(inputOperationResult: Any) = fkt(inputOperationResult.asInstanceOf[A])
}
