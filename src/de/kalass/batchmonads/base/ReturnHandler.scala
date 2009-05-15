package de.kalass.batchmonads.base


class ReturnHandler[A] extends SingleTypeService[Return[A],A](_.isInstanceOf[Return[_]]) {
    def process(monads: List[Return[A]]) = {
        monads.map(monad => new BatchMonadResult(monad.a))
    }
}
