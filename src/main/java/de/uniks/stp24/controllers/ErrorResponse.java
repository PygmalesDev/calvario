package de.uniks.stp24.controllers;

public record ErrorResponse(int statusCode, String error, String[] message) {
}
