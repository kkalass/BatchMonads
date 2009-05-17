package de.kalass.batchmonads.base

case class Return[A](private[base] result: A) extends Operation[A]

private[base] class ReturnHandler[A] extends AbstractService {
  registerOperation[Return[A], A] {case Return(a) => Return(a.asInstanceOf[A])} 
  {
    _.map(monad => Success(monad.result))
  }
}
