package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.CustomBatchProcessor;
import de.kalass.batchmonads.base.Success;
import de.kalass.batchmonads.base.Operation;

case class RetrieveSite(id: Long) extends Operation[Site]{}

class SiteService extends CustomBatchProcessor {

    /**
    * Retrieves all Sites with the requested Ids from the datasource.
    */
    registerSimpleOperation[RetrieveSite, Long, Site] {case s:RetrieveSite => s} {_.id} {
      _.map(id => {println("getSite("+ id +")"); new Site(id, "Site " + id)})
    }

}
