🏨 Desktop Hotel Reservation System
A comprehensive, object-oriented desktop application for hotel management, developed as a group project for the CSE241 Object-Oriented Computer Programming course at the Faculty of Engineering - Ain Shams University.
📑 Table of Contents
Overview
Key Features
Architecture & OOP Design
Technology Stack
Milestones
Installation & Setup
Contributors
📖 Overview
This project is a fully functional Desktop Hotel Reservation System built entirely in Java. It is designed to handle the complete lifecycle of a hotel stay, from guest registration and room browsing to reservation management, checkout, and invoicing. The system is built with a strong emphasis on core Object-Oriented Programming (OOP) principles and features a modern, responsive Graphical User Interface (GUI) powered by JavaFX.
✨ Key Features
👤 Guest Portal
Authentication: Secure registration and login with input validation.
Room Browsing: View available rooms dynamically filtered by type (Single, Double, Suite) and specific amenities (WiFi, TV, Mini-bar).
Reservation Management: Easily make, view, or cancel room reservations.
Checkout & Billing: Process checkouts with detailed invoices supporting multiple payment methods (Cash, Credit Card, Online).
🛎️ Staff Dashboard (Admin & Receptionist)
Role-Based Access: Distinct privileges for Admins and Receptionists.
Admin Controls: Full CRUD (Create, Read, Update, Delete) capabilities for managing rooms, room types, and amenities.
Receptionist Duties: Dedicated tools for managing guest check-ins and check-outs, handling reservations, and overseeing daily hotel operations.
🏗️ Architecture & OOP Design
The backend is meticulously structured using encapsulation, inheritance, and polymorphism.
Entity Hierarchies: Staff members (Admins and Receptionists) inherit from a Staff parent class to maximize code reuse.
Data Integrity: Strict encapsulation with private fields, validated setters, and custom error handling (e.g., RoomNotAvailableException) ensures robust data management.
Data Storage: Currently utilizes a structured Hotel Database via static ArrayLists for rapid testing and immediate launch capabilities, with the architecture supporting seamless transition to a real relational database.
💻 Technology StackLanguage: 
Java UI Framework: JavaFX (FXML, CSS) 
Concurrency: Java Threads / JavaFX Task & Service classes 
Version Control: Git & GitHub 
🎯 Milestones
This project was developed in two major phases:
Milestone 1: OOP Foundations: Focused entirely on building a solid, well-structured backend, defining the database schema, and implementing business logic without a GUI.
Milestone 2: GUI, Multi-threading, and Network Basics: Transitioned the backend into a fully interactive JavaFX application, integrating real-time threading and client-server chat functionality.
⚙️ Installation & Setup
Clone the repository: [git clone https://github.com/YourUsername/Hotel-Reservation-System.git]
Open the project in your preferred IDE (IntelliJ IDEA, Eclipse, etc.).
Ensure you have the JavaFX SDK configured in your project structure.
Run the main application file to launch the GUI. The Hotel database will pre-populate with dummy data for immediate testing.
👥 Contributors
This project was collaboratively developed by our group of 5 students.
Karim Ismail 
Mohamed Alaa
Yassin Mohamed
Seif Hazem
Abdullah Alaa
(Note: Detailed individual contributions and specific Git commit histories are documented in our final written report.)
