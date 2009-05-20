package de.kalass.batchmonads.example

import de.kalass.batchmonads.base.Operation;

trait SiteService {
    def getSite(id: Long): Operation[Site]
}
