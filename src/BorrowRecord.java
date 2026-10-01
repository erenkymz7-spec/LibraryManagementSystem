import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowRecord {
    public static final int LOAN_PERIOD_DAYS = 14;
    private static final String NO_DATE = "-";
    private static final int FIELD_COUNT = 5;

    private final int bookId;
    private final int memberId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public BorrowRecord(int bookId, int memberId, LocalDate borrowDate) {
        this(bookId, memberId, borrowDate, borrowDate.plusDays(LOAN_PERIOD_DAYS), null);
    }

    public BorrowRecord(int bookId, int memberId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public int getBookId() {
        return bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public void markAsReturned(LocalDate date) {
        this.returnDate = date;
    }

    public long getDaysLate() {
        LocalDate endDate = isReturned() ? returnDate : LocalDate.now();
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, endDate));
    }

    public boolean isOverdue() {
        return !isReturned() && getDaysLate() > 0;
    }

    public String toFileLine() {
        String returnText = isReturned() ? returnDate.toString() : NO_DATE;
        return String.join(Library.SEPARATOR, String.valueOf(bookId), String.valueOf(memberId),
                borrowDate.toString(), dueDate.toString(), returnText);
    }

    public static BorrowRecord fromFileLine(String line) {
        String[] parts = line.split(Library.SEPARATOR);
        if (parts.length != FIELD_COUNT) {
            throw new IllegalArgumentException("Invalid borrow record line: " + line);
        }
        LocalDate returnDate = parts[4].equals(NO_DATE) ? null : LocalDate.parse(parts[4]);
        return new BorrowRecord(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
                LocalDate.parse(parts[2]), LocalDate.parse(parts[3]), returnDate);
    }
}
