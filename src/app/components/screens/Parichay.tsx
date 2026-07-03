import { useState } from "react";
import {
  AppBar, Toolbar, Box, Typography, Avatar, Card, CardContent, Chip,
  Switch, Divider, Button, List, ListItem, ListItemText, IconButton, LinearProgress, Dialog, DialogTitle, DialogContent, DialogActions, TextField, Snackbar, Alert,
} from "@mui/material";
import { LocalFlorist, QrCode2, History, DarkMode, WbSunny, ChevronRight, EmojiEvents, Edit, Close } from "@mui/icons-material";
import { ImageWithFallback } from "@/app/components/figma/ImageWithFallback";
import kutumbIcon from "@/imports/Kutumb_icon_512x512.png";
import { useAppTheme, SEEDS } from "@/app/context/ThemeContext";
import { useApp } from "@/app/context/AppContext";

export default function Parichay() {
  const { seed, mode, toggleMode, setSeed } = useAppTheme();
  const { members, loyaltyScores } = useApp();
  const dark = mode === "dark";

  const [editOpen, setEditOpen] = useState(false);
  const [name, setName] = useState("राज शर्मा");
  const [bio, setBio] = useState("परिवार का मुखिया 🏠");
  const [snack, setSnack] = useState(false);

  const myScore = loyaltyScores["raj"] ?? 100;
  const nextLevel = 2000;
  const pct = Math.min((myScore / nextLevel) * 100, 100);

  const activityLog = [
    { text: "बर्तन साफ किए", pts: "+30", color: "#10B981", time: "आज" },
    { text: "किराने का सामान लाया", pts: "+50", color: "#10B981", time: "कल" },
    { text: "नियम तोड़ा", pts: "-15", color: "#EF4444", time: "3 दिन पहले" },
    { text: "ध्यान किया", pts: "+35", color: "#10B981", time: "4 दिन पहले" },
  ];

  return (
    <div style={{ minHeight: "100vh", background: dark ? "#0A0F1E" : "#F1F5F9" }}>
      <AppBar position="sticky" sx={{ background: "#0A0F1E", borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
        <Toolbar sx={{ justifyContent: "space-between", minHeight: "52px !important" }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <ImageWithFallback src={kutumbIcon} alt="K" style={{ width: 28, height: 28, objectFit: "contain", borderRadius: 7 }} />
            <Typography sx={{ fontWeight: 800, color: "#FFF", fontSize: "1rem", fontFamily: "Inter, sans-serif" }}>परिचय</Typography>
          </Box>
          <IconButton size="small" sx={{ color: "#475569" }} onClick={() => setEditOpen(true)}>
            <Edit sx={{ fontSize: 18 }} />
          </IconButton>
        </Toolbar>
      </AppBar>

      <Box sx={{ px: 2, pt: 3, pb: 12 }}>
        {/* Profile header */}
        <Box sx={{ display: "flex", alignItems: "center", gap: 2, mb: 3 }}>
          <Avatar sx={{ width: 68, height: 68, background: seed.gradient, fontSize: "1.8rem", fontWeight: 900, boxShadow: `0 4px 20px ${seed.glowColor}` }}>
            R
          </Avatar>
          <Box sx={{ flex: 1 }}>
            <Typography sx={{ fontWeight: 900, fontSize: "1.15rem", color: dark ? "#F1F5F9" : "#0F172A", letterSpacing: -0.3 }}>{name}</Typography>
            <Typography sx={{ fontSize: "0.78rem", color: "#64748B" }}>{bio}</Typography>
            <Box sx={{ display: "flex", gap: 0.75, mt: 0.5 }}>
              <Chip label="🥈 रजत सदस्य" size="small" sx={{ bgcolor: dark ? "#1E293B" : "#F1F5F9", color: dark ? "#94A3B8" : "#64748B", fontWeight: 700, fontSize: "0.65rem" }} />
              <Chip label={`${seed.emoji} ${seed.hindiLabel}`} size="small" sx={{ bgcolor: `${seed.primary}18`, color: seed.primary, fontWeight: 700, fontSize: "0.65rem" }} />
            </Box>
          </Box>
          <ChevronRight sx={{ color: dark ? "#334155" : "#CBD5E1" }} />
        </Box>

        {/* Loyalty card */}
        <Card sx={{ mb: 2, background: "linear-gradient(160deg,#0A0F1E,#111827)", border: "none" }}>
          <CardContent sx={{ p: "18px !important" }}>
            <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 1.75 }}>
              <LocalFlorist sx={{ color: seed.primary, fontSize: 20 }} />
              <Typography sx={{ color: "#FFF", fontWeight: 800, fontSize: "0.88rem" }}>कुटुम्ब निष्ठा वाटिका</Typography>
            </Box>
            <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "flex-end", mb: 1.25 }}>
              <Box>
                <Typography sx={{ color: "#475569", fontSize: "0.68rem" }}>कुल अंक</Typography>
                <Typography sx={{ color: "#FFF", fontSize: "2rem", fontWeight: 900, letterSpacing: -1, lineHeight: 1 }}>{myScore}</Typography>
              </Box>
              <Box sx={{ textAlign: "right" }}>
                <Chip icon={<EmojiEvents sx={{ fontSize: "12px !important", color: "#F59E0B !important" }} />}
                  label="स्वर्ण के लिए" size="small"
                  sx={{ bgcolor: "rgba(245,158,11,0.15)", color: "#F59E0B", fontWeight: 700, fontSize: "0.62rem" }}
                />
                <Typography sx={{ color: "#475569", fontSize: "0.65rem", mt: 0.4 }}>{(nextLevel - myScore)} अंक और चाहिए</Typography>
              </Box>
            </Box>
            <Box sx={{ height: 8, bgcolor: "rgba(255,255,255,0.07)", borderRadius: 4, overflow: "hidden", mb: 0.75 }}>
              <Box sx={{ width: `${pct}%`, height: "100%", background: seed.gradient, borderRadius: 4, transition: "width 0.5s ease" }} />
            </Box>
            <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1.75 }}>
              <Typography sx={{ color: "#334155", fontSize: "0.62rem" }}>0</Typography>
              <Typography sx={{ color: "#334155", fontSize: "0.62rem" }}>{nextLevel}</Typography>
            </Box>
            <Button startIcon={<History />} fullWidth size="small"
              sx={{ borderRadius: "12px", border: "1px solid rgba(255,255,255,0.08)", color: "#94A3B8", textTransform: "none", fontWeight: 600, py: 0.85 }}
            >
              पुरस्कार इतिहास
            </Button>
          </CardContent>
        </Card>

        {/* ─── Theme Picker ─── */}
        <Card sx={{ mb: 2 }}>
          <CardContent sx={{ p: "16px !important" }}>
            <Typography sx={{ fontWeight: 800, fontSize: "0.88rem", color: dark ? "#F1F5F9" : "#0F172A", mb: 1.75 }}>
              🎨 थीम चुनें
            </Typography>

            {/* Dark / Light toggle */}
            <Box sx={{ display: "flex", alignItems: "center", justifyContent: "space-between", mb: 1.75, p: 1.25, bgcolor: dark ? "#1E293B" : "#F8FAFC", borderRadius: "14px" }}>
              <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                <WbSunny sx={{ color: "#F59E0B", fontSize: 20 }} />
                <Typography sx={{ fontSize: "0.82rem", fontWeight: 600, color: dark ? "#F1F5F9" : "#0F172A" }}>
                  {dark ? "डार्क मोड" : "लाइट मोड"}
                </Typography>
                <DarkMode sx={{ color: "#6366F1", fontSize: 18 }} />
              </Box>
              <Switch checked={dark} onChange={toggleMode} size="small"
                sx={{ "& .MuiSwitch-switchBase.Mui-checked": { color: "#6366F1" }, "& .MuiSwitch-switchBase.Mui-checked + .MuiSwitch-track": { bgcolor: "#6366F1" } }}
              />
            </Box>

            {/* Seed color grid */}
            <Typography sx={{ fontSize: "0.75rem", fontWeight: 700, color: dark ? "#94A3B8" : "#64748B", mb: 1, letterSpacing: 0.5, textTransform: "uppercase" }}>रंग थीम</Typography>
            <Box sx={{ display: "grid", gridTemplateColumns: "repeat(3,1fr)", gap: 1.25 }}>
              {SEEDS.map((s) => {
                const isActive = s.id === seed.id;
                return (
                  <Box key={s.id} onClick={() => setSeed(s.id)}
                    sx={{
                      p: 1.25, borderRadius: "14px", cursor: "pointer",
                      background: isActive ? s.gradient : dark ? "#1E293B" : "#F8FAFC",
                      border: isActive ? "none" : `1.5px solid ${dark ? "#334155" : "#E2E8F0"}`,
                      display: "flex", flexDirection: "column", alignItems: "center", gap: 0.4,
                      transition: "all 0.2s", transform: isActive ? "scale(1.03)" : "scale(1)",
                      boxShadow: isActive ? `0 4px 16px ${s.glowColor}` : "none",
                    }}
                  >
                    <Typography sx={{ fontSize: "1.3rem" }}>{s.emoji}</Typography>
                    <Typography sx={{ fontSize: "0.68rem", fontWeight: isActive ? 800 : 600, color: isActive ? "#FFF" : dark ? "#94A3B8" : "#64748B" }}>{s.hindiLabel}</Typography>
                    <Typography sx={{ fontSize: "0.6rem", color: isActive ? "rgba(255,255,255,0.7)" : dark ? "#475569" : "#94A3B8" }}>{s.label}</Typography>
                  </Box>
                );
              })}
            </Box>
          </CardContent>
        </Card>

        {/* Activity */}
        <Card sx={{ mb: 2 }}>
          <CardContent sx={{ p: "14px 16px 8px !important" }}>
            <Typography sx={{ fontWeight: 800, fontSize: "0.88rem", color: dark ? "#F1F5F9" : "#0F172A", mb: 1.25 }}>⚡ गतिविधि इतिहास</Typography>
            {activityLog.map((a, i) => (
              <Box key={i} sx={{ display: "flex", alignItems: "center", gap: 1.5, py: 1, borderBottom: i < activityLog.length - 1 ? `1px solid ${dark ? "#1E293B" : "#F8FAFC"}` : "none" }}>
                <Box sx={{ width: 34, height: 34, borderRadius: "10px", bgcolor: `${a.color}12`, display: "flex", alignItems: "center", justifyContent: "center" }}>
                  <Typography sx={{ fontSize: "0.72rem", fontWeight: 800, color: a.color }}>{a.pts}</Typography>
                </Box>
                <Box sx={{ flex: 1 }}>
                  <Typography sx={{ fontSize: "0.82rem", fontWeight: 500, color: dark ? "#CBD5E1" : "#0F172A" }}>{a.text}</Typography>
                  <Typography sx={{ fontSize: "0.65rem", color: "#475569" }}>{a.time}</Typography>
                </Box>
              </Box>
            ))}
          </CardContent>
        </Card>

        {/* Family members */}
        <Card sx={{ mb: 2 }}>
          <CardContent sx={{ p: "14px 16px !important" }}>
            <Typography sx={{ fontWeight: 800, fontSize: "0.88rem", color: dark ? "#F1F5F9" : "#0F172A", mb: 1.25 }}>👨‍👩‍👧‍👦 परिवार के सदस्य</Typography>
            {members.map((m, i) => (
              <Box key={m.id} sx={{ display: "flex", alignItems: "center", gap: 1.25, py: 0.9, borderBottom: i < members.length - 1 ? `1px solid ${dark ? "#1E293B" : "#F8FAFC"}` : "none" }}>
                <Avatar sx={{ width: 34, height: 34, bgcolor: m.color, fontWeight: 800, fontSize: "0.8rem" }}>{m.shortName}</Avatar>
                <Box sx={{ flex: 1 }}>
                  <Typography sx={{ fontWeight: 600, fontSize: "0.82rem", color: dark ? "#F1F5F9" : "#0F172A" }}>{m.name}</Typography>
                  <Typography sx={{ fontSize: "0.65rem", color: "#64748B" }}>{m.role === "admin" ? "प्रशासक" : "सदस्य"}</Typography>
                </Box>
                <Chip label={`${loyaltyScores[m.id] ?? 100} pts`} size="small"
                  sx={{ bgcolor: `${m.color}15`, color: m.color, fontWeight: 700, fontSize: "0.65rem" }}
                />
              </Box>
            ))}
          </CardContent>
        </Card>

        {/* Sync */}
        <Card>
          <CardContent sx={{ p: "16px !important" }}>
            <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, mb: 1.75 }}>
              <Box sx={{ width: 40, height: 40, borderRadius: "12px", bgcolor: dark ? "#1E293B" : "#EFF6FF", color: "#3B82F6", display: "flex", alignItems: "center", justifyContent: "center" }}>
                <QrCode2 />
              </Box>
              <Box>
                <Typography sx={{ fontWeight: 800, fontSize: "0.88rem", color: dark ? "#F1F5F9" : "#0F172A" }}>Wi-Fi P2P सिंक</Typography>
                <Typography sx={{ fontSize: "0.72rem", color: "#64748B" }}>परिवार को ऑफलाइन जोड़ें</Typography>
              </Box>
            </Box>
            <Box sx={{ bgcolor: dark ? "#1E293B" : "#F8FAFC", borderRadius: "16px", p: 1.75, textAlign: "center" }}>
              <Box sx={{ width: 80, height: 80, bgcolor: "#0A0F1E", borderRadius: "14px", display: "flex", alignItems: "center", justifyContent: "center", mx: "auto", mb: 1.25, fontSize: "2.5rem" }}>📱</Box>
              <Typography sx={{ fontSize: "0.68rem", color: "#64748B", mb: 0.75 }}>युग्मन कोड</Typography>
              <Chip label="KUT-8472-FARM" sx={{ fontFamily: "monospace", fontWeight: 800, bgcolor: dark ? "#111827" : "#FFF", border: `1.5px dashed ${dark ? "#334155" : "#CBD5E1"}`, letterSpacing: 1.5, fontSize: "0.82rem" }} />
              <Button fullWidth size="small"
                sx={{ mt: 1.5, background: seed.gradient, color: "#FFF", borderRadius: "12px", textTransform: "none", fontWeight: 700, py: 0.9 }}
                onClick={() => setSnack(true)}
              >
                QR कोड साझा करें
              </Button>
            </Box>
          </CardContent>
        </Card>
      </Box>

      {/* Edit Profile Dialog */}
      <Dialog open={editOpen} onClose={() => setEditOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <Typography sx={{ fontWeight: 800, fontSize: "0.95rem" }}>प्रोफ़ाइल संपादित करें</Typography>
          <IconButton size="small" onClick={() => setEditOpen(false)}><Close sx={{ fontSize: 18 }} /></IconButton>
        </DialogTitle>
        <DialogContent sx={{ pt: 1.5 }}>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1.75 }}>
            <TextField label="नाम" fullWidth size="small" value={name} onChange={(e) => setName(e.target.value)} />
            <TextField label="बायो" fullWidth size="small" multiline rows={2} value={bio} onChange={(e) => setBio(e.target.value)} />
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2.5, gap: 1 }}>
          <Button onClick={() => setEditOpen(false)} sx={{ color: "#64748B" }}>रद्द</Button>
          <Button onClick={() => { setEditOpen(false); setSnack(true); }} sx={{ flex: 1, py: 1, borderRadius: "12px", background: seed.gradient, color: "#FFF", fontWeight: 700 }}>
            सहेजें ✓
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar open={snack} autoHideDuration={2500} onClose={() => setSnack(false)}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }} sx={{ bottom: "88px !important" }}>
        <Alert severity="success" sx={{ borderRadius: "14px", fontWeight: 700, boxShadow: "0 8px 24px rgba(0,0,0,0.15)" }}>सफलतापूर्वक सहेजा गया! ✓</Alert>
      </Snackbar>
    </div>
  );
}
