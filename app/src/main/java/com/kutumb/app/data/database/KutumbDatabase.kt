package com.kutumb.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kutumb.app.data.dao.AppDao
import com.kutumb.app.data.model.*

@Database(
    entities = [
        Member::class, Task::class, Rule::class, RuleRating::class,
        Expense::class, Loan::class, LoanPayment::class, Reward::class,
        Memory::class, GroceryList::class, GroceryItem::class, ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KutumbDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
}
