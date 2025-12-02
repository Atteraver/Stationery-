import React, { useEffect, useState } from "react";
import API from "../../api/api";
import { getUserId, getUserEmail } from "../../utils/auth";

export default function RequestForm() {
  const [items, setItems] = useState([]);
  const [selected, setSelected] = useState({});
  const userId = getUserId();
  const managerEmailDefault = getUserEmail() || "";

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

    const payload = {
      userId: userId,
      superiorEmail: managerEmailDefault || prompt("Enter your manager's email:"),
      items: itemsReq
    };

    try {
      await API.post("/api/requests", payload);
      alert("Request created successfully!");
      setSelected({});
      // optionally you can emit an event or reload lists by using a shared state
    } catch (err) {
      console.error(err);
      alert("Failed to create request. Check console for details.");
    }
  }

  return (
    <form className="form" onSubmit={handleSubmit}>
      <div style={{ maxHeight: 280, overflow: "auto" }}>
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

      <div style={{ marginTop: 10 }}>
        <button type="submit">Submit Request</button>
      </div>
    </form>
  );
}