import { useState } from "react";
import {
  AppBar, Toolbar, Box, Typography, Avatar, Card, CardContent, Chip,
  Fab, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, MenuItem, Select, FormControl, InputLabel, IconButton, Checkbox, Snackbar, Alert,
} from "@mui/material";
import { Add, ArrowBack, Close, Delete } from "@mui/icons-material";
import { ImageWithFallback } from "@/app/components/figma/ImageWithFallback";
import kutumbIcon from "@/imports/Kutumb_icon_512x512.png";
import { useAppTheme } from "@/app/context/ThemeContext";
import { useApp } from "@/app/context/AppContext";

export default function Soochi() {
  const { seed, mode } = useAppTheme();
  const { groceryLists, groceryItems, members, addGroceryItem, toggleGroceryItem, addGroceryList } = useApp();
  const dark = mode === "dark";

  const [activeList, setActiveList] = useState<string | null>(null);
  const [addItemOpen, setAddItemOpen] = useState(false);
  const [addListOpen, setAddListOpen] = useState(false);
  const [snack, setSnack] = useState<{ open: boolean; msg: string }>({ open: false, msg: "" });

  const [itemForm, setItemForm] = useState({ text: "", qty: "", addedBy: "raj" });
  const [listForm, setListForm] = useState({ name: "", emoji: "📝" });
  const [itemErrors, setItemErrors] = useState<Record<string, string>>({});

  const LIST_EMOJIS = ["🛒", "🍎", "🛋️", "🎯", "💪", "📝", "✈️", "🎁"];
  const LIST_COLORS = [seed.primary, "#6366F1", "#10B981", "#F59E0B", "#EC4899", "#0EA5E9"];

  const currentList = groceryLists.find((l) => l.id === activeList);
  const currentItems = groceryItems.filter((i) => i.listId === activeList);
  const sorted = [...currentItems].sort((a, b) => Number(a.done) - Number(b.done));
  const donePct = currentItems.length ? (currentItems.filter((i) => i.done).length / currentItems.length) * 100 : 0;

  const handleAddItem = () => {
    const e: Record<string, string> = {};
    if (!itemForm.text.trim()) e.text = "नाम आवश्यक है";
    setItemErrors(e);
    if (Object.keys(e).length) return;
    const member = members.find((m) => m.id === itemForm.addedBy);
    addGroceryItem({ text: itemForm.text.trim(), qty: itemForm.qty.trim() || "1", done: false, addedBy: member?.name || "राज", addedByColor: member?.color || seed.primary, listId: activeList! });
    setAddItemOpen(false);
    setItemForm({ text: "", qty: "", addedBy: "raj" });
    setSnack({ open: true, msg: "वस्तु जोड़ी गई! ✓" });
  };

  const handleAddList = () => {
    if (!listForm.name.trim()) return;
    addGroceryList({ name: listForm.name.trim(), emoji: listForm.emoji, color: seed.primary, bg: `${seed.primary}15` });
    setAddListOpen(false);
    setListForm({ name: "", emoji: "📝" });
    setSnack({ open: true, msg: "सूची बनाई गई! ✓" });
  };

  return (
    <div style={{ minHeight: "100vh", background: dark ? "#0A0F1E" : "#F1F5F9" }}>
      <AppBar position="sticky" sx={{ background: "#0A0F1E", borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
        <Toolbar sx={{ justifyContent: "space-between", minHeight: "52px !important" }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            {activeList ? (
              <IconButton size="small" sx={{ color: "#94A3B8", p: 0.5 }} onClick={() => setActiveList(null)}>
                <ArrowBack sx={{ fontSize: 20 }} />
              </IconButton>
            ) : (
              <ImageWithFallback src={kutumbIcon} alt="K" style={{ width: 28, height: 28, objectFit: "contain", borderRadius: 7 }} />
            )}
            <Typography sx={{ fontWeight: 800, color: "#FFF", fontSize: "1rem", fontFamily: "Inter, sans-serif" }}>
              {currentList ? currentList.name : "सूची"}
            </Typography>
          </Box>
          {activeList && (
            <Chip label={`${Math.round(donePct)}% पूर्ण`} size="small"
              sx={{ bgcolor: "rgba(16,185,129,0.2)", color: "#34D399", fontWeight: 700, fontSize: "0.68rem" }}
            />
          )}
        </Toolbar>
      </AppBar>

      <Box sx={{ px: 2, pt: 2, pb: 12 }}>
        {!activeList ? (
          <>
            <Typography sx={{ fontSize: "0.78rem", color: "#64748B", mb: 2 }}>आपकी सभी साझा सूचियाँ</Typography>
            <Box sx={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 2 }}>
              {groceryLists.map((list) => {
                const listItems = groceryItems.filter((i) => i.listId === list.id);
                const done = listItems.filter((i) => i.done).length;
                const pct = listItems.length ? (done / listItems.length) * 100 : 0;
                return (
                  <Card key={list.id} onClick={() => setActiveList(list.id)}
                    sx={{ cursor: "pointer", transition: "transform 0.15s, box-shadow 0.15s", "&:active": { transform: "scale(0.97)" } }}
                  >
                    <CardContent sx={{ p: "16px !important" }}>
                      <Typography sx={{ fontSize: "2rem", mb: 1.25 }}>{list.emoji}</Typography>
                      <Typography sx={{ fontWeight: 700, fontSize: "0.85rem", color: dark ? "#F1F5F9" : "#0F172A", mb: 0.75, lineHeight: 1.3 }}>{list.name}</Typography>
                      <Box sx={{ display: "flex", justifyContent: "space-between", mb: 0.75 }}>
                        <Typography sx={{ fontSize: "0.68rem", color: "#94A3B8" }}>{done}/{listItems.length} पूर्ण</Typography>
                        <Typography sx={{ fontSize: "0.68rem", fontWeight: 700, color: list.color }}>{Math.round(pct)}%</Typography>
                      </Box>
                      <Box sx={{ height: 5, bgcolor: dark ? "#1E293B" : "#F1F5F9", borderRadius: 3, overflow: "hidden" }}>
                        <Box sx={{ width: `${pct}%`, height: "100%", bgcolor: list.color, borderRadius: 3, transition: "width 0.3s" }} />
                      </Box>
                    </CardContent>
                  </Card>
                );
              })}
              {/* Add new list */}
              <Box onClick={() => setAddListOpen(true)}
                sx={{
                  border: `2px dashed ${dark ? "#1E293B" : "#E2E8F0"}`, borderRadius: "20px",
                  display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center",
                  minHeight: 120, cursor: "pointer", gap: 1,
                  "&:hover": { borderColor: seed.primary, bgcolor: `${seed.primary}06` },
                  transition: "all 0.2s",
                }}
              >
                <Typography sx={{ fontSize: "1.5rem" }}>➕</Typography>
                <Typography sx={{ fontSize: "0.75rem", fontWeight: 600, color: dark ? "#475569" : "#94A3B8" }}>नई सूची</Typography>
              </Box>
            </Box>
          </>
        ) : (
          <>
            {/* Progress bar */}
            <Box sx={{ height: 8, bgcolor: dark ? "#1E293B" : "#E2E8F0", borderRadius: 4, overflow: "hidden", mb: 2 }}>
              <Box sx={{ width: `${donePct}%`, height: "100%", background: "linear-gradient(90deg,#10B981,#34D399)", borderRadius: 4, transition: "width 0.4s ease" }} />
            </Box>

            {sorted.length === 0 && (
              <Box sx={{ textAlign: "center", py: 6 }}>
                <Typography sx={{ fontSize: "3rem", mb: 1.5 }}>🛒</Typography>
                <Typography sx={{ fontWeight: 700, color: dark ? "#F1F5F9" : "#0F172A" }}>सूची खाली है</Typography>
                <Typography sx={{ fontSize: "0.8rem", color: "#94A3B8", mt: 0.5 }}>वस्तु जोड़ने के लिए + दबाएं</Typography>
              </Box>
            )}
            <Card>
              {sorted.map((item, idx) => (
                <Box key={item.id} sx={{
                  display: "flex", alignItems: "center", gap: 1.5,
                  px: 2, py: 1.2,
                  borderBottom: idx < sorted.length - 1 ? `1px solid ${dark ? "#1E293B" : "#F8FAFC"}` : "none",
                  opacity: item.done ? 0.45 : 1, transition: "opacity 0.2s",
                }}>
                  <Checkbox checked={item.done} onChange={() => toggleGroceryItem(item.id)} size="small"
                    sx={{ p: 0.4, color: "#64748B", "&.Mui-checked": { color: "#10B981" } }}
                  />
                  <Box sx={{ flex: 1 }}>
                    <Typography sx={{
                      fontSize: "0.85rem", fontWeight: 600,
                      color: dark ? "#F1F5F9" : "#0F172A",
                      textDecoration: item.done ? "line-through" : "none",
                    }}>{item.text}</Typography>
                    <Typography sx={{ fontSize: "0.68rem", color: "#64748B" }}>{item.qty}</Typography>
                  </Box>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
                    <Avatar sx={{ width: 20, height: 20, bgcolor: item.addedByColor, fontSize: "0.5rem", fontWeight: 800 }}>
                      {item.addedBy[0]}
                    </Avatar>
                    <Typography sx={{ fontSize: "0.62rem", color: "#64748B" }}>{item.addedBy}</Typography>
                  </Box>
                </Box>
              ))}
            </Card>
          </>
        )}
      </Box>

      <Fab onClick={() => activeList ? setAddItemOpen(true) : setAddListOpen(true)}
        sx={{ position: "fixed", bottom: 88, right: 16, background: seed.gradient, color: "#FFF" }}>
        <Add />
      </Fab>

      {/* Add Item Dialog */}
      <Dialog open={addItemOpen} onClose={() => setAddItemOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", pb: 0.5 }}>
          <Typography sx={{ fontWeight: 800, fontSize: "0.95rem" }}>वस्तु जोड़ें</Typography>
          <IconButton size="small" onClick={() => setAddItemOpen(false)}><Close sx={{ fontSize: 18 }} /></IconButton>
        </DialogTitle>
        <DialogContent sx={{ pt: 1.5 }}>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1.75 }}>
            <TextField label="वस्तु का नाम *" fullWidth size="small" value={itemForm.text}
              onChange={(e) => setItemForm((f) => ({ ...f, text: e.target.value }))}
              error={!!itemErrors.text} helperText={itemErrors.text}
            />
            <TextField label="मात्रा" fullWidth size="small" value={itemForm.qty}
              onChange={(e) => setItemForm((f) => ({ ...f, qty: e.target.value }))}
              placeholder="जैसे: 2 kg, 500 g"
            />
            <FormControl size="small" fullWidth>
              <InputLabel>किसने जोड़ा</InputLabel>
              <Select label="किसने जोड़ा" value={itemForm.addedBy} onChange={(e) => setItemForm((f) => ({ ...f, addedBy: e.target.value }))}>
                {members.map((m) => <MenuItem key={m.id} value={m.id}>{m.name}</MenuItem>)}
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2.5, gap: 1 }}>
          <Button onClick={() => setAddItemOpen(false)} sx={{ color: "#64748B" }}>रद्द</Button>
          <Button onClick={handleAddItem} sx={{ flex: 1, py: 1, borderRadius: "12px", background: seed.gradient, color: "#FFF", fontWeight: 700 }}>
            जोड़ें ✓
          </Button>
        </DialogActions>
      </Dialog>

      {/* Add List Dialog */}
      <Dialog open={addListOpen} onClose={() => setAddListOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", pb: 0.5 }}>
          <Typography sx={{ fontWeight: 800, fontSize: "0.95rem" }}>नई सूची बनाएं</Typography>
          <IconButton size="small" onClick={() => setAddListOpen(false)}><Close sx={{ fontSize: 18 }} /></IconButton>
        </DialogTitle>
        <DialogContent sx={{ pt: 1.5 }}>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1.75 }}>
            <TextField label="सूची का नाम *" fullWidth size="small" value={listForm.name}
              onChange={(e) => setListForm((f) => ({ ...f, name: e.target.value }))}
            />
            <Box>
              <Typography sx={{ fontSize: "0.8rem", fontWeight: 600, mb: 1, color: dark ? "#CBD5E1" : "#374151" }}>इमोजी चुनें</Typography>
              <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.75 }}>
                {LIST_EMOJIS.map((e) => (
                  <Box key={e} onClick={() => setListForm((f) => ({ ...f, emoji: e }))}
                    sx={{ width: 36, height: 36, borderRadius: "10px", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "1.2rem", cursor: "pointer", bgcolor: listForm.emoji === e ? `${seed.primary}20` : dark ? "#1E293B" : "#F1F5F9", border: listForm.emoji === e ? `2px solid ${seed.primary}` : "2px solid transparent" }}
                  >{e}</Box>
                ))}
              </Box>
            </Box>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2.5, gap: 1 }}>
          <Button onClick={() => setAddListOpen(false)} sx={{ color: "#64748B" }}>रद्द</Button>
          <Button onClick={handleAddList} sx={{ flex: 1, py: 1, borderRadius: "12px", background: seed.gradient, color: "#FFF", fontWeight: 700 }}>
            बनाएं ✓
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar open={snack.open} autoHideDuration={2000} onClose={() => setSnack((s) => ({ ...s, open: false }))}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }} sx={{ bottom: "88px !important" }}>
        <Alert severity="success" sx={{ borderRadius: "14px", fontWeight: 700, boxShadow: "0 8px 24px rgba(0,0,0,0.15)" }}>{snack.msg}</Alert>
      </Snackbar>
    </div>
  );
}
