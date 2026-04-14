# db.properties — Database connection configuration
# !! ADD THIS FILE TO .gitignore — NEVER COMMIT TO VERSION CONTROL !!
#
# Instructions:
#   1. Copy this file to your project src/ directory (where it will be on the classpath)
#   2. Fill in your actual database credentials below
#   3. Add "db.properties" to your .gitignore file
#   4. Create a limited SQL Server user (see DBConnection.java for SQL commands)

db.url=jdbc:sqlserver://localhost:1433;databaseName=BMMG_HOSPITAL;encrypt=true;trustServerCertificate=true
db.user=bmmg_app_user
db.password=YourStrongPasswordHere
