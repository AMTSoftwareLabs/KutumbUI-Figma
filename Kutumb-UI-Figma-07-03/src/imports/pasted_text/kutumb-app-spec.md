Comprehensive App Specification for "Kutumb / Household Manager"
App Vision: An interactive, collaborative, and gamified household, family, or roommate management Android application. It tracks habits (rules), chores (tasks), split expenses, and shared groceries, rewarding positive actions with a points-based leaderboard system.
Design Language: Material Design 3, Edge-to-Edge, smooth bottom sheet dialogs, and celebratory micro-interactions (confetti, dynamic colors). Every user action should trigger clear feedback (snackbars for success, dialogs for destructive actions).
Phase 1: Onboarding & Authentication
1. User Registration Screen
Purpose: Allow the user to set up their local profile.
Inputs strictly limited to:
Name (Text Field)
Bio (Text Field - Optional short description)
Avatar Picker (Grid of pre-defined emojis/icons or a random generator)
Actions:
"Continue" Button.
Popups/Events:
Validation Error Popup: "Please enter your name."
Success Event: Navigates smoothly to Household Setup.
2. Household (Bond) Creation/Join Screen
Purpose: Connect users together in a shared house.
UI Components:
"Create a New Household" Button.
"Join Existing Household" (Requires a 6-digit invite code).
Actions: Enter Code -> Join; or Create -> Generates Code.
Popups/Events:
Create Success Modal: Displays the newly generated 6-digit code with a "Share to WhatsApp/SMS" button.
Join Error Popup: "Invalid Code. Please try again."
Join Success Splash: "Welcome to [Household Name]! Meet your roommates..."
Phase 2: Core Navigation & Dashboard
Always visible: Bottom Navigation Bar (Dashboard, Rules, Chores, Expenses, Groceries).
3. Dashboard (Home) Screen
Purpose: At-a-glance overview of today's household status.
UI Components:
Greeting & AI Tip Header: e.g., "Good morning, Raj!" + A dynamic Gemini AI daily tip card about living together.
Leaderboard Widget: Top 3 users ranked by current score.
Today's Chores: Horizontal scrolling list of tasks due today.
Recent Activity Feed: "Priya bought Milk", "Aryan finished cleaning".
Global FAB (Floating Action Button): Expands to allow quick adding of (Expense, Chore, or Grocery Item).
Popups/Events:
AI Tip Refresh Action: Tap to generate a new AI tip (shows a loading shimmer).
Level Up Modal: If a user crosses a point threshold, a celebratory popup with confetti appears.
4. Niyama (Rules & Habits) Screen
Purpose: Establish and track house rules.
UI Components:
List of active rules (e.g., "Wake up by 6 AM", "No shoes in the living room").
Each card has: Rule Name, Point Weight, and two buttons: "I Followed This" (+ points) & "Someone Broke This" (- points).
Popups/Events:
Add/Edit Rule Bottom Sheet: Form requesting Rule Name, Category (Dropdown), and Point Weight (Slider).
Rule Followed Popup: "Great job! +X points awarded to you."
Report Rule Break Modal: "Who broke this rule?" (Dropdown of users) -> Submit.
Warning Alert: "Are you sure you want to delete this rule?" (On delete swipe).
5. Chores & Tasks Screen
Purpose: Manage household work and assignments.
UI Components:
Tabs: "My Chores" | "All Chores" | "Completed".
Task Cards: Title, Assignee Avatar, Deadline, Frequency (Once, Daily, Weekly), and Point Reward.
Popups/Events:
Add Task Full-Screen Modal: Inputs for Title, Description, Assign To (Multi-select), Deadline (Date/Time Picker), Frequency selector.
Mark as Done Event: Task card slides away, confetti animation plays, SnackBar: "+X points earned for completing a chore!"
Nudge/Remind Action: Tap on an overdue task assigned to someone else -> SnackBar: "Reminder sent to [Name]!"
6. Expenses & Finance Screen
Purpose: Log shared expenses and track who owes whom.
UI Components:
"Total Spent This Month" Summary Card.
"Who Owes Who" Dashboard (e.g., "You owe Priya ₹500").
List of individual expense logs.
Popups/Events:
Add Expense Bottom Sheet: "What was it for?", Amount, Paid By (Dropdown), Split Method (Equally vs. Custom amounts).
Settle Up Modal: Select user to pay back -> Confirms settlement -> SnackBar: "Debt settled successfully."
Delete Expense Alert: "Deleting this will recalculate all balances. Proceed?"
7. NEW FEATURE: Shared Groceries / Wishlist Screen
Purpose: Simple collaborative shopping list.
UI Components:
List of items needed.
Popups/Events:
Add Item Dialog: Item name and optional quantity.
Claim Action: Swipe right on an item -> Status changes to "Aryan is buying this".
Mark Bought Popup: "Did you pay for this?" -> Yes (Opens Add Expense bottom sheet pre-filled) / No (Just marks off list).
8. Rewards & Punishments Shop Screen
Purpose: Gamified redemption of points.
UI Components:
User's current total points clearly visible at the top.
Grid of Rewards (e.g., "Skip a chore" - Costs 200 pts).
Grid of Punishments (e.g., "Buy ice cream for everyone" - Triggered at -50 pts).
Popups/Events:
Create Custom Reward/Punishment: Input Name, Cost/Threshold.
Redeem Reward Confirmation Modal: "Spend 200 points to claim 'Skip a chore'?" -> Success animation.
Punishment Trigger Dialog: Automatically blocks the screen if score drops too low until the user taps "Accept Punishment".
9. Profile & Settings Screen
Purpose: App management and user history.
UI Components:
User Avatar, Name, Bio (Editable).
Activity History (List of all points gained/lost and expenses made).
Popups/Events:
Edit Profile Bottom Sheet: Update Name/Bio.
Export Data Action: SnackBar: "Generating CSV... Saved to downloads."
Leave Household Alert: "DANGER: You are about to leave the household. This cannot be undone." -> Requires typing "LEAVE" to confirm.
Global Application Requirements for Developer/Designer
State Management & Offline Support: The app MUST cache data locally (using Room DB). State transitions should be instantaneous locally, syncing seamlessly without blocking the UI.
Every Form Input Validation: No form should submit empty text. A red error outline and helper text is required for all missing mandatory fields.
Haptic Feedback: The device should gently vibrate (haptic pop) on every button press, drag-and-drop, or toggle switch to make the app feel tactile.
No Dead Ends: Every empty state (e.g., no chores, no expenses, no rules) MUST have a delightful illustration and a clear "Add your first..." call-to-action button. Do not leave blank white screens.
Micro-interactions:
Checking a checkbox should have a morphing checkmark animation.
Deleting an item should require a swipe-to-delete gesture revealing a red trash can background.