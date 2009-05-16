package de.kalass.batchmonads.base

trait Service {
  protected[base] def derive(monads: List[Tuple2[Operation[_], Int]]): Tuple3[Service, List[Tuple2[Operation[_], Int]], List[Tuple2[Result[_], Int]]]
}
