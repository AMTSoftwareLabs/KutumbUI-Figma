/** Shared screen shell: dark AppBar + hero stat section + curved content area */
import { AppBar, Toolbar, Box, Typography, Avatar, IconButton, LinearProgress } from "@mui/material";
import { NotificationsNone } from "@mui/icons-material";
import { ImageWithFallback } from "@/app/components/figma/ImageWithFallback";
import kutumbIcon from "@/imports/Kutumb_icon_512x512.png";
import { useAppTheme } from "@/app/context/ThemeContext";
import { useApp } from "@/app/context/AppContext";

interface StatBox {
  label: string;
  value: string;
  sub?: string;
  color: string;
  bg: string;
  icon: string;
}

interface ScreenShellProps {
  title: string;
  showNotif?: boolean;
  heroStats?: StatBox[];
  heroBottom?: React.ReactNode;
  children: React.ReactNode;
  fab?: React.ReactNode;
}

export default function ScreenShell({ title, showNotif, heroStats, heroBottom, children, fab }: ScreenShellProps) {
  const { seed, mode } = useAppTheme();
  const { currentUser, loyaltyScores } = useApp();
  const dark = mode === "dark";
  const myScore = loyaltyScores[currentUser.id] ?? 100;

  return (
    <div style={{ minHeight: "100vh", background: dark ? "#0A0F1E" : "#F1F5F9" }}>
      <AppBar position="sticky" sx={{ background: "#0A0F1E", borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
        <Toolbar sx={{ justifyContent: "space-between", minHeight: "52px !important" }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <ImageWithFallback src={kutumbIcon} alt="K" style={{ width: 28, height: 28, objectFit: "contain", borderRadius: 7 }} />
            <Typography sx={{ fontWeight: 800, color: "#FFF", fontSize: "1rem", fontFamily: "Inter, sans-serif", letterSpacing: -0.3 }}>
              {title}
            </Typography>
          </Box>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            {showNotif && (
              <IconButton size="small" sx={{ color: "#475569" }}><NotificationsNone sx={{ fontSize: 20 }} /></IconButton>
            )}
            <Box sx={{ textAlign: "right", mr: 0.5 }}>
              <Typography sx={{ fontSize: "0.6rem", color: "#475569", lineHeight: 1 }}>अंक</Typography>
              <Typography sx={{ fontSize: "0.78rem", fontWeight: 800, color: seed.primary, lineHeight: 1 }}>{myScore}</Typography>
            </Box>
            <Avatar sx={{ width: 30, height: 30, background: seed.gradient, fontWeight: 800, fontSize: "0.72rem" }}>
              {currentUser.shortName}
            </Avatar>
          </Box>
        </Toolbar>
      </AppBar>

      {/* Hero */}
      {heroStats && (
        <Box sx={{
          background: "linear-gradient(160deg,#0A0F1E 0%,#111827 100%)",
          px: 2, pt: 2.5, pb: "28px",
          position: "relative", overflow: "hidden",
          "&::before": { content: '""', position: "absolute", top: -60, right: -60, width: 200, height: 200, borderRadius: "50%", background: `radial-gradient(circle,${seed.primary}18 0%,transparent 70%)` },
          "&::after": { content: '""', position: "absolute", bottom: 0, left: 0, right: 0, height: 24, background: dark ? "#0A0F1E" : "#F1F5F9", borderRadius: "24px 24px 0 0" },
        }}>
          <Box sx={{ display: "flex", gap: 1.5, position: "relative", zIndex: 1 }}>
            {heroStats.map((s) => (
              <Box key={s.label} sx={{ flex: 1, bgcolor: s.bg, border: `1px solid ${s.color}30`, borderRadius: "14px", p: 1.25 }}>
                <Typography sx={{ fontSize: "1rem", mb: 0.25 }}>{s.icon}</Typography>
                <Typography sx={{ color: "#FFF", fontWeight: 900, fontSize: "1.1rem", lineHeight: 1 }}>{s.value}</Typography>
                <Typography sx={{ color: "#64748B", fontSize: "0.62rem", mt: 0.2 }}>{s.label}</Typography>
              </Box>
            ))}
          </Box>
          {heroBottom && <Box sx={{ mt: 1.5, position: "relative", zIndex: 1 }}>{heroBottom}</Box>}
        </Box>
      )}

      <Box sx={{ px: 2, pt: heroStats ? 0.5 : 2, pb: 12, position: "relative" }}>
        {children}
      </Box>

      {fab}
    </div>
  );
}
