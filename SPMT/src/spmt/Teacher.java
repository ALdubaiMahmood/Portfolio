package spmt;

/**
 * INHERITANCE:
 * Teacher extends User. It inherits getName(), getId(),
 * setName(), setId(), and toString() from User automatically.
 *
 * POLYMORPHISM:
 * Teacher overrides getRole() and returns "Teacher".
 * This is different from Student which returns "Student".
 * Same method name, different behaviour = Polymorphism.
 */
public class Teacher extends User {

    public Teacher(String name, String id) {
        super(name, id);   // calls User constructor
    }
    @Override
    public String getRole() {
        return "Teacher";
    }
}
