import { useState } from "react";
import {
  AppBar, Toolbar, Box, Typography, Avatar, Card, Chip,
  Fab, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, IconButton,
} from "@mui/material";
import { AddAPhoto, Close, Favorite, FavoriteBorder } from "@mui/icons-material";
import { ImageWithFallback } from "@/app/components/figma/ImageWithFallback";
import kutumbIcon from "@/imports/Kutumb_icon_512x512.png";
import { useAppTheme } from "@/app/context/ThemeContext";
import { useApp } from "@/app/context/AppContext";

const EMOJIS = ["🎂","🏞️","🎉","🌅","🍽️","🎓","🌺","🎪","🏖️","🎵","🕯️","🌿","🎠","🎗️","🏡","💐"];
const MEM_COLORS = ["#FFF7ED","#F0FDF4","#F5F3FF","#EFF6FF","#FFF1F2","#ECFDF5","#FDF4FF","#FEFCE8"];

export default function Smriti() {
  const { seed, mode } = useAppTheme();
  const { memories, addMemory, toggleMemoryLike, members } = useApp();
  const dark = mode === "dark";

  const [selected, setSelected] = useState<typeof memories[0] | null>(null);
  const [addOpen, setAddOpen] = useState(false);
  const [form, setForm] = useState({ emoji: "🎂", caption: "", uploader: "raj", colorIdx: 0 });
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  const handleAdd = () => {
    const e: Record<string, string> = {};
    if (!form.caption.trim()) e.caption = "विवरण आवश्यक है";
    setFormErrors(e);
    if (Object.keys(e).length) return;
    const member = members.find((m) => m.id === form.uploader);
    addMemory({
      emoji: form.emoji,
      caption: form.caption.trim(),
      uploader: member?.name || "राज",
      uploaderColor: member?.color || seed.primary,
      date: new Date().toLocaleDateString("hi-IN", { day: "numeric", month: "short" }),
      liked: false,
      color: dark ? "#1E293B" : MEM_COLORS[form.colorIdx],
    });
    setAddOpen(false);
    setForm({ emoji: "🎂", caption: "", uploader: "raj", colorIdx: 0 });
  };

  const left = memories.filter((_, i) => i % 2 === 0);
  const right = memories.filter((_, i) => i % 2 === 1);

  return (
    <div style={{ minHeight: "100vh", background: dark ? "#0A0F1E" : "#F1F5F9" }}>
      <AppBar position="sticky" sx={{ background: "#0A0F1E", borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
        <Toolbar sx={{ justifyContent: "space-between", minHeight: "52px !important" }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <ImageWithFallback src={kutumbIcon} alt="K" style={{ width: 28, height: 28, objectFit: "contain", borderRadius: 7 }} />
            <Typography sx={{ fontWeight: 800, color: "#FFF", fontSize: "1rem", fontFamily: "Inter, sans-serif" }}>स्मृति</Typography>
          </Box>
          <Chip label={`${memories.length} यादें`} size="small"
            sx={{ bgcolor: "rgba(255,255,255,0.08)", color: "#94A3B8", fontWeight: 700, fontSize: "0.65rem" }}
          />
        </Toolbar>
      </AppBar>

      <Box sx={{ p: 1.5, pb: 12 }}>
        <Box sx={{ px: 0.5, py: 1.5 }}>
          <Typography sx={{ fontWeight: 900, fontSize: "1.05rem", color: dark ? "#F1F5F9" : "#0F172A" }}>जून 2025</Typography>
          <Typography sx={{ fontSize: "0.72rem", color: "#64748B" }}>परिवार की अमूल्य यादें</Typography>
        </Box>

        <Box sx={{ display: "flex", gap: 1.5 }}>
          {[left, right].map((col, ci) => (
            <Box key={ci} sx={{ flex: 1, display: "flex", flexDirection: "column", gap: 1.5 }}>
              {col.map((m, idx) => (
                <Card key={m.id} onClick={() => setSelected(m)}
                  sx={{ cursor: "pointer", overflow: "hidden", border: "none", "&:active": { transform: "scale(0.97)" }, transition: "transform 0.15s" }}
                >
                  <Box sx={{ height: idx % 3 === 0 ? 155 : 108, bgcolor: dark ? "#1E293B" : m.color, display: "flex", alignItems: "center", justifyContent: "center", fontSize: idx % 3 === 0 ? "3.5rem" : "2.5rem" }}>
                    {m.emoji}
                  </Box>
                  <Box sx={{ px: 1.25, py: 1, bgcolor: dark ? "#111827" : "#FFF" }}>
                    <Typography sx={{ fontSize: "0.76rem", fontWeight: 700, color: dark ? "#F1F5F9" : "#0F172A", lineHeight: 1.3, mb: 0.4 }}>{m.caption}</Typography>
                    <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                      <Typography sx={{ fontSize: "0.6rem", color: "#64748B" }}>{m.date}</Typography>
                      <IconButton size="small" onClick={(e) => { e.stopPropagation(); toggleMemoryLike(m.id); }} sx={{ p: 0.2 }}>
                        {m.liked
                          ? <Favorite sx={{ fontSize: 13, color: "#EF4444" }} />
                          : <FavoriteBorder sx={{ fontSize: 13, color: "#CBD5E1" }} />}
                      </IconButton>
                    </Box>
                  </Box>
                </Card>
              ))}
            </Box>
          ))}
        </Box>
      </Box>

      {/* Detail Dialog */}
      <Dialog open={!!selected} onClose={() => setSelected(null)} maxWidth="xs" fullWidth
        PaperProps={{ sx: { borderRadius: "24px", overflow: "hidden" } }}
      >
        <DialogContent sx={{ p: 0, position: "relative" }}>
          <IconButton onClick={() => setSelected(null)}
            sx={{ position: "absolute", top: 10, right: 10, bgcolor: "rgba(0,0,0,0.4)", color: "#FFF", zIndex: 1, width: 30, height: 30 }}
          >
            <Close sx={{ fontSize: 15 }} />
          </IconButton>
          {selected && (
            <>
              <Box sx={{ height: 220, bgcolor: dark ? "#1E293B" : selected.color, display: "flex", alignItems: "center", justifyContent: "center", fontSize: "6rem" }}>
                {selected.emoji}
              </Box>
              <Box sx={{ p: 2.5 }}>
                <Typography sx={{ fontWeight: 800, fontSize: "1.1rem", color: dark ? "#F1F5F9" : "#0F172A", mb: 1.25 }}>{selected.caption}</Typography>
                <Box sx={{ display: "flex", alignItems: "center", gap: 1.25, mb: 0.5 }}>
                  <Avatar sx={{ width: 30, height: 30, bgcolor: selected.uploaderColor, fontWeight: 800, fontSize: "0.72rem" }}>{selected.uploader[0]}</Avatar>
                  <Box>
                    <Typography sx={{ fontSize: "0.82rem", fontWeight: 600, color: dark ? "#F1F5F9" : "#0F172A" }}>{selected.uploader}</Typography>
                    <Typography sx={{ fontSize: "0.68rem", color: "#64748B" }}>{selected.date}</Typography>
                  </Box>
                </Box>
                <Box sx={{ display: "flex", justifyContent: "flex-end", mt: 1 }}>
                  <IconButton onClick={() => { toggleMemoryLike(selected.id); setSelected((s) => s ? { ...s, liked: !s.liked } : null); }}>
                    {selected.liked ? <Favorite sx={{ color: "#EF4444" }} /> : <FavoriteBorder sx={{ color: "#64748B" }} />}
                  </IconButton>
                </Box>
              </Box>
            </>
          )}
        </DialogContent>
      </Dialog>

      {/* Add Memory Dialog */}
      <Dialog open={addOpen} onClose={() => setAddOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", pb: 0.5 }}>
          <Typography sx={{ fontWeight: 800, fontSize: "0.95rem" }}>नई स्मृति जोड़ें</Typography>
          <IconButton size="small" onClick={() => setAddOpen(false)}><Close sx={{ fontSize: 18 }} /></IconButton>
        </DialogTitle>
        <DialogContent sx={{ pt: 1.5 }}>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1.75 }}>
            <Box>
              <Typography sx={{ fontSize: "0.78rem", fontWeight: 600, mb: 1, color: dark ? "#CBD5E1" : "#374151" }}>इमोजी चुनें</Typography>
              <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.75 }}>
                {EMOJIS.map((e) => (
                  <Box key={e} onClick={() => setForm((f) => ({ ...f, emoji: e }))}
                    sx={{ width: 36, height: 36, borderRadius: "10px", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "1.2rem", cursor: "pointer", bgcolor: form.emoji === e ? `${seed.primary}20` : dark ? "#1E293B" : "#F1F5F9", border: form.emoji === e ? `2px solid ${seed.primary}` : "2px solid transparent" }}
                  >{e}</Box>
                ))}
              </Box>
            </Box>
            <TextField label="विवरण *" fullWidth size="small" multiline rows={2} value={form.caption}
              onChange={(e) => setForm((f) => ({ ...f, caption: e.target.value }))}
              error={!!formErrors.caption} helperText={formErrors.caption}
            />
            <Box>
              <Typography sx={{ fontSize: "0.78rem", fontWeight: 600, mb: 1, color: dark ? "#CBD5E1" : "#374151" }}>पृष्ठभूमि रंग</Typography>
              <Box sx={{ display: "flex", gap: 0.75 }}>
                {MEM_COLORS.map((c, i) => (
                  <Box key={i} onClick={() => setForm((f) => ({ ...f, colorIdx: i }))}
                    sx={{ width: 26, height: 26, borderRadius: "50%", bgcolor: c, cursor: "pointer", border: form.colorIdx === i ? `2px solid ${seed.primary}` : "2px solid transparent", boxShadow: form.colorIdx === i ? `0 0 0 2px ${seed.primary}` : "none" }}
                  />
                ))}
              </Box>
            </Box>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2.5, gap: 1 }}>
          <Button onClick={() => setAddOpen(false)} sx={{ color: "#64748B" }}>रद्द</Button>
          <Button onClick={handleAdd} sx={{ flex: 1, py: 1, borderRadius: "12px", background: seed.gradient, color: "#FFF", fontWeight: 700 }}>
            सहेजें ✓
          </Button>
        </DialogActions>
      </Dialog>

      <Fab onClick={() => setAddOpen(true)} sx={{ position: "fixed", bottom: 88, right: 16, background: seed.gradient, color: "#FFF" }}>
        <AddAPhoto />
      </Fab>
    </div>
  );
}
