const API = "http://localhost:8080/api/auth";

const loginCard = document.getElementById("loginCard");
const registerCard = document.getElementById("registerCard");

const showRegister = document.getElementById("showRegister");
const showLogin = document.getElementById("showLogin");

const loginBtn = document.getElementById("loginBtn");
const registerBtn = document.getElementById("registerBtn");

const loginMsg = document.getElementById("loginMsg");
const regMsg = document.getElementById("regMsg");

showRegister.addEventListener("click", () => {
  loginCard.style.display = "none";
  registerCard.style.display = "block";
});

showLogin.addEventListener("click", () => {
  registerCard.style.display = "none";
  loginCard.style.display = "block";
});

loginBtn.addEventListener("click", async () => {
  loginMsg.textContent = "";

  const payload = {
    email: document.getElementById("loginEmail").value.trim(),
    password: document.getElementById("loginPassword").value
  };

  const res = await fetch(`${API}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });

  const data = await res.json();

  if (!res.ok) {
    loginMsg.textContent = "Invalid email or password";
    return;
  }

  localStorage.setItem("token", data.token);
  localStorage.setItem("userEmail", data.email);

  window.location.href = "./index.html";
});

registerBtn.addEventListener("click", async () => {
  regMsg.textContent = "";

  const payload = {
    name: document.getElementById("regName").value.trim(),
    email: document.getElementById("regEmail").value.trim(),
    password: document.getElementById("regPassword").value
  };

  const res = await fetch(`${API}/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });

  const data = await res.json();

  if (!res.ok) {
    regMsg.textContent = "Registration failed";
    return;
  }

  regMsg.textContent = "Account created. You can login now.";

  registerCard.style.display = "none";
  loginCard.style.display = "block";
});
