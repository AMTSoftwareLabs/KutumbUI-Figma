import React, { createContext, useContext, useState, useCallback, useMemo } from "react";

// ── Types ──────────────────────────────────────────────────────────────────

export interface Member {
  id: string; name: string; shortName: string; color: string; role: "admin" | "member";
}
export interface Task {
  id: string; title: string; desc: string; assignedTo: string;
  points: number; deadline: string; frequency: "Once" | "Daily" | "Weekly" | "Monthly";
  completed: boolean; urgency: 1 | 2 | 3; category: string; createdAt: string;
}
export interface Rule {
  id: string; title: string; category: string; pointWeight: number;
  color: string; bg: string; createdAt: string;
}
export interface RuleRating {
  id: string; ruleId: string; userId: string; followed: boolean; createdAt: string;
}
export interface Expense {
  id: string; amount: number; desc: string; category: string;
  categoryColor: string; categoryIcon: string; isIncome: boolean;
  paidBy: string; date: string; split: "equal" | "single"; createdAt: string;
}
export interface Loan {
  id: string; name: string; bank: string; principal: number; remaining: number;
  rate: number; emi: number; startDate: string; endDate: string;
  iconColor: string; createdAt: string;
}
export interface LoanPayment {
  id: string; loanId: string; amount: number; date: string; note: string;
}
export interface Memory {
  id: string; emoji: string; caption: string; uploader: string;
  uploaderColor: string; date: string; liked: boolean; color: string;
}
export interface GroceryItem {
  id: string; text: string; qty: string; done: boolean;
  addedBy: string; addedByColor: string; listId: string;
}
export interface GroceryList {
  id: string; name: string; emoji: string; color: string; bg: string;
}
export interface ChatMessage {
  id: string; text: string; senderId: string; senderName: string;
  senderColor: string; senderShort: string; time: string; isMe: boolean;
}

// ── Initial Data ────────────────────────────────────────────────────────────

export const MEMBERS: Member[] = [
  { id: "raj",    name: "राज",     shortName: "R",  color: "#FF6B35", role: "admin" },
  { id: "priya",  name: "प्रिया",  shortName: "प",  color: "#6366F1", role: "member" },
  { id: "maa",    name: "माँ",     shortName: "म",  color: "#10B981", role: "member" },
  { id: "papa",   name: "पिताजी", shortName: "पि", color: "#F59E0B", role: "member" },
  { id: "didi",   name: "दीदी",   shortName: "द",  color: "#EC4899", role: "member" },
  { id: "aryan",  name: "आर्यन",  shortName: "आ",  color: "#8B5CF6", role: "member" },
];

const RULE_COLORS = [
  { color: "#FF6B35", bg: "#FFF3EE" },
  { color: "#6366F1", bg: "#EEF2FF" },
  { color: "#10B981", bg: "#ECFDF5" },
  { color: "#F59E0B", bg: "#FFFBEB" },
  { color: "#EC4899", bg: "#FDF2F8" },
  { color: "#8B5CF6", bg: "#F5F3FF" },
];

const INIT_TASKS: Task[] = [
  { id: "t1", title: "बर्तन साफ करें",    desc: "रात के खाने के बाद", assignedTo: "raj",   points: 30, deadline: "2025-06-19", frequency: "Daily",   completed: true,  urgency: 1, category: "सफाई",    createdAt: "2025-06-18" },
  { id: "t2", title: "झाड़ू-पोंछा",        desc: "पूरे घर में",        assignedTo: "priya", points: 25, deadline: "2025-06-19", frequency: "Daily",   completed: false, urgency: 2, category: "सफाई",    createdAt: "2025-06-18" },
  { id: "t3", title: "कपड़े धोएं",         desc: "सभी के कपड़े",       assignedTo: "didi",  points: 40, deadline: "2025-06-19", frequency: "Weekly",  completed: false, urgency: 2, category: "धुलाई",   createdAt: "2025-06-18" },
  { id: "t4", title: "पूजा करें",          desc: "सुबह की पूजा",       assignedTo: "maa",   points: 50, deadline: "2025-06-19", frequency: "Daily",   completed: true,  urgency: 1, category: "धर्म",    createdAt: "2025-06-18" },
  { id: "t5", title: "बाज़ार जाएं",        desc: "किराने का सामान",    assignedTo: "papa",  points: 35, deadline: "2025-06-20", frequency: "Weekly",  completed: false, urgency: 3, category: "खरीदारी", createdAt: "2025-06-18" },
  { id: "t6", title: "होमवर्क जाँचें",    desc: "आर्यन का होमवर्क",   assignedTo: "raj",   points: 20, deadline: "2025-06-19", frequency: "Daily",   completed: false, urgency: 1, category: "शिक्षा",  createdAt: "2025-06-18" },
  { id: "t7", title: "बिल जमा करें",      desc: "बिजली और पानी",      assignedTo: "papa",  points: 45, deadline: "2025-06-22", frequency: "Monthly", completed: false, urgency: 3, category: "वित्त",   createdAt: "2025-06-18" },
];

const INIT_RULES: Rule[] = [
  { id: "r1", title: "सुबह 6 बजे उठें",              category: "स्वास्थ्य",   pointWeight: 50, ...RULE_COLORS[3], createdAt: "2025-06-01" },
  { id: "r2", title: "डाइनिंग टेबल पर फोन नहीं",   category: "अनुशासन",     pointWeight: 30, ...RULE_COLORS[0], createdAt: "2025-06-01" },
  { id: "r3", title: "रात का खाना साथ खाएं",        category: "परिवार",      pointWeight: 40, ...RULE_COLORS[4], createdAt: "2025-06-01" },
  { id: "r4", title: "ध्यान 15 मिनट",               category: "स्वास्थ्य",   pointWeight: 35, ...RULE_COLORS[2], createdAt: "2025-06-01" },
  { id: "r5", title: "जूते घर के अंदर नहीं",        category: "स्वच्छता",    pointWeight: 20, ...RULE_COLORS[1], createdAt: "2025-06-01" },
];

const INIT_EXPENSES: Expense[] = [
  { id: "e1", amount: 2500, desc: "किराने का सामान", category: "किराना",      categoryColor: "#FF6B35", categoryIcon: "🛒", isIncome: false, paidBy: "raj",   date: "2025-06-18", split: "equal",  createdAt: "2025-06-18" },
  { id: "e2", amount: 1800, desc: "बिजली बिल",       category: "उपयोगिता",   categoryColor: "#F59E0B", categoryIcon: "⚡", isIncome: false, paidBy: "papa",  date: "2025-06-17", split: "single", createdAt: "2025-06-17" },
  { id: "e3", amount: 1200, desc: "रेस्तरां",         category: "भोजन",        categoryColor: "#10B981", categoryIcon: "🍽️", isIncome: false, paidBy: "raj",   date: "2025-06-16", split: "equal",  createdAt: "2025-06-16" },
  { id: "e4", amount: 3000, desc: "पेट्रोल",          category: "परिवहन",      categoryColor: "#6366F1", categoryIcon: "⛽", isIncome: false, paidBy: "papa",  date: "2025-06-15", split: "single", createdAt: "2025-06-15" },
  { id: "e5", amount: 800,  desc: "दवाईयाँ",          category: "स्वास्थ्य",  categoryColor: "#EC4899", categoryIcon: "💊", isIncome: false, paidBy: "maa",   date: "2025-06-14", split: "single", createdAt: "2025-06-14" },
  { id: "e6", amount: 80000, desc: "वेतन",            category: "आय",          categoryColor: "#10B981", categoryIcon: "💰", isIncome: true,  paidBy: "raj",   date: "2025-06-01", split: "single", createdAt: "2025-06-01" },
];

const INIT_LOANS: Loan[] = [
  { id: "l1", name: "गृह ऋण",        bank: "SBI",   principal: 2500000, remaining: 1800000, rate: 8.5,  emi: 25000, startDate: "जन॰ 2020",  endDate: "दिस॰ 2035", iconColor: "#6366F1", createdAt: "2020-01-01" },
  { id: "l2", name: "कार ऋण",         bank: "HDFC",  principal: 800000,  remaining: 450000,  rate: 10.5, emi: 18000, startDate: "मार्च 2022", endDate: "फर॰ 2027",  iconColor: "#10B981", createdAt: "2022-03-01" },
  { id: "l3", name: "व्यक्तिगत ऋण",  bank: "ICICI", principal: 300000,  remaining: 120000,  rate: 12.0, emi: 7500,  startDate: "जून 2023",   endDate: "मई 2027",   iconColor: "#F59E0B", createdAt: "2023-06-01" },
];

const INIT_MEMORIES: Memory[] = [
  { id: "m1", emoji: "🎂", caption: "माँ का जन्मदिन",    uploader: "राज",    uploaderColor: "#FF6B35", date: "15 जून",  liked: true,  color: "#FFF7ED" },
  { id: "m2", emoji: "🏞️", caption: "पिकनिक पर",         uploader: "दीदी",   uploaderColor: "#EC4899", date: "10 जून",  liked: false, color: "#F0FDF4" },
  { id: "m3", emoji: "🎉", caption: "दीपावली उत्सव",     uploader: "माँ",    uploaderColor: "#10B981", date: "3 जून",   liked: true,  color: "#F5F3FF" },
  { id: "m4", emoji: "🌅", caption: "सुबह की पूजा",      uploader: "पिताजी", uploaderColor: "#F59E0B", date: "1 जून",   liked: false, color: "#EFF6FF" },
  { id: "m5", emoji: "🍽️", caption: "रविवार का खाना",   uploader: "माँ",    uploaderColor: "#10B981", date: "28 मई",  liked: true,  color: "#FFF1F2" },
  { id: "m6", emoji: "🎓", caption: "आर्यन का रिज़ल्ट", uploader: "राज",    uploaderColor: "#FF6B35", date: "20 मई",  liked: true,  color: "#ECFDF5" },
];

const INIT_LISTS: GroceryList[] = [
  { id: "gl1", name: "किराने का सामान", emoji: "🛒", color: "#FF6B35", bg: "#FFF3EE" },
  { id: "gl2", name: "IKEA विशलिस्ट",  emoji: "🛋️", color: "#6366F1", bg: "#EEF2FF" },
  { id: "gl3", name: "वीकेंड प्लान",   emoji: "🎯", color: "#10B981", bg: "#ECFDF5" },
];

const INIT_ITEMS: GroceryItem[] = [
  { id: "gi1", text: "दूध",    qty: "2 लीटर", done: true,  addedBy: "माँ",    addedByColor: "#10B981", listId: "gl1" },
  { id: "gi2", text: "आटा",    qty: "10 kg",  done: true,  addedBy: "राज",    addedByColor: "#FF6B35", listId: "gl1" },
  { id: "gi3", text: "प्याज",  qty: "2 kg",   done: false, addedBy: "प्रिया", addedByColor: "#6366F1", listId: "gl1" },
  { id: "gi4", text: "चावल",   qty: "5 kg",   done: false, addedBy: "पिताजी", addedByColor: "#F59E0B", listId: "gl1" },
  { id: "gi5", text: "दाल",    qty: "2 kg",   done: false, addedBy: "माँ",    addedByColor: "#10B981", listId: "gl1" },
  { id: "gi6", text: "हल्दी",  qty: "100 g",  done: false, addedBy: "माँ",    addedByColor: "#10B981", listId: "gl1" },
];

const INIT_CHATS: ChatMessage[] = [
  { id: "c1", text: "आज का खाना किसने बनाया? 😊",          senderId: "maa",   senderName: "माँ",    senderColor: "#10B981", senderShort: "म",  time: "10:02 AM", isMe: false },
  { id: "c2", text: "मैंने बना दिया, दाल-चावल है।",         senderId: "raj",   senderName: "राज",   senderColor: "#FF6B35", senderShort: "R",  time: "10:05 AM", isMe: true  },
  { id: "c3", text: "वाह बेटा! बहुत अच्छा 🙏 +50 pts!",    senderId: "maa",   senderName: "माँ",    senderColor: "#10B981", senderShort: "म",  time: "10:06 AM", isMe: false },
  { id: "c4", text: "शाम को दूध लाना याद रहे।",             senderId: "papa",  senderName: "पिताजी", senderColor: "#F59E0B", senderShort: "पि", time: "10:30 AM", isMe: false },
  { id: "c5", text: "जी पिताजी, ले आऊँगा।",                 senderId: "raj",   senderName: "राज",   senderColor: "#FF6B35", senderShort: "R",  time: "10:31 AM", isMe: true  },
  { id: "c6", text: "कल परिवार का भोजन साथ करेंगे 🥗",    senderId: "didi",  senderName: "दीदी",   senderColor: "#EC4899", senderShort: "द",  time: "11:00 AM", isMe: false },
];

// ── Loyalty Score Engine ────────────────────────────────────────────────────

function computeScores(
  tasks: Task[],
  rules: Rule[],
  ratings: RuleRating[],
  expenses: Expense[],
  loans: Loan[],
  payments: LoanPayment[],
  chats: ChatMessage[]
): Record<string, number> {
  const scores: Record<string, number> = {};
  MEMBERS.forEach((m) => { scores[m.id] = 100; });

  // Tasks
  tasks.forEach((t) => {
    if (t.completed) {
      scores[t.assignedTo] = (scores[t.assignedTo] || 100) + t.points;
    }
  });

  // Rules
  ratings.forEach((r) => {
    const rule = rules.find((ru) => ru.id === r.ruleId);
    if (!rule) return;
    if (r.followed) {
      scores[r.userId] = (scores[r.userId] || 100) + rule.pointWeight;
    } else {
      scores[r.userId] = (scores[r.userId] || 100) - Math.floor(rule.pointWeight / 2);
    }
  });

  // Expenses — +2 pts per ₹1000 logged
  expenses.filter((e) => !e.isIncome).forEach((e) => {
    scores[e.paidBy] = (scores[e.paidBy] || 100) + Math.floor(e.amount / 1000) * 2;
  });

  // Loan payments — +15 per payment
  payments.forEach((p) => {
    const loan = loans.find((l) => l.id === p.loanId);
    if (loan) {
      // Award points to raj as owner (simplified)
      scores["raj"] = (scores["raj"] || 100) + 15;
    }
  });

  // Chat positive words
  const positive = ["thanks", "great", "proud", "awesome", "अच्छा", "शुक्रिया", "धन्यवाद", "बहुत"];
  chats.forEach((c) => {
    if (positive.some((w) => c.text.toLowerCase().includes(w))) {
      scores[c.senderId] = (scores[c.senderId] || 100) + 5;
    }
  });

  return scores;
}

// ── Context ─────────────────────────────────────────────────────────────────

interface AppContextType {
  members: Member[];
  currentUser: Member;
  tasks: Task[];
  rules: Rule[];
  ratings: RuleRating[];
  expenses: Expense[];
  loans: Loan[];
  loanPayments: LoanPayment[];
  memories: Memory[];
  groceryLists: GroceryList[];
  groceryItems: GroceryItem[];
  chatMessages: ChatMessage[];
  loyaltyScores: Record<string, number>;
  streak: number;
  // Actions
  addTask: (t: Omit<Task, "id" | "createdAt">) => void;
  toggleTask: (id: string) => void;
  deleteTask: (id: string) => void;
  addRule: (r: Omit<Rule, "id" | "createdAt">) => void;
  deleteRule: (id: string) => void;
  rateRule: (ruleId: string, userId: string, followed: boolean) => void;
  addExpense: (e: Omit<Expense, "id" | "createdAt">) => void;
  deleteExpense: (id: string) => void;
  addLoan: (l: Omit<Loan, "id" | "createdAt">) => void;
  addLoanPayment: (p: Omit<LoanPayment, "id">) => void;
  addMemory: (m: Omit<Memory, "id">) => void;
  toggleMemoryLike: (id: string) => void;
  addGroceryItem: (item: Omit<GroceryItem, "id">) => void;
  toggleGroceryItem: (id: string) => void;
  addGroceryList: (l: Omit<GroceryList, "id">) => void;
  sendMessage: (text: string) => void;
}

const AppContext = createContext<AppContextType>(null!);
export const useApp = () => useContext(AppContext);

let _uid = 1000;
const uid = () => String(++_uid);

export function AppProvider({ children }: { children: React.ReactNode }) {
  const [tasks, setTasks]             = useState<Task[]>(INIT_TASKS);
  const [rules, setRules]             = useState<Rule[]>(INIT_RULES);
  const [ratings, setRatings]         = useState<RuleRating[]>([]);
  const [expenses, setExpenses]       = useState<Expense[]>(INIT_EXPENSES);
  const [loans, setLoans]             = useState<Loan[]>(INIT_LOANS);
  const [payments, setPayments]       = useState<LoanPayment[]>([]);
  const [memories, setMemories]       = useState<Memory[]>(INIT_MEMORIES);
  const [lists, setLists]             = useState<GroceryList[]>(INIT_LISTS);
  const [items, setItems]             = useState<GroceryItem[]>(INIT_ITEMS);
  const [chats, setChats]             = useState<ChatMessage[]>(INIT_CHATS);

  const loyaltyScores = useMemo(
    () => computeScores(tasks, rules, ratings, expenses, loans, payments, chats),
    [tasks, rules, ratings, expenses, loans, payments, chats]
  );

  const addTask = useCallback((t: Omit<Task, "id" | "createdAt">) => {
    setTasks((p) => [{ ...t, id: uid(), createdAt: new Date().toISOString().split("T")[0] }, ...p]);
  }, []);
  const toggleTask = useCallback((id: string) => {
    setTasks((p) => p.map((t) => t.id === id ? { ...t, completed: !t.completed } : t));
  }, []);
  const deleteTask = useCallback((id: string) => {
    setTasks((p) => p.filter((t) => t.id !== id));
  }, []);

  const ruleColorIdx = { r1: 3, r2: 0, r3: 4, r4: 2, r5: 1 } as Record<string, number>;
  let _rcIdx = 5;
  const addRule = useCallback((r: Omit<Rule, "id" | "createdAt">) => {
    setRules((p) => [{ ...r, id: uid(), createdAt: new Date().toISOString().split("T")[0] }, ...p]);
  }, []);
  const deleteRule = useCallback((id: string) => {
    setRules((p) => p.filter((r) => r.id !== id));
  }, []);
  const rateRule = useCallback((ruleId: string, userId: string, followed: boolean) => {
    setRatings((p) => [
      ...p.filter((r) => !(r.ruleId === ruleId && r.userId === userId)),
      { id: uid(), ruleId, userId, followed, createdAt: new Date().toISOString() },
    ]);
  }, []);

  const addExpense = useCallback((e: Omit<Expense, "id" | "createdAt">) => {
    setExpenses((p) => [{ ...e, id: uid(), createdAt: new Date().toISOString().split("T")[0] }, ...p]);
  }, []);
  const deleteExpense = useCallback((id: string) => {
    setExpenses((p) => p.filter((e) => e.id !== id));
  }, []);

  const addLoan = useCallback((l: Omit<Loan, "id" | "createdAt">) => {
    setLoans((p) => [{ ...l, id: uid(), createdAt: new Date().toISOString().split("T")[0] }, ...p]);
  }, []);
  const addLoanPayment = useCallback((p: Omit<LoanPayment, "id">) => {
    const pay: LoanPayment = { ...p, id: uid() };
    setPayments((prev) => [pay, ...prev]);
    setLoans((prev) =>
      prev.map((l) => l.id === p.loanId ? { ...l, remaining: Math.max(0, l.remaining - p.amount) } : l)
    );
  }, []);

  const addMemory = useCallback((m: Omit<Memory, "id">) => {
    setMemories((p) => [{ ...m, id: uid() }, ...p]);
  }, []);
  const toggleMemoryLike = useCallback((id: string) => {
    setMemories((p) => p.map((m) => m.id === id ? { ...m, liked: !m.liked } : m));
  }, []);

  const addGroceryItem = useCallback((item: Omit<GroceryItem, "id">) => {
    setItems((p) => [{ ...item, id: uid() }, ...p]);
  }, []);
  const toggleGroceryItem = useCallback((id: string) => {
    setItems((p) => p.map((i) => i.id === id ? { ...i, done: !i.done } : i));
  }, []);
  const addGroceryList = useCallback((l: Omit<GroceryList, "id">) => {
    setLists((p) => [...p, { ...l, id: uid() }]);
  }, []);

  const sendMessage = useCallback((text: string) => {
    const now = new Date();
    const time = now.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit" });
    setChats((p) => [...p, {
      id: uid(), text, senderId: "raj", senderName: "राज",
      senderColor: "#FF6B35", senderShort: "R", time, isMe: true,
    }]);
  }, []);

  return (
    <AppContext.Provider value={{
      members: MEMBERS,
      currentUser: MEMBERS[0],
      tasks, rules, ratings, expenses, loans, loanPayments: payments,
      memories, groceryLists: lists, groceryItems: items, chatMessages: chats,
      loyaltyScores, streak: 12,
      addTask, toggleTask, deleteTask,
      addRule, deleteRule, rateRule,
      addExpense, deleteExpense,
      addLoan, addLoanPayment,
      addMemory, toggleMemoryLike,
      addGroceryItem, toggleGroceryItem, addGroceryList,
      sendMessage,
    }}>
      {children}
    </AppContext.Provider>
  );
}
