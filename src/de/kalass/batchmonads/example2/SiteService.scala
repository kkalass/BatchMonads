package de.kalass.batchmonads.example2

import de.kalass.batchmonads.base.AbstractService;
import de.kalass.batchmonads.base.Success;
import de.kalass.batchmonads.base.Operation;

trait SiteService {
    def getSite(id: Long): Operation[Site]
}
