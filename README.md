# OnlineCourseCatalog

Mini Project

Project: Online Course Catalog

Objective:
Build a backend system using Java, Spring Boot, and PostgreSQL to manage an online course catalog with courses, lessons, and supporting materials.

Key Requirements:
•⁠  ⁠Implement APIs to perform CRUD operations for:
    * Courses (title, description, category, etc.)
    * Lessons within each course (title, content, duration)
    * Supporting materials attached to lessons, limited to:
        * PDF documents (e.g., manuals, guides)
        * Video links (e.g., tutorials, lectures)
•⁠  ⁠Allow filtering courses by category.
•⁠  ⁠Support user enrollment into courses via APIs.
•⁠  ⁠Include endpoints to:
    * Add, update, delete, and list courses, lessons, and supporting materials
    * Enroll users and list their enrolled courses
•⁠  ⁠Thoroughly test the system with unit and integration tests.
•⁠  ⁠(Optional) Add an endpoint to generate a PDF report listing all available courses.

Guidelines:
Create a new repository and raise PR on daily tasks.
Design the database schema based on these requirements.
Define the API structure and endpoint naming conventions.
Emphasize clean code, proper error handling, and good documentation.


Project Title: Professional E-Learning Platform (like Udemy / GeeksforGeeks)

Goal: Build a complete responsive e-learning platform that connects to my existing REST API endpoints listed below.
It should allow users to browse courses, enroll, watch lessons, manage materials, and view progress reports.

API Endpoints:
Course Controller:

GET /courses/{id} – Get course by ID

PUT /courses/{id} – Update course

DELETE /courses/{id} – Delete course

GET /courses – List all courses

POST /courses – Create course

Lesson Controller:

GET /courses/{courseId}/lessons/{lessonId} – Get lesson by ID

PUT /courses/{courseId}/lessons/{lessonId} – Update lesson

DELETE /courses/{courseId}/lessons/{lessonId} – Delete lesson

GET /courses/{courseId}/lessons – List lessons for a course

POST /courses/{courseId}/lessons – Create lesson

Material Controller:

PUT /courses/{courseId}/lessons/{lessonId}/materials/{materialId} – Update material

DELETE /courses/{courseId}/lessons/{lessonId}/materials/{materialId} – Delete material

GET /courses/{courseId}/lessons/{lessonId}/materials – List materials

POST /courses/{courseId}/lessons/{lessonId}/materials – Create material

Enrollment Controller:

POST /enrollments – Create enrollment

POST /courses/{courseId}/enroll – Enroll in a course

GET /users/{userId}/enrollments – Get user enrollments

GET /courses/{courseId}/students – List enrolled students

User Controller:

GET /users/{id} – Get user profile

PUT /users/{id} – Update profile

POST /users – Register new user

Report Controller:

GET /courses/report – Generate report
