package de.kalass.batchmonads.base.impl

private[base] class ReturnHandler[A] extends CustomBatchProcessor {
  registerOperation[Return[A], A] {case Return(a) => Return(a.asInstanceOf[A])} 
  {
    _.map(monad => Success(monad.result))
  }
}
