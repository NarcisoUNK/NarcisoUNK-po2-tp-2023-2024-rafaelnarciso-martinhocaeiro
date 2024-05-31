package pt.ipbeja.app.model;

import java.util.List;

/**
 * Message to be sent from the model to update the interface with positions and a message.
 *
 * @version 31/05/2024 (Final)
 * @authors Martinho Caeiro (23917) and Rafael Narciso (24473)
 */
public record MessageToUI(List<Position> positions, String message) {}
