import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json"
  }
});

// Attach Authorization header automatically for authenticated requests
API.interceptors.request.use((config) => {
  // Try to get stored credentials
  const email = localStorage.getItem("userEmail");
  const password = localStorage.getItem("userPassword");

  if (email && password) {
    // Create Basic Auth header
    const basicAuth = btoa(`${email}:${password}`);
    config.headers["Authorization"] = `Basic ${basicAuth}`;
  }

  return config;
});

export default API;