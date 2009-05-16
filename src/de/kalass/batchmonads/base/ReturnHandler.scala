package de.kalass.batchmonads.base

case class Return[A](private[base] result: A) extends BatchMonad[A]

private[base] class ReturnHandler[A] extends AbstractService {
  registerOperation[Return[A], A](_.isInstanceOf[Return[_]]) (_.map(monad => Success(monad.result)))
}
