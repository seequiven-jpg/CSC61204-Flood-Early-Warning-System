# CSC61204-Flood-Early-Warning-System
Flood Early Warning Community System - CSC61204 Group Assignment

## Login interface

The app opens on the resident login screen. Admins use the **Admin login** link at the bottom of that screen.

### Run the app

1. Open the project in IntelliJ IDEA and use JDK 25.
2. Let IntelliJ import the Maven project, then run `Main.java` from the `com.group.floodwarning.app` package.
3. If the app cannot find the accounts you set up, check **Run → Edit Configurations**. The working directory should be `$PROJECT_DIR$`, the folder that contains `pom.xml`.

### Resident login

Click **Sign up** to create a resident account. Enter your full name, email, password, and password confirmation. Use at least 8 characters for the password. After registering, go back to the login screen and sign in with the same email and password.

### Admin login

Click **Admin login**, enter the email and password you set up, and click **Sign in as admin**. On a fresh copy, create the admin account once. Open the **Terminal** tab in IntelliJ from the project folder and run:

```powershell
mvn compile
java -cp target/classes com.group.floodwarning.app.CreateFirstAdmin
```

If `mvn` is unavailable in the terminal, run **Maven → Lifecycle → compile** in IntelliJ, then run the `java` command from the terminal. Enter a name, an email address, and a password for your local demo, then type the password again to confirm. The terminal hides the password as you type. Once you see **Admin account created**, you can sign in from the app.
