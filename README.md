# 🏨 Hotel Reservation System

A comprehensive, object-oriented desktop application for hotel management, developed as a group project for the CSE241 Object-Oriented Computer Programming course at the Faculty of Engineering - Ain Shams University.


## 📑 Table of Contents

[Overview](#ℹ️-Overview)

[Key Features](#✨-Key-Features)

[Architecture & OOP Design](#🏗️-Architecture-&-OOP-Design)

[Technology Stack](#💻-Technology-Stack)

[Milestones](#🎯-Milestones)

[Installation & Setup](#⚙️-Installation-&-Setup)

[Contributors](#Contributors)


## ℹ️ Overview

This project is a fully functional Desktop Hotel Reservation System built entirely in Java. It is designed to handle the complete lifecycle of a hotel stay, from guest registration and room browsing to reservation management, checkout, and invoicing. The system is built with a strong emphasis on core Object-Oriented Programming (OOP) principles and features a modern, responsive Graphical User Interface (GUI) powered by JavaFX.


## ✨ Key Features

### 👤 Guest Portal
* **Authentication**: Secure registration and login with input validation.
* **Room Browsing**: View available rooms dynamically filtered by type (Single, Double, Suite) and specific amenities (WiFi, TV, Mini-bar).
* **Reservation Management**: Easily make, view, or cancel room reservations.
* **Checkout & Billing**: Process checkouts with detailed invoices supporting multiple payment methods (Cash, Credit Card, Online).

### 🛎️Staff Dashboard (Admin & Receptionist)
* **Role-Based Access**: Distinct privileges for Admins and Receptionists.
* **Admin Controls**: Full CRUD (Create, Read, Update, Delete) capabilities for managing rooms, room types, and amenities.
* **Receptionist Duties**: Dedicated tools for managing guest check-ins and check-outs, handling reservations, and overseeing daily hotel operations.

## 🏗️ Architecture & OOP Design
*The backend is meticulously structured using encapsulation, inheritance, and polymorphism.*


* **Entity Hierarchies**: Staff members (Admins and Receptionists) inherit from a Staff parent class to maximize code reuse.
* **Data Integrity**: Strict encapsulation with private fields, validated setters, and custom error handling ensures robust data management.
* **Data Storage**: Currently utilizes a structured In-Memory Database via static ArrayLists for rapid testing and immediate launch capabilities, with the architecture supporting seamless transition to a real relational database.


## 💻 Technology Stack
Language: Java UI Framework: JavaFX (FXML, CSS).
Concurrency: Java Threads / JavaFX Task & Service classes.
Networking: Java Sockets (ServerSocket and Socket).
Version Control: Git & GitHub.


## 🎯 Milestones
*This project was developed in two major phases:*
1. **Milestone 1**: OOP Foundations: Focused entirely on building a solid, well-structured backend, defining the database schema, and implementing business logic without a GUI.
2. **Milestone 2**: GUI: Transitioned the backend into a fully interactive JavaFX application.
3. **Bonus**: Multi-threading, and Network Basics: Integrating real-time threading and client-server chat functionality.

## ⚙️ Installation & Setup
1. Clone the repository:
```
git clone https://github.com/YassinMohamed07/ProjrctOOPHotelSystem.git
```
2. Open the project in your preferred IDE (IntelliJ IDEA, Eclipse, etc.).
3. Ensure you have the JavaFX SDK configured in your project structure.
4. Run the main application file to launch the GUI. The in-memory database will pre-populate with dummy data for immediate testing.

## 👥 Contributors
* **Abdullah Alaa Abdelkader (25P0139)**
* **Karim Ismail Samy (25P0060)**
* **Mohamed Alaa ElSharkawy (25P0202)**
* **Seif Hazem Mahmoud (25P0005)**
* **Yassin Mohamed Yehia (25P0006)**

*(Note: Detailed individual contributions and specific Git commit histories are documented in our final written report.)* 
