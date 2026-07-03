import React, { createContext, useContext, useState, useMemo } from "react";
import { createTheme, Theme } from "@mui/material";

export type ThemeMode = "light" | "dark";

export interface ThemeSeed {
  id: string;
  label: string;
  hindiLabel: string;
  emoji: string;
  primary: string;
  secondary: string;
  gradient: string;
  glowColor: string;
}

export const SEEDS: ThemeSeed[] = [
  { id: "agni",   label: "Agni",   hindiLabel: "अग्नि",  emoji: "🔥", primary: "#FF6B35", secondary: "#F7B32B", gradient: "linear-gradient(135deg,#FF6B35,#F7B32B)", glowColor: "rgba(255,107,53,0.35)" },
  { id: "indigo", label: "Indigo", hindiLabel: "नील",    emoji: "💜", primary: "#6366F1", secondary: "#8B5CF6", gradient: "linear-gradient(135deg,#6366F1,#8B5CF6)", glowColor: "rgba(99,102,241,0.35)" },
  { id: "vana",   label: "Vana",   hindiLabel: "वन",     emoji: "🌿", primary: "#10B981", secondary: "#34D399", gradient: "linear-gradient(135deg,#10B981,#34D399)", glowColor: "rgba(16,185,129,0.35)" },
  { id: "neel",   label: "Neel",   hindiLabel: "नीला",   emoji: "💙", primary: "#0EA5E9", secondary: "#38BDF8", gradient: "linear-gradient(135deg,#0EA5E9,#38BDF8)", glowColor: "rgba(14,165,233,0.35)" },
  { id: "gulabi", label: "Gulabi", hindiLabel: "गुलाबी", emoji: "🌸", primary: "#EC4899", secondary: "#F472B6", gradient: "linear-gradient(135deg,#EC4899,#F472B6)", glowColor: "rgba(236,72,153,0.35)" },
  { id: "ratri",  label: "Ratri",  hindiLabel: "रात्रि", emoji: "🌙", primary: "#8B5CF6", secondary: "#A78BFA", gradient: "linear-gradient(135deg,#8B5CF6,#A78BFA)", glowColor: "rgba(139,92,246,0.35)" },
];

interface ThemeContextType {
  mode: ThemeMode;
  seed: ThemeSeed;
  theme: Theme;
  toggleMode: () => void;
  setSeed: (id: string) => void;
}

const ThemeContext = createContext<ThemeContextType>(null!);
export const useAppTheme = () => useContext(ThemeContext);

function buildTheme(seed: ThemeSeed, mode: ThemeMode): Theme {
  const dark = mode === "dark";
  return createTheme({
    palette: {
      mode,
      primary: { main: seed.primary, light: seed.secondary, dark: seed.primary },
      secondary: { main: seed.secondary },
      success: { main: "#10B981" },
      error: { main: "#EF4444" },
      warning: { main: "#F59E0B" },
      background: {
        default: dark ? "#0A0F1E" : "#F1F5F9",
        paper: dark ? "#111827" : "#FFFFFF",
      },
      text: {
        primary: dark ? "#F1F5F9" : "#0F172A",
        secondary: dark ? "#94A3B8" : "#64748B",
      },
      divider: dark ? "#1E293B" : "#E2E8F0",
    },
    shape: { borderRadius: 20 },
    typography: {
      fontFamily: '"Inter", "Roboto", sans-serif',
      h3: { fontWeight: 900, letterSpacing: -1 },
      h4: { fontWeight: 800, letterSpacing: -0.5 },
      h5: { fontWeight: 700, letterSpacing: -0.25 },
      h6: { fontWeight: 700 },
      button: { fontWeight: 700, textTransform: "none" } as object,
    },
    components: {
      MuiCard: {
        styleOverrides: {
          root: {
            borderRadius: 20,
            backgroundImage: "none",
            boxShadow: dark
              ? "0 2px 8px rgba(0,0,0,0.4), 0 0 0 1px rgba(255,255,255,0.04)"
              : "0 1px 3px rgba(0,0,0,0.04), 0 8px 24px rgba(0,0,0,0.06)",
            border: dark ? "1px solid #1E293B" : "1px solid rgba(226,232,240,0.8)",
          },
        },
      },
      MuiButton: {
        styleOverrides: {
          root: { borderRadius: 12, fontWeight: 700, boxShadow: "none", textTransform: "none", "&:hover": { boxShadow: "none" } },
          containedPrimary: { background: seed.gradient },
        },
      },
      MuiFab: {
        styleOverrides: { root: { boxShadow: `0 4px 20px ${seed.glowColor}` } },
      },
      MuiAppBar: { styleOverrides: { root: { boxShadow: "none" } } },
      MuiLinearProgress: { styleOverrides: { root: { borderRadius: 8 }, bar: { borderRadius: 8 } } },
      MuiChip: { styleOverrides: { root: { fontWeight: 700 } } },
      MuiDialog: { styleOverrides: { paper: { borderRadius: 24, backgroundImage: "none" } } },
      MuiTextField: {
        styleOverrides: {
          root: {
            "& .MuiOutlinedInput-root": {
              borderRadius: 12,
              background: dark ? "rgba(255,255,255,0.04)" : "rgba(0,0,0,0.02)",
            },
          },
        },
      },
    },
  });
}

export function AppThemeProvider({ children }: { children: React.ReactNode }) {
  const [mode, setMode] = useState<ThemeMode>(
    () => (localStorage.getItem("k_mode") as ThemeMode) || "light"
  );
  const [seedId, setSeedId] = useState(
    () => localStorage.getItem("k_seed") || "agni"
  );

  const seed = SEEDS.find((s) => s.id === seedId) || SEEDS[0];
  const theme = useMemo(() => buildTheme(seed, mode), [seed.id, mode]);

  const toggleMode = () => {
    const next = mode === "light" ? "dark" : "light";
    setMode(next);
    localStorage.setItem("k_mode", next);
  };
  const setSeed = (id: string) => {
    setSeedId(id);
    localStorage.setItem("k_seed", id);
  };

  return (
    <ThemeContext.Provider value={{ mode, seed, theme, toggleMode, setSeed }}>
      {children}
    </ThemeContext.Provider>
  );
}
