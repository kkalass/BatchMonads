package de.kalass.batchmonads.base.impl

// case class, so that we get a good equals implementation for free
private[base] case class BaseOperation[I, A](value: I, creator: BatchOperation[I, A]) extends Operation[A] {
    private[base] def addSelf(c: BaseOperationBatchBuilder, idx: Int) = {
        c.addOperation(this, idx)
    }
}