import { useState } from "react";
import { Card, CardContent, Box, Typography, Avatar, Chip, LinearProgress, IconButton } from "@mui/material";
import { LightbulbOutlined, CheckCircle, CheckCircleOutline, EmojiEvents, Refresh } from "@mui/icons-material";
import ScreenShell from "@/app/components/ScreenShell";
import { useAppTheme } from "@/app/context/ThemeContext";
import { useApp } from "@/app/context/AppContext";
import { ImageWithFallback } from "@/app/components/figma/ImageWithFallback";
import kutumbIcon from "@/imports/Kutumb_icon_512x512.png";
import { AppBar, Toolbar, NotificationsNone as NotifIcon } from "@mui/material";
import { NotificationsNone } from "@mui/icons-material";

const TIPS = [
  "रात के भोजन पर फोन दूर रखें — परिवार का बंधन मजबूत होगा! 🙏",
  "सुबह साथ मिलकर पूजा करें — दिन अच्छा जाएगा। 🪔",
  "सप्ताह में एक बार परिवार के साथ खेल खेलें। 🎲",
  "अपने घर की सफाई को आदत बनाएं — मन और घर दोनों खुश रहेंगे! 🏠",
];

const LEADERBOARD_COLORS = ["#F59E0B", "#94A3B8", "#CD7C3B"];
const RANK_EMOJI = ["🥇", "🥈", "🥉"];

export default function KutumbHome() {
  const { seed, mode } = useAppTheme();
  const { members, tasks, expenses, streak, loyaltyScores, toggleTask, currentUser } = useApp();
  const [tipIdx, setTipIdx] = useState(0);
  const dark = mode === "dark";

  const todayTasks = tasks.filter((t) => t.deadline === "2025-06-19");
  const totalExpenses = expenses.filter((e) => !e.isIncome).reduce((s, e) => s + e.amount, 0);
  const myScore = loyaltyScores[currentUser.id] ?? 100;

  const leaderboard = [...members]
    .map((m) => ({ ...m, score: loyaltyScores[m.id] ?? 100 }))
    .sort((a, b) => b.score - a.score)
    .slice(0, 3);

  const activities = [
    { id: 1, text: "राज ने किराने का सामान खरीदा", pts: "+50", ptColor: "#10B981", av: "R", avColor: "#FF6B35", time: "2h ago" },
    { id: 2, text: "माँ ने पूजा की", pts: "+50", ptColor: "#10B981", av: "म", avColor: "#10B981", time: "3h ago" },
    { id: 3, text: "प्रिया ने झाड़ू लगाया", pts: "+25", ptColor: "#10B981", av: "प", avColor: "#6366F1", time: "5h ago" },
    { id: 4, text: "आर्यन ने कपड़े धोए", pts: "+40", ptColor: "#10B981", av: "आ", avColor: "#8B5CF6", time: "कल" },
  ];

  return (
    <div style={{ minHeight: "100vh", background: dark ? "#0A0F1E" : "#F1F5F9" }}>
      {/* AppBar */}
      <AppBar position="sticky" sx={{ background: "#0A0F1E", borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
        <Toolbar sx={{ justifyContent: "space-between", minHeight: "52px !important" }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <ImageWithFallback src={kutumbIcon} alt="K" style={{ width: 28, height: 28, objectFit: "contain", borderRadius: 7 }} />
            <Typography sx={{ fontWeight: 800, color: "#FFF", fontSize: "1.1rem", fontFamily: "Inter, sans-serif", letterSpacing: -0.3 }}>Kutumb</Typography>
          </Box>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <Chip label={`🌿 ${myScore} pts`} size="small"
              sx={{ bgcolor: `${seed.primary}20`, color: seed.primary, fontWeight: 800, fontSize: "0.7rem", border: `1px solid ${seed.primary}30` }}
            />
            <IconButton size="small" sx={{ color: "#475569" }}><NotificationsNone sx={{ fontSize: 20 }} /></IconButton>
            <Avatar sx={{ width: 30, height: 30, background: seed.gradient, fontWeight: 800, fontSize: "0.72rem" }}>R</Avatar>
          </Box>
        </Toolbar>
      </AppBar>

      {/* Dark hero */}
      <Box sx={{
        background: "linear-gradient(160deg,#0A0F1E 0%,#111827 100%)",
        px: 2.5, pt: 3, pb: "30px", position: "relative", overflow: "hidden",
        "&::before": { content: '""', position: "absolute", top: -80, right: -40, width: 240, height: 240, borderRadius: "50%", background: `radial-gradient(circle,${seed.primary}20 0%,transparent 65%)` },
        "&::after": { content: '""', position: "absolute", bottom: 0, left: 0, right: 0, height: 28, background: dark ? "#0A0F1E" : "#F1F5F9", borderRadius: "28px 28px 0 0" },
      }}>
        <Box sx={{ mb: 2 }}>
          <Typography sx={{ color: "#475569", fontSize: "0.78rem", fontWeight: 500 }}>नमस्ते 🙏</Typography>
          <Typography sx={{ color: "#FFF", fontSize: "1.7rem", fontWeight: 900, lineHeight: 1.1, letterSpacing: -0.5 }}>राज शर्मा</Typography>
          <Typography sx={{ color: "#475569", fontSize: "0.75rem", mt: 0.2 }}>आपके घर का आज का सारांश</Typography>
        </Box>
        <Box sx={{ display: "grid", gridTemplateColumns: "repeat(3,1fr)", gap: 1.5, position: "relative", zIndex: 1 }}>
          {[
            { icon: "🔥", val: `${streak}d`, label: "स्ट्रीक", color: seed.primary, bg: `${seed.primary}18` },
            { icon: "💰", val: `₹${(totalExpenses/1000).toFixed(0)}K`, label: "व्यय", color: "#6366F1", bg: "rgba(99,102,241,0.15)" },
            { icon: "⭐", val: String(myScore), label: "स्कोर", color: "#10B981", bg: "rgba(16,185,129,0.15)" },
          ].map((s) => (
            <Box key={s.label} sx={{ bgcolor: s.bg, border: `1px solid ${s.color}30`, borderRadius: "14px", p: 1.25 }}>
              <Typography sx={{ fontSize: "1rem" }}>{s.icon}</Typography>
              <Typography sx={{ color: "#FFF", fontWeight: 900, fontSize: "1.15rem", lineHeight: 1.1 }}>{s.val}</Typography>
              <Typography sx={{ color: "#475569", fontSize: "0.6rem", mt: 0.2 }}>{s.label}</Typography>
            </Box>
          ))}
        </Box>
      </Box>

      <Box sx={{ px: 2, pt: 0.5, pb: 12 }}>
        {/* AI Tip */}
        <Card sx={{ mb: 2 }}>
          <CardContent sx={{ p: "12px 14px !important" }}>
            <Box sx={{ display: "flex", alignItems: "flex-start", gap: 1.5 }}>
              <Box sx={{ width: 36, height: 36, borderRadius: "11px", background: "linear-gradient(135deg,#F59E0B,#FBBF24)", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0 }}>
                <LightbulbOutlined sx={{ color: "#FFF", fontSize: 19 }} />
              </Box>
              <Box sx={{ flex: 1 }}>
                <Box sx={{ display: "flex", justifyContent: "space-between", mb: 0.25 }}>
                  <Typography sx={{ fontSize: "0.72rem", fontWeight: 800, color: dark ? "#F1F5F9" : "#0F172A" }}>AI सुझाव</Typography>
                  <IconButton size="small" sx={{ p: 0 }} onClick={() => setTipIdx((i) => (i + 1) % TIPS.length)}>
                    <Refresh sx={{ fontSize: 14, color: "#94A3B8" }} />
                  </IconButton>
                </Box>
                <Typography sx={{ fontSize: "0.78rem", color: dark ? "#94A3B8" : "#64748B", lineHeight: 1.5 }}>{TIPS[tipIdx]}</Typography>
              </Box>
            </Box>
          </CardContent>
        </Card>

        {/* Leaderboard */}
        <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 1.25 }}>
          <Typography sx={{ fontWeight: 800, fontSize: "0.92rem", color: dark ? "#F1F5F9" : "#0F172A" }}>🏆 लीडरबोर्ड</Typography>
          <Typography sx={{ fontSize: "0.72rem", color: seed.primary, fontWeight: 700 }}>सभी देखें →</Typography>
        </Box>
        <Card sx={{ mb: 2 }}>
          {leaderboard.map((u, i) => (
            <Box key={u.id} sx={{
              display: "flex", alignItems: "center", gap: 1.5,
              px: 2, py: 1.1,
              borderBottom: i < 2 ? `1px solid ${dark ? "#1E293B" : "#F8FAFC"}` : "none",
              background: i === 0 ? `linear-gradient(90deg,${seed.primary}08,transparent)` : "transparent",
            }}>
              <Typography sx={{ fontSize: "1.1rem", width: 22 }}>{RANK_EMOJI[i]}</Typography>
              <Avatar sx={{ width: 34, height: 34, bgcolor: u.color, fontWeight: 800, fontSize: "0.8rem" }}>{u.shortName}</Avatar>
              <Box sx={{ flex: 1 }}>
                <Typography sx={{ fontWeight: 700, fontSize: "0.84rem", color: dark ? "#F1F5F9" : "#0F172A" }}>{u.name}</Typography>
                <LinearProgress variant="determinate" value={Math.min((u.score / 500) * 100, 100)}
                  sx={{ height: 4, mt: 0.4, bgcolor: dark ? "#1E293B" : "#F1F5F9", "& .MuiLinearProgress-bar": { bgcolor: LEADERBOARD_COLORS[i] } }}
                />
              </Box>
              <Chip label={`${u.score} pts`} size="small"
                sx={{ bgcolor: `${LEADERBOARD_COLORS[i]}18`, color: LEADERBOARD_COLORS[i], fontSize: "0.68rem", fontWeight: 800 }}
              />
            </Box>
          ))}
        </Card>

        {/* Today's Tasks Carousel */}
        <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 1 }}>
          <Box>
            <Typography sx={{ fontWeight: 800, fontSize: "0.92rem", color: dark ? "#F1F5F9" : "#0F172A" }}>📋 आज के कार्य</Typography>
            <Typography sx={{ fontSize: "0.68rem", color: "#94A3B8" }}>
              {todayTasks.filter((t) => t.completed).length}/{todayTasks.length} पूर्ण
            </Typography>
          </Box>
          <LinearProgress variant="determinate"
            value={todayTasks.length ? (todayTasks.filter((t) => t.completed).length / todayTasks.length) * 100 : 0}
            sx={{ width: 64, height: 6, bgcolor: dark ? "#1E293B" : "#E2E8F0", "& .MuiLinearProgress-bar": { bgcolor: "#10B981" } }}
          />
        </Box>
        <Box sx={{ display: "flex", gap: 1.5, overflowX: "auto", pb: 1, scrollbarWidth: "none", "&::-webkit-scrollbar": { display: "none" }, mb: 2 }}>
          {todayTasks.map((task) => {
            const member = members.find((m) => m.id === task.assignedTo);
            return (
              <Box key={task.id} onClick={() => toggleTask(task.id)}
                sx={{
                  flexShrink: 0, width: 130, p: 1.5, borderRadius: "18px", cursor: "pointer",
                  background: task.completed
                    ? `linear-gradient(135deg,${seed.primary}22,${seed.primary}10)`
                    : dark ? "#111827" : "#FFF",
                  border: `1.5px solid ${task.completed ? seed.primary + "50" : dark ? "#1E293B" : "#E2E8F0"}`,
                  transition: "all 0.2s",
                }}
              >
                <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
                  <Box sx={{ width: 30, height: 30, borderRadius: "9px", bgcolor: task.completed ? seed.primary : `${seed.primary}18`, display: "flex", alignItems: "center", justifyContent: "center" }}>
                    {task.completed
                      ? <CheckCircle sx={{ color: "#FFF", fontSize: 17 }} />
                      : <CheckCircleOutline sx={{ color: seed.primary, fontSize: 17 }} />}
                  </Box>
                  <Chip label={`+${task.points}`} size="small"
                    sx={{ height: 18, fontSize: "0.58rem", fontWeight: 800, bgcolor: "#10B98115", color: "#10B981", "& .MuiChip-label": { px: 0.75 } }}
                  />
                </Box>
                <Typography sx={{
                  fontSize: "0.78rem", fontWeight: 700, color: dark ? (task.completed ? "#475569" : "#F1F5F9") : (task.completed ? "#94A3B8" : "#0F172A"),
                  textDecoration: task.completed ? "line-through" : "none", lineHeight: 1.3, mb: 0.5,
                }}>{task.title}</Typography>
                <Typography sx={{ fontSize: "0.62rem", color: member?.color || "#94A3B8" }}>{member?.name}</Typography>
              </Box>
            );
          })}
        </Box>

        {/* Recent Activity */}
        <Typography sx={{ fontWeight: 800, fontSize: "0.92rem", color: dark ? "#F1F5F9" : "#0F172A", mb: 1.25 }}>⚡ हाल की गतिविधियाँ</Typography>
        <Card>
          {activities.map((a, i) => (
            <Box key={a.id} sx={{
              display: "flex", alignItems: "center", gap: 1.5, px: 2, py: 1.2,
              borderBottom: i < activities.length - 1 ? `1px solid ${dark ? "#1E293B" : "#F8FAFC"}` : "none",
            }}>
              <Avatar sx={{ width: 34, height: 34, bgcolor: a.avColor, fontWeight: 800, fontSize: "0.78rem" }}>{a.av}</Avatar>
              <Box sx={{ flex: 1 }}>
                <Typography sx={{ fontSize: "0.8rem", fontWeight: 500, color: dark ? "#CBD5E1" : "#0F172A", lineHeight: 1.4 }}>{a.text}</Typography>
                <Typography sx={{ fontSize: "0.65rem", color: "#475569" }}>{a.time}</Typography>
              </Box>
              <Chip label={a.pts} size="small"
                sx={{ bgcolor: "#10B98115", color: "#10B981", fontSize: "0.68rem", fontWeight: 800 }}
              />
            </Box>
          ))}
        </Card>
      </Box>
    </div>
  );
}
