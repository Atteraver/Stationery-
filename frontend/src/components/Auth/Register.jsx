import React, { useState } from "react";
import API from "../../api/api";
import { useNavigate } from "react-router-dom";

export default function Register() {
  const [email, setEmail] = useState("");
  const [fullName, setFullName] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("EMPLOYEE");
  const [maxPurchaseLimit, setMaxPurchaseLimit] = useState(500);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const body = { email, password, fullName, role, maxPurchaseLimit };
      const res = await API.post("/api/auth/register", body);
      console.log(res.data);
      alert("Registered successfully. You can login now.");
      navigate("/login");
    } catch (err) {
      console.error(err);
      alert("Registration failed. Check console for details.");
    }
  };

  return (
    <div className="form card">
      <h2>Register</h2>
      <form onSubmit={handleSubmit}>
        <label className="small">Full name</label>
        <input value={fullName} onChange={(e) => setFullName(e.target.value)} required />
        <label className="small">Email</label>
        <input value={email} onChange={(e) => setEmail(e.target.value)} required />
        <label className="small">Password</label>
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        <label className="small">Role</label>
        <select value={role} onChange={(e) => setRole(e.target.value)}>
          <option value="EMPLOYEE">EMPLOYEE</option>
          <option value="MANAGER">MANAGER</option>
        </select>

        <label className="small">Max purchase limit</label>
        <input type="number" value={maxPurchaseLimit} onChange={(e) => setMaxPurchaseLimit(Number(e.target.value))} />

        <button type="submit">Register</button>
      </form>
    </div>
  );
}