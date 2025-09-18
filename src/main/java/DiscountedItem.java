import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

public class DiscountedItem {
    private JsonObject item;
    private String name;
    private String float_value;
    private int p;
    private double discount_value;
    private String created;
    private String itemId;

    public DiscountedItem(JsonObject item, String name, String float_value, int p, double discount_value, String created, String itemId){
        this.item = item;
        this.name = name;
        this.float_value = float_value;
        this.p = p;
        this.discount_value = discount_value;
        this.created = created;
        this.itemId = itemId;
    }
    public JsonObject getItem(){
        return item;
    }
    public String getName(){
        return name;
    }
    public String getFloatValue(){
        return float_value;
    }
    public int getP(){
        return p;
    }
    public double getDiscountValue(){
        return discount_value;
    }
    public String getCreated(){
        return created;
    }
    public String getItemId(){
        return itemId;
    }
    public void setItem(JsonObject item){
        this.item = item;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setFloatValue(String float_value){
        this.float_value = float_value;
    }
    public void setP(int p){
        this.p = p;
    }
    public void setDiscountValue(double discount_value){
        this.discount_value = discount_value;
    }
    public void setCreated(String created){
        this.created = created;
    }
    public void setItemId(String itemId){
        this.itemId = itemId;
    }
}
