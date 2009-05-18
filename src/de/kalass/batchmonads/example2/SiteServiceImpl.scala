package de.kalass.batchmonads.example2

import de.kalass.batchmonads.base.Success
import de.kalass.batchmonads.base.Operation
import de.kalass.batchmonads.base.BatchOperation


class SiteServiceImpl extends SiteService {
    private val retrieveSites: BatchOperation[Long, Site] = BatchOperation.create ({
        _.map(id =>  {
            println("getSite(" + id + ")")
            new Site(id, "Site " + id)
        })
    }) 
    def retrieveSite(id: Long): Operation[Site] = retrieveSites.singleOperation(id)
}
