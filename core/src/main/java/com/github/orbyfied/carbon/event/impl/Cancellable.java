package com.github.orbyfied.carbon.event.impl;

/**
 * An event that can be cancelled.
 */
public interface Cancellable {

    /**
     * Sets the cancel status of the event.
     * @param b True/false.
     */
    void setCancelled(boolean b);

    /**
     * Gets the cancel status of the event.
     * @return True/false.
     */
    boolean isCancelled();

}
