// =================== PAGE NAVIGATION ===================
window.navigateTo = function(pageId) {
  document.querySelectorAll('.content-page').forEach(p => p.style.display = 'none');
  document.getElementById(pageId).style.display = 'block';
  if (pageId === 'tips') showTips();
  if (pageId === 'support') showFAQs();
  if (pageId === 'calendar') renderCalendar();
}

// =================== GLOBAL VARIABLES ===================
let transactions = []

// =================== INITIALIZE DASHBOARD ===================
document.addEventListener("DOMContentLoaded", function () {
  applyStoredTheme();
  document.getElementById("home").style.display = "block";
  updateDashboard();
  navigateTo("home");
});

// =================== DASHBOARD UPDATE ===================
function updateDashboard() {
  renderTransactionsTable();
  renderBarChart();
  updatePieChart();
  showDateTime();
}

// =================== RENDER TRANSACTIONS ===================
function renderTransactionsTable() {
  fetch("/transactions/all")
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        transactions = data.transactions;
        const table = document.getElementById("transactions-table");
        table.innerHTML = "";

        const currencySelect = document.getElementById("currency");
        const currency = currencySelect ? currencySelect.value : "INR";

        transactions.forEach((t) => {
          const row = document.createElement("tr");
          row.innerHTML = `
            <td>${t.description}</td>
            <td>${currency} ${t.amount}</td>
            <td>${t.type}</td>
            <td>${t.category}</td>
            <td>${new Date(t.date).toLocaleDateString()}</td>
            <td><button class="btn btn-danger btn-sm" onclick="deleteTransaction(${t.id})">Delete</button></td>`;
          table.appendChild(row);
        });
      }
    })
    .catch((error) => console.error("Error fetching transactions:", error));
}

// =================== DELETE TRANSACTION ===================
function deleteTransaction(id) {
  fetch(`/transactions/delete/${id}`, {
    method: "DELETE",
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        document.getElementById("savings").textContent = data.savings;
        document.getElementById("monthly-income").textContent = data.totalIncome;
        document.getElementById("monthly-expense").textContent = data.totalExpense;
        renderTransactionsTable();
        renderBarChart();
        updatePieChart();
      } else {
        alert(data.message);
      }
    })
    .catch((error) => console.error("Error deleting transaction:", error));
}

// =================== ADD TRANSACTION ===================
document.getElementById("transaction-form").addEventListener("submit", function (e) {
  e.preventDefault();

  const desc = document.getElementById("transaction-desc").value;
  const amt = Number.parseFloat(document.getElementById("transaction-amount").value);
  const type = document.getElementById("transaction-type").value;
  const cat = document.getElementById("transaction-category").value;

  const formData = new FormData();
  formData.append("description", desc);
  formData.append("amount", amt);
  formData.append("type", type);
  formData.append("category", cat);

  fetch("/transactions/add", {
    method: "POST",
    body: formData,
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        document.getElementById("savings").textContent = data.savings;
        document.getElementById("monthly-income").textContent = data.totalIncome;
        document.getElementById("monthly-expense").textContent = data.totalExpense;
        this.reset();
        renderTransactionsTable();
        renderBarChart();
        updatePieChart();
      } else {
        alert(data.message);
      }
    })
    .catch((error) => console.error("Error adding transaction:", error));
});

// =================== RENDER BAR CHART ===================
function renderBarChart() {
  const ctx = document.getElementById("barChart").getContext("2d");
  const data = Array(7).fill(0);
  const days = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

  transactions.forEach((t) => {
    if (t.type === "expense") {
      const dayIndex = new Date(t.date).getDay();
      data[dayIndex] += t.amount;
    }
  });

  if (window.barChartInstance) window.barChartInstance.destroy();
  window.barChartInstance = new Chart(ctx, {
    type: "bar",
    data: {
      labels: days,
      datasets: [{
        label: "Expenses by Day",
        data,
        backgroundColor: "#ff6f61",
      }],
    },
  });
}

// =================== RENDER PIE CHART ===================
function updatePieChart() {
  const ctx = document.getElementById("pieChart").getContext("2d");
  const range = document.getElementById("timeRange").value;
  const now = new Date();
  const filtered = transactions.filter((t) => {
    const txDate = new Date(t.date);
    if (range === "weekly") return (now - txDate) / (1000 * 60 * 60 * 24) <= 7;
    if (range === "monthly") return txDate.getMonth() === now.getMonth();
    if (range === "yearly") return txDate.getFullYear() === now.getFullYear();
    return true;
  });

  let income = 0, expense = 0;
  filtered.forEach((t) => {
    if (t.type === "income") income += t.amount;
    else expense += t.amount;
  });

  if (window.pieChartInstance) window.pieChartInstance.destroy();
  window.pieChartInstance = new Chart(ctx, {
    type: "pie",
    data: {
      labels: ["Income", "Expenses"],
      datasets: [{
        data: [income, expense],
        backgroundColor: ["#007bff", "#ff6f61"],
      }],
    },
  });
}

// =================== RENDER CALENDAR ===================
function renderCalendar() {
  const events = transactions.map(t => ({
    title: `${t.description} ₹${t.amount}`,
    start: t.date,
    color: t.type === "expense" ? "#dc3545" : "#198754"
  }));

  const calendarEl = document.getElementById("fc-calendar");
  const calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: "dayGridMonth",
    events
  });
  calendar.render();
}

// =================== SHOW DATE & TIME ===================
function showDateTime() {
  const now = new Date();
  const options = { weekday: "long", year: "numeric", month: "long", day: "numeric" };
  document.getElementById("date-time").textContent =
    now.toLocaleDateString(undefined, options) + " | " + now.toLocaleTimeString();
}

// =================== SHOW TIPS ===================
function showTips() {
  const tips = [
    "Track your expenses regularly to understand your spending habits.",
    "Set a monthly budget and try to stick to it.",
    "Save at least 20% of your income for future needs.",
    "Invest in long-term assets like stocks or mutual funds for better growth.",
    "Cut down on unnecessary expenses, like subscriptions you don't use.",
  ];

  const tipsList = document.getElementById("tips-list");
  tipsList.innerHTML = "";
  tips.forEach((tip) => {
    const li = document.createElement("li");
    li.textContent = tip;
    tipsList.appendChild(li);
  });
}

// =================== SHOW FAQs ===================
function showFAQs() {
  const faqs = [
    {
      question: "How do I add a transaction?",
      answer: "Go to the 'Expenses' page and fill out the transaction form.",
    },
    {
      question: "How do I track my savings?",
      answer: "Savings are shown on the Home page and calculated automatically.",
    },
    {
      question: "Can I get a monthly report?",
      answer: "Yes, you can export it from the dashboard.",
    },
  ];

  const list = document.getElementById("faqs-list");
  list.innerHTML = "";
  faqs.forEach((faq) => {
    const item = document.createElement("li");
    item.classList.add("list-group-item");
    item.innerHTML = `<strong>Q: ${faq.question}</strong><br/>A: ${faq.answer}`;
    list.appendChild(item);
  });
}

// =================== SETTINGS ===================
document.getElementById("settings-form").addEventListener("submit", (e) => {
  e.preventDefault();

  const theme = document.getElementById("theme").value;
  const currency = document.getElementById("currency").value;
  const language = document.getElementById("language").value;
  const emailNotifications = document.getElementById("email-notifications").checked;

  const formData = new FormData();
  formData.append("theme", theme);
  formData.append("currency", currency);
  formData.append("language", language);
  formData.append("emailNotifications", emailNotifications);

  fetch("/settings/update", {
    method: "POST",
    body: formData,
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        applyTheme(theme);
        alert("Settings saved successfully!");
      } else {
        alert(data.message);
      }
    })
    .catch((error) => console.error("Error updating settings:", error));
});

// =================== APPLY & STORE THEME ===================
function applyTheme(theme) {
  const body = document.body;

  if (theme === "light") {
    body.classList.remove("bg-dark", "text-white");
    body.classList.add("bg-light", "text-dark");
  } else {
    body.classList.remove("bg-light", "text-dark");
    body.classList.add("bg-dark", "text-white");
  }

  localStorage.setItem("theme", theme);
}

function exportFile(type) {
  window.open(`/transactions/export/${type}`, "_blank");
}

function applyStoredTheme() {
  const stored = localStorage.getItem("theme") || "dark";
  applyTheme(stored);
}
