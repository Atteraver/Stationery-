import React, { useEffect, useState } from "react";
import API from "../../api/api";
import { getUserEmail, getUserRole } from "../../utils/auth";

export default function ManagerRequests() {
  const role = getUserRole();
  const email = getUserEmail();
  const [requests, setRequests] = useState([]);

  useEffect(() => {
    if (role !== "MANAGER") return;
    fetchPending();
    // eslint-disable-next-line
  }, [role, email]);

  function fetchPending() {
    const managerEmail = encodeURIComponent(email || "");
    API.get(`/api/manager/requests/${managerEmail}`)
      .then(res => setRequests(res.data || []))
      .catch(err => {
        console.error(err);
        setRequests([]);
      });
  }

  async function takeAction(requestId, action) {
    try {
      await API.put(`/api/manager/requests/${requestId}/action`, { action });
      fetchPending();
      alert(`Action ${action} performed.`);
    } catch (err) {
      console.error(err);
      alert("Failed to perform action.");
    }
  }

  async function approveCancel(requestId) {
    if (!window.confirm("Approve cancellation and restock items?")) return;
    try {
      await API.put(`/api/manager/requests/${requestId}/cancel-approve`);
      fetchPending();
      alert("Cancellation approved.");
    } catch (err) {
      console.error(err);
      alert("Failed to approve cancellation.");
    }
  }

  async function fetchReport() {
    try {
      const res = await API.get("/api/manager/reports/item-cost");
      // simple display
      alert("Report fetched. Check console for details.");
      console.log("Item cost report", res.data);
    } catch (err) {
      console.error(err);
      alert("Failed to fetch report.");
    }
  }

  if (role !== "MANAGER") return <div className="small">You are not a manager.</div>;

  return (
    <div>
      <div style={{ marginBottom: 10 }}>
        <button onClick={fetchPending} className="secondary">Refresh</button>{" "}
        <button onClick={fetchReport}>Get Item Cost Report</button>
      </div>

      {requests.length === 0 ? <div className="small">No pending requests.</div> :
        <ul className="simple">
          {requests.map(r => (
            <li key={r.id} className="item-row">
              <div>
                <div style={{ fontWeight: 700 }}>{r.id} • {r.status}</div>
                <div className="small">Requester: {r.requester?.fullName} ({r.requester?.email})</div>
                <div className="small">Created: {r.createdDate ? new Date(r.createdDate).toLocaleString() : "—"}</div>
                <div className="small">Items: {r.requestDetails ? r.requestDetails.map(d => `${d.item.itemName} x${d.quantity}`).join(", ") : "—"}</div>
                <div className="small">Total: ${r.totalCost || 0}</div>
              </div>

              <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {r.status === "PENDING" && <>
                  <button onClick={() => takeAction(r.id, "APPROVE")}>Approve</button>
                  <button className="secondary" onClick={() => takeAction(r.id, "REJECT")}>Reject</button>
                </>}
                {r.status === "CANCEL_REQUESTED" && <button onClick={() => approveCancel(r.id)}>Approve Cancel</button>}
              </div>
            </li>
          ))}
        </ul>
      }
    </div>
  );
}