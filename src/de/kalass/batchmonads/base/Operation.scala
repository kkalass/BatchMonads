package de.kalass.batchmonads.base

import impl.Sequence

/**
 * Base class for batchable operations.
 * 
 * <p>Instances of this class can be combined with the sequence operator and
 * executed with the Executor, such that all operations of a kind in the 
 * same position of execution will always be batched.</p>
 * 
 * <p>If you want to create your own basic operations (and not just build sequences), 
 * you have basically two Options:</p>
 * 
 * <ol>
 *  <li>Use {@link BatchOperation.create} to create a batch operation and use it to create the batchable Operation instances.</li>
 *  <li>Use the full power and create a subclass of {@link Operation}, create an implementation of {@link BatchProcessor}
 *      that executes your instances in whichever way you like (and perhaps returns a new instance that contains
 *       some cached data for future usage within the same execution), and then register it with the Executor.</li>
 * </ol>
 * 
 */
abstract class Operation[A] {
  
  /**
   * "then" operator, builds a sequence.
   * 
   * <p>When the returned Operation is executed, its result value will be used as the input
   * value of the given function.</p>
   * 
   * @param fkt the function to call after executing this operation
   * @return an Operation that (when executed) will first execute this operation, 
   *         then take the result value and pass it to the given function and 
   *         last but not least execute the resulting Operation.
   */
  def ~[B](fkt: A => Operation[B]): Operation[B] = new Sequence[A, B](this, fkt)
  
}
