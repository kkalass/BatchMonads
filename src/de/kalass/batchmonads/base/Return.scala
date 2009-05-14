package de.kalass.batchmonads.base

object Return {
  def apply[A](a: A): BatchMonad[A] = new Return[A](a);
}
class Return[A](private[base] val a:A) extends BatchMonad[A] {
  override def toString = "return " + a
}
