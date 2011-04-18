package {
    /**
     * <p>Title: WaitListener</p>
     * <p>Description: A Listener to the Waiter class.<br>
     * Its only methode receive 4 kind of messages:
     * <ul>
     * <li>INIT			when the Waiter is created.</li>
     * <li>START		when the Waiter starts.</li>
     * <li>INTERRUPTED	when the Waiter is interrupted.</li>
     * <li>END			when the Waiter ends.</li>
     * </p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public interface WaitListener
    {
        /**
         * The Waiter has just been created using its constructor.
         */
        public const INIT:int= 0;
        
        /**
         * The Waiter starts its loop, after a specified delay.
         */
        public const START:int= 1;
        
        /**
         * The Waiter has been interrupted by a Thread interruption or by setting its m_isInterrupted field to true.
         */
        public const INTERRUPTED:int= 2;
        
        /**
         * The Waiter has finished its life cycle. It is dead of natural die or by a call to finish()
         */
        public const END:int= 3;
        
        /**
         * The associated Waiter has changed its state.
         * @param params	An Object table to pass useful parameter that where previously stored during the Waiter creation.
         * @param state		A state identifier that can be one of {INIT,START,INTERRUPTED,END}.
         */
        public function stateChanged( params:Array, state:int):void ;
    }
}