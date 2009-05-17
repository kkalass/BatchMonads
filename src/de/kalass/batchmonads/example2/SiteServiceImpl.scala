package de.kalass.batchmonads.example2

import de.kalass.batchmonads.base.AbstractService;
import de.kalass.batchmonads.base.Success;
import de.kalass.batchmonads.base.Operation;


class SiteServiceImpl extends AbstractService with SiteService {
  
    def retrieveSite(id: Long): Operation[Site] = RetrieveSite(id)
  
    //
    // Define and register all base operations of this service
    //
    private case class RetrieveSite(id: Long) extends Operation[Site]{}
    /**
    * Retrieves all Sites with the requested Ids from the datasource.
    */
    registerOperation[RetrieveSite, Site]{case s: RetrieveSite => s}
    { 
        _.map(retrieveSite =>  {
            println("getSite(" + retrieveSite.id + ")")
            Success(new Site(retrieveSite.id, "Site " + retrieveSite.id))
        })
    }

}
