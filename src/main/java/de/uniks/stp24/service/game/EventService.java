package de.uniks.stp24.service.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.dto.EffectDto;
import de.uniks.stp24.dto.EffectSourceDto;

import javax.inject.Inject;

public class EventService {

    @Inject
    TimerService timerService;

    EffectSourceDto event;

    ObjectMapper objectMapper;

    @Inject
    public EventService() {

    }

    public void setEvent(EffectSourceDto event) {
        this.event = event;
    }

    public EffectSourceDto getEvent() {
        // TODO: Make dynamic event generation, this is only a dummy Event (To text use ids from JSON)
        return new EffectSourceDto("abundance", new EffectDto[]{new EffectDto("gold", 100, 1, 0)});
    }

}
