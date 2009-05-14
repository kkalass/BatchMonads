package de.kalass.batchmonads.base

trait TypeHandler[M <: BatchMonad[_]] {
  def derive(monads: List[Tuple2[BatchMonad[_], Int]]): Tuple2[TypeHandler[M], List[Tuple2[BatchMonad[_], Int]]]
  
  def process[A](): List[Tuple2[BatchMonadResult[A], Int]]
}
