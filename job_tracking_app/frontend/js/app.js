// frontend/js/app.js
const API_BASE = "http://localhost:8080/api/applications";

const loginBtn = document.getElementById("loginBtn");
const logoutBtn = document.getElementById("logoutBtn");
const who = document.getElementById("who");

const form = document.getElementById("appForm");
const msg = document.getElementById("msg");
const tableBody = document.getElementById("appsTable");
const refreshBtn = document.getElementById("refreshBtn");

function getToken() {
  return localStorage.getItem("token");
}

function updateAuthUI() {
  const token = getToken();

  if (!token) {
    loginBtn.style.display = "inline-block";
    logoutBtn.style.display = "none";
    who.textContent = "";
  } else {
    loginBtn.style.display = "none";
    logoutBtn.style.display = "inline-block";
    who.textContent = localStorage.getItem("userEmail") || "";
  }
}

loginBtn.addEventListener("click", () => {
  window.location.href = "./auth.html";
});

logoutBtn.addEventListener("click", () => {
  localStorage.removeItem("token");
  localStorage.removeItem("userEmail");
  location.reload();
});

async function loadApps() {
  const token = getToken();
  if (!token) return;

  tableBody.innerHTML = "";

  const res = await fetch(API_BASE, {
    headers: { Authorization: `Bearer ${token}` }
  });

  if (!res.ok) {
    msg.textContent = "Error loading data";
    return;
  }

  const data = await res.json();

  data.forEach(app => {
    const tr = document.createElement("tr");

    tr.innerHTML = `
      <td>${app.id}</td>
      <td>${app.companyName}</td>
      <td><a href="${app.website}" target="_blank">${app.website}</a></td>
      <td>${app.dateApplied}</td>
      <td>${app.timeApplied}</td>
      <td>
        <select data-id="${app.id}" class="statusSelect">
          <option ${app.status === "APPLIED" ? "selected" : ""} value="APPLIED">APPLIED</option>
          <option ${app.status === "INTERVIEW" ? "selected" : ""} value="INTERVIEW">INTERVIEW</option>
          <option ${app.status === "REJECTED" ? "selected" : ""} value="REJECTED">REJECTED</option>
          <option ${app.status === "OFFER" ? "selected" : ""} value="OFFER">OFFER</option>
        </select>
      </td>
      <td>${app.notes ?? ""}</td>
      <td>
        <button class="action-btn" data-del="${app.id}">Delete</button>
      </td>
    `;

    tableBody.appendChild(tr);
  });

  document.querySelectorAll(".statusSelect").forEach(sel => {
    sel.addEventListener("change", async (e) => {
      const token2 = getToken();
      if (!token2) return;

      const id = e.target.getAttribute("data-id");
      const newStatus = e.target.value;

      await fetch(`${API_BASE}/${id}/status?status=${newStatus}`, {
        method: "PATCH",
        headers: { Authorization: `Bearer ${token2}` }
      });

      loadApps();
    });
  });

  document.querySelectorAll("[data-del]").forEach(btn => {
    btn.addEventListener("click", async (e) => {
      const token2 = getToken();
      if (!token2) return;

      const id = e.target.getAttribute("data-del");

      await fetch(`${API_BASE}/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token2}` }
      });

      loadApps();
    });
  });
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const token = getToken();
  if (!token) {
    msg.textContent = "Please login first";
    return;
  }

  const payload = {
    companyName: document.getElementById("companyName").value.trim(),
    website: document.getElementById("website").value.trim(),
    status: document.getElementById("status").value,
    notes: document.getElementById("notes").value.trim()
  };

  msg.textContent = "Saving...";

  const res = await fetch(API_BASE, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify(payload)
  });

  const data = await res.json();

  if (!res.ok) {
    msg.textContent = "Error: " + JSON.stringify(data);
    return;
  }

  msg.textContent = "Saved";
  form.reset();
  document.getElementById("status").value = "APPLIED";
  loadApps();
});

refreshBtn.addEventListener("click", loadApps);

updateAuthUI();
loadApps();
