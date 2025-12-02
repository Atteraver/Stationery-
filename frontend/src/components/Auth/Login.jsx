import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../../api/api";
import { saveAuth } from "../../utils/auth";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await API.post("/api/auth/login", { email, password });

      // The backend returns a String message with user info
      const data = res.data;

      // Parse user info from response if available
      let role = null, userId = null;
      if (typeof data === "string") {
        // Extract role and userId from string like "Login successful. Token placeholder. User ID: 1, Role: EMPLOYEE"
        const roleMatch = data.match(/Role:\s*(\w+)/);
        const idMatch = data.match(/User ID:\s*(\d+)/);
        if (roleMatch) role = roleMatch[1];
        if (idMatch) userId = idMatch[1];
      }

      // Store credentials for Basic Auth
      localStorage.setItem("userEmail", email);
      localStorage.setItem("userPassword", password);
      localStorage.setItem("userId", userId);
      localStorage.setItem("role", role);

      // Also save to auth utility for compatibility
      saveAuth({ token: "basic-auth", userId, role, email });

      // Redirect to dashboard
      navigate("/dashboard");
    } catch (err) {
      console.error(err);
      alert("Login failed. Check credentials and backend availability.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form card">
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <label className="small">Email</label>
        <input value={email} onChange={(e) => setEmail(e.target.value)} required />
        <label className="small">Password</label>
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        <button type="submit" disabled={loading}>{loading ? "Logging in..." : "Login"}</button>
      </form>
    </div>
  );
}