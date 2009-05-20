package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.Operation
import de.kalass.batchmonads.base.BatchOperation


class SiteServiceImpl extends SiteService {
  
    private val getSites: BatchOperation[Long, Site] = BatchOperation.create ({
        _.map(id =>  {
            println("getSite(" + id + ")")
            new Site(id, "Site " + id)
        })
    }) 
    def getSite(id: Long): Operation[Site] = getSites.singleOperation(id)
}
