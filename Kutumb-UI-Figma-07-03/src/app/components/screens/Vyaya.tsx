import { useState } from "react";
import {
  AppBar, Toolbar, Box, Typography, Avatar, Card, CardContent, Chip,
  Fab, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, MenuItem, Select, FormControl, InputLabel,
  LinearProgress, IconButton, Snackbar, Alert, Switch, FormControlLabel, Tabs, Tab,
} from "@mui/material";
import { Add, Delete, Close, TrendingUp, TrendingDown, SwapHoriz } from "@mui/icons-material";
import { ImageWithFallback } from "@/app/components/figma/ImageWithFallback";
import kutumbIcon from "@/imports/Kutumb_icon_512x512.png";
import { useAppTheme } from "@/app/context/ThemeContext";
import { useApp, type Expense } from "@/app/context/AppContext";
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from "recharts";

const EXPENSE_CATS = [
  { name: "किराना",    icon: "🛒", color: "#FF6B35" },
  { name: "भोजन",     icon: "🍽️", color: "#10B981" },
  { name: "उपयोगिता", icon: "⚡", color: "#F59E0B" },
  { name: "परिवहन",   icon: "🚗", color: "#6366F1" },
  { name: "स्वास्थ्य", icon: "💊", color: "#EC4899" },
  { name: "शिक्षा",   icon: "📚", color: "#8B5CF6" },
  { name: "मनोरंजन",  icon: "🎬", color: "#0EA5E9" },
  { name: "आय",        icon: "💰", color: "#10B981" },
  { name: "अन्य",      icon: "📦", color: "#94A3B8" },
];

export default function Vyaya() {
  const { seed, mode } = useAppTheme();
  const { expenses, members, addExpense, deleteExpense } = useApp();
  const dark = mode === "dark";

  const [tab, setTab] = useState(0);
  const [open, setOpen] = useState(false);
  const [delConfirm, setDelConfirm] = useState<string | null>(null);
  const [snack, setSnack] = useState<{ open: boolean; msg: string; severity: "success" | "error" | "info" }>({ open: false, msg: "", severity: "success" });

  const [form, setForm] = useState({
    amount: "", desc: "", category: "किराना", paidBy: "raj",
    isIncome: false, split: "equal" as Expense["split"], date: new Date().toISOString().split("T")[0],
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  const totalExp = expenses.filter((e) => !e.isIncome).reduce((s, e) => s + e.amount, 0);
  const totalInc = expenses.filter((e) => e.isIncome).reduce((s, e) => s + e.amount, 0);
  const monthlyLimit = 30000;
  const usagePct = Math.min((totalExp / monthlyLimit) * 100, 100);

  const rajPaid = expenses.filter((e) => !e.isIncome && e.paidBy === "raj").reduce((s, e) => s + e.amount, 0);
  const priyaPaid = expenses.filter((e) => !e.isIncome && e.paidBy === "priya").reduce((s, e) => s + e.amount, 0);

  const catData = EXPENSE_CATS.filter((c) => c.name !== "आय").map((c) => ({
    ...c, value: expenses.filter((e) => e.category === c.name && !e.isIncome).reduce((s, e) => s + e.amount, 0),
  })).filter((c) => c.value > 0);

  const filtered = tab === 0 ? expenses : tab === 1 ? expenses.filter((e) => !e.isIncome) : expenses.filter((e) => e.isIncome);

  const validate = () => {
    const e: Record<string, string> = {};
    if (!form.amount || isNaN(+form.amount) || +form.amount <= 0) e.amount = "सही राशि दर्ज करें";
    if (!form.desc.trim()) e.desc = "विवरण आवश्यक है";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSave = () => {
    if (!validate()) return;
    const cat = EXPENSE_CATS.find((c) => c.name === form.category) || EXPENSE_CATS[0];
    addExpense({
      amount: +form.amount, desc: form.desc.trim(), category: form.category,
      categoryColor: cat.color, categoryIcon: cat.icon,
      isIncome: form.isIncome, paidBy: form.paidBy,
      split: form.split, date: form.date,
    });
    setOpen(false);
    setForm({ amount: "", desc: "", category: "किराना", paidBy: "raj", isIncome: false, split: "equal", date: new Date().toISOString().split("T")[0] });
    setSnack({ open: true, msg: `${form.isIncome ? "आय" : "व्यय"} सफलतापूर्वक जोड़ा गया! ✓`, severity: "success" });
  };

  return (
    <div style={{ minHeight: "100vh", background: dark ? "#0A0F1E" : "#F1F5F9" }}>
      <AppBar position="sticky" sx={{ background: "#0A0F1E", borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
        <Toolbar sx={{ justifyContent: "space-between", minHeight: "52px !important" }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <ImageWithFallback src={kutumbIcon} alt="K" style={{ width: 28, height: 28, objectFit: "contain", borderRadius: 7 }} />
            <Typography sx={{ fontWeight: 800, color: "#FFF", fontSize: "1rem", fontFamily: "Inter, sans-serif" }}>व्यय</Typography>
          </Box>
          <Avatar sx={{ width: 30, height: 30, background: seed.gradient, fontWeight: 800, fontSize: "0.72rem" }}>R</Avatar>
        </Toolbar>
      </AppBar>

      {/* Hero */}
      <Box sx={{
        background: "linear-gradient(160deg,#0A0F1E,#111827)", px: 2.5, pt: 2.5, pb: "30px",
        position: "relative", overflow: "hidden",
        "&::before": { content: '""', position: "absolute", top: -80, right: -40, width: 240, height: 240, borderRadius: "50%", background: `radial-gradient(circle,${seed.primary}18,transparent 65%)` },
        "&::after": { content: '""', position: "absolute", bottom: 0, left: 0, right: 0, height: 28, background: dark ? "#0A0F1E" : "#F1F5F9", borderRadius: "28px 28px 0 0" },
      }}>
        <Typography sx={{ color: "#475569", fontSize: "0.75rem" }}>इस महीने कुल व्यय</Typography>
        <Typography sx={{ color: "#FFF", fontSize: "2.6rem", fontWeight: 900, letterSpacing: -1.5, lineHeight: 1.1, mt: 0.1 }}>
          ₹{(totalExp / 1000).toFixed(1)}K
        </Typography>
        <Box sx={{ display: "flex", gap: 1.5, mt: 2, position: "relative", zIndex: 1 }}>
          <Box sx={{ flex: 1, bgcolor: "rgba(16,185,129,0.12)", border: "1px solid rgba(16,185,129,0.25)", borderRadius: "12px", p: 1.1 }}>
            <Box sx={{ display: "flex", alignItems: "center", gap: 0.5, mb: 0.2 }}>
              <TrendingUp sx={{ color: "#10B981", fontSize: 14 }} />
              <Typography sx={{ color: "#64748B", fontSize: "0.62rem" }}>साझा आय</Typography>
            </Box>
            <Typography sx={{ color: "#10B981", fontWeight: 800, fontSize: "1rem" }}>₹{(totalInc / 1000).toFixed(0)}K</Typography>
          </Box>
          <Box sx={{ flex: 1, bgcolor: "rgba(239,68,68,0.12)", border: "1px solid rgba(239,68,68,0.25)", borderRadius: "12px", p: 1.1 }}>
            <Box sx={{ display: "flex", alignItems: "center", gap: 0.5, mb: 0.2 }}>
              <TrendingDown sx={{ color: "#EF4444", fontSize: 14 }} />
              <Typography sx={{ color: "#64748B", fontSize: "0.62rem" }}>बचत</Typography>
            </Box>
            <Typography sx={{ color: "#EF4444", fontWeight: 800, fontSize: "1rem" }}>₹{((totalInc - totalExp) / 1000).toFixed(1)}K</Typography>
          </Box>
          <Box sx={{ flex: 1, bgcolor: "rgba(245,158,11,0.12)", border: "1px solid rgba(245,158,11,0.25)", borderRadius: "12px", p: 1.1 }}>
            <Typography sx={{ color: "#64748B", fontSize: "0.62rem", mb: 0.2 }}>बजट</Typography>
            <Typography sx={{ color: "#F59E0B", fontWeight: 800, fontSize: "1rem" }}>{usagePct.toFixed(0)}%</Typography>
          </Box>
        </Box>
      </Box>

      {/* Tabs */}
      <Box sx={{ bgcolor: dark ? "#111827" : "#FFF", borderBottom: `1px solid ${dark ? "#1E293B" : "#E2E8F0"}`, position: "sticky", top: 52, zIndex: 100 }}>
        <Tabs value={tab} onChange={(_, v) => setTab(v)} variant="fullWidth"
          sx={{ minHeight: 44, "& .MuiTab-root": { minHeight: 44, textTransform: "none", fontWeight: 600, fontSize: "0.8rem", color: dark ? "#475569" : "#64748B" }, "& .Mui-selected": { color: `${seed.primary} !important`, fontWeight: 800 }, "& .MuiTabs-indicator": { bgcolor: seed.primary, height: 2.5, borderRadius: 2 } }}
        >
          <Tab label="सभी" />
          <Tab label="व्यय" />
          <Tab label="आय" />
        </Tabs>
      </Box>

      <Box sx={{ px: 2, pt: 1.5, pb: 12 }}>
        {/* Budget progress */}
        <Card sx={{ mb: 2 }}>
          <CardContent sx={{ p: "14px !important" }}>
            <Box sx={{ display: "flex", justifyContent: "space-between", mb: 0.75 }}>
              <Typography sx={{ fontSize: "0.82rem", fontWeight: 700, color: dark ? "#F1F5F9" : "#0F172A" }}>मासिक बजट</Typography>
              <Typography sx={{ fontSize: "0.78rem", fontWeight: 700, color: usagePct >= 80 ? "#EF4444" : "#10B981" }}>
                ₹{(totalExp / 1000).toFixed(0)}K / ₹{(monthlyLimit / 1000).toFixed(0)}K
              </Typography>
            </Box>
            <LinearProgress variant="determinate" value={usagePct}
              sx={{ height: 10, mb: 1.5, bgcolor: dark ? "#1E293B" : "#F1F5F9", "& .MuiLinearProgress-bar": { background: usagePct >= 80 ? "linear-gradient(90deg,#F59E0B,#EF4444)" : "linear-gradient(90deg,#10B981,#34D399)" } }}
            />
            {/* Who owes who */}
            <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, p: 1.25, bgcolor: dark ? "#1E293B" : "#F8FAFC", borderRadius: "12px" }}>
              <Avatar sx={{ width: 30, height: 30, background: seed.gradient, fontWeight: 800, fontSize: "0.7rem" }}>R</Avatar>
              <Box sx={{ flex: 1 }}>
                <Typography sx={{ fontSize: "0.7rem", color: dark ? "#94A3B8" : "#64748B" }}>हिसाब-किताब</Typography>
                <Typography sx={{ fontSize: "0.78rem", fontWeight: 700, color: dark ? "#F1F5F9" : "#0F172A" }}>
                  राज ने ₹{((rajPaid - priyaPaid) / 2).toFixed(0)} अधिक चुकाए
                </Typography>
              </Box>
              <Chip label="Settle Up" size="small" onClick={() => setSnack({ open: true, msg: "हिसाब बराबर हो गया! ✓", severity: "success" })}
                sx={{ bgcolor: seed.gradient, color: "#FFF", fontWeight: 700, fontSize: "0.68rem", cursor: "pointer" }}
              />
            </Box>
          </CardContent>
        </Card>

        {/* Pie chart (only on all tab) */}
        {tab === 0 && catData.length > 0 && (
          <Card sx={{ mb: 2 }}>
            <CardContent sx={{ p: "14px !important" }}>
              <Typography sx={{ fontSize: "0.85rem", fontWeight: 700, color: dark ? "#F1F5F9" : "#0F172A", mb: 1 }}>श्रेणी विभाजन</Typography>
              <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                <ResponsiveContainer width={110} height={110}>
                  <PieChart>
                    <Pie data={catData} innerRadius={28} outerRadius={50} dataKey="value" paddingAngle={3}>
                      {catData.map((c, i) => <Cell key={i} fill={c.color} />)}
                    </Pie>
                    <Tooltip formatter={(v: number) => `₹${v.toLocaleString("en-IN")}`} />
                  </PieChart>
                </ResponsiveContainer>
                <Box sx={{ flex: 1, display: "flex", flexDirection: "column", gap: 0.6 }}>
                  {catData.map((c) => (
                    <Box key={c.name} sx={{ display: "flex", alignItems: "center", gap: 0.75 }}>
                      <Box sx={{ width: 7, height: 7, borderRadius: "50%", bgcolor: c.color, flexShrink: 0 }} />
                      <Typography sx={{ flex: 1, fontSize: "0.72rem", color: dark ? "#94A3B8" : "#64748B" }}>{c.name}</Typography>
                      <Typography sx={{ fontSize: "0.72rem", fontWeight: 700, color: dark ? "#F1F5F9" : "#0F172A" }}>₹{(c.value / 1000).toFixed(1)}K</Typography>
                    </Box>
                  ))}
                </Box>
              </Box>
            </CardContent>
          </Card>
        )}

        {/* Transaction list */}
        <Typography sx={{ fontWeight: 800, fontSize: "0.88rem", color: dark ? "#F1F5F9" : "#0F172A", mb: 1.25 }}>
          {tab === 2 ? "आय" : "लेन-देन"} ({filtered.length})
        </Typography>
        {filtered.length === 0 && (
          <Box sx={{ textAlign: "center", py: 6 }}>
            <Typography sx={{ fontSize: "3rem", mb: 1.5 }}>💸</Typography>
            <Typography sx={{ fontWeight: 700, color: dark ? "#F1F5F9" : "#0F172A" }}>कोई रिकॉर्ड नहीं</Typography>
          </Box>
        )}
        <Card>
          {filtered.map((exp, idx) => (
            <Box key={exp.id} sx={{
              display: "flex", alignItems: "center", gap: 1.5, px: 2, py: 1.25,
              borderBottom: idx < filtered.length - 1 ? `1px solid ${dark ? "#1E293B" : "#F8FAFC"}` : "none",
            }}>
              <Box sx={{ width: 40, height: 40, borderRadius: "12px", bgcolor: exp.isIncome ? "#ECFDF5" : `${exp.categoryColor}15`, display: "flex", alignItems: "center", justifyContent: "center", fontSize: "1.1rem", flexShrink: 0 }}>
                {exp.categoryIcon}
              </Box>
              <Box sx={{ flex: 1 }}>
                <Typography sx={{ fontWeight: 600, fontSize: "0.84rem", color: dark ? "#F1F5F9" : "#0F172A" }}>{exp.desc}</Typography>
                <Typography sx={{ fontSize: "0.68rem", color: "#64748B" }}>{exp.category} • {exp.date}</Typography>
              </Box>
              <Box sx={{ textAlign: "right" }}>
                <Typography sx={{ fontWeight: 800, fontSize: "0.9rem", color: exp.isIncome ? "#10B981" : dark ? "#F1F5F9" : "#0F172A" }}>
                  {exp.isIncome ? "+" : "-"}₹{exp.amount.toLocaleString("en-IN")}
                </Typography>
                <Box sx={{ display: "flex", alignItems: "center", gap: 0.5, justifyContent: "flex-end" }}>
                  <Typography sx={{ fontSize: "0.6rem", color: "#64748B" }}>{members.find((m) => m.id === exp.paidBy)?.name}</Typography>
                  <IconButton size="small" sx={{ p: 0.1 }} onClick={() => setDelConfirm(exp.id)}>
                    <Delete sx={{ fontSize: 13, color: "#EF444450" }} />
                  </IconButton>
                </Box>
              </Box>
            </Box>
          ))}
        </Card>
      </Box>

      <Fab onClick={() => setOpen(true)} sx={{ position: "fixed", bottom: 88, right: 16, background: seed.gradient, color: "#FFF" }}>
        <Add />
      </Fab>

      {/* Add Dialog */}
      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", pb: 0.5 }}>
          <Box>
            <Typography sx={{ fontWeight: 800, fontSize: "1rem" }}>नया {form.isIncome ? "आय" : "व्यय"} जोड़ें</Typography>
          </Box>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <FormControlLabel control={<Switch checked={form.isIncome} onChange={(e) => setForm((f) => ({ ...f, isIncome: e.target.checked, category: e.target.checked ? "आय" : "किराना" }))} size="small" sx={{ "& .MuiSwitch-switchBase.Mui-checked": { color: "#10B981" }, "& .MuiSwitch-switchBase.Mui-checked + .MuiSwitch-track": { bgcolor: "#10B981" } }} />}
              label={<Typography sx={{ fontSize: "0.75rem", fontWeight: 700, color: form.isIncome ? "#10B981" : "#64748B" }}>आय</Typography>}
            />
            <IconButton size="small" onClick={() => setOpen(false)}><Close sx={{ fontSize: 18 }} /></IconButton>
          </Box>
        </DialogTitle>
        <DialogContent sx={{ pt: 1.5 }}>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1.75 }}>
            <TextField label="राशि (₹) *" fullWidth size="small" type="number" value={form.amount}
              onChange={(e) => setForm((f) => ({ ...f, amount: e.target.value }))}
              error={!!errors.amount} helperText={errors.amount}
              InputProps={{ startAdornment: <Typography sx={{ mr: 0.5, color: "#64748B" }}>₹</Typography> }}
            />
            <TextField label="विवरण *" fullWidth size="small" value={form.desc}
              onChange={(e) => setForm((f) => ({ ...f, desc: e.target.value }))}
              error={!!errors.desc} helperText={errors.desc}
            />
            <Box sx={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 1.5 }}>
              <FormControl size="small" fullWidth>
                <InputLabel>श्रेणी</InputLabel>
                <Select label="श्रेणी" value={form.category} onChange={(e) => setForm((f) => ({ ...f, category: e.target.value }))}>
                  {EXPENSE_CATS.map((c) => <MenuItem key={c.name} value={c.name}>{c.icon} {c.name}</MenuItem>)}
                </Select>
              </FormControl>
              <FormControl size="small" fullWidth>
                <InputLabel>किसने चुकाया</InputLabel>
                <Select label="किसने चुकाया" value={form.paidBy} onChange={(e) => setForm((f) => ({ ...f, paidBy: e.target.value }))}>
                  {members.map((m) => <MenuItem key={m.id} value={m.id}>{m.name}</MenuItem>)}
                </Select>
              </FormControl>
              <TextField label="तारीख" size="small" type="date" value={form.date}
                onChange={(e) => setForm((f) => ({ ...f, date: e.target.value }))}
                InputLabelProps={{ shrink: true }}
              />
              {!form.isIncome && (
                <FormControl size="small" fullWidth>
                  <InputLabel>बंटवारा</InputLabel>
                  <Select label="बंटवारा" value={form.split} onChange={(e) => setForm((f) => ({ ...f, split: e.target.value as Expense["split"] }))}>
                    <MenuItem value="equal">समान रूप से</MenuItem>
                    <MenuItem value="single">एकल</MenuItem>
                  </Select>
                </FormControl>
              )}
            </Box>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2.5, gap: 1 }}>
          <Button onClick={() => setOpen(false)} sx={{ color: "#64748B" }}>रद्द करें</Button>
          <Button onClick={handleSave} variant="contained" sx={{ flex: 1, py: 1.1, borderRadius: "12px", background: seed.gradient }}>
            {form.isIncome ? "आय" : "व्यय"} सहेजें ✓
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirm */}
      <Dialog open={!!delConfirm} onClose={() => setDelConfirm(null)} maxWidth="xs" fullWidth>
        <DialogTitle><Typography sx={{ fontWeight: 800 }}>रिकॉर्ड हटाएं?</Typography></DialogTitle>
        <DialogContent><Typography sx={{ fontSize: "0.85rem", color: "#64748B" }}>यह सभी बैलेंस को पुनः गणना करेगा।</Typography></DialogContent>
        <DialogActions sx={{ px: 3, pb: 2, gap: 1 }}>
          <Button onClick={() => setDelConfirm(null)} sx={{ color: "#64748B" }}>रद्द</Button>
          <Button onClick={() => { deleteExpense(delConfirm!); setDelConfirm(null); setSnack({ open: true, msg: "रिकॉर्ड हटाया गया।", severity: "info" }); }}
            sx={{ bgcolor: "#FEF2F2", color: "#EF4444" }}>हटाएं</Button>
        </DialogActions>
      </Dialog>

      <Snackbar open={snack.open} autoHideDuration={2500} onClose={() => setSnack((s) => ({ ...s, open: false }))}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }} sx={{ bottom: "88px !important" }}>
        <Alert severity={snack.severity} sx={{ borderRadius: "14px", fontWeight: 700, boxShadow: "0 8px 24px rgba(0,0,0,0.15)" }}>{snack.msg}</Alert>
      </Snackbar>
    </div>
  );
}
