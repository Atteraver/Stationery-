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

      // The backend returns String in AuthController. Be tolerant:
      const data = res.data;

      // Try to parse if backend returned JSON-stringified object, or accept object
      let token = null, role = null, userId = null, userEmail = email;
      if (typeof data === "string") {
        // Try JSON parse
        try {
          const parsed = JSON.parse(data);
          token = parsed.token || parsed.authToken || null;
          role = parsed.role || null;
          userId = parsed.userId || parsed.id || null;
          userEmail = parsed.email || userEmail;
        } catch {
          // fallback: treat string as token or role
          // If backend returns just role like "MANAGER" or token string, we store as token.
          token = data;
        }
      } else if (typeof data === "object") {
        token = data.token || data.authToken || data.accessToken || null;
        role = data.role || null;
        userId = data.userId || data.id || null;
        userEmail = data.email || userEmail;
      }

      // Save anything we found
      saveAuth({ token, userId, role, email: userEmail });

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