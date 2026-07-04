package com.kutumb.app.data.dao

import androidx.room.*
import com.kutumb.app.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // ── Members ───────────────────────────────────────────────────────────
    @Query("SELECT * FROM members ORDER BY role DESC, name ASC")
    fun getMembers(): Flow<List<Member>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMember(member: Member)

    // ── Tasks ─────────────────────────────────────────────────────────────
    @Query("SELECT * FROM tasks ORDER BY urgency DESC, deadline ASC")
    fun getTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    // ── Rules ─────────────────────────────────────────────────────────────
    @Query("SELECT * FROM rules ORDER BY createdAt DESC")
    fun getRules(): Flow<List<Rule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRule(rule: Rule)

    @Delete
    suspend fun deleteRule(rule: Rule)

    @Query("SELECT * FROM rule_ratings ORDER BY createdAt DESC")
    fun getRatings(): Flow<List<RuleRating>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRating(rating: RuleRating)

    // ── Expenses ──────────────────────────────────────────────────────────
    @Query("SELECT * FROM expenses ORDER BY date DESC, createdAt DESC")
    fun getExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // ── Loans ─────────────────────────────────────────────────────────────
    @Query("SELECT * FROM loans ORDER BY createdAt DESC")
    fun getLoans(): Flow<List<Loan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLoan(loan: Loan)

    @Query("SELECT * FROM loan_payments ORDER BY createdAt DESC")
    fun getPayments(): Flow<List<LoanPayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPayment(payment: LoanPayment)

    // ── Rewards ───────────────────────────────────────────────────────────
    @Query("SELECT * FROM rewards ORDER BY type ASC, createdAt DESC")
    fun getRewards(): Flow<List<Reward>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReward(reward: Reward)

    // ── Memories ──────────────────────────────────────────────────────────
    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    fun getMemories(): Flow<List<Memory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMemory(memory: Memory)

    // ── Lists & Items ─────────────────────────────────────────────────────
    @Query("SELECT * FROM grocery_lists")
    fun getGroceryLists(): Flow<List<GroceryList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGroceryList(list: GroceryList)

    @Query("SELECT * FROM grocery_items ORDER BY isDone ASC, createdAt DESC")
    fun getAllItems(): Flow<List<GroceryItem>>

    @Query("SELECT * FROM grocery_items WHERE listId = :listId ORDER BY isDone ASC, createdAt DESC")
    fun getItemsForList(listId: String): Flow<List<GroceryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGroceryItem(item: GroceryItem)

    // ── Chat ──────────────────────────────────────────────────────────────
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getMessages(): Flow<List<ChatMessage>>

    @Insert
    suspend fun insertMessage(message: ChatMessage)
}
