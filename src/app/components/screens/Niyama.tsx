import { useState } from "react";
import {
  AppBar, Toolbar, Box, Typography, Avatar, Card, CardContent, Chip,
  Fab, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, MenuItem, Select, FormControl, InputLabel,
  IconButton, Snackbar, Alert, Slider,
} from "@mui/material";
import { Add, ThumbUp, ThumbDown, Delete, Close } from "@mui/icons-material";
import { ImageWithFallback } from "@/app/components/figma/ImageWithFallback";
import kutumbIcon from "@/imports/Kutumb_icon_512x512.png";
import { useAppTheme } from "@/app/context/ThemeContext";
import { useApp } from "@/app/context/AppContext";

const RULE_CATS = ["स्वास्थ्य", "अनुशासन", "परिवार", "स्वच्छता", "शिक्षा", "वित्त", "अन्य"];
const PALETTE = [
  { color: "#FF6B35", bg: "#FFF3EE" },
  { color: "#6366F1", bg: "#EEF2FF" },
  { color: "#10B981", bg: "#ECFDF5" },
  { color: "#F59E0B", bg: "#FFFBEB" },
  { color: "#EC4899", bg: "#FDF2F8" },
  { color: "#8B5CF6", bg: "#F5F3FF" },
];

export default function Niyama() {
  const { seed, mode } = useAppTheme();
  const { rules, ratings, members, addRule, deleteRule, rateRule } = useApp();
  const dark = mode === "dark";

  const [open, setOpen] = useState(false);
  const [rateDialog, setRateDialog] = useState<{ open: boolean; ruleId: string; ruleTitle: string } | null>(null);
  const [delConfirm, setDelConfirm] = useState<string | null>(null);
  const [snack, setSnack] = useState<{ open: boolean; msg: string; severity: "success" | "error" | "info" }>({ open: false, msg: "", severity: "success" });

  const [form, setForm] = useState({ title: "", category: "स्वास्थ्य", pointWeight: 30, colorIdx: 0 });
  const [rateUser, setRateUser] = useState("raj");
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = () => {
    const e: Record<string, string> = {};
    if (!form.title.trim()) e.title = "नियम का विवरण आवश्यक है";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSave = () => {
    if (!validate()) return;
    const pal = PALETTE[form.colorIdx];
    addRule({ title: form.title.trim(), category: form.category, pointWeight: form.pointWeight, color: pal.color, bg: pal.bg });
    setOpen(false);
    setForm({ title: "", category: "स्वास्थ्य", pointWeight: 30, colorIdx: 0 });
    setSnack({ open: true, msg: "नियम सफलतापूर्वक जोड़ा गया! ✓", severity: "success" });
  };

  const handleRate = (followed: boolean) => {
    if (!rateDialog) return;
    rateRule(rateDialog.ruleId, rateUser, followed);
    setRateDialog(null);
    const pts = rules.find((r) => r.id === rateDialog.ruleId)?.pointWeight ?? 0;
    setSnack({
      open: true,
      msg: followed ? `बहुत अच्छा! +${pts} अंक दिए गए ✓` : `नियम टूटा — ${Math.floor(pts / 2)} अंक काटे गए`,
      severity: followed ? "success" : "error",
    });
  };

  const handleDelete = (id: string) => {
    deleteRule(id);
    setDelConfirm(null);
    setSnack({ open: true, msg: "नियम हटाया गया।", severity: "info" });
  };

  return (
    <div style={{ minHeight: "100vh", background: dark ? "#0A0F1E" : "#F1F5F9" }}>
      <AppBar position="sticky" sx={{ background: "#0A0F1E", borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
        <Toolbar sx={{ justifyContent: "space-between", minHeight: "52px !important" }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <ImageWithFallback src={kutumbIcon} alt="K" style={{ width: 28, height: 28, objectFit: "contain", borderRadius: 7 }} />
            <Typography sx={{ fontWeight: 800, color: "#FFF", fontSize: "1rem", fontFamily: "Inter, sans-serif" }}>नियम</Typography>
          </Box>
          <Chip label={`${rules.length} नियम`} size="small" sx={{ bgcolor: `${seed.primary}20`, color: seed.primary, fontWeight: 700, fontSize: "0.68rem" }} />
        </Toolbar>
      </AppBar>

      {/* Hero */}
      <Box sx={{
        background: "linear-gradient(160deg,#0A0F1E,#111827)", px: 2.5, pt: 2.5, pb: "30px",
        position: "relative", overflow: "hidden",
        "&::before": { content: '""', position: "absolute", top: -60, right: -60, width: 200, height: 200, borderRadius: "50%", background: `radial-gradient(circle,${seed.primary}18,transparent 70%)` },
        "&::after": { content: '""', position: "absolute", bottom: 0, left: 0, right: 0, height: 28, background: dark ? "#0A0F1E" : "#F1F5F9", borderRadius: "28px 28px 0 0" },
      }}>
        <Box sx={{ display: "grid", gridTemplateColumns: "repeat(3,1fr)", gap: 1.5, position: "relative", zIndex: 1 }}>
          {[
            { icon: "📜", val: String(rules.length), label: "सक्रिय नियम", color: seed.primary, bg: `${seed.primary}18` },
            { icon: "✅", val: String(ratings.filter((r) => r.followed).length), label: "माने गए", color: "#10B981", bg: "rgba(16,185,129,0.15)" },
            { icon: "⚠️", val: String(ratings.filter((r) => !r.followed).length), label: "तोड़े गए", color: "#EF4444", bg: "rgba(239,68,68,0.15)" },
          ].map((s) => (
            <Box key={s.label} sx={{ bgcolor: s.bg, border: `1px solid ${s.color}30`, borderRadius: "14px", p: 1.25 }}>
              <Typography sx={{ fontSize: "1rem" }}>{s.icon}</Typography>
              <Typography sx={{ color: "#FFF", fontWeight: 900, fontSize: "1.2rem", lineHeight: 1.1 }}>{s.val}</Typography>
              <Typography sx={{ color: "#64748B", fontSize: "0.6rem", mt: 0.2 }}>{s.label}</Typography>
            </Box>
          ))}
        </Box>
      </Box>

      <Box sx={{ px: 2, pt: 1.5, pb: 12 }}>
        {rules.length === 0 && (
          <Box sx={{ textAlign: "center", py: 8 }}>
            <Typography sx={{ fontSize: "3.5rem", mb: 2 }}>📋</Typography>
            <Typography sx={{ fontWeight: 800, color: dark ? "#F1F5F9" : "#0F172A", mb: 0.5 }}>कोई नियम नहीं</Typography>
            <Typography sx={{ fontSize: "0.82rem", color: "#94A3B8" }}>नया नियम जोड़ने के लिए + दबाएं</Typography>
          </Box>
        )}
        {rules.map((rule) => (
          <Card key={rule.id} sx={{ mb: 1.5, p: 0 }}>
            <CardContent sx={{ p: "0 !important" }}>
              <Box sx={{ display: "flex" }}>
                <Box sx={{ width: 4, borderRadius: "20px 0 0 20px", bgcolor: rule.color, flexShrink: 0 }} />
                <Box sx={{ flex: 1, p: "14px 16px" }}>
                  <Box sx={{ display: "flex", alignItems: "flex-start", gap: 1.5 }}>
                    <Box sx={{ width: 40, height: 40, borderRadius: "12px", bgcolor: dark ? `${rule.color}20` : rule.bg, color: rule.color, display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0, fontSize: "1.2rem" }}>
                      📜
                    </Box>
                    <Box sx={{ flex: 1 }}>
                      <Typography sx={{ fontWeight: 700, fontSize: "0.88rem", color: dark ? "#F1F5F9" : "#0F172A", lineHeight: 1.3, mb: 0.4 }}>{rule.title}</Typography>
                      <Box sx={{ display: "flex", gap: 0.5, flexWrap: "wrap" }}>
                        <Chip label={rule.category} size="small" sx={{ height: 18, fontSize: "0.6rem", bgcolor: dark ? `${rule.color}20` : rule.bg, color: rule.color, "& .MuiChip-label": { px: 0.75 } }} />
                        <Chip label={`±${rule.pointWeight} pts`} size="small" sx={{ height: 18, fontSize: "0.6rem", bgcolor: dark ? "#1E293B" : "#F1F5F9", color: "#64748B", "& .MuiChip-label": { px: 0.75 } }} />
                      </Box>
                    </Box>
                    <IconButton size="small" sx={{ p: 0.1 }} onClick={() => setDelConfirm(rule.id)}>
                      <Delete sx={{ fontSize: 15, color: "#EF444460" }} />
                    </IconButton>
                  </Box>
                  {/* Action buttons */}
                  <Box sx={{ display: "flex", gap: 1, mt: 1.5 }}>
                    <Button
                      startIcon={<ThumbUp sx={{ fontSize: "15px !important" }} />}
                      fullWidth size="small"
                      onClick={() => setRateDialog({ open: true, ruleId: rule.id, ruleTitle: rule.title })}
                      sx={{ bgcolor: "#ECFDF5", color: "#10B981", fontSize: "0.72rem", py: 0.85, borderRadius: "10px", "&:hover": { bgcolor: "#D1FAE5" } }}
                    >
                      मैंने माना (+{rule.pointWeight})
                    </Button>
                    <Button
                      startIcon={<ThumbDown sx={{ fontSize: "15px !important" }} />}
                      fullWidth size="small"
                      onClick={() => { rateRule(rule.id, "raj", false); setSnack({ open: true, msg: `नियम टूटा — ${Math.floor(rule.pointWeight / 2)} अंक काटे गए`, severity: "error" }); }}
                      sx={{ bgcolor: "#FEF2F2", color: "#EF4444", fontSize: "0.72rem", py: 0.85, borderRadius: "10px", "&:hover": { bgcolor: "#FEE2E2" } }}
                    >
                      किसीने तोड़ा
                    </Button>
                  </Box>
                </Box>
              </Box>
            </CardContent>
          </Card>
        ))}
      </Box>

      <Fab onClick={() => setOpen(true)} sx={{ position: "fixed", bottom: 88, right: 16, background: seed.gradient, color: "#FFF" }}>
        <Add />
      </Fab>

      {/* Add Rule Dialog */}
      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", pb: 0.5 }}>
          <Typography sx={{ fontWeight: 800, fontSize: "1rem" }}>नया नियम जोड़ें</Typography>
          <IconButton size="small" onClick={() => setOpen(false)}><Close sx={{ fontSize: 18 }} /></IconButton>
        </DialogTitle>
        <DialogContent sx={{ pt: 1.5 }}>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
            <TextField label="नियम का विवरण *" fullWidth size="small" multiline rows={2} value={form.title}
              onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
              error={!!errors.title} helperText={errors.title}
            />
            <FormControl size="small" fullWidth>
              <InputLabel>श्रेणी</InputLabel>
              <Select label="श्रेणी" value={form.category} onChange={(e) => setForm((f) => ({ ...f, category: e.target.value }))}>
                {RULE_CATS.map((c) => <MenuItem key={c} value={c}>{c}</MenuItem>)}
              </Select>
            </FormControl>
            <Box>
              <Typography sx={{ fontSize: "0.8rem", fontWeight: 600, mb: 1, color: dark ? "#CBD5E1" : "#374151" }}>
                अंक भार: <span style={{ color: seed.primary }}>{form.pointWeight}</span>
              </Typography>
              <Slider value={form.pointWeight} min={5} max={100} step={5}
                onChange={(_, v) => setForm((f) => ({ ...f, pointWeight: v as number }))}
                sx={{ color: seed.primary }}
              />
            </Box>
            <Box>
              <Typography sx={{ fontSize: "0.8rem", fontWeight: 600, mb: 1, color: dark ? "#CBD5E1" : "#374151" }}>रंग चुनें</Typography>
              <Box sx={{ display: "flex", gap: 1 }}>
                {PALETTE.map((p, i) => (
                  <Box key={i} onClick={() => setForm((f) => ({ ...f, colorIdx: i }))}
                    sx={{ width: 28, height: 28, borderRadius: "50%", bgcolor: p.color, cursor: "pointer", border: form.colorIdx === i ? `3px solid ${dark ? "#FFF" : "#0F172A"}` : "2px solid transparent", boxShadow: form.colorIdx === i ? `0 0 0 2px ${p.color}` : "none", transition: "all 0.15s" }}
                  />
                ))}
              </Box>
            </Box>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2.5, gap: 1 }}>
          <Button onClick={() => setOpen(false)} sx={{ color: "#64748B" }}>रद्द करें</Button>
          <Button onClick={handleSave} variant="contained" sx={{ flex: 1, py: 1.1, borderRadius: "12px", background: seed.gradient }}>
            नियम सहेजें ✓
          </Button>
        </DialogActions>
      </Dialog>

      {/* Rate Rule Dialog */}
      <Dialog open={!!rateDialog?.open} onClose={() => setRateDialog(null)} maxWidth="xs" fullWidth>
        <DialogTitle>
          <Typography sx={{ fontWeight: 800, fontSize: "0.95rem" }}>किसने माना?</Typography>
          <Typography sx={{ fontSize: "0.75rem", color: "#64748B", mt: 0.25 }}>{rateDialog?.ruleTitle}</Typography>
        </DialogTitle>
        <DialogContent>
          <FormControl size="small" fullWidth>
            <InputLabel>सदस्य</InputLabel>
            <Select label="सदस्य" value={rateUser} onChange={(e) => setRateUser(e.target.value)}>
              {members.map((m) => <MenuItem key={m.id} value={m.id}>{m.name}</MenuItem>)}
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2.5, gap: 1 }}>
          <Button onClick={() => setRateDialog(null)} sx={{ color: "#64748B" }}>रद्द</Button>
          <Button onClick={() => handleRate(true)} sx={{ bgcolor: "#ECFDF5", color: "#10B981", flex: 1, borderRadius: "12px", py: 0.9 }}>✓ माना</Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirm */}
      <Dialog open={!!delConfirm} onClose={() => setDelConfirm(null)} maxWidth="xs" fullWidth>
        <DialogTitle><Typography sx={{ fontWeight: 800 }}>नियम हटाएं?</Typography></DialogTitle>
        <DialogContent><Typography sx={{ fontSize: "0.85rem", color: "#64748B" }}>यह नियम स्थायी रूप से हट जाएगा।</Typography></DialogContent>
        <DialogActions sx={{ px: 3, pb: 2, gap: 1 }}>
          <Button onClick={() => setDelConfirm(null)} sx={{ color: "#64748B" }}>रद्द करें</Button>
          <Button onClick={() => handleDelete(delConfirm!)} sx={{ bgcolor: "#FEF2F2", color: "#EF4444" }}>हटाएं</Button>
        </DialogActions>
      </Dialog>

      <Snackbar open={snack.open} autoHideDuration={2500} onClose={() => setSnack((s) => ({ ...s, open: false }))}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }} sx={{ bottom: "88px !important" }}>
        <Alert severity={snack.severity} sx={{ borderRadius: "14px", fontWeight: 700, boxShadow: "0 8px 24px rgba(0,0,0,0.15)" }}>{snack.msg}</Alert>
      </Snackbar>
    </div>
  );
}
