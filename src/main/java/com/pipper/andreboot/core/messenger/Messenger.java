package com.pipper.andreboot.core.messenger;

/**
 * <p>Abstraction for sending messages between the various componentes of the application.
 * Implementations are responsible for the guranteed delivery of the message.</p>

 */
public interface Messenger {

    void send(String aRoutingKey, Object aMessage);
}

