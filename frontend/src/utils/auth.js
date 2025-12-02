// simple auth helpers for storing token + user info in localStorage

export function saveAuth({ token, userId, role, email }) {
  if (token) localStorage.setItem("token", token);
  if (userId !== undefined) localStorage.setItem("userId", String(userId));
  if (role) localStorage.setItem("role", role);
  if (email) localStorage.setItem("email", email);
}

export function getAuth() {
  return localStorage.getItem("token");
}

export function getUserId() {
  const v = localStorage.getItem("userId");
  return v ? Number(v) : null;
}

export function getUserRole() {
  return localStorage.getItem("role");
}

export function getUserEmail() {
  return localStorage.getItem("email");
}

export function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userId");
  localStorage.removeItem("role");
  localStorage.removeItem("email");
}