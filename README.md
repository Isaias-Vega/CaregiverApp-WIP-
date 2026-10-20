# CaregiverApp-WIP-
Status: Work in progress | Platform: Android | Language: Java

About the Project
Patient Caregiver is a native Android portfolio project designed to help family members and professional caregivers organize information about the people in their care. The long-term vision is a welcoming dashboard for patient profiles, location monitoring, device status, and safety alerts.

The interface uses a warm, home-inspired visual style: ivory backgrounds, sage and forest greens, rounded Material Design components, and straightforward navigation. The goal is to make routine caregiving tasks feel approachable without obscuring important safety information.

This is a demonstration project, not a functioning medical or emergency-monitoring service. Monitoring information is currently placeholder data; it should not be relied on for real-world patient safety.
Tools and Technologies
- Android Studio — native Android development and testing
- Java — application logic
- XML — layouts and visual resources
- Material Components / Material 3 — text fields, cards, buttons, dialogs, and navigation
- SQLite and SQLiteOpenHelper — local relational data storage
- RecyclerView — displaying patient cards dynamically
- Git and GitHub — source control and portfolio hosting

Features Developed So Far
The project currently includes code and layouts for:
- Caregiver registration: input validation, duplicate-email checks, and local account storage using salted password hashes.
- Caregiver login: email/password validation and SQLite credential checking, with the database operation run on a background executor.
- Homepage dashboard: overview cards, patient list, and bottom-navigation UI.
- Add Patient: a form for entering patient information, including required fields and optional contact details.
- Patient list: a RecyclerView-based approach for displaying patient names and temporary monitoring placeholders.
- Patient details: display of an individual patient record selected by ID.
- Edit patient information: SQLite update logic and a proposed Material alert dialog for editing a patient without leaving the details screen.
- Shared utilities: password hashing and reusable phone-number format validation.

Some screen integrations and workflows are still being tested or connected. In particular, patient-to-caregiver assignment and live monitoring are not complete.
Patient Information and Validation
Field	Required?
Full name	Yes
Date of birth	Yes
Relationship to caregiver	Yes
Phone number	No
Home address	No
Caregiving notes	No


Optional fields can be stored as NULL when omitted. Phone numbers are checked only when provided. Validation is performed in the UI and database methods where applicable.
Database Design
The app uses a local SQLite database called caregiver.db, with three planned/core tables:
caregivers
Column	Description
caregiver_id	Unique caregiver ID (primary key)
full_name	Caregiver's name
email	Unique sign-in email
password_hash	Salted password hash, stored as a BLOB
password_salt	Salt used to hash the password, stored as a BLOB
phone_number	Optional phone number


patients
Column	Description
patient_id	Unique patient ID (primary key)
full_name	Patient's name
date_of_birth	Date of birth
relationship	Relationship recorded in the current prototype
phone_number	Optional phone number
home_address	Optional address
caregiver_notes	Optional notes


caregiver_patients
Column	Description
caregiver_id	Foreign key to caregivers
patient_id	Foreign key to patients
role	Caregiver role (such as primary caregiver)


The junction table is intended to support multiple caregivers per patient and multiple patients per caregiver. Linking newly added patients to the signed-in caregiver is a remaining task. Longer term, the relationship field may move from patients to the junction table because relationships depend on the specific caregiver.
Database operations
The DatabaseHelper class is responsible for creating/opening the database and for operations such as:
- registerUser(...) and emailExists(...)
- loginUser(...)
- addPatient(...)
- getAllItems() for patient cards
- getPatientById(...)
- updatePatient(...)

The patient model holds the saved information, while PatientItem represents the information shown on a homepage card.
Application Screens
Screen/layout	Purpose
activity_login.xml	Caregiver sign-in
activity_register.xml	Create an account
activity_home.xml	Dashboard and patient list (Activity may be named HomepageActivity)
item_patient.xml	Reusable patient card
activity_add_patient.xml	Add a patient
activity_patient_details.xml	View a patient's information
dialog_edit_patient.xml	Edit patient information in a dialog
bottom_nav_menu.xml	Bottom navigation menu
Other planned screens include geolocation, alerts, devices, safe-zone settings, and account settings.

Getting Started
1. Clone the repository:
   git clone <YOUR_REPOSITORY_URL>
2. Open the project folder in Android Studio.
3. Allow Gradle to sync and install any required Android SDK components.
4. Run the project on an Android emulator or compatible Android device.
5. Register a test caregiver account, then sign in to explore the available screens.

The database is created locally when DatabaseHelper first opens it. It can be inspected while the app runs through Android Studio → View → Tool Windows → App Inspection → Database Inspector.
Replace <YOUR_REPOSITORY_URL> with the actual GitHub clone URL before publishing.

Planned Improvements
- Complete and test the Add / View / Edit patient workflows end to end.
- Connect caregiver-patient relationships and filter the dashboard to the signed-in caregiver.
- Add patient deletion with confirmation and appropriate relationship handling.
- Improve session handling and navigation.
- Implement simulated location updates, battery levels, and safety-event history.
- Add map display, geofencing concepts, notifications, and connected-device support in later versions.
- Add automated tests for validation and database CRUD methods.
- Strengthen data protection and authentication before considering any real patient use.

Privacy and Limitations

SQLite is local to the device and does not automatically synchronize patient information between caregivers. It also cannot independently provide real-time remote location monitoring or remote emergency notifications. Those capabilities would require additional device and server/cloud integrations.
Use fictional patient records for portfolio demonstrations. The app is not currently intended to store real medical data or to replace professional caregiving or emergency systems.

What I Learned / Project Goals

This project is an opportunity to practice native Android development, Java, XML layouts, reusable UI components, input validation, password handling, relational database design, CRUD operations, background tasks, and working with data in a RecyclerView.

Project Status

Work in progress. The current focus is completing reliable local caregiver account and patient management features before moving on to simulated monitoring and alerts.
