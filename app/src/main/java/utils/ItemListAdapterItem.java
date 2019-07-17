package utils;

public class ItemListAdapterItem {

    String itemName;
    String itemExpiration;

    public ItemListAdapterItem(String itemName, String itemExpiration) {
        this.itemName = itemName;
        this.itemExpiration = itemExpiration;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemExpiration() {
        return itemExpiration;
    }

    public void setItemExpiration(String itemExpiration) {
        this.itemExpiration = itemExpiration;
    }
}
