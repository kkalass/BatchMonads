package de.kalass.batchmonads.example
import de.kalass.batchmonads.base.BatchMonad

object RetrieveSite {
  def apply(id: Long) = new RetrieveSite(id)
}

class RetrieveSite(val id: Long) extends BatchMonad[Site] {
  override def toString = "getSite(" + id + ")"
}
