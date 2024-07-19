package de.uniks.stp24;

import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceBundleTest {

    @Spy
    public ResourceBundle enResources = ResourceBundle.getBundle("de/uniks/stp24/lang/main", Locale.ENGLISH);
    @Spy
    public ResourceBundle deResources = ResourceBundle.getBundle("de/uniks/stp24/lang/main", Locale.GERMAN);
    @Spy
    public ResourceBundle enGameResources = ResourceBundle.getBundle("de/uniks/stp24/lang/game", Locale.ENGLISH);
    @Spy
    public ResourceBundle deGameResources = ResourceBundle.getBundle("de/uniks/stp24/lang/game", Locale.GERMAN);
    @Spy
    public ResourceBundle enVariablResources = ResourceBundle.getBundle("de/uniks/stp24/lang/variables", Locale.ENGLISH);
    @Spy
    public ResourceBundle deVariablResources = ResourceBundle.getBundle("de/uniks/stp24/lang/variables", Locale.GERMAN);

    @Test
    public void testBothLangsHaveSameKeys() {
        Set<String> enResourcesKeys = enResources.keySet();
        Set<String> deResourcesKeys = deResources.keySet();
        assertEquals(enResourcesKeys, deResourcesKeys);

        /*
        Set<String> enGameResourcesKeys = enGameResources.keySet();
        Set<String> deGameResourcesKeys = deGameResources.keySet();
        assertEquals(enGameResourcesKeys, deGameResourcesKeys);

         */

        Set<String> enVariablResourcesKeys = enVariablResources.keySet();
        Set<String> deVariablResourcesKeys = deVariablResources.keySet();
        assertEquals(enVariablResourcesKeys, deVariablResourcesKeys);
    }
}
