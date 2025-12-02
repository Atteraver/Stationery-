import React from "react";
import ItemsList from "../Items/ItemsList";
import RequestForm from "../Requests/RequestForm";
import MyRequests from "../Requests/MyRequests";
import ManagerRequests from "../Manager/ManagerRequests";
import { getUserRole } from "../../utils/auth";

export default function Dashboard() {
  const role = getUserRole();

  return (
    <div>
      <h1>Dashboard</h1>

      <div className="grid">
        <div>
          <div className="card">
            <h3>Create Request</h3>
            <RequestForm />
          </div>

          <div style={{ marginTop: 18 }} className="card">
            <h3>My Requests</h3>
            <MyRequests />
          </div>
        </div>

        <div>
          <div className="card">
            <h3>Items Available</h3>
            <ItemsList />
          </div>

          {role === "MANAGER" && (
            <div style={{ marginTop: 18 }} className="card">
              <h3>Manager - Pending Requests</h3>
              <ManagerRequests />
            </div>
          )}
        </div>
      </div>
    </div>
  );
}