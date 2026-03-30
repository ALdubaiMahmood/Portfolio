package spmt;

/**
 * ABSTRACTION:
 * This is an abstract class. It defines what every User must have
 * (name, id, getRole) but does NOT implement getRole() itself.
 * You cannot create a "new User()" directly.
 *
 * INHERITANCE:
 * Student and Teacher both extend this class and inherit
 * getName(), getId(), setName(), setId() automatically.
 */
public abstract class User {

    private String name;
    private String id;

    public User(String name, String id) {
        this.name = name;
        this.id   = id;
    }

    // Getters and Setters (Encapsulation)
    public String getName() { return name; }
    public String getId()   { return id;   }
    public void setName(String name) { this.name = name; }
    public void setId(String id)     { this.id   = id;   }
    public abstract String getRole();

    /**
     * POLYMORPHISM (method overriding)
     */
    @Override
    public String toString() {
        return getRole() + ": " + name;
    }
}
