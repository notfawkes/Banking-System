# Deployment Steps for Render

This guide outlines exactly how to deploy the Spring Boot Banking System to Render as a Web Service.

## Prerequisites
1. Ensure your Postgres Database (e.g. from Supabase) is created and secure.
2. Grab the Database `JDBC Connection URL`, `.username`, and `.password` from Supabase settings.
3. Have a 256-bit or better `JWT_SECRET` ready (If using hexadecimal, make sure it is exactly 32-characters or longer, or use base64).

## 1. Create a Web Service on Render

- Go to your Render Dashboard.
- Click **"New"** and select **"Web Service"**.
- Connect your GitHub repository containing this codebase.

## 2. Configure Service Settings

- **Name**: `banking-api-backend` (or whatever you prefer)
- **Environment**: **Docker** (Render will automatically detect the `Dockerfile` at the root of the repository).

## 3. Configure Environment Variables

Scroll down to the "Environment Variables" section and define the following crucial bindings exactly as follows:

| Key | Example Value | Description |
| :--- | :--- | :--- |
| `SUPABASE_DB_URL` | `jdbc:postgresql://db.YOUR_INSTANCE.supabase.co:5432/postgres` | The connection JDBC URL provided by Supabase. Ensure `jdbc:postgresql://` is at the beginning. |
| `SUPABASE_DB_USER` | `postgres` | Your database user role. |
| `SUPABASE_DB_PASSWORD` | `YOUR_SECURE_PASSWORD` | The database password. |
| `JWT_SECRET` | `YOUR_SECURE_SECRET_STRING_AT_LEAST_32_CHARS` | Secure random seed for signing user JWTs. |

> NOTE: You do **not** need to define `$PORT`. Render will automatically inject a `PORT` variable during startup and Spring Boot is natively configured in `application.properties` to read from it.

## 4. Deploy

- Click the **"Create Web Service"** or **Deploy** button.
- Monitor the build logs. Render will execute Maven to download dependencies, package the JAR into `target/app.jar`.
- Render will launch the application and give you a public URL (e.g., `https://banking-api.onrender.com`).

## 5. Verify Online

Once deployed, visit `https://YOUR_RENDER_URL.onrender.com/swagger-ui/index.html` to see the live Swagger API Interface or hit `/auth/register` using Postman!
