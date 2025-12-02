import React, { useEffect, useState } from "react";
import API from "../../api/api";

export default function ItemsList() {
  const [items, setItems] = useState([]);

  useEffect(() => {
    API.get("/api/items")
      .then((res) => setItems(res.data || []))
      .catch((err) => {
        console.error(err);
        setItems([]);
      });
  }, []);

  return (
    <div>
      <ul className="simple">
        {items.map((it) => (
          <li key={it.id} className="item-row">
            <div>
              <div style={{ fontWeight: 600 }}>{it.itemName}</div>
              <div className="small">Price: ${it.unitPrice} • In stock: {it.stockQuantity}</div>
            </div>
            <div className="small">${(it.unitPrice).toFixed(2)}</div>
          </li>
        ))}
      </ul>
    </div>
  );
}