package ca.cmpt213.a4.client.control;

import ca.cmpt213.a4.client.model.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Keeps track of Consumable list information
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
public class ConsumableManager {

    private List<Consumable> list;

    // Possible expiry date periods
    public enum ExpiryPeriod {
        ALL,
        UNEXPIRED,
        EXPIRED,
        EXPIRING_WITHIN_WEEK
    }

    /**
     * Default constructor
     */
    public ConsumableManager() {
        list = new ArrayList<>();
    }

    /**
     * Get the consumable list in this object
     * @return Consumable list
     */
    public List<Consumable> getConsumableList() {
        return list;
    }

    /**
     * Set the consumable list in this object
     * @param inList Input list
     */
    public void setConsumableList(List<Consumable> inList) {
        list = inList;
    }

    /**
     * Add consumable to list and then sort
     * Could ideally modify to add in sorted order in the future
     * @param item Consumable item
     */
    public void addToConsumableList(Consumable item) {
        list.add(item);
        Collections.sort(list);
    }

    /**
     * Sort the consumable list in this object
     */
    public void sortConsumableList() {
        Collections.sort(list);
    }

    /**
     * Get a sublist of a Consumables list with only objects of a particular ExpiryPeriod
     * @param inList Input list
     * @param exp ExpiryPeriod
     * @return Filtered list
     */
    public static List<Consumable> getSublistWithExpiryCriteria(List<Consumable> inList, ExpiryPeriod exp) {
        List<Consumable> newList = new ArrayList<>();

        // Traverse input list
        for (Consumable c : inList) {
            int timeToExp = c.timeUntilExpiryInt();
            // Forge new list depending on ExpiryPeriod
            switch (exp) {
                case ALL:
                    newList = inList;
                    break;
                case EXPIRED:
                    if (timeToExp < 0)
                        newList.add(c);
                    break;
                case UNEXPIRED:
                    if (timeToExp >= 0)
                        newList.add(c);
                    break;
                case EXPIRING_WITHIN_WEEK:
                    if (timeToExp >= 0 && timeToExp <= 7)
                        newList.add(c);
                    break;
            }
        }
        return newList;
    }

    /**
     * Get a list of String objects with the toString for each Consumable in a given list
     * @param objList Input consumable list
     * @return toString list
     */
    public static List<String> getToStringList(List<Consumable> objList) {
        List<String> stringList = new ArrayList<>();
        for (Consumable i : objList) {
            stringList.add(i.toString());
        }
        return stringList;
    }

    /**
     * Get the text warning for an empty list of a particular ExpiryPeriod
     * @param exp ExpiryPeriod
     * @return String message
     */
    public static String getEmptyListWarning(ExpiryPeriod exp) {

        // Determine keyword based on given ExpiryPeriod
        String key = "";
        switch (exp) {
            case ALL -> key = "items";
            case EXPIRED -> key = "expired items";
            case UNEXPIRED -> key = "non-expired items";
            case EXPIRING_WITHIN_WEEK -> key = "items expiring within 7 days";
        }

        // Return message
        return "No " + key + " to show.";
    }

    /**
     * Convert Consumable list into json object array
     * @param list Consumable list
     * @return json object as string
     */
    public static String serializeConsumableList(List<Consumable> list) throws JsonSyntaxException {

        /* Note: Using toJson directly on a List<Consumable> only serializes Consumable fields.
         As a workaround, we will individually serialize each list object as a Food/Drink,
         and then bundle all into one json object and return that.
         */

        // Get Gson object
        Gson gs = customGsonBuilder();

        // Traverse list, create objects, add to json array
        JsonArray arr = new JsonArray();
        for (Consumable item : list) {
            arr.add(JsonParser.parseString(gs.toJson(item)));
        }

        // Return serialized list
        return arr.toString();
    }

    /**
     * Convert json string array into valid list of consumables
     * @precondition jsonString argument is valid json array object
     * @param jsonString json object array
     * @return Proper list of consumables
     */
    public static List<Consumable> deserializeConsumableList(String jsonString) throws JsonSyntaxException {

        // Get Gson object
        Gson gs = customGsonBuilder();

        // Create new list
        List<Consumable> newList = new ArrayList<>();

        // Case for empty string
        if (jsonString == null || jsonString.isBlank())
            return newList;

        // Parse string as json array
        JsonArray arr = JsonParser.parseString(jsonString).getAsJsonArray();

        // Traverse json objects and add to list
        for (JsonElement item : arr) {
            // Parse the item as a JsonObject
            JsonObject obj = item.getAsJsonObject();

            // Get specific object
            if (obj.has("weight")) { // Food
                newList.add(gs.fromJson(item, Food.class));
            } else if (obj.has("volume")) { // Drink
                newList.add(gs.fromJson(item, Drink.class));
            }
        }

        return newList;
    }

    /**
     * Receive Consumable item, return serialized JsonObject
     * @param item Consumable item
     * @return Serialized String
     */
    public static String serializeConsumableItem(Consumable item) throws JsonSyntaxException {

        // Get Gson object
        Gson gs = customGsonBuilder();

        // Create
        JsonObject el = JsonParser.parseString(gs.toJson(item)).getAsJsonObject();

        // Return serialized list
        return el.toString();
    }

    /**
     * Receive String JsonObject, return Consumable item
     * @param jsonElem
     * @return
     */
    public static Consumable deserializeConsumableItem(String jsonElem) throws JsonSyntaxException {

        // Get Gson object
        Gson gs = customGsonBuilder();

        // Parse string as json object
        JsonObject obj = JsonParser.parseString(jsonElem).getAsJsonObject();

        // Parse JsonObject as Consumable
        if (obj.has("weight")) { // Food
            return gs.fromJson(obj, Food.class);
        } else if (obj.has("volume")) { // Drink
            return gs.fromJson(obj, Drink.class);
        } else {
            return null;
        }
    }

    /**
     * Return gson builder adapted to LocalDate objects
     * (as given in assignment instructions)
     * @return Gson object
     */
    private static Gson customGsonBuilder() {
        return new GsonBuilder().registerTypeAdapter(LocalDate.class,
                new TypeAdapter<LocalDate>() {
                    @Override
                    public void write(JsonWriter jsonWriter,
                                      LocalDate localDate) throws IOException {
                        jsonWriter.value(localDate.toString());
                    }

                    @Override
                    public LocalDate read(JsonReader jsonReader) throws IOException {
                        return LocalDate.parse(jsonReader.nextString());
                    }
                }).create();
    }

}