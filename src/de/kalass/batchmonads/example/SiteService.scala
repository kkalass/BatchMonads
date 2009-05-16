package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.AbstractService;
import de.kalass.batchmonads.base.Success;
import de.kalass.batchmonads.base.Operation;

case class RetrieveSite(id: Long) extends Operation[Site]{}

class SiteService extends AbstractService {

    /**
    * Retrieves all Sites with the requested Ids from the datasource.
    */
    registerOperation[RetrieveSite, Site](_.isInstanceOf[RetrieveSite])
    { 
        _.map(retrieveSite =>  {
            println("getSite(" + retrieveSite.id + ")")
            Success(new Site(retrieveSite.id, "Site " + retrieveSite.id))
        })
    }

}
