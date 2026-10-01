import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final int MIN_PUBLICATION_YEAR = 1000;
    private static final String EMAIL_PATTERN = "[^@\\s]+@[^@\\s]+\\.[^@\\s]+";

    private static final Scanner scanner = new Scanner(System.in);
    private static final Library library = new Library();

    public static void main(String[] args) {
        library.loadData();
        System.out.println("Data folder: " + library.getDataLocation());
        boolean running = true;

        while (running) {
            printMainMenu();
            switch (readInt("Choose an option: ")) {
                case 1 -> bookMenu();
                case 2 -> memberMenu();
                case 3 -> borrowBook();
                case 4 -> returnBook();
                case 5 -> searchBooks();
                case 6 -> library.showBooks();
                case 7 -> library.showBorrowedBooks();
                case 8 -> saveData(true);
                case 0 -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
            if (running) {
                waitForEnter();
            }
        }

        saveData(true);
        System.out.println("Goodbye!");
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println("================================");
        System.out.println("       LIBRARY MANAGEMENT");
        System.out.println("================================");
        System.out.println("1 - Book Management");
        System.out.println("2 - Member Management");
        System.out.println("3 - Borrow Book");
        System.out.println("4 - Return Book");
        System.out.println("5 - Search Book");
        System.out.println("6 - Show All Books");
        System.out.println("7 - Show Borrowed Books");
        System.out.println("8 - Save Data");
        System.out.println("0 - Exit");
    }

    // ---------- Sub menus ----------

    private static void bookMenu() {
        System.out.println("\n--- Book Management ---");
        System.out.println("1 - Add Book");
        System.out.println("2 - Remove Book");
        System.out.println("3 - Show All Books");
        System.out.println("0 - Back");

        switch (readInt("Choose an option: ")) {
            case 1 -> addBook();
            case 2 -> removeBook();
            case 3 -> library.showBooks();
            case 0 -> { }
            default -> System.out.println("Invalid option.");
        }
    }

    private static void memberMenu() {
        System.out.println("\n--- Member Management ---");
        System.out.println("1 - Add Member");
        System.out.println("2 - Remove Member");
        System.out.println("3 - Search Member");
        System.out.println("4 - Show All Members");
        System.out.println("0 - Back");

        switch (readInt("Choose an option: ")) {
            case 1 -> addMember();
            case 2 -> removeMember();
            case 3 -> library.printMembers(library.searchMembers(readText("Enter name or email: ")));
            case 4 -> library.showMembers();
            case 0 -> { }
            default -> System.out.println("Invalid option.");
        }
    }

    // ---------- Actions ----------

    private static void addBook() {
        int id = readPositiveInt("Enter book ID: ");
        if (library.findBookById(id) != null) {
            System.out.println("A book with ID " + id + " already exists.");
            return;
        }
        String title = readText("Enter title: ");
        String author = readText("Enter author: ");
        String category = readText("Enter category: ");
        int year = readIntInRange("Enter publication year: ", MIN_PUBLICATION_YEAR, LocalDate.now().getYear());

        library.addBook(new Book(id, title, author, category, year));
        System.out.println("Book added successfully.");
        saveData(false);
    }

    private static void removeBook() {
        int id = readPositiveInt("Enter book ID to remove: ");
        try {
            Book removed = library.removeBook(id);
            System.out.println("\"" + removed.getTitle() + "\" removed successfully.");
            saveData(false);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addMember() {
        int id = readPositiveInt("Enter member ID: ");
        if (library.findMemberById(id) != null) {
            System.out.println("A member with ID " + id + " already exists.");
            return;
        }
        String name = readText("Enter name: ");
        String surname = readText("Enter surname: ");
        String email = readEmail("Enter email: ");

        library.addMember(new Member(id, name, surname, email));
        System.out.println("Member added successfully.");
        saveData(false);
    }

    private static void removeMember() {
        int id = readPositiveInt("Enter member ID to remove: ");
        try {
            Member removed = library.removeMember(id);
            System.out.println(removed.getFullName() + " removed successfully.");
            saveData(false);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void borrowBook() {
        int bookId = readPositiveInt("Enter book ID: ");
        int memberId = readPositiveInt("Enter member ID: ");
        try {
            BorrowRecord record = library.borrowBook(bookId, memberId);
            System.out.println("Book borrowed successfully.");
            System.out.println("Borrow Date: " + record.getBorrowDate());
            System.out.println("Due Date:    " + record.getDueDate());
            saveData(false);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void returnBook() {
        int bookId = readPositiveInt("Enter book ID: ");
        try {
            BorrowRecord record = library.returnBook(bookId);
            System.out.println("Book returned successfully.");
            System.out.println("Return Date: " + record.getReturnDate());
            if (record.getDaysLate() > 0) {
                System.out.println("Note: The book was returned " + record.getDaysLate() + " day(s) late.");
            }
            saveData(false);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchBooks() {
        System.out.println("\n--- Search Book ---");
        System.out.println("1 - Search by ID");
        System.out.println("2 - Search by Title");
        System.out.println("3 - Search by Author");
        System.out.println("4 - Search by Category");

        List<Book> results;
        switch (readInt("Choose an option: ")) {
            case 1 -> {
                Book book = library.findBookById(readPositiveInt("Enter book ID: "));
                results = (book == null) ? List.of() : List.of(book);
            }
            case 2 -> results = library.searchBooks(Library.SearchField.TITLE, readText("Enter title: "));
            case 3 -> results = library.searchBooks(Library.SearchField.AUTHOR, readText("Enter author: "));
            case 4 -> results = library.searchBooks(Library.SearchField.CATEGORY, readText("Enter category: "));
            default -> {
                System.out.println("Invalid option.");
                return;
            }
        }

        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            System.out.println(results.size() + " book(s) found:");
            library.printBooks(results);
        }
    }

    private static void saveData(boolean showConfirmation) {
        try {
            library.saveData();
            if (showConfirmation) {
                System.out.println("Data saved successfully.");
            }
        } catch (IOException e) {
            System.out.println("Error while saving data: " + e.getMessage());
        }
    }

    // ---------- Input helpers ----------

    private static void waitForEnter() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Please enter a number between " + min + " and " + max + ".");
        }
    }

    private static int readPositiveInt(String prompt) {
        return readIntInRange(prompt, 1, Integer.MAX_VALUE);
    }

    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("This field cannot be empty.");
            } else if (input.contains(Library.SEPARATOR)) {
                System.out.println("The character '" + Library.SEPARATOR + "' is not allowed.");
            } else {
                return input;
            }
        }
    }

    private static String readEmail(String prompt) {
        while (true) {
            String email = readText(prompt);
            if (email.matches(EMAIL_PATTERN)) {
                return email;
            }
            System.out.println("Invalid email format. Example: john@example.com");
        }
    }
}
