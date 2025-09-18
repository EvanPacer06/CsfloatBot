import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//TO-DO: discountItems.txt displays an arrayList of objects that contain (itemId,  discountPercentage)
//       Implement a timer that calls program, jittering between 900ms and 1100ms
public class CsfloatBot{

    //category, type, min_float, max_float, min_price, max_price, def_index, limit, paint_index

    //mvn --% -q clean compile exec:java -Dexec.mainClass=CsfloatBot -Dexec.args="limit 1"

    private static String category = "1";
    private static String type = "buy_now";
    private static String min_float = "";
    private static String max_float = "";
    private static String min_price = "";
    private static String max_price = "";
    private static String def_index = "";
    private static String paint_index = "";
    private static String sort_by = "";//most_recent, highest_discount(basically to see what not to buy), lowest_float
    private static String limit = "50";
    private static double discount_value = 25.0;
    private static String api_key = "xKowFyRcypK-3SzbkYLTwDz8SI0jRsR-";

    private static ArrayList<DiscountedItem> items;

    private static boolean first = true;
    private static String defaultURL = "https://csfloat.com/api/v1/listings?";

    public static void HandleURLCases(String type, String value){
        if(!value.isEmpty()){
            if(first){
                defaultURL = defaultURL + type + "=" + value;
                first = false;
            }
            else{
                defaultURL = defaultURL + "&" + type +  "=" + value;
            }
        }
    }

    public static boolean ContainsItem(JsonObject item, String asset_id){
        boolean contains = false;
        for(DiscountedItem i : items){
            if(i.getItemId().equals(asset_id)){
                contains = true;
            }
        }
        return contains;
    }

    public static int LocateMin(){
        double curMin = 100.0;
        int index = 0;
        for(int i = 0; i < items.size(); i++){
            if(items.get(i).getDiscountValue() < curMin){
                curMin = items.get(i).getDiscountValue();
                index = i;
            }
        }
        return index;
    }
        

    public static void CheckWriteToFile(JsonObject item, String name, String float_value, int p, double discount, String created, String asset_id){
        if(discount >= discount_value && !ContainsItem(item, asset_id)){
            DiscountedItem d = new DiscountedItem(item, name, float_value, p, discount, created, asset_id);
            if(items.size() < 10){
                items.add(d);
            }

            if(items.size() >= 10){
                int index = LocateMin();
                if(items.get(index).getDiscountValue() < d.getDiscountValue()){
                    items.remove(index);
                    items.add(d);
                }
            }
            try (FileWriter fw = new FileWriter("discountItems.txt", true)) 
            {
                new FileWriter("discountItems.txt", false).close();
                for(int i = 0; i < items.size(); i++){
                    fw.write("---------------------------\n" +
                             "Skin: " + items.get(i).getName() + "\n" +
                             "Float: " + items.get(i).getFloatValue() + "\n" +
                             "Price(cents): " + items.get(i).getP() + "\n" +
                             "Discount: " + items.get(i).getDiscountValue() + "\n" +
                             "Created: " + items.get(i).getCreated()+ "\n" +
                             "ID: " + items.get(i).getItemId());
                }
            }
            catch (IOException e) {
            e.printStackTrace();
            }
        }
    }
    public static void runOnce(){
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(defaultURL)).header("Authorization", api_key).header("Accept", "application/json").header("User-Agent", "CsfloatBot/1.0").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();

            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray data = root.getAsJsonArray("data");

            int n = Math.min(data.size(), Integer.parseInt(limit));

            
            for (int i = 0; i < n; i++){
                JsonObject listing = data.get(i).getAsJsonObject();
                JsonObject listingItem = listing.getAsJsonObject("item");
                String name = listingItem.get("market_hash_name").getAsString();
                String floatValue = "";
                if(listingItem.has("float_value") && !listingItem.get("float_value").isJsonNull()){
                    floatValue = listingItem.get("float_value").getAsString();
                }
                else{
                    floatValue = "N/A";
                }

                String asset_id = listingItem.get("asset_id").getAsString();

                int p = listing.get("price").getAsInt();
                int pp;
                try {
                    pp = listing.getAsJsonObject("reference").get("predicted_price").getAsInt();
                } catch (Exception e) {
                    continue;
                }
                String created = listing.get("created_at").getAsString();

                double discount;

                if (pp <= 0) {
                    break;
                } else {
                    discount = (1.0 - (double) p / (double) pp) * 100.0; // e.g., 70 means 70% off
                }

                System.out.println((i + 1) + ")-----------------");
                System.out.println("Skin: " + name);
                System.out.println("Float: " + floatValue);
                System.out.println("Price(cents): " + p);
                System.out.println("Discount: " + discount + "%");
                System.out.println("Since: " + created);
                System.out.println("ID: " + asset_id);

                CheckWriteToFile(listingItem, name, floatValue, p, discount, created, asset_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        String key;
        String value;

        items = new ArrayList<>();
        
        if(args.length >= 1){
            for(int i = 0; i < args.length - 1; i += 2){
                key = args[i];
                value = args[i+1];

                switch(key){
                    case "category":
                        category = value;
                        HandleURLCases(key, value);
                        break;
                    case "type":
                        type = value;
                        HandleURLCases(key, value);
                        break;
                    case "min_float":
                        min_float = value;
                        HandleURLCases(key, value);
                        break;
                    case "max_float":
                        max_float = value;
                        HandleURLCases(key, value);
                        break;
                    case "min_price":
                        min_price = value;
                        HandleURLCases(key, value);
                        break;
                    case "max_price":
                        max_price = value;
                        HandleURLCases(key, value);
                        break;
                    case "def_index":
                        def_index = value;
                        HandleURLCases(key, value);
                        break;
                    case "paint_index":
                        paint_index = value;
                        HandleURLCases(key, value);
                        break;
                    case "sort_by":
                        sort_by = value;
                        HandleURLCases(key,value);
                        break;
                    case "limit":
                        limit = value;
                        HandleURLCases(key, value);
                        break;
                    case "discount_value":
                        discount_value = Double.parseDouble(value);
                        break;
                    default:
                        System.out.print("You didn't put any args");
                        break;
                }
            }
        }
        System.out.println("Requesting: " + defaultURL);

        new java.util.Timer().scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                runOnce();
            }
        }, 0, 5000);
    }
}