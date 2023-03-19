package ca.cmpt213.a4.client.control;

import ca.cmpt213.a4.client.model.Consumable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Communicates with a server via a given URL to send/receive Consumable-related info
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
public class ConsumableServerCommunicator {

    // Server base URL
    private URL baseURL;

    /**
     * Constructor (requires ip & port)
     * @param baseURLString e.g. "localhost:8080"
     */
    public ConsumableServerCommunicator(String baseURLString) {

        // Establish base URL
        try {
            this.baseURL = new URL(baseURLString);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check the server status and return boolean
     * @return boolean
     */
    public boolean checkStatus() {
        try {
            // Create connection object
            URL fullURL = new URL(baseURL, "ping");
            HttpURLConnection server = (HttpURLConnection)fullURL.openConnection();
            server.setRequestMethod("GET");
            server.disconnect();

            // Check response
            if (server.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;

            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fetches Consumable list
     * Returns null if an error was encountered
     * @return
     */
    public List<Consumable> listAll() {
        try {
            // Create connection object
            URL fullURL = new URL(baseURL, "listAll");
            HttpURLConnection server = (HttpURLConnection)fullURL.openConnection();
            server.setRequestMethod("GET");

            // Check response
            if (server.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader resp = new BufferedReader(new InputStreamReader(server.getInputStream()));
                String line = resp.readLine();

                List<Consumable> fetchedList = ConsumableManager.deserializeConsumableList(line);

                // Close BufferedReader and return list
                resp.close();
                server.disconnect();
                return fetchedList;

            } else {
                server.disconnect();
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * List based on template
     * Template can be: listAll, listExpired, listNonExpired, listExpiringIn7Days
     * Unused as its simpler just to listAll and filter client-sided
     * @param command String command (see above)
     * @return Desired list
     */
    public List<Consumable> listTemplate(String command) {
        try {
            // Create connection object
            URL fullURL = new URL(baseURL, command);
            HttpURLConnection server = (HttpURLConnection)fullURL.openConnection();
            server.setRequestMethod("GET");

            // Check response
            if (server.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader resp = new BufferedReader(new InputStreamReader(server.getInputStream()));
                String line = resp.readLine();

                List<Consumable> fetchedList = ConsumableManager.deserializeConsumableList(line);

                // Close BufferedReader and return list
                resp.close();
                server.disconnect();
                return fetchedList;

            } else {
                server.disconnect();
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Add an item to the server list
     *    There are probably arguments for returning a JsonObject instead of a boolean here,
     *    but for my use case a boolean was more useful.
     *    As I did with removeItem(), I could always switch if necessary.
     * @param item
     * @return
     */
    public boolean addItem(Consumable item) {
        try {
            // Create connection object
            URL fullURL = new URL(baseURL, "addItem");
            HttpURLConnection server = (HttpURLConnection)fullURL.openConnection();
            server.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            server.setRequestProperty("Accept", "application/json");
            server.setRequestMethod("POST");
            server.setDoOutput(true);
            server.setDoInput(true);

            // Send request
            JsonObject obj = JsonParser.parseString(ConsumableManager.serializeConsumableItem(item)).getAsJsonObject();
            OutputStream os = server.getOutputStream();
            os.write(obj.toString().getBytes("UTF-8"));

            // If add was successful, return true
            if (server.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                BufferedReader resp = new BufferedReader(new InputStreamReader(server.getInputStream()));
                server.disconnect();
                return true;

            } else {
                // unsuccessful add
                server.disconnect();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Uses addItem to add a Food item
     * @param food
     * @return success status
     */
    public boolean addFood(Consumable food) {
        return addItem(food);
    }

    /**
     * Uses addItem to add a Drink item
     * @param drink
     * @return success status
     */
    public boolean addDrink(Consumable drink) {
        return addItem(drink);
    }

    /**
     * Remove an item and return the updated listAll JsonArray
     * @param item Item to remove
     * @return Updated list
     */
    public JsonArray removeItem(Consumable item) {

        try {
            // Create connection object
            URL fullURL = new URL(baseURL, "removeItem");
            HttpURLConnection server = (HttpURLConnection)fullURL.openConnection();
            server.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            server.setRequestProperty("Accept", "application/json");
            server.setRequestMethod("POST");
            server.setDoOutput(true);
            server.setDoInput(true);

            // Send request
            JsonObject obj = JsonParser.parseString(ConsumableManager.serializeConsumableItem(item)).getAsJsonObject();
            OutputStream os = server.getOutputStream();
            os.write(obj.toString().getBytes("UTF-8"));

            // Successful remove -> return JsonArray
            if (server.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                BufferedReader resp = new BufferedReader(new InputStreamReader(server.getInputStream()));
                String response = resp.readLine();
                server.disconnect();
                return JsonParser.parseString(response).getAsJsonArray();

            } else {
                // Unsuccessful -> return null
                server.disconnect();
                return null;
            }

        } catch (Exception e) {
            // Exception -> return null
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves the list on the server side
     * @return success status
     */
    public boolean exit() {
        try {
            // Create connection object
            URL fullURL = new URL(baseURL, "exit");
            HttpURLConnection serverConnection = (HttpURLConnection)fullURL.openConnection();
            serverConnection.setRequestMethod("GET");
            serverConnection.disconnect();

            // Check response
            return serverConnection.getResponseCode() == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            return false;
        }
    }
}
