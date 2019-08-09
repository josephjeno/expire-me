package utils;

/**
 * Represents a food item that a user has previously saved in the database. Used to auto-populate
 * expiration dates when a user adds a previously added food item, expediting item entry.
 */
public class StoredFood {

    private final String name;
    private final Integer days;

    public StoredFood(String name, Integer days) {
        this.name = name;
        this.days = days;
    }

    public String getName() {
        return name;
    }

// --Commented out by Inspection START (08-Aug-19 22:57):
//    public void setName(String name) {
//        this.name = name;
//    }
// --Commented out by Inspection STOP (08-Aug-19 22:57)

    public Integer getDays() {
        return days;
    }

// --Commented out by Inspection START (08-Aug-19 22:57):
//    public void setDays(Integer days) {
//        this.days = days;
//    }
// --Commented out by Inspection STOP (08-Aug-19 22:57)
}
