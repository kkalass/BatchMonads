package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.SingleTypeService;
import de.kalass.batchmonads.base.BatchMonadResult;
import de.kalass.batchmonads.base.BatchMonad;

case class RetrieveSite(id: Long) extends BatchMonad[Site]

class SiteService extends SingleTypeService[RetrieveSite, Site](_.isInstanceOf[RetrieveSite]) {
    
    def process(monads: List[RetrieveSite]) = {
        monads.map(monad => {
            println("getSite(" + monad.id + ")")
            new BatchMonadResult(new Site(monad.id, "Site " + monad.id))
        })
    }
}
