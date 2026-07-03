App Overview: "Kutumb" (Family / Group Management App)
Purpose: An offline-first, shared household management app for families, roommates, or groups. It gamifies daily living by tracking chores, expenses, loans, household rules, and communication, awarding "Loyalty Points" for positive contributions.
Key Selling Point: Completely offline-first peer-to-peer sync (via Wi-Fi/Tethering), AI-powered automated SMS expense/loan parsing, and a dynamic gamification engine.
🛠 Technical Stack & Architecture
Language: Kotlin
UI Framework: Jetpack Compose (Material Design 3)
Architecture: MVVM (Model-View-ViewModel) with Clean Architecture principles.
State Management: Kotlin Coroutines & StateFlow / MutableStateFlow (e.g., combine, flatMapLatest).
Local Database: Room (SQLite) – single source of truth.
Networking / Sync: Custom SyncManager using local TCP Sockets / Wi-Fi Direct for peer-to-peer offline database syncing. Distributed events are logged via SyncEvents (Event Sourcing pattern).
AI Integration: Server-side Gemini API (for SMS batch parsing, expense categorization, and daily tips).
🌳 App Features & Technical Tree
code
Text
Kutumb Application Feature Tree
│
├── 1. Parichay (Identity & Group Management)
│   ├── Data Models: `User` (id, name, bio, mood, avatar, role), `Bond` (Group entity: id, name, type, memberIds, themeColor)
│   ├── Features:
│   │   ├── Create multiple user profiles (Admin vs Standard).
│   │   └── Create, switch between, and manage multiple groups (Bonds).
│   └── Actions (ViewModel): `createProfile()`, `addNewBond()`, `selectBond()`, `updateMood()`, `addUserToGroup()`, `unfriendUser()`
│
├── 2. KutumbHomeScreen (Dashboard)
│   ├── Features:
│   │   ├── AI-generated "Daily Household Tip".
│   │   ├── High-level stats: Active Streak, Total Monthly Vyaya (Expenses), and User Loyalty Score.
│   │   ├── Live "Leaderboard" showing top 3 members by points.
│   │   ├── Horizontal carousel of "Today's Tasks" with completion checkboxes.
│   │   └── Feed of "Recent Activities" (e.g., "X added an expense", "Y completed a task").
│   └── Actions: `getDailyHouseholdTip()`, `startPeerDiscovery()`, `stopPeerDiscovery()`
│
├── 3. Karya (Chores & Tasks)
│   ├── Data Model: `Task` (id, title, desc, assignedUserId, weight, deadline, frequency, isCompleted, urgency)
│   ├── Features:
│   │   ├── Assign chores to specific members or "Everyone".
│   │   ├── Recurring frequencies (Daily, Weekly, Monthly, Once).
│   │   └── Dynamic sorting by Urgency and Deadlines.
│   └── Actions: `editTask()` (Toggling completion awards/deducts points dynamically).
│
├── 4. Vyaya (Expenses, Budgets & SMS Parsing)
│   ├── Data Models: `Expense` (id, amount, desc, category, isIncome, payerId, splits, hasReceipt), `Category`
│   ├── Features:
│   │   ├── Manual expense/income logging with smart categorization (falls back to Gemini if local heuristic fails).
│   │   ├── Global Expense Budgets and Income Goals.
│   │   └── Auto-Scan Inbox: Reads device SMS, packages them, and sends them to Gemini AI to extract `date`, `amount`, `merchant`, and `category` as JSON, auto-creating `Expense` entities.
│   └── Actions: `addExpense()`, `autoScanAndImportSms()`, `updateExpenseBudget()`, `determineCategory()`
│
├── 5. Rina (Loans & EMIs)
│   ├── Data Models: `Loan` (lender, goal, amount, rate, term, balance), `Payment`
│   ├── Features:
│   │   ├── Track outstanding loans, lenders, and interest rates.
│   │   ├── Log manual payments.
│   │   └── Auto-Scan Loan SMS: Uses Gemini AI to identify EMI deduction messages and automatically deducts the balance.
│   └── Actions: `autoScanAndImportLoanSms()`, `addLoan()`, `addPayment()`
│
├── 6. Niyama (Rules & Ratings)
│   ├── Data Models: `Rule` (desc, weight, category), `Rating` (userId, ruleId, followed: Boolean)
│   ├── Features:
│   │   └── Establish household guidelines (e.g., "No screens at dinner"). Group members can "rate" each other (followed = +pts, broken = -pts).
│   └── Actions: `addRule()`, `rateUserRule()`
│
├── 7. Samvaad (Chat & Communication)
│   ├── Data Model: `SyncEvent` (payload, type, timestamp, originUser)
│   ├── Features:
│   │   ├── Real-time local chat interface.
│   │   └── Sentiment Analysis: Using positive words ("thanks", "awesome") automatically awards bonus loyalty points to the sender.
│   └── Actions: `sendChatMessageTcp()`
│
├── 8. Smriti & Soochi (Memories & Lists)
│   ├── Data Models: `Memory` (type, content, mediaData), `GroceryItem`, `WishlistItem`
│   ├── Features:
│   │   ├── Shared photo and text memories feed.
│   │   └── Collaborative grocery checklists and personal wishlists.
│   └── Actions: `addMemory()`, `addGroceryItem()`, `toggleGroceryItem()`
│
└── 9. Settings & Synchronization
    ├── Features:
    │   ├── Offline Wi-Fi P2P Pairing using `SyncManager`.
    │   ├── Event Sourcing: Every change (Add Expense, Complete Task) creates a JSON `SyncEvent` with an SHA-256 Checksum, which is broadcasted to peers.
    │   └── Material 3 Theming: Dynamic UI (Light/Dark, user-selectable seed colors and fonts).
    └── Actions: `pairWithPeer()`, `setThemeMode()`, `setThemeSeedColor()`
🧠 The Loyalty Score Engine (Crucial Logic)
To build this app properly, the AI must implement the loyaltyScores StateFlow exactly as described below. It is a reactive pipeline that combines all database tables into a live Map<Long, Int> (UserId -> Score).
Base Score: Every user starts at 100 points.
Modifiers (Evaluated on every DB change):
Rules (Niyama): If a user followed a rule, + Rule Weight. If broken, - (Rule Weight / 2).
Tasks (Karya): If completed, + Task Weight. If overdue, deducts (Weight / 4) + (DaysOverdue * Urgency * 5).
Expenses (Vyaya): Logging expenses on time awards dynamic points based on the transaction amount.
Loans (Rina): Logging a verified loan EMI payment gives +15 points.
Chat (Samvaad): If a chat message contains positive reinforcement words (e.g., "thanks", "great", "proud"), the sender gets +5 points.
Rewards: If a user claims a Reward, their score is reduced by the Reward's threshold cost.
🤖 Prompt / "Small Actions" for another AI to rebuild this app
If you are providing instructions to an AI coding agent to generate this app, feed it the overview above, followed by these sequential execution steps:
Phase 1: Database & Entities
"Action 1: Setup the Room Database. Create data classes for User, Bond, Expense, Loan, Payment, Task, Rule, Rating, Reward, Memory, SyncEvent, and Category. Implement DAOs returning Flow<List<T>> for reactive state, and create the AppRepository."
Phase 2: State Management (ViewModel)
"Action 2: Create MainViewModel. Expose StateFlows using .combine() and flatMapLatest() filtering by the selectedBondId. Implement the loyaltyScores reactive variable exactly as described in the Loyalty Score Engine specs, ensuring it reacts to all DB table changes."
Phase 3: AI Parsers & Utilities
"Action 3: Create SmsExtractor.kt to read device SMS. Implement autoScanAndImportSms() in the ViewModel. It must batch SMS strings and send them to the Gemini API with a system prompt demanding a strict JSON array output of transactions. Map this JSON directly to Expense and Payment inserts in Room."
Phase 4: Core UI Screens (Compose)
"Action 4: Build KutumbHomeScreen. Use Scaffold and LazyColumn. Create a hero header showing the User's score and streak. Add a leaderboard component sorting the loyaltyScores map. Build a horizontal carousel for 'Today's Tasks'."
"Action 5: Build the individual feature screens: KaryaScreen (Task list with tabs for frequencies), VyayaScreen (Expense list, Budget progress bars, and an 'Auto-Scan SMS' FAB), and SamvaadScreen (Chat UI)."
Phase 5: Networking & Sync
"Action 6: Implement SyncManager.kt using java.net.ServerSocket. On any database modification (like addExpense), generate a JSON payload, compute an SHA-256 Checksum, wrap it in a SyncEvent, save it to Room, and dispatch it over the TCP socket to connected peers."