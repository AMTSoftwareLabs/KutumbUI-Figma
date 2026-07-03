import { useState } from "react";
import {
  AppBar, Toolbar, Box, Typography, Avatar, Card, CardContent, Chip,
  Fab, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, MenuItem, Select, FormControl, InputLabel,
  LinearProgress, IconButton, Snackbar, Alert, Tabs, Tab,
} from "@mui/material";
import { Add, Close, Payment } from "@mui/icons-material";
import { ImageWithFallback } from "@/app/components/figma/ImageWithFallback";
import kutumbIcon from "@/imports/Kutumb_icon_512x512.png";
import { useAppTheme } from "@/app/context/ThemeContext";
import { useApp } from "@/app/context/AppContext";

const LOAN_COLORS = ["#6366F1", "#10B981", "#F59E0B", "#EC4899", "#0EA5E9", "#FF6B35"];

export default function Rina() {
  const { seed, mode } = useAppTheme();
  const { loans, loanPayments, addLoan, addLoanPayment } = useApp();
  const dark = mode === "dark";

  const [tab, setTab] = useState(0);
  const [addOpen, setAddOpen] = useState(false);
  const [payOpen, setPayOpen] = useState<string | null>(null);
  const [snack, setSnack] = useState<{ open: boolean; msg: string; severity: "success" | "error" | "info" }>({ open: false, msg: "", severity: "success" });

  const [loanForm, setLoanForm] = useState({ name: "", bank: "", principal: "", rate: "", emi: "", startDate: "", endDate: "", colorIdx: 0 });
  const [payForm, setPayForm] = useState({ amount: "", note: "", date: new Date().toISOString().split("T")[0] });
  const [loanErrors, setLoanErrors] = useState<Record<string, string>>({});
  const [payErrors, setPayErrors] = useState<Record<string, string>>({});

  const totalDebt = loans.reduce((s, l) => s + l.remaining, 0);
  const totalEMI = loans.reduce((s, l) => s + l.emi, 0);
  const totalPrincipal = loans.reduce((s, l) => s + l.principal, 0);
  const overallPct = totalPrincipal > 0 ? ((totalPrincipal - totalDebt) / totalPrincipal) * 100 : 0;

  const validateLoan = () => {
    const e: Record<string, string> = {};
    if (!loanForm.name.trim()) e.name = "ऋण का नाम आवश्यक है";
    if (!loanForm.principal || +loanForm.principal <= 0) e.principal = "मूलधन सही दर्ज करें";
    if (!loanForm.rate || +loanForm.rate <= 0) e.rate = "ब्याज दर सही दर्ज करें";
    if (!loanForm.emi || +loanForm.emi <= 0) e.emi = "EMI सही दर्ज करें";
    setLoanErrors(e);
    return Object.keys(e).length === 0;
  };

  const validatePay = () => {
    const e: Record<string, string> = {};
    if (!payForm.amount || +payForm.amount <= 0) e.amount = "राशि सही दर्ज करें";
    setPayErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleAddLoan = () => {
    if (!validateLoan()) return;
    addLoan({
      name: loanForm.name.trim(), bank: loanForm.bank.trim() || "बैंक",
      principal: +loanForm.principal, remaining: +loanForm.principal,
      rate: +loanForm.rate, emi: +loanForm.emi,
      startDate: loanForm.startDate || "2025", endDate: loanForm.endDate || "2030",
      iconColor: LOAN_COLORS[loanForm.colorIdx],
    });
    setAddOpen(false);
    setLoanForm({ name: "", bank: "", principal: "", rate: "", emi: "", startDate: "", endDate: "", colorIdx: 0 });
    setSnack({ open: true, msg: "ऋण सफलतापूर्वक जोड़ा गया! ✓", severity: "success" });
  };

  const handlePay = () => {
    if (!validatePay() || !payOpen) return;
    addLoanPayment({ loanId: payOpen, amount: +payForm.amount, date: payForm.date, note: payForm.note });
    setPayOpen(null);
    setPayForm({ amount: "", note: "", date: new Date().toISOString().split("T")[0] });
    setSnack({ open: true, msg: `₹${payForm.amount} भुगतान सफल! +15 pts 🎉`, severity: "success" });
  };

  return (
    <div style={{ minHeight: "100vh", background: dark ? "#0A0F1E" : "#F1F5F9" }}>
      <AppBar position="sticky" sx={{ background: "#0A0F1E", borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
        <Toolbar sx={{ justifyContent: "space-between", minHeight: "52px !important" }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <ImageWithFallback src={kutumbIcon} alt="K" style={{ width: 28, height: 28, objectFit: "contain", borderRadius: 7 }} />
            <Typography sx={{ fontWeight: 800, color: "#FFF", fontSize: "1rem", fontFamily: "Inter, sans-serif" }}>ऋण</Typography>
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
        <Typography sx={{ color: "#475569", fontSize: "0.75rem" }}>कुल सक्रिय ऋण</Typography>
        <Typography sx={{ color: "#FFF", fontSize: "2.4rem", fontWeight: 900, letterSpacing: -1.5, lineHeight: 1.1, mt: 0.1 }}>
          ₹{(totalDebt / 100000).toFixed(1)}L
        </Typography>
        <Box sx={{ mt: 1.5, mb: 0.75, position: "relative", zIndex: 1 }}>
          <Box sx={{ display: "flex", justifyContent: "space-between", mb: 0.5 }}>
            <Typography sx={{ color: "#64748B", fontSize: "0.72rem" }}>संपूर्ण प्रगति</Typography>
            <Typography sx={{ color: "#10B981", fontWeight: 800, fontSize: "0.72rem" }}>{overallPct.toFixed(1)}% चुकाया</Typography>
          </Box>
          <LinearProgress variant="determinate" value={overallPct}
            sx={{ height: 8, bgcolor: "rgba(255,255,255,0.08)", "& .MuiLinearProgress-bar": { background: "linear-gradient(90deg,#10B981,#34D399)" } }}
          />
        </Box>
        <Box sx={{ display: "grid", gridTemplateColumns: "repeat(3,1fr)", gap: 1.25, mt: 2, position: "relative", zIndex: 1 }}>
          {[
            { icon: "📅", val: `₹${(totalEMI / 1000).toFixed(0)}K`, label: "मासिक EMI", color: "#F59E0B", bg: "rgba(245,158,11,0.15)" },
            { icon: "🏦", val: String(loans.length), label: "ऋण संख्या", color: "#6366F1", bg: "rgba(99,102,241,0.15)" },
            { icon: "💳", val: String(loanPayments.length), label: "भुगतान", color: "#10B981", bg: "rgba(16,185,129,0.15)" },
          ].map((s) => (
            <Box key={s.label} sx={{ bgcolor: s.bg, border: `1px solid ${s.color}30`, borderRadius: "12px", p: 1 }}>
              <Typography sx={{ fontSize: "0.9rem" }}>{s.icon}</Typography>
              <Typography sx={{ color: "#FFF", fontWeight: 900, fontSize: "1rem", lineHeight: 1.1 }}>{s.val}</Typography>
              <Typography sx={{ color: "#64748B", fontSize: "0.58rem", mt: 0.1 }}>{s.label}</Typography>
            </Box>
          ))}
        </Box>
      </Box>

      {/* Tabs */}
      <Box sx={{ bgcolor: dark ? "#111827" : "#FFF", borderBottom: `1px solid ${dark ? "#1E293B" : "#E2E8F0"}`, position: "sticky", top: 52, zIndex: 100 }}>
        <Tabs value={tab} onChange={(_, v) => setTab(v)} variant="fullWidth"
          sx={{ minHeight: 44, "& .MuiTab-root": { minHeight: 44, textTransform: "none", fontWeight: 600, fontSize: "0.82rem", color: dark ? "#475569" : "#64748B" }, "& .Mui-selected": { color: `${seed.primary} !important`, fontWeight: 800 }, "& .MuiTabs-indicator": { bgcolor: seed.primary, height: 2.5, borderRadius: 2 } }}
        >
          <Tab label="दृश्य" />
          <Tab label="सक्रिय ऋण" />
        </Tabs>
      </Box>

      <Box sx={{ px: 2, pt: 1.5, pb: 12 }}>
        {loans.length === 0 && (
          <Box sx={{ textAlign: "center", py: 8 }}>
            <Typography sx={{ fontSize: "3.5rem", mb: 2 }}>🏦</Typography>
            <Typography sx={{ fontWeight: 800, color: dark ? "#F1F5F9" : "#0F172A", mb: 0.5 }}>कोई ऋण नहीं</Typography>
            <Typography sx={{ fontSize: "0.82rem", color: "#94A3B8" }}>नया ऋण जोड़ने के लिए + दबाएं</Typography>
          </Box>
        )}
        {loans.map((loan) => {
          const pct = ((loan.principal - loan.remaining) / loan.principal) * 100;
          return (
            <Card key={loan.id} sx={{ mb: 1.5, p: 0 }}>
              <CardContent sx={{ p: "0 !important" }}>
                <Box sx={{ display: "flex" }}>
                  <Box sx={{ width: 4, borderRadius: "20px 0 0 20px", bgcolor: loan.iconColor, flexShrink: 0 }} />
                  <Box sx={{ flex: 1, p: "14px 16px" }}>
                    <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", mb: 1.25 }}>
                      <Box>
                        <Typography sx={{ fontWeight: 800, fontSize: "0.92rem", color: dark ? "#F1F5F9" : "#0F172A" }}>{loan.name}</Typography>
                        <Box sx={{ display: "flex", gap: 0.5, mt: 0.4 }}>
                          <Chip label={loan.bank} size="small" sx={{ height: 18, fontSize: "0.6rem", bgcolor: `${loan.iconColor}15`, color: loan.iconColor, "& .MuiChip-label": { px: 0.75 } }} />
                          <Chip label={`${loan.rate}% ब्याज`} size="small" sx={{ height: 18, fontSize: "0.6rem", bgcolor: dark ? "#1E293B" : "#F1F5F9", color: "#64748B", "& .MuiChip-label": { px: 0.75 } }} />
                        </Box>
                      </Box>
                      <Chip label="सक्रिय" size="small" sx={{ bgcolor: "#ECFDF5", color: "#10B981", fontWeight: 700, fontSize: "0.65rem" }} />
                    </Box>
                    <Box sx={{ display: "flex", justifyContent: "space-between", mb: 0.5 }}>
                      <Typography sx={{ fontSize: "0.7rem", color: "#64748B" }}>₹{(loan.remaining / 100000).toFixed(1)}L शेष</Typography>
                      <Typography sx={{ fontSize: "0.7rem", fontWeight: 700, color: loan.iconColor }}>{pct.toFixed(1)}%</Typography>
                    </Box>
                    <LinearProgress variant="determinate" value={pct}
                      sx={{ height: 7, mb: 1.25, bgcolor: dark ? "#1E293B" : "#F1F5F9", "& .MuiLinearProgress-bar": { bgcolor: loan.iconColor } }}
                    />
                    <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                      <Box>
                        <Typography sx={{ fontSize: "0.65rem", color: "#64748B" }}>{loan.startDate} – {loan.endDate}</Typography>
                        <Chip label={`EMI ₹${(loan.emi / 1000).toFixed(0)}K/माह`} size="small" sx={{ height: 18, mt: 0.3, fontSize: "0.6rem", bgcolor: `${loan.iconColor}12`, color: loan.iconColor, "& .MuiChip-label": { px: 0.75 } }} />
                      </Box>
                      {tab === 1 && (
                        <Button
                          startIcon={<Payment sx={{ fontSize: "15px !important" }} />}
                          size="small"
                          onClick={() => setPayOpen(loan.id)}
                          sx={{ background: seed.gradient, color: "#FFF", fontWeight: 700, borderRadius: "10px", fontSize: "0.72rem", px: 1.5, py: 0.7 }}
                        >
                          भुगतान
                        </Button>
                      )}
                    </Box>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          );
        })}

        {/* Payment history */}
        {tab === 1 && loanPayments.length > 0 && (
          <>
            <Typography sx={{ fontWeight: 800, fontSize: "0.88rem", color: dark ? "#F1F5F9" : "#0F172A", mt: 2, mb: 1 }}>📜 भुगतान इतिहास</Typography>
            <Card>
              {loanPayments.map((p, i) => (
                <Box key={p.id} sx={{
                  display: "flex", alignItems: "center", px: 2, py: 1.1, gap: 1.5,
                  borderBottom: i < loanPayments.length - 1 ? `1px solid ${dark ? "#1E293B" : "#F8FAFC"}` : "none",
                }}>
                  <Box sx={{ width: 36, height: 36, borderRadius: "10px", bgcolor: "#ECFDF5", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "1rem" }}>💳</Box>
                  <Box sx={{ flex: 1 }}>
                    <Typography sx={{ fontWeight: 600, fontSize: "0.82rem", color: dark ? "#F1F5F9" : "#0F172A" }}>{loans.find((l) => l.id === p.loanId)?.name}</Typography>
                    <Typography sx={{ fontSize: "0.68rem", color: "#64748B" }}>{p.date}{p.note ? ` • ${p.note}` : ""}</Typography>
                  </Box>
                  <Typography sx={{ fontWeight: 800, fontSize: "0.88rem", color: "#10B981" }}>-₹{p.amount.toLocaleString("en-IN")}</Typography>
                </Box>
              ))}
            </Card>
          </>
        )}
      </Box>

      <Fab onClick={() => setAddOpen(true)} sx={{ position: "fixed", bottom: 88, right: 16, background: seed.gradient, color: "#FFF" }}>
        <Add />
      </Fab>

      {/* Add Loan Dialog */}
      <Dialog open={addOpen} onClose={() => setAddOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", pb: 0.5 }}>
          <Typography sx={{ fontWeight: 800, fontSize: "1rem" }}>नया ऋण जोड़ें</Typography>
          <IconButton size="small" onClick={() => setAddOpen(false)}><Close sx={{ fontSize: 18 }} /></IconButton>
        </DialogTitle>
        <DialogContent sx={{ pt: 1.5 }}>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1.75 }}>
            <TextField label="ऋण का नाम *" fullWidth size="small" value={loanForm.name}
              onChange={(e) => setLoanForm((f) => ({ ...f, name: e.target.value }))}
              error={!!loanErrors.name} helperText={loanErrors.name}
            />
            <TextField label="बैंक / ऋणदाता" fullWidth size="small" value={loanForm.bank}
              onChange={(e) => setLoanForm((f) => ({ ...f, bank: e.target.value }))}
            />
            <Box sx={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 1.5 }}>
              <TextField label="मूलधन (₹) *" size="small" type="number" value={loanForm.principal}
                onChange={(e) => setLoanForm((f) => ({ ...f, principal: e.target.value }))}
                error={!!loanErrors.principal} helperText={loanErrors.principal}
              />
              <TextField label="ब्याज दर (%) *" size="small" type="number" value={loanForm.rate}
                onChange={(e) => setLoanForm((f) => ({ ...f, rate: e.target.value }))}
                error={!!loanErrors.rate} helperText={loanErrors.rate}
              />
              <TextField label="मासिक EMI (₹) *" size="small" type="number" value={loanForm.emi}
                onChange={(e) => setLoanForm((f) => ({ ...f, emi: e.target.value }))}
                error={!!loanErrors.emi} helperText={loanErrors.emi}
              />
              <TextField label="शुरुआत" size="small" value={loanForm.startDate}
                onChange={(e) => setLoanForm((f) => ({ ...f, startDate: e.target.value }))}
                placeholder="जन॰ 2025"
              />
            </Box>
            <Box>
              <Typography sx={{ fontSize: "0.8rem", fontWeight: 600, mb: 1, color: dark ? "#CBD5E1" : "#374151" }}>रंग चुनें</Typography>
              <Box sx={{ display: "flex", gap: 1 }}>
                {LOAN_COLORS.map((c, i) => (
                  <Box key={i} onClick={() => setLoanForm((f) => ({ ...f, colorIdx: i }))}
                    sx={{ width: 28, height: 28, borderRadius: "50%", bgcolor: c, cursor: "pointer", border: loanForm.colorIdx === i ? `3px solid ${dark ? "#FFF" : "#0F172A"}` : "2px solid transparent", boxShadow: loanForm.colorIdx === i ? `0 0 0 2px ${c}` : "none", transition: "all 0.15s" }}
                  />
                ))}
              </Box>
            </Box>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2.5, gap: 1 }}>
          <Button onClick={() => setAddOpen(false)} sx={{ color: "#64748B" }}>रद्द करें</Button>
          <Button onClick={handleAddLoan} variant="contained" sx={{ flex: 1, py: 1.1, borderRadius: "12px", background: seed.gradient }}>
            ऋण सहेजें ✓
          </Button>
        </DialogActions>
      </Dialog>

      {/* Payment Dialog */}
      <Dialog open={!!payOpen} onClose={() => setPayOpen(null)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", pb: 0.5 }}>
          <Box>
            <Typography sx={{ fontWeight: 800, fontSize: "0.95rem" }}>भुगतान करें</Typography>
            <Typography sx={{ fontSize: "0.72rem", color: "#64748B" }}>{loans.find((l) => l.id === payOpen)?.name}</Typography>
          </Box>
          <IconButton size="small" onClick={() => setPayOpen(null)}><Close sx={{ fontSize: 18 }} /></IconButton>
        </DialogTitle>
        <DialogContent sx={{ pt: 1.5 }}>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1.75 }}>
            <TextField label="राशि (₹) *" fullWidth size="small" type="number" value={payForm.amount}
              onChange={(e) => setPayForm((f) => ({ ...f, amount: e.target.value }))}
              error={!!payErrors.amount} helperText={payErrors.amount}
              InputProps={{ startAdornment: <Typography sx={{ mr: 0.5, color: "#64748B" }}>₹</Typography> }}
            />
            <TextField label="तारीख" size="small" type="date" fullWidth value={payForm.date}
              onChange={(e) => setPayForm((f) => ({ ...f, date: e.target.value }))}
              InputLabelProps={{ shrink: true }}
            />
            <TextField label="नोट (वैकल्पिक)" fullWidth size="small" value={payForm.note}
              onChange={(e) => setPayForm((f) => ({ ...f, note: e.target.value }))}
            />
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2.5, gap: 1 }}>
          <Button onClick={() => setPayOpen(null)} sx={{ color: "#64748B" }}>रद्द</Button>
          <Button onClick={handlePay} sx={{ flex: 1, py: 1.1, borderRadius: "12px", background: seed.gradient, color: "#FFF", fontWeight: 700 }}>
            भुगतान करें ✓
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar open={snack.open} autoHideDuration={2500} onClose={() => setSnack((s) => ({ ...s, open: false }))}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }} sx={{ bottom: "88px !important" }}>
        <Alert severity={snack.severity} sx={{ borderRadius: "14px", fontWeight: 700, boxShadow: "0 8px 24px rgba(0,0,0,0.15)" }}>{snack.msg}</Alert>
      </Snackbar>
    </div>
  );
}
