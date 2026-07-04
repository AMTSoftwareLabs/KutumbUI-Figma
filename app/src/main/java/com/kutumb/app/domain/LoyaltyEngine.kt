package com.kutumb.app.domain

import androidx.compose.ui.graphics.Color
import com.kutumb.app.data.model.*
import com.kutumb.app.ui.theme.*

// ── Level definition ───────────────────────────────────────────────────────
data class LoyaltyLevel(
    val label: String,
    val emoji: String,
    val minPts: Int,
    val maxPts: Int,
    val color: Color
)

val LOYALTY_LEVELS = listOf(
    LoyaltyLevel("कांस्य",  "🥉",    0,    199, LevelBronze),
    LoyaltyLevel("रजत",    "🥈",  200,    499, LevelSilver),
    LoyaltyLevel("स्वर्ण",  "🥇",  500,    999, LevelGold),
    LoyaltyLevel("हीरा",   "💎", 1000,   1999, LevelDiamond),
    LoyaltyLevel("सम्राट", "👑", 2000, Int.MAX_VALUE, LevelEmperor),
)

fun getLoyaltyLevel(score: Int): LoyaltyLevel =
    LOYALTY_LEVELS.lastOrNull { score >= it.minPts } ?: LOYALTY_LEVELS.first()

fun getNextLevel(score: Int): LoyaltyLevel? =
    LOYALTY_LEVELS.firstOrNull { score < it.minPts }

fun levelProgress(score: Int): Float {
    val cur  = getLoyaltyLevel(score)
    val next = getNextLevel(score) ?: return 1f
    return ((score - cur.minPts).toFloat() / (next.minPts - cur.minPts)).coerceIn(0f, 1f)
}

// ── Score engine ───────────────────────────────────────────────────────────
private val POSITIVE_WORDS = setOf(
    "thanks","great","proud","awesome","wonderful","superb",
    "अच्छा","शुक्रिया","धन्यवाद","बहुत","वाह","शाबाश"
)

fun computeLoyaltyScores(
    memberIds: List<String>,
    tasks: List<Task>,
    rules: List<Rule>,
    ratings: List<RuleRating>,
    expenses: List<Expense>,
    payments: List<LoanPayment>,
    chats: List<ChatMessage>,
    rewards: List<Reward>
): Map<String, Int> {
    val scores = memberIds.associateWith { 100 }.toMutableMap()

    // Tasks: completed → +points
    tasks.filter { it.isCompleted }.forEach { t ->
        scores[t.assignedToId] = (scores[t.assignedToId] ?: 100) + t.points
    }

    // Rules: followed → +weight; broken → -(weight/2)
    ratings.forEach { r ->
        val rule = rules.find { it.id == r.ruleId } ?: return@forEach
        val delta = if (r.followed) rule.pointWeight else -(rule.pointWeight / 2)
        scores[r.userId] = (scores[r.userId] ?: 100) + delta
    }

    // Expenses: +2 pts per ₹1000 logged
    expenses.filter { !it.isIncome }.forEach { e ->
        scores[e.paidById] = (scores[e.paidById] ?: 100) + (e.amount / 1000).toInt() * 2
    }

    // Loan payments: +15 per payment (awarded to payer — extend as needed)
    payments.forEach { _ ->
        val ownerId = memberIds.firstOrNull() ?: return@forEach
        scores[ownerId] = (scores[ownerId] ?: 100) + 15
    }

    // Chat positive sentiment: +5 per matching message
    chats.forEach { msg ->
        val lower = msg.text.lowercase()
        if (POSITIVE_WORDS.any { lower.contains(it) }) {
            scores[msg.senderId] = (scores[msg.senderId] ?: 100) + 5
        }
    }

    // Reward redemptions: deduct cost
    rewards.filter { it.type == "reward" && it.redeemedById != null }.forEach { r ->
        scores[r.redeemedById!!] = maxOf(0, (scores[r.redeemedById] ?: 100) - r.cost)
    }

    return scores
}

// ── Seed data for Room pre-population ─────────────────────────────────────
object SeedData {
    val members = listOf(
        Member("raj",   "राज",    "R",  "#FF6B35", "admin"),
        Member("priya", "प्रिया", "प",  "#6366F1"),
        Member("maa",   "माँ",    "म",  "#10B981"),
        Member("papa",  "पिताजी","पि",  "#F59E0B"),
        Member("didi",  "दीदी",  "द",  "#EC4899"),
        Member("aryan", "आर्यन", "आ",  "#8B5CF6"),
    )

    val tasks = listOf(
        Task("t1","बर्तन साफ करें","रात के खाने के बाद","raj",  30,"2025-06-19","Daily",  true, 1,"सफाई"),
        Task("t2","झाड़ू-पोंछा",  "पूरे घर में",        "priya",25,"2025-06-19","Daily",  false,2,"सफाई"),
        Task("t3","कपड़े धोएं",   "सभी के कपड़े",       "didi", 40,"2025-06-19","Weekly", false,2,"धुलाई"),
        Task("t4","पूजा करें",    "सुबह की पूजा",       "maa",  50,"2025-06-19","Daily",  true, 1,"धर्म"),
        Task("t5","बाज़ार जाएं",  "किराने का सामान",    "papa", 35,"2025-06-20","Weekly", false,3,"खरीदारी"),
        Task("t6","होमवर्क जाँचें","आर्यन का होमवर्क", "raj",  20,"2025-06-19","Daily",  false,1,"शिक्षा"),
    )

    val rules = listOf(
        Rule("r1","सुबह 6 बजे उठें",           "स्वास्थ्य",50,"#F59E0B","#FFFBEB"),
        Rule("r2","डाइनिंग टेबल पर फोन नहीं","अनुशासन", 30,"#FF6B35","#FFF3EE"),
        Rule("r3","रात का खाना साथ खाएं",     "परिवार",  40,"#EC4899","#FDF2F8"),
        Rule("r4","ध्यान 15 मिनट",             "स्वास्थ्य",35,"#10B981","#ECFDF5"),
        Rule("r5","जूते घर के अंदर नहीं",      "स्वच्छता",20,"#6366F1","#EEF2FF"),
    )

    val rewards = listOf(
        Reward("rw1","एक काम छोड़ें","😴","किसी एक काम को छोड़ने का अधिकार",150,"reward","#10B981","#ECFDF5"),
        Reward("rw2","मूवी नाइट","🎬","पूरे परिवार के साथ मूवी देखें",200,"reward","#6366F1","#EEF2FF"),
        Reward("rw3","स्पेशल डिश","🍜","अपनी पसंदीदा डिश बनवाएं",100,"reward","#F59E0B","#FFFBEB"),
        Reward("rw4","एक दिन की छुट्टी","🌴","पूरे दिन घर के काम से छूट",250,"reward","#0EA5E9","#EFF6FF"),
        Reward("rw5","आइसक्रीम खरीदें","🍦","सबके लिए आइसक्रीम खरीदनी होगी",-50,"punishment","#EF4444","#FEF2F2"),
        Reward("rw6","बर्तन धोएं सप्ताह भर","🍽️","पूरे हफ्ते बर्तन धोने होंगे",-30,"punishment","#EF4444","#FEF2F2"),
    )

    val expenses = listOf(
        Expense("e1",2500.0,"किराने का सामान","किराना","#FF6B35","🛒",false,"raj","equal","2025-06-18"),
        Expense("e2",1800.0,"बिजली बिल","उपयोगिता","#F59E0B","⚡",false,"papa","single","2025-06-17"),
        Expense("e3",1200.0,"रेस्तरां","भोजन","#10B981","🍽️",false,"raj","equal","2025-06-16"),
        Expense("e6",80000.0,"वेतन","आय","#10B981","💰",true,"raj","single","2025-06-01"),
    )

    val loans = listOf(
        Loan("l1","गृह ऋण","SBI",2500000.0,1800000.0,8.5,25000.0,"जन॰ 2020","दिस॰ 2035","#6366F1"),
        Loan("l2","कार ऋण","HDFC",800000.0,450000.0,10.5,18000.0,"मार्च 2022","फर॰ 2027","#10B981"),
        Loan("l3","व्यक्तिगत ऋण","ICICI",300000.0,120000.0,12.0,7500.0,"जून 2023","मई 2027","#F59E0B"),
    )

    val memories = listOf(
        Memory("m1","🎂","माँ का जन्मदिन","maa","माँ","#10B981","15 जून",false,"#FFF7ED"),
        Memory("m2","🏞️","पिकनिक पर","didi","दीदी","#EC4899","10 जून",false,"#F0FDF4"),
        Memory("m3","🎉","दीपावली उत्सव","maa","माँ","#10B981","3 जून",true,"#F5F3FF"),
        Memory("m4","🎓","आर्यन का रिज़ल्ट","raj","राज","#FF6B35","20 मई",true,"#ECFDF5"),
    )

    val groceryLists = listOf(
        GroceryList("gl1","किराने का सामान","🛒","#FF6B35","#FFF3EE"),
        GroceryList("gl2","IKEA विशलिस्ट","🛋️","#6366F1","#EEF2FF"),
        GroceryList("gl3","वीकेंड प्लान","🎯","#10B981","#ECFDF5"),
    )

    val groceryItems = listOf(
        GroceryItem("gi1","gl1","दूध","2 लीटर",true,"माँ","#10B981"),
        GroceryItem("gi2","gl1","आटा","10 kg",true,"राज","#FF6B35"),
        GroceryItem("gi3","gl1","प्याज","2 kg",false,"प्रिया","#6366F1"),
        GroceryItem("gi4","gl1","चावल","5 kg",false,"पिताजी","#F59E0B"),
        GroceryItem("gi5","gl1","दाल","2 kg",false,"माँ","#10B981"),
    )

    val messages = listOf(
        ChatMessage("c1","आज का खाना किसने बनाया? 😊","maa","माँ","#10B981","म",false),
        ChatMessage("c2","मैंने बना दिया, दाल-चावल है।","raj","राज","#FF6B35","R",true),
        ChatMessage("c3","वाह! बहुत अच्छा 🙏 +50 pts!","maa","माँ","#10B981","म",false),
        ChatMessage("c4","शाम को दूध लाना याद रहे।","papa","पिताजी","#F59E0B","पि",false),
        ChatMessage("c5","जी पिताजी, ले आऊँगा।","raj","राज","#FF6B35","R",true),
    )
}
