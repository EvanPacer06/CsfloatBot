import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;


public class CsfloatBot{

    //category, type, min_float, max_float, min_price, max_price, def_index, limit, paint_index
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
    private static String api_key = "xKowFyRcypK-3SzbkYLTwDz8SI0jRsR-";

    private static boolean first = true;
    private static String defaultURL = "https://csfloat.com/api/v1/listings?";

    public static void HandleCases(String type, String value){
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

    public static void main(String[] args){
        String key;
        String value;
        
        if(args.length >= 1){
            for(int i = 0; i < args.length - 1; i += 2){
                key = args[i];
                value = args[i+1];

                switch(key){
                    case "category":
                        category = value;
                        HandleCases(key, value);
                        break;
                    case "type":
                        type = value;
                        HandleCases(key, value);
                        break;
                    case "min_float":
                        min_float = value;
                        HandleCases(key, value);
                        break;
                    case "max_float":
                        max_float = value;
                        HandleCases(key, value);
                        break;
                    case "min_price":
                        min_price = value;
                        HandleCases(key, value);
                        break;
                    case "max_price":
                        max_price = value;
                        HandleCases(key, value);
                        break;
                    case "def_index":
                        def_index = value;
                        HandleCases(key, value);
                        break;
                    case "paint_index":
                        paint_index = value;
                        HandleCases(key, value);
                        break;
                    case "limit":
                        limit = value;
                        HandleCases(key, value);
                        break;
                    default:
                        System.out.print("You didn't put any args");
                        break;
                }
            }
        }
        System.out.println("Requesting: " + defaultURL);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(defaultURL)).header("Authorization", api_key).header("Accept", "application/json").header("User-Agent", "CsfloatBot/1.0").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();

            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray data = root.getAsJsonArray("data");
            
            for(int i = 0; i < Integer.parseInt(limit); i++){
                JsonObject listing = data.get(i).getAsJsonObject();
                JsonObject listingItem = listing.getAsJsonObject("item");
                String name = listingItem.get("market_hash_name").getAsString();
                String floatValue = listingItem.get("float_value").getAsString();
                int p = listing.get("price").getAsInt();
                String created = listing.get("created_at").getAsString();

                System.out.println(i + ")-----------------");
                System.out.println("Skin: " + name);
                System.out.println("Float: " + floatValue);
                System.out.println("Price(cents): " + p);
                System.out.println("Since: " + created);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}