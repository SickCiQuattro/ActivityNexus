# ActivityNexus

![Java](https://img.shields.io/badge/Java-11%2B-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Built_with-Gradle-02303A?style=flat-square&logo=gradle)
![Testing](https://img.shields.io/badge/Tested_with-JUnit5-25A162?style=flat-square&logo=junit5)
![Architecture](https://img.shields.io/badge/Architecture-MVC-8A2BE2?style=flat-square)
![Console](https://img.shields.io/badge/UI-CLI_Console-4D4D4D?style=flat-square)

## About the Project

**ActivityNexus** is an advanced, standalone Java console application designed to manage complex category hierarchies, geographic areas, and structured exchange proposals. Developed as a comprehensive Software Engineering academic project, it solves the problem of modeling, comparing, and coordinating complex configurations between users with different roles.

A significant portion of the development (Phase B) was dedicated to **code refactoring and software quality**, focusing heavily on decoupling components via Dependency Injection and implementing rigorous black-box functional testing.

## Architecture & Engineering Highlights

This project was built strictly adhering to object-oriented design principles and modern software engineering practices:

- **Strict MVC Architecture:** Clear separation of concerns between domain logic (Model), user interaction (View), and application flow (Controller).
- **Design Patterns (GoF & GRASP):** \* _Factory Method & Singleton:_ For robust multi-role user creation and management.
    - _State Pattern:_ To handle the lifecycle of proposals (Pending, Approved, Rejected).
    - _Observer Pattern:_ Implemented for internal state notifications.
    - _Controller as a Facade:_ Centralizing system operations.
- **Test-Driven Refactoring:** The codebase utilizes Dependency Injection to ensure high testability. Testing includes functional black-box test cases focusing on equivalence classes and boundary values via **JUnit 5**.
- **Advanced Data Modeling:** Implements N-ary trees (via the Scalified Tree library) to model category hierarchies and calculate intra/inter-hierarchy conversion factors dynamically.
- **Standalone Persistence:** Entire application state is maintained via Java Serialization (`.ser` files), requiring no external RDBMS setup to run.

## Key Features

- **Multi-Role User Management:** Distinct capabilities for `Configurators` (Admins) and `Fruitori` (Standard Users).
- **Hierarchy & Territory Modeling:** Create geographic areas and define deep category hierarchies with specific characteristic fields and value domains.
- **Conversion Engine:** Automated calculations for intra-hierarchy and inter-hierarchy conversion factors.
- **Proposal Lifecycle:** Users can create exchange proposals that go through a formal approval workflow.
- **Closed Exchange Sets:** Advanced validation logic to manage and balance closed sets of exchanges between users.
- **Interactive CLI:** Fully functional, menu-driven console interface.

## Tech Stack

- **Language:** Java (11+)
- **Architecture:** MVC (Model-View-Controller)
- **Testing:** JUnit 5
- **Build System:** Gradle (for tree library) & Custom Bash/Batch Scripts
- **External Libraries:** [Scalified Tree](https://github.com/scalified/tree) (v0.2.5) for N-ary tree data structures.

## Getting Started

The application is designed to be easily executable as a standalone JAR.

### Prerequisites

- Java JRE/JDK 11 or higher.

### Quick Run (Using Pre-built JAR)

1. Download the latest `ActivityNexusV4.jar` from the repository.
2. Run the application from your terminal:
    ```bash
    java -jar ActivityNexusV4.jar
    ```
    (Note: On the first run, the application will automatically create a data/ directory to handle persistence and ask you to register the first Configurator user).

### Building from Source

To compile the project and generate the executable JAR yourself:

1. Clone the repository:

    ```bash
    git clone https://github.com/SickCiQuattro/ActivityNexus.git
    ```

2. Build the Scalified Tree dependency via Gradle:

    ```bash
    cd tree
    ./gradlew build -x test
    cd ..
    ```

3. Compile the main Java project and create the JAR (Linux/macOS):

    ```bash
    cd Versione1
    # Use the provided build script
    ./build.sh
    ```

### Testing

The project includes a comprehensive suite of black-box tests. To run the JUnit 5 test suite, compile the test classes against the JUnit platform and execute them via your preferred IDE or command line.
