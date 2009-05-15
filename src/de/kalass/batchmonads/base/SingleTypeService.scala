package de.kalass.batchmonads.base

abstract class SingleTypeService[M <: BatchMonad[A], A](canHandle: (BatchMonad[_])=>Boolean) extends AbstractService with TypeHandler[M, A]{
  def canProcess(monad: BatchMonad[_]) = canHandle(monad)
  def getHandlers() = List[TypeHandler[_,_]](this)
}
