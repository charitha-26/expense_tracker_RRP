# Expense Tracker

This is a Spring Boot-based Expense Tracker application designed to help users manage their personal finances by tracking income, expenses, and generating reports.

## Features

- User registration and login
- Add, edit, and delete transactions (income/expense)
- View transaction history
- Export reports as PDF or Excel
- User settings and preferences
- Support ticket management
- Responsive UI (Thymeleaf / React)

## Technologies Used

- Java 17
- Spring Boot 3.1.0
- Spring Data JPA
- MySQL
- Thymeleaf (or React if frontend separate)
- Apache POI (Excel export)
- iTextPDF (PDF export)
- Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- MySQL database

### Setup

1. Clone the repository:
    git clone https://gitlab.com/srikeerthana_05/expense_tracker.git
    cd expense_tracker

2. Configure your database in `src/main/resources/application.properties`:
    spring.datasource.url=jdbc:mysql://localhost:3306/your_database
    spring.datasource.username=your_username
    spring.datasource.password=your_password

3. Build the project:
    mvn clean install


4. Run the application:
    mvn spring-boot:run


5. Access the app at `http://localhost:8080/`.

## Project Structure

- `src/main/java/com/financetracker` - Java source code
- `src/main/resources` - Configuration and static files
- `src/main/resources/templates` - Thymeleaf templates
- `src/main/resources/static` - CSS/JS files

## License

This project is for educational purposes.



