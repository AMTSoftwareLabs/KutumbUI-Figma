import { Outlet } from "react-router";
import BottomNavBar from "./BottomNavBar";
import { ThemeProvider as MuiThemeProvider, CssBaseline } from "@mui/material";
import { AppThemeProvider, useAppTheme } from "@/app/context/ThemeContext";
import { AppProvider } from "@/app/context/AppContext";

function ThemedApp() {
  const { theme, mode } = useAppTheme();
  return (
    <MuiThemeProvider theme={theme}>
      <CssBaseline />
      <div style={{
        minHeight: "100vh",
        paddingBottom: 72,
        background: mode === "dark" ? "#0A0F1E" : "#F1F5F9",
        transition: "background 0.3s ease",
      }}>
        <Outlet />
        <BottomNavBar />
      </div>
    </MuiThemeProvider>
  );
}

export default function Root() {
  return (
    <AppThemeProvider>
      <AppProvider>
        <ThemedApp />
      </AppProvider>
    </AppThemeProvider>
  );
}
