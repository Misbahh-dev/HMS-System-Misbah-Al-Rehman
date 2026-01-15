package model;

public abstract class Person {

    // Common attributes shared by all person entities
    protected String id;        // Unique person identifier
    protected String name;      // Display name of person
    protected String phone;     // Contact telephone number
    protected String email;     // Contact email address

    // Default constructor for object instantiation
    public Person() { }

    // Primary constructor with core person attributes
    public Person (String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    // Accessor and mutator methods for person data
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
       //Made By Misbah Al Rehman. SRN: 24173647
    // String representation for display and debugging
    @Override
    public String toString() {
        return id + " - " + name;
    }
}