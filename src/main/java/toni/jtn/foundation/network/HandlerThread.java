package toni.jtn.foundation.network;

/**
 * Used by PayloadRegistrar to declare the default handling thread for registered {@link IPayloadHandler}s.
 */
public enum HandlerThread {
    /**
     * The main thread of the receiving side.
     * <p>
     * On the logical client, this is the Render Thread.
     * <p>
     * On the logical server, this is the Server Thread.
     */
    MAIN,

    /**
     * The network thread, which executes concurrently to the main thread.
     */
    NETWORK;
}