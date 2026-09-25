# WebForge AI — Full-Stack AI Website Builder Platform

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-blue.svg)](https://developer.android.com/jetpack/compose)
[![Room Database](https://img.shields.io/badge/Room-SQLite-green.svg)](https://developer.android.com/training/data-storage/room)
[![Gemini AI](https://img.shields.io/badge/Gemini%20AI-Ready-orange.svg)](https://ai.google.dev/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**WebForge AI** is an intelligent AI Website Builder Platform application built with modern **Android, Jetpack Compose, Material Design 3, Room, and Gemini AI**. Users can describe their desired website in natural language (Bengali, English, or Arabic), and WebForge AI generates complete multi-page websites with responsive layouts, PostgreSQL database schemas, REST APIs, interactive live previews, and one-click deployment workflows.

---

## 🌟 Key Features

### 1. Natural Language Website Generator
- **Multi-lingual Input**: Supports prompts in Bengali (বাংলা), English, and Arabic (العربية).
- **7-Stage Workflow Visualizer**:
  1. Requirement Analysis
  2. Project Specification
  3. Architecture Generation
  4. File Structure Creation
  5. Full-Stack Code Generation
  6. Sandbox Compilation & Verification
  7. Interactive Live Preview

### 2. Full-Stack Multi-Page Code Generation
For every project, WebForge AI generates:
- **Frontend Pages**: `index.html` (Landing/Hero), `products.html` (Dynamic Catalog with search & category filters), `cart.html` (Real-time cart calculation), `checkout.html` (Multi-step checkout with COD, bKash, and Stripe Card options), `about.html`, and `contact.html`.
- **Responsive Styling**: Pure modern CSS `styles.css` with dark/light mode toggle.
- **Client Logic**: `app.js` with cart persistence (`localStorage`), product filtering, and order modal.
- **Production Database**: PostgreSQL schema `schema.sql` with tables for users, products, orders, categories, items, and performance indexes.
- **Backend REST API**: Node.js/Express TypeScript endpoints `api.ts` (`GET /api/products`, `POST /api/checkout`, `POST /api/contact`).
- **Configuration & Docs**: `deployment.json` and `README.md`.

### 3. Interactive Sandbox Live Preview
- **Device Viewports**: Instant toggle between Mobile (375px), Tablet (768px), and Desktop (100%).
- **Seamless Page Switcher**: Navigate between all generated HTML pages.
- Real JavaScript runtime for cart interactions, instant search, and order placement.

### 4. Professional Visual Code Editor
- **File Explorer**: Browse and inspect all HTML, CSS, JS, SQL, TS, and JSON files.
- **Editor Controls**: Line numbers, syntax coloration tags, character count, save, undo/redo, and file creation/deletion.
- **AI Tools**: *AI Explain Code* and *AI Quick Inline Edit*.

### 5. Conversational Project Context & Auto-Fix
- Remembers project context across revisions:
  - *"Header-এর রঙ পরিবর্তন করো"* ➔ Updates theme color palette across CSS and HTML.
  - *"একটি Blog section যোগ করো"* ➔ Adds blog page and navigation links.
  - *"Login/Register যোগ করো"* ➔ Injects auth modal and `auth.html`.
  - *"Product search যোগ করো"* ➔ Adds instant search bar and filter logic.
- **AI Auto-Fix**: Automatically lints code, repairs unclosed tags, and fixes script listeners.

### 6. Deployment & Export
- Subdomain (`sitename.webforge.app`) and Custom Domain configuration.
- Automated SSL and production build logs terminal.
- **Export ZIP**: Downloads complete project bundle with all source files.

### 7. Supervisor Admin Console & Plans
- Platform KPIs: Total Users, Websites Generated, Monthly Revenue, AI Token Usage.
- Gemini Model Switcher (`gemini-3.5-flash`, `gemini-3.1-pro-preview`) and API key config.
- Subscription Plan Manager: FREE, PRO, BUSINESS with limit overrides.
- In-app global broadcast announcement banner.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Local Persistence**: Room Database with KSP & Reactive Kotlin `Flow`
- **Networking**: Retrofit, OkHttp, Moshi
- **AI Engine**: Gemini 3.5 Flash REST API + Offline Deterministic Generator
- **Web Rendering**: Android WebView with custom JavaScript bridge

---

## 🚀 How to Build & Run Locally

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17 or JDK 21
- Android SDK 36 (minSdk 24)

### Steps
1. Clone this repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/webforge-ai.git
   cd webforge-ai
   ```
2. Open the project in **Android Studio**.
3. (Optional) Set your Gemini API key:
   - Create a `.env` file in the root directory:
     ```env
     GEMINI_API_KEY=your_gemini_api_key_here
     ```
4. Run the app on an Android device or emulator:
   ```bash
   ./gradlew installDebug
   ```

---

## 🧪 Testing

Run JVM unit and Robolectric tests:
```bash
./gradlew testDebugUnitTest
```

---

## 📄 License
MIT License. Feel free to use, modify, and distribute.
