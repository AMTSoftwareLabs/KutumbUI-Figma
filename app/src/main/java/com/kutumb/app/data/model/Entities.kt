package com.kutumb.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey val id: String,
    val name: String,
    val shortName: String,
    val colorHex: String,
    val role: String = "member",   // "admin" | "member"
    val bio: String = "",
    val mood: String = "😊"
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val assignedToId: String,
    val points: Int,
    val deadline: String,          // "2025-06-19"
    val frequency: String,         // Once | Daily | Weekly | Monthly
    val isCompleted: Boolean = false,
    val urgency: Int = 1,          // 1=Normal 2=Urgent 3=Critical
    val category: String = "सफाई",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rules")
data class Rule(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val pointWeight: Int,
    val colorHex: String,
    val bgColorHex: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rule_ratings")
data class RuleRating(
    @PrimaryKey val id: String,
    val ruleId: String,
    val userId: String,
    val followed: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey val id: String,
    val amount: Double,
    val description: String,
    val category: String,
    val categoryColorHex: String,
    val categoryIcon: String,
    val isIncome: Boolean = false,
    val paidById: String,
    val splitType: String = "equal",   // equal | single
    val date: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "loans")
data class Loan(
    @PrimaryKey val id: String,
    val name: String,
    val bank: String,
    val principal: Double,
    val remaining: Double,
    val interestRate: Double,
    val emiAmount: Double,
    val startDate: String,
    val endDate: String,
    val colorHex: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "loan_payments")
data class LoanPayment(
    @PrimaryKey val id: String,
    val loanId: String,
    val amount: Double,
    val date: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rewards")
data class Reward(
    @PrimaryKey val id: String,
    val title: String,
    val emoji: String,
    val description: String,
    val cost: Int,
    val type: String,              // "reward" | "punishment"
    val colorHex: String,
    val bgColorHex: String,
    val redeemedById: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "memories")
data class Memory(
    @PrimaryKey val id: String,
    val emoji: String,
    val caption: String,
    val uploaderId: String,
    val uploaderName: String,
    val uploaderColorHex: String,
    val date: String,
    val isLiked: Boolean = false,
    val bgColorHex: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "grocery_lists")
data class GroceryList(
    @PrimaryKey val id: String,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val bgColorHex: String
)

@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey val id: String,
    val listId: String,
    val text: String,
    val quantity: String = "1",
    val isDone: Boolean = false,
    val addedByName: String,
    val addedByColorHex: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String,
    val text: String,
    val senderId: String,
    val senderName: String,
    val senderColorHex: String,
    val senderShortName: String,
    val isMe: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
