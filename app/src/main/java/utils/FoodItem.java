package utils;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodItem implements Parcelable {

    private Long id;
    private String name;
    private String note;
    private String dateAdded;
    private String expiryDate;


    @Override
    public int describeContents() {
        return hashCode();
    }

    public FoodItem(Parcel in){
        id = in.readLong();
        name = in.readString();
        note = in.readString();
        dateAdded = in.readString();
        expiryDate = in.readString();
    }

    public static final Creator<FoodItem> CREATOR = new Creator<FoodItem>() {
        @Override
        public FoodItem createFromParcel(Parcel in) {
            return new FoodItem(in);
        }

        @Override
        public FoodItem[] newArray(int size) {
            return new FoodItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(note);
        parcel.writeString(dateAdded);
        parcel.writeString(expiryDate);
    }

    public FoodItem(String name, String note, String dateAdded, String expiryDate) {
        this.name = name;
        this.note = note;
        this.dateAdded = dateAdded;
        this.expiryDate = expiryDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
