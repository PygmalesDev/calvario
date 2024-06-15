package de.uniks.stp24.service.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.dto.EffectDto;
import de.uniks.stp24.dto.EffectSourceDto;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

public class EventService {

    @Inject
    TimerService timerService;
    @Inject
    EmpireApiService empireApiService;
    @Inject
    Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;
    EffectSourceDto event;
    private int remainingSeasons;
    private boolean eventActive = false;
    private int nextEvent;

    ObjectMapper objectMapper = new ObjectMapper();
    Random random = new Random(1000);

    ArrayList<String> eventNames = new ArrayList<>(Arrays.asList(/* Good Events */"abundance", "crapulence", "equivEx",
            "grandExp", "reckoning", "rogerFeast", /* Bad Events */ "blackSpot", "dutchman", "foolsGold", "pestilence",
            "rumBottle", "submerge"));

    Map<String, EffectSourceDto> eventMap = new HashMap<>();


    @Inject
    public EventService() {
        nextEvent = 2;
    }

    public void setEvent(EffectSourceDto event) {
        this.event = event;
    }

    public EffectSourceDto getNewRandomEvent() {

        System.out.println("REMAINING SEASONS: " + nextEvent);

        if ((nextEvent <= 0 && !eventActive)) {
            eventActive = true;
            int eventName = random.nextInt(0, eventNames.size());
            event = readEvent(eventName);
            System.out.println(event);

            subscriber.subscribe(sendEffect(),
                    result -> System.out.println("Effect gesendet: " + result),
                    error -> System.out.println("Error beim Senden von Effect: " + error));
            setNextEvent();
            return event;
        }
        // if no event can occur
        return null;
    }

    public void setNextEvent() {
        nextEvent = random.nextInt(100, 120);
    }

    public EffectSourceDto getEvent() {
        return event;
    }

    public void countEventDown() {
        remainingSeasons--;
        nextEvent--;
        if (remainingSeasons <= 0) {
            eventActive = false;
        }
    }

    public boolean getEventActive() {
        return eventActive;
    }

    public int getRemainingSeasons() {
        return remainingSeasons;
    }

    // Parameter eventName is index for List<String> eventNames
    // Method reads the JSONs in folder .data and creates an EffectSourceDto
    // if the event has not been added to the eventMap
    private @Nullable EffectSourceDto readEvent(int eventName) {

        try {
            File jsonFile = new File(Constants.EVENT_FOLDER_NAME + eventNames.get(eventName) + "Event.json");

            // Read the JSON file
            JsonNode rootNode = objectMapper.readTree(jsonFile);
            String id = rootNode.get("id").asText();
            String eventType = rootNode.get("event_type").asText();
            int duration = rootNode.get("duration").asInt();

            // Check if the event has already been added to the eventMap
            if (!eventMap.containsKey(id)) {
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

                    // Last parameter is an array of EffectDto
                    eventMap.put(id, new EffectSourceDto(id, eventType, duration, effectsDto.toArray(new EffectDto[0])));
                    remainingSeasons = duration;
                }
            }
            return eventMap.get(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // if no event can occur
        return null;
    }

    public Observable<EmpireDto> sendEffect() {
        return empireApiService.setEffect(tokenStorage.getGameId(), tokenStorage.getEmpireId(), event);
    }
}