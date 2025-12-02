import React, { useEffect, useState } from "react";
import API from "../../api/api";
import { getUserId } from "../../utils/auth";

export default function MyRequests() {
  const userId = getUserId();
  const [requests, setRequests] = useState([]);

  useEffect(() => {
    if (!userId) return;
    fetchRequests();
    // eslint-disable-next-line
  }, [userId]);

  function fetchRequests() {
    API.get(`/api/requests/my/${userId}`)
      .then(res => setRequests(res.data || []))
      .catch(err => {
        console.error(err);
        setRequests([]);
      });
  }

  async function withdraw(id) {
    if (!window.confirm("Withdraw this PENDING request?")) return;
    try {
      await API.delete(`/api/requests/${id}`);
      fetchRequests();
      alert("Request withdrawn.");
    } catch (err) {
      console.error(err);
      alert("Failed to withdraw.");
    }
  }

  async function requestCancel(id) {
    if (!window.confirm("Request cancellation for this APPROVED request?")) return;
    try {
      await API.put(`/api/requests/${id}/cancel`);
      fetchRequests();
      alert("Cancellation requested.");
    } catch (err) {
      console.error(err);
      alert("Failed to request cancellation.");
    }
  }

  return (
    <div>
      {requests.length === 0 ? <div className="small">No requests yet</div> :
        <ul className="simple">
          {requests.map(r => (
            <li key={r.id} className="item-row">
              <div>
                <div style={{ fontWeight: 700 }}>{r.id} • {r.status}</div>
                <div className="small">Created: {r.createdDate ? new Date(r.createdDate).toLocaleString() : "—"}</div>
                <div className="small">Manager: {r.superiorEmail}</div>
                <div className="small">Total: ${r.totalCost || 0}</div>
                <div className="small">Items: {r.requestDetails ? r.requestDetails.map(d => `${d.item.itemName} x${d.quantity}`).join(", ") : "—"}</div>
              </div>
              <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {r.status === "PENDING" && <button className="secondary" onClick={() => withdraw(r.id)}>Withdraw</button>}
                {r.status === "APPROVED" && <button className="secondary" onClick={() => requestCancel(r.id)}>Request Cancel</button>}
              </div>
            </li>
          ))}
        </ul>
      }
    </div>
  );
}