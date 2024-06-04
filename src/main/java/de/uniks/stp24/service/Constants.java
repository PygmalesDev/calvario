package de.uniks.stp24.service;

import de.uniks.stp24.model.Resource;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Constants {
    // Folder
    public static final String DATA_FOLDER_NAME = "data";
    public static final Path DATA_FOLDER = Path.of(DATA_FOLDER_NAME);

    // Description and names for Empires
    public static final Map<String, String[]> empireTemplatesEnglish = Map.ofEntries(
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

    public static final Map<String, String[]> empireTemplatesGerman = Map.ofEntries(
            Map.entry("Prefix", new String[]{
                    "Gierige", "Böse", "Siegessichere", "Alte", "Schelmische", "Tote", "Amateurhafte", "Berühmte", "Hungrige",
                    "Übergriffige", "Hoffnungsvolle", "Ängstliche", "Abscheuliche", "Gemeine", "Stinkende", "Wütende"}),
            Map.entry("Type", new String[]{
                    "Piraten", "Seeräuber", "Diebe", "Händler", "Korsaren", "Entdecker", "Abenteurer", "Reisende",
                    "Wanderer", "Helden", "Freibeuter", "Kriminelle", "Banditen", "Einbrecher", "Schützen", "Bösewichte", "Schurken",
                    "Fürsten", "Herzöge", "Könige", "Herrscher", "Barone", "Kapitäne"}),
            Map.entry("Suffix", new String[]{
                    "Eisigen", "Schattigen", "Stählernen", "Eisernen", "Versunkenen", "Chaotischen", "Korrupten", "Steinigen", "Zeitlosen", "Gefährlichen", "Untergegangenen",
                    "Roten", "Braunen", "Feuerroten", "Azurblauen", "Saphirblauen", "Goldenen", "Dampfenden", "Geheimen", "Schwarzen", "Dunkelen", "Kryptischen",
                    "Trüben", "Düsteren", "Sonnenlosen", "Tiefen", "Mysteriösen"}),
            Map.entry("Definition", new String[]{
                    "Festungen", "Verstecke", "Buchten", "Strände", "Inseln", "Riffe", "Archipele", "Häfen", "Enklaven", "Imperien",
                    "Reiche", "Klippen", "Herrschaftsgebiete", "Zufluchtsorte", "Felsen", "Meere", "Seen", "Lagunen", "Gewässer"}),
            Map.entry("Description", new String[]{
                    "Dieses rücksichtsloses Piratenkartell ist bekannt für seine skrupellosen Taktiken und unersättliche Gier auf hoher See.",
                    "Berüchtigte Plünderer, die den Seeleuten Angst einjagen und durch Piraterie und Einschüchterung Reichtümer anhäufen.",
                    "Ein schattenhaftes Syndikat von Freibeutern, deren Flagge einen Totenkopf und gekreuzte Krummsäbel trägt und ihre gnadenlose Jagd nach Reichtümern symbolisiert.",
                    "Legenden der maritimen Unterwelt, deren Name unter Seeleuten voller Angst geflüstert wird, während sie die Wellen mit eiserner Faust beherrschen.",
                    "In den gefährlichen Gewässern des Calvario ist diese Piratenallianz dafür bekannt, Reichtümer zu horten und überall eine Schneise der Verwüstung zu hinterlassen.",
                    "Unter dieser schwarzen Flagge vereinen sich Piraten in einer erbitterten Bruderschaft, die nach Reichtum und Ruhm über die grenzenlose Weite des Ozeans strebt.",
                    "Im gefährlichen Reich der Sieben Meere gilt dies Piratenbande als Meister des Plünderns, gefürchtet für ihre gnadenlosen Überfälle und uneinnehmbaren Verstecke.",
                    "Unter dieser schwarzen Flagge wird der Ozean zur Bühne für gewagte Abenteuer und unermessliche Reichtümer, wo jede Welle Geschichten ihrer legendären Taten flüstert.",
                    "Gefürchtete Piraten und Meister der maritimen Unterwelt, regieren mit eiserner Faust und einem unstillbaren Durst nach Gold.",
                    "Mit ihren gerissenen Taktiken und unersättlicher Gier jagen die Mitglieder dieser Freibeutergruppe den Seeleuten Angst ein und beherrschen die Ozeane.",
                    "Angeführt von gerissenen Kapitänen dominieren diese Seeräuber die Handelsrouten und hinterlassen eine Spur von Angst und Neid bei rivalisierenden Seefahrern.",
                    "In der schattenhaften Welt der Piraterie versetzt der Name {NAME} Kaufleute in Angst und Schrecken, während sie in ihren verborgenen Inselverstecken unvorstellbare Schätze horten."
            })
    );

    public static final Map<String, String> resourceTranslation = Map.of(
            "credits", "resource.doubloons",
            "minerals", "resource.sparklingGeode",
            "population", "resource.crewmates",
            "energy", "resource.gunpowder",
            "food", "resource.provisions",
            "fuel","resource.coal",
            "research", "resource.scoutReports",
            "alloys", "resource.gemmyAlloys",
            "consumer_goods","resource.rum"
    );

    public static final Map<String, String> siteTranslation = Map.of(
            "city", "site.village",
            "energy", "site.thaumaturgy",
            "mining","site.mining",
            "agriculture","site.harvesting",
            "industry", "site.production",
            "research", "site.expedition",
            "ancient_fondry","site.epoch",
            "ancient_factory","site.merchant",
            "ancient_refinery", "site.coalmine"
    );

    public static final Map<String, String> buildingTranslation = Map.of(
            "exchange", "building.seaside",
            "power_plant","building.theurgy",
            "mine","building.resonatingDelves",
            "farm","building.farmside",
            "research_lab","building.soutHub",
            "foundry","building.alloySmeltery",
            "factory","building.chophouse",
            "refinery","coalQuarry"
    );
}
