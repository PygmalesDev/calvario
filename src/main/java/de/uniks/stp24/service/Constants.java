package de.uniks.stp24.service;

import java.nio.file.Path;
import java.util.Map;

public class Constants {
    // Folder
    public static final String DATA_FOLDER_NAME = "data";
    public static final Path DATA_FOLDER = Path.of(DATA_FOLDER_NAME);

    public static final Map<String, String[]> empireTemplates = Map.ofEntries(
            Map.entry("Prefix", new String[]{
                    "Greedy", "Evil", "Vicious", "Old", "Mischievous", "Dead", "Amateur", "Famous", "Hungry",
                    "Grabby", "Hopeful", "Anxious", "Hideous", "Wicked", "Stinky", "Angry"}),
            Map.entry("Type", new String[]{
                    "Pirates", "Buccaneers", "Thieves", "Traders", "Corsairs", "Explorers", "Adventurers", "Voyagers",
                    "Wanderers", "Heroes", "Charlatans", "Criminals", "Bandits", "Burglars", "Gunmen", "Baddies", "Rogues",
                    "Lords", "Dukes", "Kings", "Rulers", "Barons", "Captains"}),
            Map.entry("Suffix", new String[]{
                    "Iced", "Shadow", "Steel", "Iron", "Sunken", "Chaotic", "Corrupt", "Wicked", "Elder", "Careless", "Reckless",
                    "Red", "Brown", "Crimson", "Azure", "Sapphire", "Golden", "Steamy", "Secret", "Black", "Dark", "Cryptic",
                    "Murky", "Sombre", "Sunless", "Deep", "Misty"}),
            Map.entry("Definition", new String[]{
                    "Cartel", "Gang", "Syndicate", "Ring", "Island", "Reef", "Archipelago", "Triangle", "Enclave", "Empire",
                    "Realm", "Domain", "Reign", "Sanctuary", "Terrain", "Sea", "Lake", "Deeps", "Waters"}),
            Map.entry("Description", new String[]{
                    "A ruthless pirate cartel {NAME}, known for their cutthroat tactics and insatiable greed on the high seas.",
                    "Infamous plunderers who strike fear into the hearts of sailors, amassing wealth through piracy and intimidation",
                    "A shadowy syndicate of swashbucklers, their flag bearing the skull and crossed cutlasses, symbolizing their merciless pursuit of riches.",
                    "Legends of the maritime underworld, their name whispered in fear among sailors, as they command the waves with an iron fist wrapped in gold.",
                    "In the treacherous waters of the Calvario, {NAME} as the most feared pirate alliance, hoarding riches and leaving a trail of chaos in its wake.",
                    "Under the black flag of {NAME}, pirates unite in a fierce brotherhood, seeking fortune and infamy across the boundless expanse of the ocean.",
                    "In the treacherous domain of the Seven Seas, {NAME} stands as the sovereign of plunder, feared for their merciless raids and impenetrable hideouts.",
                    "Under the black flag of {NAME}, the oceans become a stage for daring escapades and untold riches, where every wave whispers tales of their legendary exploits.",
                    "The feared {NAME}, masters of the maritime underworld, ruled with an iron fist and a thirst for gold that knew no bounds.",
                    "With their cunning tactics and insatiable greed, the members of {NAME} strike fear into the hearts of sailors, dominating the oceans with impunity.",
                    "Led by cunning captains, the {NAME} dominate the trade routes, leaving a wake of fear and envy among rival seafarers.",
                    "In the shadowy world of piracy, the name {NAME} strikes dread into the hearts of merchants as they hoard treasures beyond imagination in their hidden island strongholds."
            })
    );
}
