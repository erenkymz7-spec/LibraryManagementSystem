# Library Management System

## About

A console-based library management application written in plain Java. It manages the books, members and borrowing records of a small library, and keeps its data in text files between runs.

I built this project while learning Java, to practise object-oriented design, collections, exception handling and file I/O in a real application instead of isolated exercises.

## Features

- **Book management**: add, remove and list books (duplicate IDs are rejected)
- **Member management**: add, remove, search and list members
- **Borrow and return system**: 14-day loan period, up to 3 books per member
- **Book search**: by ID, title, author or category (case-insensitive, partial match)
- **Borrowing records**: borrow date, due date, return date and overdue detection
- **File-based data storage**: data is loaded on startup and saved after every change
- **Input validation**: numbers, year ranges, empty fields and email format are checked
- **Exception handling**: invalid input, missing books or members and file errors never crash the program

## Technologies

```text
Java 17+
OOP (classes, objects, encapsulation, constructors)
ArrayList / Collections
LocalDate (java.time)
File I/O (BufferedReader / BufferedWriter)
Exception Handling
```

No external libraries or build tools are used.

## Project Structure

```text
LibraryManagementSystem/
│
├── src/
│   ├── Main.java          # Entry point, menus and user input validation
│   ├── Library.java       # Core logic: books, members, borrowing, save/load
│   ├── Book.java          # Book model
│   ├── Member.java        # Member model
│   └── BorrowRecord.java  # One borrowing transaction (dates, overdue check)
│
├── data/
│   ├── books.txt          # id;title;author;category;year;borrowed
│   ├── members.txt        # id;name;surname;email
│   └── borrow_records.txt # bookId;memberId;borrowDate;dueDate;returnDate
│
├── README.md
└── .gitignore
```

`Main` only handles user interaction. All rules (duplicate IDs, availability checks, borrow limits) live in `Library`, which reports problems by throwing exceptions that `Main` catches and shows to the user.

## How to Run

Requires **Java 17 or newer**. Run the commands from the project root so the `data/` folder is found:

```bash
javac -d out src/*.java
java -cp out Main
```

## Example

```text
================================
       LIBRARY MANAGEMENT
================================
1 - Book Management
2 - Member Management
3 - Borrow Book
4 - Return Book
5 - Search Book
6 - Show All Books
7 - Show Borrowed Books
8 - Save Data
0 - Exit
Choose an option: abc
Invalid input. Please enter a number.
Choose an option: 3
Enter book ID: 2
Enter member ID: 1001
Book borrowed successfully.
Borrow Date: 2026-10-01
Due Date:    2026-10-15

Choose an option: 5
1 - Search by ID
2 - Search by Title
3 - Search by Author
4 - Search by Category
Choose an option: 3
Enter author: orwell
2 book(s) found:
ID    Title                          Author                 Category        Year   Status
2     1984                           George Orwell          Dystopian       1949   Borrowed
5     Animal Farm                    George Orwell          Fiction         1945   Available

Choose an option: 4
Enter book ID: 2
Book returned successfully.
Return Date: 2026-10-01
```

## Future Improvements

- Store data in a MySQL database instead of text files
- Graphical user interface (JavaFX or Swing)
- REST API with Spring Boot
- Librarian login / authentication
- Late return fines and borrowing history per member
- Unit tests with JUnit
