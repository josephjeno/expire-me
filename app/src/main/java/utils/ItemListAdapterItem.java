package utils;

public class ItemListAdapterItem {

    String itemName;
    String itemExpiration;
    String itemNotes;
    String itemAddedDate;
    String itemId;


    public ItemListAdapterItem(String itemName, String itemExpiration, String itemNotes, String itemAddedDate, String itemId) {
        this.itemName = itemName;
        this.itemExpiration = itemExpiration;
        this.itemNotes = itemNotes;
        this.itemAddedDate = itemAddedDate;
        this.itemId = itemId;
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

    public String getItemNotes() {
        return itemNotes;
    }

    public void setItemNotes(String itemNotes) {
        this.itemNotes = itemNotes;
    }

    public String getItemAddedDate() {
        return itemAddedDate;
    }

    public void setItemAddedDate(String itemAddedDate) {
        this.itemAddedDate = itemAddedDate;
    }

    public String getItemId() { return itemId; }

    public void setItemId(String itemId) { this.itemId = itemId; }
}
