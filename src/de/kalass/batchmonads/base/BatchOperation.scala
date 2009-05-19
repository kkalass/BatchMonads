package de.kalass.batchmonads.base

/**
 * Factory object for creating instances of BatchOperation.
 */
object BatchOperation {
  
  /**
   * Creates a BatchOperation instance.
   * 
   * <p>The returned instance can be used to create Operation instances. All Operation instances created
   * by this BatchOperation that are used at a corresponding position of their surrounding
   * Operation, will be executed together in one batch. The input values given at the 
   * creation of the Operation instances will be passed to the function "fkt" in one list.</p>
   * 
   * <p>The function needs to include an output value for each input value. The index of this 
   * value in the output list needs to be exactly the same as the index of the 
   * corresponding input value in the input list.</p>
   * 
   * @param fkt The function to use for mapping a list of input values into a list of output values.
   * @return a new instance of BatchOperation
   */
    def create[I, A](fkt: List[I] => List[A]) = new BatchOperation(fkt)
}

/**
 * Encapsulates a batch function and acts as a factory for Operation instances 
 * that will be batched during execution for the encapsulated batch function.
 */
final class BatchOperation[I, A] private (val fkt: List[I] => List[A]) {
  
    /**
     * Creates an Operation instance that can be sequenced into a more complex
     * operation, or directly executed with the BatchExecutor.
     * 
     * All Operation instances returned by calls to "singleOperation(...)" that 
     * are used at a corresponding position of the surrounding Operation  
     * will be grouped into one List during execution. 
     * This list will be passed back to this BatchOperation  to create the list of result values.
     * 
     * @param value the value to use as input to the BatchOperation when the operations are executed
     * @return an instance of Operation that can be used for sequencing and subsequent batched execution
     */
    def singleOperation(value: I): Operation[A] = new impl.SingleOperation[I, A](value, this)
}

