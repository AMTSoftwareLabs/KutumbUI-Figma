import { Paper, Box, Typography } from "@mui/material";
import {
  HomeOutlined, Home,
  CheckCircleOutline, CheckCircle,
  AssignmentOutlined, Assignment,
  ChatBubbleOutline, ChatBubble,
  ShoppingCartOutlined, ShoppingCart,
  AccountBalanceWalletOutlined, AccountBalanceWallet,
  ListAltOutlined, ListAlt,
  PhotoAlbumOutlined, PhotoAlbum,
  PersonOutline, Person,
} from "@mui/icons-material";
import { useNavigate, useLocation } from "react-router";
import { useEffect, useState } from "react";
import { useAppTheme } from "@/app/context/ThemeContext";

const NAV = [
  { label: "Kutumb", icon: <HomeOutlined />,                  active: <Home />,                  path: "/" },
  { label: "Karya",  icon: <AssignmentOutlined />,            active: <Assignment />,             path: "/karya" },
  { label: "Niyama", icon: <CheckCircleOutline />,            active: <CheckCircle />,            path: "/niyama" },
  { label: "Vyaya",  icon: <ShoppingCartOutlined />,          active: <ShoppingCart />,           path: "/vyaya" },
  { label: "Rina",   icon: <AccountBalanceWalletOutlined />,  active: <AccountBalanceWallet />,   path: "/rina" },
  { label: "Samvaad",icon: <ChatBubbleOutline />,             active: <ChatBubble />,             path: "/samvaad" },
  { label: "Soochi", icon: <ListAltOutlined />,               active: <ListAlt />,                path: "/soochi" },
  { label: "Smriti", icon: <PhotoAlbumOutlined />,            active: <PhotoAlbum />,             path: "/smriti" },
  { label: "Parichay",icon: <PersonOutline />,                active: <Person />,                 path: "/parichay" },
];

export default function BottomNavBar() {
  const navigate = useNavigate();
  const location = useLocation();
  const { seed, mode } = useAppTheme();
  const [active, setActive] = useState(0);

  useEffect(() => {
    const idx = NAV.findIndex((n) => n.path === location.pathname);
    if (idx !== -1) setActive(idx);
  }, [location.pathname]);

  const dark = mode === "dark";

  return (
    <Paper
      elevation={0}
      sx={{
        position: "fixed", bottom: 0, left: 0, right: 0, zIndex: 1000,
        borderRadius: 0,
        borderTop: `1px solid ${dark ? "rgba(255,255,255,0.06)" : "rgba(226,232,240,0.9)"}`,
        background: dark ? "rgba(17,24,39,0.97)" : "rgba(255,255,255,0.97)",
        backdropFilter: "blur(20px)",
      }}
    >
      <Box sx={{
        display: "flex", height: 68,
        overflowX: "auto", scrollbarWidth: "none", "&::-webkit-scrollbar": { display: "none" },
        px: 0.5,
      }}>
        {NAV.map((item, idx) => {
          const isActive = idx === active;
          return (
            <Box
              key={item.path}
              onClick={() => { setActive(idx); navigate(item.path); }}
              sx={{
                flex: "0 0 auto", minWidth: 60,
                display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center",
                gap: 0.3, cursor: "pointer", py: 0.75, px: 0.25, userSelect: "none",
              }}
            >
              <Box sx={{
                width: 44, height: 28, borderRadius: "14px",
                bgcolor: isActive ? `${seed.primary}18` : "transparent",
                display: "flex", alignItems: "center", justifyContent: "center",
                transition: "background 0.25s",
                "& svg": {
                  fontSize: 20,
                  color: isActive ? seed.primary : dark ? "#475569" : "#94A3B8",
                  transition: "color 0.25s",
                },
              }}>
                {isActive ? item.active : item.icon}
              </Box>
              <Typography sx={{
                fontSize: "0.58rem", fontWeight: isActive ? 800 : 500,
                color: isActive ? seed.primary : dark ? "#475569" : "#94A3B8",
                transition: "all 0.25s", lineHeight: 1, fontFamily: "Inter, sans-serif",
                letterSpacing: isActive ? 0 : 0.1,
              }}>
                {item.label}
              </Typography>
            </Box>
          );
        })}
      </Box>
    </Paper>
  );
}
