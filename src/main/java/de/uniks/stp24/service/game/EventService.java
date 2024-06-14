package de.uniks.stp24.service.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.dto.EffectDto;
import de.uniks.stp24.dto.EffectSourceDto;
import de.uniks.stp24.service.Constants;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class EventService {

    @Inject
    TimerService timerService;

    EffectSourceDto event;

    private boolean eventActive = false;
    private int nextEvent;

    // TODO: Remove this test variable
    private boolean test = true;

    ObjectMapper objectMapper = new ObjectMapper();
    Random random = new Random();

    ArrayList<String> eventNames = new ArrayList<>(Arrays.asList(/* Good Events */"abundance", "crapulence", "equivEx",
            "grandExp", "reckoning", "rogerFeast", /* Bad Events */ "blackSpot", "dutchman", "foolsGold", "pestilence",
            "rumBottle", "submerge"));

    @Inject
    public EventService() {
        setNextEvent();
    }

    public void setEvent(EffectSourceDto event) {
        this.event = event;
    }

    public EffectSourceDto getEvent() {

        if (test || (nextEvent == 0 && !eventActive)) {

            int eventName = random.nextInt(0, eventNames.size());

            File jsonFile = new File(Constants.EVENT_FOLDER_NAME + eventNames.get(eventName) + "Event.json");

            try {
                // Read the JSON file
                JsonNode rootNode = objectMapper.readTree(jsonFile);
                String id = rootNode.get("id").asText();
                JsonNode effects = rootNode.get("effects");

                if (effects.isArray()) {
                    ArrayList<EffectDto> effectsDto = new ArrayList<>();
                    Iterator<JsonNode> elements = effects.elements();
                    // Iterate through the effects, adding parameters in EffectDto and adding them to the ArrayList
                    while (elements.hasNext()) {
                        JsonNode effect = elements.next();
                        String variable = effect.get("variable").asText();
                        double base = effect.get("base").asDouble();
                        double multiplier = effect.get("multiplier").asDouble();
                        double bonus = effect.get("bonus").asDouble();
                        effectsDto.add(new EffectDto(variable, base, multiplier, bonus));
                    }
                    System.out.println(Arrays.toString(effectsDto.toArray(new EffectDto[0])));
                    eventActive = true;
                    // Second parameter is an array of EffectDto
                    return new EffectSourceDto(id, effectsDto.toArray(new EffectDto[0]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // if no event can occur
        return null;
    }

    public void setNextEvent() {
        nextEvent = random.nextInt(100, 120);
    }


    public void setEventActive(boolean eventActive) {
        this.eventActive = eventActive;
    }

    public boolean getEventActive() {
        return eventActive;
    }
}


