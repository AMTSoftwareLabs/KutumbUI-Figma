package com.kutumb.app.data.repository

import com.kutumb.app.data.dao.AppDao
import com.kutumb.app.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(private val dao: AppDao) {

    val members: Flow<List<Member>>          = dao.getMembers()
    val tasks: Flow<List<Task>>              = dao.getTasks()
    val rules: Flow<List<Rule>>              = dao.getRules()
    val ratings: Flow<List<RuleRating>>      = dao.getRatings()
    val expenses: Flow<List<Expense>>        = dao.getExpenses()
    val loans: Flow<List<Loan>>              = dao.getLoans()
    val payments: Flow<List<LoanPayment>>    = dao.getPayments()
    val rewards: Flow<List<Reward>>          = dao.getRewards()
    val memories: Flow<List<Memory>>         = dao.getMemories()
    val groceryLists: Flow<List<GroceryList>>= dao.getGroceryLists()
    val allItems: Flow<List<GroceryItem>>    = dao.getAllItems()
    val messages: Flow<List<ChatMessage>>    = dao.getMessages()

    // Members
    suspend fun upsertMember(m: Member) = dao.upsertMember(m)

    // Tasks
    suspend fun upsertTask(t: Task)   = dao.upsertTask(t)
    suspend fun deleteTask(t: Task)   = dao.deleteTask(t)

    // Rules
    suspend fun upsertRule(r: Rule)   = dao.upsertRule(r)
    suspend fun deleteRule(r: Rule)   = dao.deleteRule(r)
    suspend fun upsertRating(r: RuleRating) = dao.upsertRating(r)

    // Expenses
    suspend fun upsertExpense(e: Expense) = dao.upsertExpense(e)
    suspend fun deleteExpense(e: Expense) = dao.deleteExpense(e)

    // Loans
    suspend fun upsertLoan(l: Loan)        = dao.upsertLoan(l)
    suspend fun upsertPayment(p: LoanPayment) = dao.upsertPayment(p)

    // Rewards
    suspend fun upsertReward(r: Reward) = dao.upsertReward(r)

    // Memories
    suspend fun upsertMemory(m: Memory) = dao.upsertMemory(m)

    // Lists
    suspend fun upsertGroceryList(l: GroceryList) = dao.upsertGroceryList(l)
    suspend fun upsertGroceryItem(i: GroceryItem) = dao.upsertGroceryItem(i)

    // Chat
    suspend fun insertMessage(m: ChatMessage) = dao.insertMessage(m)

    fun getItemsForList(listId: String): Flow<List<GroceryItem>> =
        dao.getItemsForList(listId)
}
