package utils;

/**
 * Represents a food item that a user has previously saved in the database. Used to auto-populate
 * expiration dates when a user adds a previously added food item, expediting item entry.
 */
public class StoredFood {

    private String name;
    private Integer days;

    public StoredFood(String name, Integer days) {
        this.name = name;
        this.days = days;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
