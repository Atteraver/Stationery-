import React, { useEffect, useState } from "react";
import API from "../../api/api";
import { getUserId, getUserEmail } from "../../utils/auth";

export default function RequestForm() {
  const [items, setItems] = useState([]);
  const [selected, setSelected] = useState({});
  const [superiorEmail, setSuperiorEmail] = useState("");
  const userId = getUserId();

  useEffect(() => {
    API.get("/api/items")
      .then((res) => setItems(res.data || []))
      .catch((err) => {
        console.error(err);
        setItems([]);
      });
  }, []);

  function updateQty(itemId, qty) {
    setSelected((s) => ({ ...s, [itemId]: qty }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    const itemsReq = Object.entries(selected)
      .map(([itemId, qty]) => ({ itemId: Number(itemId), quantity: Number(qty) }))
      .filter(i => i.quantity > 0);

    if (itemsReq.length === 0) {
      alert("Select at least one item with quantity.");
      return;
    }

    if (!superiorEmail || !superiorEmail.includes("@")) {
      alert("Please enter a valid manager/superior email address.");
      return;
    }

    const payload = {
      userId: userId,
      superiorEmail: superiorEmail,
      items: itemsReq
    };

    try {
      await API.post("/api/requests", payload);
      alert("Request created successfully!");
      setSelected({});
      setSuperiorEmail("");
      // optionally you can emit an event or reload lists by using a shared state
    } catch (err) {
      console.error(err);
      alert("Failed to create request. Check console for details.");
    }
  }

  return (
    <form className="form" onSubmit={handleSubmit}>
      <h3>Create New Request</h3>

      <label className="small">Manager/Superior Email *</label>
      <input
        type="email"
        placeholder="e.g., alice.manager@college.edu"
        value={superiorEmail}
        onChange={(e) => setSuperiorEmail(e.target.value)}
        required
        style={{ marginBottom: 16 }}
      />

      <label className="small">Select Items and Quantities</label>
      <div style={{ maxHeight: 280, overflow: "auto", border: "1px solid #e9ecef", borderRadius: 4 }}>
        {items.length === 0 && (
          <div style={{ padding: 20, textAlign: "center", color: "#868e96" }}>
            Loading items...
          </div>
        )}
        {items.map(it => (
          <div key={it.id} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: 8, borderBottom: "1px solid #f1f3f5" }}>
            <div>
              <div style={{fontWeight:600}}>{it.itemName}</div>
              <div className="small">Price: ${it.unitPrice} • Stock: {it.stockQuantity}</div>
            </div>
            <div>
              <input
                type="number"
                min="0"
                max={it.stockQuantity}
                style={{ width: 80 }}
                value={selected[it.id] || ""}
                onChange={(e) => updateQty(it.id, e.target.value)}
                placeholder="Qty"
              />
            </div>
          </div>
        ))}
      </div>

      <div style={{ marginTop: 16 }}>
        <button type="submit">Submit Request</button>
      </div>
    </form>
  );
}