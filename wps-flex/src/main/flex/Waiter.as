package  {
    
    /**
     * <p>Title: Waiter</p>
     * <p>Description: A Waiter is a waiting Thread that call a WaiterListener.<br>
     * It's usefull to create anonymous timer-like classes as a Server wake-up, a resize notifier,
     * a dblclick notifier or a tooltip timing manager.</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    
    public class Waiter extends Thread
    {
        /**
         * A listener that is called by this Waiter.
         */
        private var m_listener:WaitListener;
        
        /**
         * Delay before starting.
         */
        private var m_beg:int;
        
        /**
         * Time to stay alive after starting.
         */
        private var m_len:int;
        
        /**
         * Parameters that are passed to the listener.
         */
        protected   var m_params:Array;
        
        /**
         * True while this should repeat it's waiting loop.
         */
        protected   var m_loop:Boolean= true;
        
        /**
         * True if this should stop immediatly. It replace the interrupt() method that is not reliable in Java1.1!
         */
        protected   var m_isInterrupted:Boolean= false;
        
        /**
         * True if this should not execute the End step. This shortcut should be used with m_isInterrupted.
         */
        protected   var m_hasFinished:Boolean= false;
        
        /**
         * Create a Waiter that will call a listener if one of the next 4 cases happen:<br>
         * <ul>
         * <li>INIT			When this is created.</li>
         * <li>START		When this starts, after beg ms.</li>
         * <li>INTERRUPTED	When this is interrupted because m_isInterrupted is set to true.</li>
         * <li>END			When this ends, after beg + loops x len or after being interrupted and not finished.</li>
         * </ul>
         * @param listener	A listener that will received the 4 possible messages and the params array.
         * @param params	An array of value that are stored to be passed to the listener.
         * 					This is usefull to remind the initial value of a parameter that change with time (ex:mouse position).
         * @param beg		The delay in ms before the waiting loop begins.
         * @param len		The time to wait between each loop.
         */
        public function Waiter( listener:WaitListener, params:Array, beg:int, len:int)
        {
            super( "WPSWaiter" );
            
            m_listener  = listener;
            m_params    = params;
            m_beg       = beg;
            m_len       = len;
            
            m_listener.stateChanged( m_params, WaitListener.INIT );
        }
        
        /**
         * This Thread main method.
         */
        public function run():void {
            while ( m_loop )
            {
                try
                {
                    var i:int, n;
                    
                    for ( i = 0, n = m_beg / 20; i < n && !m_isInterrupted; i ++ )
                    {
                        sleep( 20);
                    }
                    
                    if ( !m_isInterrupted )
                    {
                        m_listener.stateChanged( m_params, WaitListener.START );
                    }
                    
                    do
                    {
                        m_loop = false;
                        
                        for ( i = 0, n = m_len / 20; ( i < n || m_len == -1)&& !m_isInterrupted; i ++ )
                        {
                            sleep( 20);
                        }
                    }
                    while ( m_loop );
                    
                }
                catch ( e:InterruptedException)
                {
                    m_isInterrupted = true;
                }
                
                if ( !m_hasFinished )
                {
                    m_listener.stateChanged( m_params, m_isInterrupted ? WaitListener.INTERRUPTED : WaitListener.END );
                }
            }
        }
        
        /**
         * Kills this Waiter by calling the listener END state. In fact the thread will die "quietly" later.
         */
        public function finish():void {
            m_hasFinished = true;
            m_listener.stateChanged( m_params, WaitListener.END );
        }
    }
}