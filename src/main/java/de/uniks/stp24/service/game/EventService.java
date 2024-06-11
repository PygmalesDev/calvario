package de.uniks.stp24.service.game;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;

public class EventService {

    @Inject
    TimerService timerService;

    ObjectMapper objectMapper;

}
