import { useState } from "react";
import {
  AppBar, Toolbar, Box, Typography, Avatar, Card, CardContent, Chip,
  Fab, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, MenuItem, Select, FormControl, InputLabel,
  Tabs, Tab, IconButton, Snackbar, Alert, LinearProgress,
} from "@mui/material";
import {
  Add, CheckCircle, CheckCircleOutline, Delete, EmojiEvents, Close,
} from "@mui/icons-material";
import { ImageWithFallback } from "@/app/components/figma/ImageWithFallback";
import kutumbIcon from "@/imports/Kutumb_icon_512x512.png";
import { useAppTheme } from "@/app/context/ThemeContext";
import { useApp, type Task } from "@/app/context/AppContext";

const CATS = ["सफाई", "धुलाई", "खरीदारी", "खाना", "धर्म", "शिक्षा", "वित्त", "अन्य"];
const FREQS: Task["frequency"][] = ["Daily", "Weekly", "Monthly", "Once"];
const FREQ_LABELS: Record<string, string> = { Daily: "दैनिक", Weekly: "साप्ताहिक", Monthly: "मासिक", Once: "एकबार" };
const URGENCY_COLOR = ["", "#10B981", "#F59E0B", "#EF4444"];
const URGENCY_LABEL = ["", "सामान्य", "जरूरी", "अति जरूरी"];

export default function Karya() {
  const { seed, mode } = useAppTheme();
  const { tasks, members, addTask, toggleTask, deleteTask, loyaltyScores } = useApp();
  const dark = mode === "dark";

  const [tab, setTab] = useState(0);
  const [open, setOpen] = useState(false);
  const [snack, setSnack] = useState<{ open: boolean; msg: string; severity: "success" | "error" | "info" }>({ open: false, msg: "", severity: "success" });
  const [delConfirm, setDelConfirm] = useState<string | null>(null);

  // Form state
  const [form, setForm] = useState({
    title: "", desc: "", assignedTo: "raj", points: "30",
    deadline: "2025-06-20", frequency: "Daily" as Task["frequency"],
    urgency: 1 as Task["urgency"], category: "सफाई",
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  const tabs = [
    { label: "आज", filter: (t: Task) => t.deadline === "2025-06-19" && !t.completed },
    { label: "सभी",  filter: (t: Task) => !t.completed },
    { label: "पूर्ण", filter: (t: Task) => t.completed },
  ];
  const filtered = tasks.filter(tabs[tab].filter);

  const validate = () => {
    const e: Record<string, string> = {};
    if (!form.title.trim()) e.title = "शीर्षक आवश्यक है";
    if (!form.points || isNaN(+form.points) || +form.points < 1) e.points = "अंक सही दर्ज करें";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSave = () => {
    if (!validate()) return;
    addTask({
      title: form.title.trim(), desc: form.desc.trim(),
      assignedTo: form.assignedTo, points: +form.points,
      deadline: form.deadline, frequency: form.frequency,
      urgency: form.urgency, category: form.category, completed: false,
    });
    setOpen(false);
    setForm({ title: "", desc: "", assignedTo: "raj", points: "30", deadline: "2025-06-20", frequency: "Daily", urgency: 1, category: "सफाई" });
    setSnack({ open: true, msg: "कार्य सफलतापूर्वक जोड़ा गया! ✓", severity: "success" });
  };

  const handleToggle = (id: string, completed: boolean) => {
    toggleTask(id);
    setSnack({ open: true, msg: completed ? "कार्य अपूर्ण किया।" : `बधाई! कार्य पूर्ण हुआ 🎉`, severity: completed ? "info" : "success" });
  };

  const handleDelete = (id: string) => {
    deleteTask(id);
    setDelConfirm(null);
    setSnack({ open: true, msg: "कार्य हटाया गया।", severity: "info" });
  };

  const completedPct = tasks.length ? (tasks.filter((t) => t.completed).length / tasks.length) * 100 : 0;

  return (
    <div style={{ minHeight: "100vh", background: dark ? "#0A0F1E" : "#F1F5F9" }}>
      <AppBar position="sticky" sx={{ background: "#0A0F1E", borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
        <Toolbar sx={{ justifyContent: "space-between", minHeight: "52px !important" }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <ImageWithFallback src={kutumbIcon} alt="K" style={{ width: 28, height: 28, objectFit: "contain", borderRadius: 7 }} />
            <Typography sx={{ fontWeight: 800, color: "#FFF", fontSize: "1rem", fontFamily: "Inter, sans-serif" }}>कार्य</Typography>
          </Box>
          <Avatar sx={{ width: 30, height: 30, background: seed.gradient, fontWeight: 800, fontSize: "0.72rem" }}>R</Avatar>
        </Toolbar>
      </AppBar>

      {/* Hero */}
      <Box sx={{
        background: "linear-gradient(160deg,#0A0F1E,#111827)",
        px: 2.5, pt: 2.5, pb: "30px", position: "relative", overflow: "hidden",
        "&::before": { content: '""', position: "absolute", top: -60, right: -60, width: 200, height: 200, borderRadius: "50%", background: `radial-gradient(circle,${seed.primary}18,transparent 70%)` },
        "&::after": { content: '""', position: "absolute", bottom: 0, left: 0, right: 0, height: 28, background: dark ? "#0A0F1E" : "#F1F5F9", borderRadius: "28px 28px 0 0" },
      }}>
        <Box sx={{ display: "grid", gridTemplateColumns: "repeat(3,1fr)", gap: 1.5, mb: 2, position: "relative", zIndex: 1 }}>
          {[
            { icon: "📋", val: String(tasks.length), label: "कुल कार्य", color: seed.primary, bg: `${seed.primary}18` },
            { icon: "✅", val: String(tasks.filter((t) => t.completed).length), label: "पूर्ण", color: "#10B981", bg: "rgba(16,185,129,0.15)" },
            { icon: "⏳", val: String(tasks.filter((t) => !t.completed).length), label: "शेष", color: "#F59E0B", bg: "rgba(245,158,11,0.15)" },
          ].map((s) => (
            <Box key={s.label} sx={{ bgcolor: s.bg, border: `1px solid ${s.color}30`, borderRadius: "14px", p: 1.25 }}>
              <Typography sx={{ fontSize: "1rem" }}>{s.icon}</Typography>
              <Typography sx={{ color: "#FFF", fontWeight: 900, fontSize: "1.2rem", lineHeight: 1.1 }}>{s.val}</Typography>
              <Typography sx={{ color: "#64748B", fontSize: "0.6rem", mt: 0.2 }}>{s.label}</Typography>
            </Box>
          ))}
        </Box>
        <Box sx={{ position: "relative", zIndex: 1 }}>
          <Box sx={{ display: "flex", justifyContent: "space-between", mb: 0.5 }}>
            <Typography sx={{ color: "#64748B", fontSize: "0.72rem" }}>आज की प्रगति</Typography>
            <Typography sx={{ color: "#10B981", fontWeight: 700, fontSize: "0.72rem" }}>{Math.round(completedPct)}%</Typography>
          </Box>
          <LinearProgress variant="determinate" value={completedPct}
            sx={{ height: 8, bgcolor: "rgba(255,255,255,0.08)", "& .MuiLinearProgress-bar": { background: "linear-gradient(90deg,#10B981,#34D399)" } }}
          />
        </Box>
      </Box>

      {/* Tabs */}
      <Box sx={{ bgcolor: dark ? "#111827" : "#FFF", borderBottom: `1px solid ${dark ? "#1E293B" : "#E2E8F0"}`, position: "sticky", top: 52, zIndex: 100 }}>
        <Tabs value={tab} onChange={(_, v) => setTab(v)} variant="fullWidth"
          sx={{ minHeight: 44, "& .MuiTab-root": { minHeight: 44, textTransform: "none", fontWeight: 600, fontSize: "0.82rem", color: dark ? "#475569" : "#64748B" }, "& .Mui-selected": { color: `${seed.primary} !important`, fontWeight: 800 }, "& .MuiTabs-indicator": { bgcolor: seed.primary, height: 2.5, borderRadius: 2 } }}
        >
          {tabs.map((t) => <Tab key={t.label} label={`${t.label} (${tasks.filter(t.filter).length})`} />)}
        </Tabs>
      </Box>

      <Box sx={{ px: 2, pt: 1.5, pb: 12 }}>
        {filtered.length === 0 && (
          <Box sx={{ textAlign: "center", py: 8 }}>
            <Typography sx={{ fontSize: "3.5rem", mb: 2 }}>📭</Typography>
            <Typography sx={{ fontWeight: 800, color: dark ? "#F1F5F9" : "#0F172A", mb: 0.5 }}>कोई कार्य नहीं</Typography>
            <Typography sx={{ fontSize: "0.82rem", color: "#94A3B8" }}>नया कार्य जोड़ने के लिए + दबाएं</Typography>
          </Box>
        )}
        {filtered.map((task) => {
          const member = members.find((m) => m.id === task.assignedTo);
          return (
            <Card key={task.id} sx={{ mb: 1.5, p: 0, opacity: task.completed ? 0.65 : 1, transition: "opacity 0.2s" }}>
              <CardContent sx={{ p: "0 !important" }}>
                <Box sx={{ display: "flex" }}>
                  <Box sx={{ width: 4, borderRadius: "20px 0 0 20px", bgcolor: URGENCY_COLOR[task.urgency], flexShrink: 0 }} />
                  <Box sx={{ flex: 1, p: "14px 16px" }}>
                    <Box sx={{ display: "flex", alignItems: "flex-start", gap: 1.5 }}>
                      <IconButton size="small" sx={{ p: 0.25, mt: 0.1 }} onClick={() => handleToggle(task.id, task.completed)}>
                        {task.completed
                          ? <CheckCircle sx={{ color: "#10B981", fontSize: 22 }} />
                          : <CheckCircleOutline sx={{ color: "#94A3B8", fontSize: 22 }} />}
                      </IconButton>
                      <Box sx={{ flex: 1 }}>
                        <Typography sx={{
                          fontWeight: 700, fontSize: "0.88rem", color: dark ? "#F1F5F9" : "#0F172A",
                          textDecoration: task.completed ? "line-through" : "none", lineHeight: 1.3, mb: 0.4,
                        }}>{task.title}</Typography>
                        {task.desc && <Typography sx={{ fontSize: "0.72rem", color: "#64748B", mb: 0.5 }}>{task.desc}</Typography>}
                        <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.5 }}>
                          <Chip label={task.category} size="small" sx={{ height: 18, fontSize: "0.6rem", bgcolor: `${seed.primary}12`, color: seed.primary, "& .MuiChip-label": { px: 0.75 } }} />
                          <Chip label={FREQ_LABELS[task.frequency]} size="small" sx={{ height: 18, fontSize: "0.6rem", bgcolor: dark ? "#1E293B" : "#F1F5F9", color: "#64748B", "& .MuiChip-label": { px: 0.75 } }} />
                          <Chip label={URGENCY_LABEL[task.urgency]} size="small" sx={{ height: 18, fontSize: "0.6rem", bgcolor: `${URGENCY_COLOR[task.urgency]}15`, color: URGENCY_COLOR[task.urgency], "& .MuiChip-label": { px: 0.75 } }} />
                        </Box>
                      </Box>
                      <Box sx={{ display: "flex", flexDirection: "column", alignItems: "flex-end", gap: 0.75 }}>
                        <Chip
                          icon={<EmojiEvents sx={{ fontSize: "13px !important", color: "#10B981 !important" }} />}
                          label={`+${task.points}`}
                          size="small"
                          sx={{ height: 20, fontSize: "0.62rem", fontWeight: 800, bgcolor: "#10B98115", color: "#10B981", "& .MuiChip-label": { px: 0.5 } }}
                        />
                        <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
                          {member && <Avatar sx={{ width: 20, height: 20, bgcolor: member.color, fontSize: "0.5rem", fontWeight: 800 }}>{member.shortName}</Avatar>}
                          <IconButton size="small" sx={{ p: 0.1 }} onClick={() => setDelConfirm(task.id)}>
                            <Delete sx={{ fontSize: 15, color: "#EF444460" }} />
                          </IconButton>
                        </Box>
                      </Box>
                    </Box>
                    <Typography sx={{ fontSize: "0.65rem", color: "#475569", mt: 0.75 }}>⏰ {task.deadline}</Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          );
        })}
      </Box>

      {/* Add FAB */}
      <Fab onClick={() => setOpen(true)}
        sx={{ position: "fixed", bottom: 88, right: 16, background: seed.gradient, color: "#FFF" }}>
        <Add />
      </Fab>

      {/* Add Task Dialog */}
      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", pb: 0.5 }}>
          <Typography sx={{ fontWeight: 800, fontSize: "1rem" }}>नया कार्य जोड़ें</Typography>
          <IconButton size="small" onClick={() => setOpen(false)}><Close sx={{ fontSize: 18 }} /></IconButton>
        </DialogTitle>
        <DialogContent sx={{ pt: 1.5 }}>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1.75 }}>
            <TextField label="कार्य का शीर्षक *" fullWidth size="small" value={form.title}
              onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
              error={!!errors.title} helperText={errors.title}
            />
            <TextField label="विवरण (वैकल्पिक)" fullWidth size="small" multiline rows={2} value={form.desc}
              onChange={(e) => setForm((f) => ({ ...f, desc: e.target.value }))}
            />
            <Box sx={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 1.5 }}>
              <FormControl size="small" fullWidth>
                <InputLabel>सदस्य</InputLabel>
                <Select label="सदस्य" value={form.assignedTo} onChange={(e) => setForm((f) => ({ ...f, assignedTo: e.target.value }))}>
                  {members.map((m) => <MenuItem key={m.id} value={m.id}>{m.name}</MenuItem>)}
                </Select>
              </FormControl>
              <FormControl size="small" fullWidth>
                <InputLabel>श्रेणी</InputLabel>
                <Select label="श्रेणी" value={form.category} onChange={(e) => setForm((f) => ({ ...f, category: e.target.value }))}>
                  {CATS.map((c) => <MenuItem key={c} value={c}>{c}</MenuItem>)}
                </Select>
              </FormControl>
              <TextField label="अंक *" size="small" type="number" value={form.points}
                onChange={(e) => setForm((f) => ({ ...f, points: e.target.value }))}
                error={!!errors.points} helperText={errors.points}
              />
              <FormControl size="small" fullWidth>
                <InputLabel>आवृत्ति</InputLabel>
                <Select label="आवृत्ति" value={form.frequency} onChange={(e) => setForm((f) => ({ ...f, frequency: e.target.value as Task["frequency"] }))}>
                  {FREQS.map((f) => <MenuItem key={f} value={f}>{FREQ_LABELS[f]}</MenuItem>)}
                </Select>
              </FormControl>
              <TextField label="अंतिम तारीख" size="small" type="date" value={form.deadline}
                onChange={(e) => setForm((f) => ({ ...f, deadline: e.target.value }))}
                InputLabelProps={{ shrink: true }}
              />
              <FormControl size="small" fullWidth>
                <InputLabel>प्राथमिकता</InputLabel>
                <Select label="प्राथमिकता" value={form.urgency} onChange={(e) => setForm((f) => ({ ...f, urgency: e.target.value as Task["urgency"] }))}>
                  <MenuItem value={1}>सामान्य</MenuItem>
                  <MenuItem value={2}>जरूरी</MenuItem>
                  <MenuItem value={3}>अति जरूरी</MenuItem>
                </Select>
              </FormControl>
            </Box>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2.5, gap: 1 }}>
          <Button onClick={() => setOpen(false)} sx={{ color: "#64748B", fontWeight: 600 }}>रद्द करें</Button>
          <Button onClick={handleSave} variant="contained" sx={{ flex: 1, py: 1.1, borderRadius: "12px", background: seed.gradient }}>
            कार्य सहेजें ✓
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirm Dialog */}
      <Dialog open={!!delConfirm} onClose={() => setDelConfirm(null)} maxWidth="xs" fullWidth>
        <DialogTitle><Typography sx={{ fontWeight: 800 }}>कार्य हटाएं?</Typography></DialogTitle>
        <DialogContent><Typography sx={{ fontSize: "0.85rem", color: "#64748B" }}>यह कार्य स्थायी रूप से हट जाएगा।</Typography></DialogContent>
        <DialogActions sx={{ px: 3, pb: 2, gap: 1 }}>
          <Button onClick={() => setDelConfirm(null)} sx={{ color: "#64748B" }}>रद्द करें</Button>
          <Button onClick={() => handleDelete(delConfirm!)} sx={{ bgcolor: "#EF444415", color: "#EF4444", "&:hover": { bgcolor: "#EF444425" } }}>हटाएं</Button>
        </DialogActions>
      </Dialog>

      <Snackbar open={snack.open} autoHideDuration={2500} onClose={() => setSnack((s) => ({ ...s, open: false }))}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }} sx={{ bottom: "88px !important" }}>
        <Alert severity={snack.severity} sx={{ borderRadius: "14px", fontWeight: 700, boxShadow: "0 8px 24px rgba(0,0,0,0.15)" }}>
          {snack.msg}
        </Alert>
      </Snackbar>
    </div>
  );
}
