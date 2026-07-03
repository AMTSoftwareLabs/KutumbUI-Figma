import { useState, useRef, useEffect } from "react";
import {
  AppBar, Toolbar, Box, Typography, Avatar, IconButton,
  TextField, InputAdornment, Chip,
} from "@mui/material";
import { AttachFile, Image, Send, MoreVert } from "@mui/icons-material";
import { ImageWithFallback } from "@/app/components/figma/ImageWithFallback";
import kutumbIcon from "@/imports/Kutumb_icon_512x512.png";
import { useAppTheme } from "@/app/context/ThemeContext";
import { useApp } from "@/app/context/AppContext";

export default function Samvaad() {
  const { seed, mode } = useAppTheme();
  const { chatMessages, sendMessage, members } = useApp();
  const dark = mode === "dark";

  const [input, setInput] = useState("");
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: "smooth" }); }, [chatMessages]);

  const handleSend = () => {
    if (!input.trim()) return;
    sendMessage(input.trim());
    setInput("");
  };

  const onlineMembers = members.slice(0, 4);

  return (
    <div style={{ minHeight: "100vh", background: dark ? "#0A0F1E" : "#F0F4F8", display: "flex", flexDirection: "column" }}>
      <AppBar position="sticky" sx={{ background: "#0A0F1E", borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
        <Toolbar sx={{ justifyContent: "space-between", minHeight: "52px !important" }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1.25 }}>
            <ImageWithFallback src={kutumbIcon} alt="K" style={{ width: 28, height: 28, objectFit: "contain", borderRadius: 7 }} />
            <Box>
              <Typography sx={{ fontWeight: 800, color: "#FFF", fontSize: "0.95rem", lineHeight: 1.2, fontFamily: "Inter, sans-serif" }}>संवाद</Typography>
              <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
                <Box sx={{ width: 6, height: 6, borderRadius: "50%", bgcolor: "#10B981" }} />
                <Typography sx={{ color: "#475569", fontSize: "0.6rem" }}>5 सदस्य ऑनलाइन</Typography>
              </Box>
            </Box>
          </Box>
          <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
            {onlineMembers.slice(0, 3).map((m) => (
              <Avatar key={m.id} sx={{ width: 24, height: 24, bgcolor: m.color, fontSize: "0.55rem", fontWeight: 800 }}>{m.shortName}</Avatar>
            ))}
            <IconButton size="small" sx={{ color: "#475569" }}><MoreVert sx={{ fontSize: 18 }} /></IconButton>
          </Box>
        </Toolbar>
      </AppBar>

      {/* Messages area */}
      <Box sx={{
        flex: 1, overflowY: "auto", px: 2, py: 1.5, pb: "120px",
        display: "flex", flexDirection: "column", gap: 1,
      }}>
        <Chip label="आज" size="small"
          sx={{ alignSelf: "center", mb: 0.5, bgcolor: dark ? "#1E293B" : "#E2E8F0", color: dark ? "#64748B" : "#94A3B8", fontSize: "0.65rem" }}
        />
        {chatMessages.map((msg) => (
          <Box key={msg.id} sx={{ display: "flex", justifyContent: msg.isMe ? "flex-end" : "flex-start", alignItems: "flex-end", gap: 0.75 }}>
            {!msg.isMe && (
              <Avatar sx={{ width: 26, height: 26, bgcolor: msg.senderColor, fontSize: "0.6rem", fontWeight: 800, flexShrink: 0, mb: 0.25 }}>
                {msg.senderShort}
              </Avatar>
            )}
            <Box sx={{ maxWidth: "72%" }}>
              {!msg.isMe && (
                <Typography sx={{ fontSize: "0.6rem", color: msg.senderColor, ml: 0.5, mb: 0.2, fontWeight: 700 }}>{msg.senderName}</Typography>
              )}
              <Box sx={{
                bgcolor: msg.isMe ? seed.primary : dark ? "#1E293B" : "#FFFFFF",
                borderRadius: msg.isMe ? "18px 18px 4px 18px" : "18px 18px 18px 4px",
                px: 1.5, py: 0.9,
                boxShadow: msg.isMe ? `0 4px 14px ${seed.glowColor}` : dark ? "0 1px 4px rgba(0,0,0,0.3)" : "0 1px 4px rgba(0,0,0,0.06)",
                border: msg.isMe ? "none" : dark ? "1px solid #1E293B" : "1px solid rgba(226,232,240,0.8)",
              }}>
                <Typography sx={{ fontSize: "0.84rem", color: msg.isMe ? "#FFF" : dark ? "#F1F5F9" : "#0F172A", lineHeight: 1.5, whiteSpace: "pre-line" }}>
                  {msg.text}
                </Typography>
                <Typography sx={{ fontSize: "0.55rem", opacity: 0.6, textAlign: msg.isMe ? "right" : "left", mt: 0.2, color: msg.isMe ? "#FFF" : "#94A3B8" }}>
                  {msg.time}
                </Typography>
              </Box>
            </Box>
            {msg.isMe && (
              <Avatar sx={{ width: 26, height: 26, background: seed.gradient, fontSize: "0.6rem", fontWeight: 800, flexShrink: 0, mb: 0.25 }}>R</Avatar>
            )}
          </Box>
        ))}
        <div ref={bottomRef} />
      </Box>

      {/* Input bar */}
      <Box sx={{
        position: "fixed", bottom: 68, left: 0, right: 0,
        bgcolor: dark ? "rgba(17,24,39,0.97)" : "rgba(255,255,255,0.97)",
        backdropFilter: "blur(20px)",
        borderTop: `1px solid ${dark ? "rgba(255,255,255,0.05)" : "rgba(226,232,240,0.8)"}`,
        px: 1.25, py: 0.9, display: "flex", alignItems: "center", gap: 0.75,
      }}>
        <IconButton size="small" sx={{ color: "#64748B" }}><AttachFile sx={{ fontSize: 19 }} /></IconButton>
        <IconButton size="small" sx={{ color: "#64748B" }}><Image sx={{ fontSize: 19 }} /></IconButton>
        <TextField fullWidth size="small" placeholder="संदेश लिखें..."
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSend()}
          sx={{
            "& .MuiOutlinedInput-root": {
              borderRadius: "20px",
              bgcolor: dark ? "#1E293B" : "#F1F5F9",
              "& fieldset": { border: "none" },
              fontSize: "0.85rem",
            },
          }}
          InputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <IconButton size="small" onClick={handleSend} disabled={!input.trim()}
                  sx={{
                    width: 28, height: 28, borderRadius: "50%",
                    bgcolor: input.trim() ? seed.primary : "transparent",
                    color: input.trim() ? "#FFF" : "#64748B",
                    transition: "all 0.2s",
                    "&:hover": { bgcolor: input.trim() ? seed.primary : "transparent" },
                  }}
                >
                  <Send sx={{ fontSize: 15 }} />
                </IconButton>
              </InputAdornment>
            ),
          }}
        />
      </Box>
    </div>
  );
}
