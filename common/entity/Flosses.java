package common.entity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Flosses {
    private Map<String, String> flossColorCodeMap = new HashMap<>();
    private void readFromFile() throws FileNotFoundException {
        // FlossConstants.json
        //Read the JSON file
        JsonElement root = new JsonParser().parse(new FileReader("FlossConstants.json"));

        //Get the content of the first map
        JsonObject object = root.getAsJsonObject();

        //Iterate over this map
        Gson gson = new Gson();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String flossNo = entry.getKey();
            String colorCode = entry.getValue().getAsJsonObject().get("hexCode").getAsString();
            flossColorCodeMap.put(flossNo, colorCode);
        }
    }

    public Flosses() throws FileNotFoundException {
        readFromFile();
    }

    public String getColorCode(String flossNo) {
        return flossColorCodeMap.getOrDefault(flossNo, "FFFFFF");
    }
}
