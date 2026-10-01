import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Library {
    public static final String SEPARATOR = ";";
    public static final int MAX_BOOKS_PER_MEMBER = 3;

    private static final String DATA_DIRECTORY = "data";
    private static final String BOOKS_FILE = DATA_DIRECTORY + "/books.txt";
    private static final String MEMBERS_FILE = DATA_DIRECTORY + "/members.txt";
    private static final String RECORDS_FILE = DATA_DIRECTORY + "/borrow_records.txt";

    public enum SearchField { TITLE, AUTHOR, CATEGORY }

    private final ArrayList<Book> books = new ArrayList<>();
    private final ArrayList<Member> members = new ArrayList<>();
    private final ArrayList<BorrowRecord> borrowRecords = new ArrayList<>();

    // ---------- Books ----------

    public void addBook(Book book) {
        if (findBookById(book.getId()) != null) {
            throw new IllegalArgumentException("A book with ID " + book.getId() + " already exists.");
        }
        books.add(book);
    }

    public Book removeBook(int bookId) {
        Book book = getBookOrThrow(bookId);
        if (book.isBorrowed()) {
            throw new IllegalStateException("\"" + book.getTitle() + "\" is currently borrowed and cannot be removed.");
        }
        books.remove(book);
        return book;
    }

    public Book findBookById(int bookId) {
        for (Book book : books) {
            if (book.getId() == bookId) {
                return book;
            }
        }
        return null;
    }

    public List<Book> searchBooks(SearchField field, String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            String value = switch (field) {
                case TITLE -> book.getTitle();
                case AUTHOR -> book.getAuthor();
                case CATEGORY -> book.getCategory();
            };
            if (value.toLowerCase().contains(lowerKeyword)) {
                results.add(book);
            }
        }
        return results;
    }

    private Book getBookOrThrow(int bookId) {
        Book book = findBookById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("No book found with ID " + bookId + ".");
        }
        return book;
    }

    // ---------- Members ----------

    public void addMember(Member member) {
        if (findMemberById(member.getId()) != null) {
            throw new IllegalArgumentException("A member with ID " + member.getId() + " already exists.");
        }
        members.add(member);
    }

    public Member removeMember(int memberId) {
        Member member = getMemberOrThrow(memberId);
        if (countActiveBorrows(memberId) > 0) {
            throw new IllegalStateException(member.getFullName() + " still has borrowed books and cannot be removed.");
        }
        members.remove(member);
        return member;
    }

    public Member findMemberById(int memberId) {
        for (Member member : members) {
            if (member.getId() == memberId) {
                return member;
            }
        }
        return null;
    }

    public List<Member> searchMembers(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        List<Member> results = new ArrayList<>();
        for (Member member : members) {
            if (member.getFullName().toLowerCase().contains(lowerKeyword)
                    || member.getEmail().toLowerCase().contains(lowerKeyword)) {
                results.add(member);
            }
        }
        return results;
    }

    private Member getMemberOrThrow(int memberId) {
        Member member = findMemberById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("No member found with ID " + memberId + ".");
        }
        return member;
    }

    // ---------- Borrowing ----------

    public BorrowRecord borrowBook(int bookId, int memberId) {
        Book book = getBookOrThrow(bookId);
        Member member = getMemberOrThrow(memberId);
        if (book.isBorrowed()) {
            throw new IllegalStateException("\"" + book.getTitle() + "\" is already borrowed.");
        }
        if (countActiveBorrows(memberId) >= MAX_BOOKS_PER_MEMBER) {
            throw new IllegalStateException(member.getFullName() + " has reached the limit of "
                    + MAX_BOOKS_PER_MEMBER + " books.");
        }
        BorrowRecord record = new BorrowRecord(bookId, memberId, LocalDate.now());
        borrowRecords.add(record);
        book.setBorrowed(true);
        return record;
    }

    public BorrowRecord returnBook(int bookId) {
        Book book = getBookOrThrow(bookId);
        if (!book.isBorrowed()) {
            throw new IllegalStateException("\"" + book.getTitle() + "\" is not currently borrowed.");
        }
        BorrowRecord record = findActiveRecord(bookId);
        if (record == null) {
            throw new IllegalStateException("No active borrow record found for book ID " + bookId + ".");
        }
        record.markAsReturned(LocalDate.now());
        book.setBorrowed(false);
        return record;
    }

    private BorrowRecord findActiveRecord(int bookId) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getBookId() == bookId && !record.isReturned()) {
                return record;
            }
        }
        return null;
    }

    private int countActiveBorrows(int memberId) {
        int count = 0;
        for (BorrowRecord record : borrowRecords) {
            if (record.getMemberId() == memberId && !record.isReturned()) {
                count++;
            }
        }
        return count;
    }

    // ---------- Display ----------

    public void showBooks() {
        printBooks(books);
    }

    public void printBooks(List<Book> bookList) {
        if (bookList.isEmpty()) {
            System.out.println("No books to show.");
            return;
        }
        System.out.println(String.format(Book.TABLE_FORMAT, "ID", "Title", "Author", "Category", "Year", "Status"));
        for (Book book : bookList) {
            System.out.println(book);
        }
    }

    public void showMembers() {
        printMembers(members);
    }

    public void printMembers(List<Member> memberList) {
        if (memberList.isEmpty()) {
            System.out.println("No members to show.");
            return;
        }
        System.out.println(String.format(Member.TABLE_FORMAT, "ID", "Name", "Email"));
        for (Member member : memberList) {
            System.out.println(member);
        }
    }

    public void showBorrowedBooks() {
        boolean found = false;
        for (BorrowRecord record : borrowRecords) {
            if (!record.isReturned()) {
                printRecord(record);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books are currently borrowed.");
        }
    }

    private void printRecord(BorrowRecord record) {
        Book book = findBookById(record.getBookId());
        Member member = findMemberById(record.getMemberId());
        String status = record.isOverdue() ? "OVERDUE (" + record.getDaysLate() + " days)" : "On time";

        System.out.println("Book:        " + (book == null ? "Unknown" : book.getTitle()) + " (ID: " + record.getBookId() + ")");
        System.out.println("Member:      " + (member == null ? "Unknown" : member.getFullName()) + " (ID: " + record.getMemberId() + ")");
        System.out.println("Borrow Date: " + record.getBorrowDate());
        System.out.println("Due Date:    " + record.getDueDate());
        System.out.println("Status:      " + status);
        System.out.println("--------------------------------");
    }

    // ---------- File I/O ----------

    public void saveData() throws IOException {
        new File(DATA_DIRECTORY).mkdirs();
        writeLines(BOOKS_FILE, books.stream().map(Book::toFileLine).toList());
        writeLines(MEMBERS_FILE, members.stream().map(Member::toFileLine).toList());
        writeLines(RECORDS_FILE, borrowRecords.stream().map(BorrowRecord::toFileLine).toList());
    }

    public String getDataLocation() {
        return new File(DATA_DIRECTORY).getAbsolutePath();
    }

    public void loadData() {
        books.clear();
        members.clear();
        borrowRecords.clear();
        readLines(BOOKS_FILE, line -> addBook(Book.fromFileLine(line)));
        readLines(MEMBERS_FILE, line -> addMember(Member.fromFileLine(line)));
        readLines(RECORDS_FILE, line -> borrowRecords.add(BorrowRecord.fromFileLine(line)));
    }

    private void writeLines(String fileName, List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private void readLines(String fileName, Consumer<String> lineHandler) {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    lineHandler.accept(line);
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    System.out.println("Skipping invalid line " + lineNumber + " in " + fileName + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read " + fileName + ": " + e.getMessage());
        }
    }
}
