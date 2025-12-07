public abstract class User {
    protected String id;
    protected String hashedPassword;

    public User(String id, String hashedPassword) {
        this.id = id;
        this.hashedPassword = hashedPassword;
    }

    public String getId() {
        return id;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public abstract void viewProfile(); //polimorphism
}