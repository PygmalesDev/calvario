package de.uniks.stp24.service.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.dto.EffectDto;
import de.uniks.stp24.dto.EffectSourceDto;
import de.uniks.stp24.dto.EffectSourceParentDto;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class EventService {
    private static final Logger LOGGER = Logger.getLogger(EventService.class.getName());

    protected PropertyChangeSupport listeners;
    public static final String PROPERTY_REMAININGSEASONS = "remainingSeasons";
    public static final String PROPERTY_EVENT = "event";
    public static final String PROPERTY_NEXTEVENT = "nextEvent";
    private volatile int remainingSeasons;
    EffectSourceParentDto event = null;
    private int nextEvent;
    ObjectMapper objectMapper = new ObjectMapper();
    Random random = new Random(1000);


    @Inject
    TimerService timerService;

    @Inject
    EmpireApiService empireApiService;
    @Inject
    Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;


ArrayList<String> eventNames = new ArrayList<>(Arrays.asList(/* Good Events */"abundance", "crapulence", "equivEx",
            "grandExp", "reckoning", "rogerFeast", /* Bad Events */ "blackSpot", "dutchman", "foolsGold", "pestilence",
            "rumBottle", "submerge"));

    Map<String, EffectSourceParentDto> eventMap = new HashMap<>();


    @Inject
    public EventService() {
        nextEvent = 1;
        this.listeners = new PropertyChangeSupport(this);
    }

    public void setEvent(EffectSourceParentDto event) {
        EffectSourceParentDto oldValue;
        if (event == this.event) {
            return;
        }
        oldValue = this.event;
        this.event = event;
        this.firePropertyChange(PROPERTY_EVENT, oldValue, event);
    }

    // Gets random a new event
    public EffectSourceParentDto getNewRandomEvent() {

        System.out.println("UNTIL NEXT EVENT: " + nextEvent);

        if (nextEvent <= 0) {

            System.out.println("NEW EVENT");

            int eventName = random.nextInt(0, eventNames.size());
            EffectSourceParentDto tmp = readEvent(eventName);
            setNextEvent();
            return tmp;
        }
        // if no event can occur
        return null;
    }

    public void setNextEvent() {
        nextEvent = random.nextInt(100, 120);
    }

    public EffectSourceParentDto getEvent() {
        return event;
    }

    // Counts down the time until the next event
    public void setNextEventTimer(int nextEvent) {

        if (nextEvent == this.nextEvent) {
            return;
        }
        final int oldValue = this.nextEvent;
        this.nextEvent = nextEvent;
        this.firePropertyChange(PROPERTY_NEXTEVENT, oldValue, nextEvent);

        setEvent(getNewRandomEvent());
         if (remainingSeasons <= 0) {
            setEvent(null);
            // If event is done reset it in Server
             subscriber.subscribe(sendEffect(),
                     result -> System.out.println("Effect sollte null sein oder: " + result),
                     error -> System.out.println("Error beim wegmachen von Effect: " + error));
        } else {
            setRemainingSeasons(getRemainingSeasons()-1);
            System.out.println("Remaining the event: " + remainingSeasons);
        }
    }

    public int getNextEventTimer() {
        return nextEvent;
    }

    public int getRemainingSeasons() {
        return remainingSeasons;
    }

    // Sets Seasons how long event last
    public void setRemainingSeasons(int remainingSeasons) {
        if (remainingSeasons == this.remainingSeasons) {
            return;
        }
        final int oldValue = this.remainingSeasons;
        this.remainingSeasons = remainingSeasons;
        this.firePropertyChange(PROPERTY_REMAININGSEASONS, oldValue, remainingSeasons);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (this.listeners != null) {
            this.listeners.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    public PropertyChangeSupport listeners() {
        if (this.listeners == null) {
            this.listeners = new PropertyChangeSupport(this);
        }
        return this.listeners;
    }

    /* Parameter eventName is index for List<String> eventNames
     * Method reads the JSONs in folder .data and creates an EffectSourceParentDto
    /* if the event has not been added to the eventMap yet */
    private @Nullable EffectSourceParentDto readEvent(int eventName) {

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

                    // Convert an ArrayList into an Array
                    EffectDto[] effectArrayDtos = new EffectDto[effectsDto.size()];
                    effectArrayDtos = effectsDto.toArray(effectArrayDtos);
                    eventMap.put(id, new EffectSourceParentDto(new EffectSourceDto[]
                            {new EffectSourceDto(id, eventType, duration, effectArrayDtos)}));
                }
            }
            setRemainingSeasons(duration);
            return eventMap.get(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception occurred", e);
        }
        // if no event can occur
        return null;
    }

    public Observable<EmpireDto> sendEffect() {
        // If event is not null, sends the event, else sends an empty Array
        return empireApiService.setEffect(tokenStorage.getGameId(), tokenStorage.getEmpireId(),
                Objects.requireNonNullElseGet(event, () -> new EffectSourceParentDto(new EffectSourceDto[]{})));
    }
}