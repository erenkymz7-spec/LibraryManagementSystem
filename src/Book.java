public class Book {
    public static final String TABLE_FORMAT = "%-5s %-30s %-22s %-15s %-6s %s";
    private static final int FIELD_COUNT = 6;

    private final int id;
    private String title;
    private String author;
    private String category;
    private int publicationYear;
    private boolean borrowed;

    public Book(int id, String title, String author, String category, int publicationYear) {
        this(id, title, author, category, publicationYear, false);
    }

    public Book(int id, String title, String author, String category, int publicationYear, boolean borrowed) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publicationYear = publicationYear;
        this.borrowed = borrowed;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public String getStatus() {
        return borrowed ? "Borrowed" : "Available";
    }

    public String toFileLine() {
        return String.join(Library.SEPARATOR, String.valueOf(id), title, author, category,
                String.valueOf(publicationYear), String.valueOf(borrowed));
    }

    public static Book fromFileLine(String line) {
        String[] parts = line.split(Library.SEPARATOR);
        if (parts.length != FIELD_COUNT) {
            throw new IllegalArgumentException("Invalid book line: " + line);
        }
        return new Book(Integer.parseInt(parts[0].trim()), parts[1], parts[2], parts[3],
                Integer.parseInt(parts[4].trim()), Boolean.parseBoolean(parts[5].trim()));
    }

    @Override
    public String toString() {
        return String.format(TABLE_FORMAT, id, title, author, category, publicationYear, getStatus());
    }
}
