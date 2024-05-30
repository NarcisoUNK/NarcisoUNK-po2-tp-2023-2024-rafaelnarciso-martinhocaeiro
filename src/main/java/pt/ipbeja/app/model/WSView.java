package pt.ipbeja.app.model;

/**
 * View interface for the application.
 * Defines a method to update the view with a message.
 *
 * @version 30/05/2024
 * @authors Martinho Caeiro (23917) and Rafael Narciso (24473)
 */
public interface WSView {

    /**
     * Updates the view with the given message.
     *
     * @param messageToUI the message containing update information
     */
    void update(MessageToUI messageToUI);

}
