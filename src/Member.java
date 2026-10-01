public class Member {
    public static final String TABLE_FORMAT = "%-6s %-25s %s";
    private static final int FIELD_COUNT = 4;

    private final int id;
    private String name;
    private String surname;
    private String email;

    public Member(int id, String name, String surname, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return name + " " + surname;
    }

    public String toFileLine() {
        return String.join(Library.SEPARATOR, String.valueOf(id), name, surname, email);
    }

    public static Member fromFileLine(String line) {
        String[] parts = line.split(Library.SEPARATOR);
        if (parts.length != FIELD_COUNT) {
            throw new IllegalArgumentException("Invalid member line: " + line);
        }
        return new Member(Integer.parseInt(parts[0].trim()), parts[1], parts[2], parts[3]);
    }

    @Override
    public String toString() {
        return String.format(TABLE_FORMAT, id, getFullName(), email);
    }
}
