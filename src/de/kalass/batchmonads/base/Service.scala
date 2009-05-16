package de.kalass.batchmonads.base

trait Service {
  protected[base] def derive(monads: List[Tuple2[BatchMonad[_], Int]]): Tuple3[Service, List[Tuple2[BatchMonad[_], Int]], List[Tuple2[BatchMonadResult[_], Int]]]
}
