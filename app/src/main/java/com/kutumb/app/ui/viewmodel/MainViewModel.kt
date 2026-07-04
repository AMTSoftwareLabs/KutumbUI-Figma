package com.kutumb.app.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutumb.app.data.model.*
import com.kutumb.app.data.repository.AppRepository
import com.kutumb.app.domain.*
import com.kutumb.app.ui.theme.SeedTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// ── Settings state ─────────────────────────────────────────────────────────
data class SettingsState(
    val isDarkMode: Boolean = false,
    val seedTheme: SeedTheme = SeedTheme.AGNI
)

private val KEY_DARK  = booleanPreferencesKey("dark_mode")
private val KEY_SEED  = stringPreferencesKey("seed_theme")

// ── UI State per screen ────────────────────────────────────────────────────
data class HomeUiState(
    val currentUser: Member = Member("raj","राज","R","#FF6B35","admin"),
    val leaderboard: List<Pair<Member, Int>> = emptyList(),
    val todayTasks: List<Task> = emptyList(),
    val totalMonthlyExpense: Double = 0.0,
    val loyaltyScores: Map<String, Int> = emptyMap(),
    val streak: Int = 12,
    val snack: String? = null
)

data class KaryaUiState(
    val tasks: List<Task> = emptyList(),
    val members: List<Member> = emptyList(),
    val loyaltyScores: Map<String, Int> = emptyMap(),
    val snack: String? = null
)

data class NiyamaUiState(
    val rules: List<Rule> = emptyList(),
    val rewards: List<Reward> = emptyList(),
    val ratings: List<RuleRating> = emptyList(),
    val members: List<Member> = emptyList(),
    val loyaltyScores: Map<String, Int> = emptyMap(),
    val snack: String? = null
)

data class VyayaUiState(
    val expenses: List<Expense> = emptyList(),
    val members: List<Member> = emptyList(),
    val monthlyLimit: Double = 30000.0,
    val snack: String? = null
)

data class RinaUiState(
    val loans: List<Loan> = emptyList(),
    val payments: List<LoanPayment> = emptyList(),
    val snack: String? = null
)

data class SamvaadUiState(
    val messages: List<ChatMessage> = emptyList(),
    val members: List<Member> = emptyList()
)

data class SoochiUiState(
    val lists: List<GroceryList> = emptyList(),
    val items: List<GroceryItem> = emptyList(),
    val snack: String? = null
)

data class SmritiUiState(
    val memories: List<Memory> = emptyList(),
    val snack: String? = null
)

data class ParichayUiState(
    val members: List<Member> = emptyList(),
    val loyaltyScores: Map<String, Int> = emptyMap(),
    val snack: String? = null
)

// ── ViewModel ──────────────────────────────────────────────────────────────
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: AppRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val settingsState: StateFlow<SettingsState> = dataStore.data.map { prefs ->
        SettingsState(
            isDarkMode = prefs[KEY_DARK] ?: false,
            seedTheme  = SeedTheme.values().find { it.id == (prefs[KEY_SEED] ?: "agni") } ?: SeedTheme.AGNI
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsState())

    // ── Live loyalty scores (combines all tables) ──────────────────────────
    @Suppress("UNCHECKED_CAST")
    private val loyaltyScores: StateFlow<Map<String, Int>> = combine(
        repo.members, repo.tasks, repo.rules, repo.ratings,
        repo.expenses, repo.payments, repo.messages, repo.rewards
    ) { arr ->
        val members  = arr[0] as List<Member>
        val tasks    = arr[1] as List<Task>
        val rules    = arr[2] as List<Rule>
        val ratings  = arr[3] as List<RuleRating>
        val expenses = arr[4] as List<Expense>
        val payments = arr[5] as List<LoanPayment>
        val chats    = arr[6] as List<ChatMessage>
        val rewards  = arr[7] as List<Reward>
        computeLoyaltyScores(
            memberIds = members.map { it.id },
            tasks = tasks, rules = rules, ratings = ratings,
            expenses = expenses, payments = payments,
            chats = chats, rewards = rewards
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // ── Per-screen states ──────────────────────────────────────────────────
    val homeState: StateFlow<HomeUiState> = combine(
        repo.members, repo.tasks, repo.expenses, loyaltyScores
    ) { members, tasks, expenses, scores ->
        val today = todayIso()
        HomeUiState(
            currentUser = members.firstOrNull { it.role == "admin" }
                ?: members.firstOrNull() ?: Member("raj","राज","R","#FF6B35","admin"),
            leaderboard = members.map { it to (scores[it.id] ?: 100) }
                .sortedByDescending { it.second }.take(5),
            todayTasks = tasks.filter { it.deadline == today },
            totalMonthlyExpense = expenses
                .filter { !it.isIncome && it.date.startsWith(currentMonthPrefix()) }
                .sumOf { it.amount },
            loyaltyScores = scores,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    val karyaState: StateFlow<KaryaUiState> = combine(
        repo.tasks, repo.members, loyaltyScores
    ) { tasks, members, scores ->
        KaryaUiState(tasks = tasks, members = members, loyaltyScores = scores)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), KaryaUiState())

    val niyamaState: StateFlow<NiyamaUiState> = combine(
        repo.rules, repo.rewards, repo.ratings, repo.members, loyaltyScores
    ) { rules, rewards, ratings, members, scores ->
        NiyamaUiState(rules = rules, rewards = rewards, ratings = ratings, members = members, loyaltyScores = scores)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NiyamaUiState())

    val vyayaState: StateFlow<VyayaUiState> = combine(
        repo.expenses, repo.members
    ) { expenses, members ->
        VyayaUiState(expenses = expenses, members = members)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VyayaUiState())

    val rinaState: StateFlow<RinaUiState> = combine(
        repo.loans, repo.payments
    ) { loans, payments ->
        RinaUiState(loans = loans, payments = payments)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RinaUiState())

    val samvaadState: StateFlow<SamvaadUiState> = combine(
        repo.messages, repo.members
    ) { messages, members ->
        SamvaadUiState(messages = messages, members = members)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SamvaadUiState())

    val soochiState: StateFlow<SoochiUiState> = combine(
        repo.groceryLists, repo.allItems
    ) { lists, items ->
        SoochiUiState(lists = lists, items = items)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SoochiUiState())

    val smritiState: StateFlow<SmritiUiState> = repo.memories.map { SmritiUiState(memories = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SmritiUiState())

    val parichayState: StateFlow<ParichayUiState> = combine(
        repo.members, loyaltyScores
    ) { members, scores ->
        ParichayUiState(members = members, loyaltyScores = scores)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ParichayUiState())

    // ── Actions ────────────────────────────────────────────────────────────
    fun toggleTask(task: Task) = launch { repo.upsertTask(task.copy(isCompleted = !task.isCompleted)) }
    fun addTask(task: Task)    = launch { repo.upsertTask(task) }
    fun deleteTask(task: Task) = launch { repo.deleteTask(task) }

    fun addRule(rule: Rule)    = launch { repo.upsertRule(rule) }
    fun deleteRule(rule: Rule) = launch { repo.deleteRule(rule) }
    fun rateRule(ruleId: String, userId: String, followed: Boolean) = launch {
        repo.upsertRating(RuleRating(id = uuid(), ruleId = ruleId, userId = userId, followed = followed))
    }

    fun addExpense(e: Expense)    = launch { repo.upsertExpense(e) }
    fun deleteExpense(e: Expense) = launch { repo.deleteExpense(e) }

    fun addLoan(loan: Loan) = launch { repo.upsertLoan(loan) }
    fun makePayment(payment: LoanPayment, loan: Loan) = launch {
        repo.upsertPayment(payment)
        repo.upsertLoan(loan.copy(remaining = maxOf(0.0, loan.remaining - payment.amount)))
    }

    fun addReward(reward: Reward)   = launch { repo.upsertReward(reward) }
    fun redeemReward(reward: Reward, userId: String) = launch {
        repo.upsertReward(reward.copy(redeemedById = userId))
    }

    fun addMemory(memory: Memory)          = launch { repo.upsertMemory(memory) }
    fun toggleMemoryLike(memory: Memory)   = launch { repo.upsertMemory(memory.copy(isLiked = !memory.isLiked)) }

    fun addGroceryList(list: GroceryList)  = launch { repo.upsertGroceryList(list) }
    fun addGroceryItem(item: GroceryItem)  = launch { repo.upsertGroceryItem(item) }
    fun toggleGroceryItem(item: GroceryItem) = launch { repo.upsertGroceryItem(item.copy(isDone = !item.isDone)) }

    fun sendMessage(text: String, currentUser: Member) = launch {
        repo.insertMessage(
            ChatMessage(
                id = uuid(), text = text, senderId = currentUser.id,
                senderName = currentUser.name, senderColorHex = currentUser.colorHex,
                senderShortName = currentUser.shortName, isMe = true
            )
        )
    }

    fun setDarkMode(dark: Boolean) = launch { dataStore.edit { it[KEY_DARK] = dark } }
    fun setSeedTheme(theme: SeedTheme) = launch { dataStore.edit { it[KEY_SEED] = theme.id } }

    // Seed initial data (call once on first launch)
    fun seedIfEmpty() = launch {
        val existing = repo.members.firstOrNull()
        if (existing.isNullOrEmpty()) {
            SeedData.members.forEach { repo.upsertMember(it) }
            SeedData.tasks.forEach { repo.upsertTask(it) }
            SeedData.rules.forEach { repo.upsertRule(it) }
            SeedData.rewards.forEach { repo.upsertReward(it) }
            SeedData.expenses.forEach { repo.upsertExpense(it) }
            SeedData.loans.forEach { repo.upsertLoan(it) }
            SeedData.memories.forEach { repo.upsertMemory(it) }
            SeedData.groceryLists.forEach { repo.upsertGroceryList(it) }
            SeedData.groceryItems.forEach { repo.upsertGroceryItem(it) }
            SeedData.messages.forEach { repo.insertMessage(it) }
        }
    }

    private fun launch(block: suspend () -> Unit) = viewModelScope.launch { block() }
}

// ── Helpers ────────────────────────────────────────────────────────────────
fun uuid() = UUID.randomUUID().toString()

fun todayIso(): String {
    val c = java.util.Calendar.getInstance()
    return "%04d-%02d-%02d".format(c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH) + 1, c.get(java.util.Calendar.DAY_OF_MONTH))
}

fun currentMonthPrefix(): String {
    val c = java.util.Calendar.getInstance()
    return "%04d-%02d".format(c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH) + 1)
}
