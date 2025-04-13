// script.js
let transactions = JSON.parse(localStorage.getItem('transactions')) || [];
let currentBalance = parseFloat(localStorage.getItem('currentBalance')) || 0;

document.addEventListener('DOMContentLoaded', () => {
    if (!localStorage.getItem('loggedIn')) return;
    document.getElementById('appContainer').style.display = 'block';
    
    // Initialize UI
    updateBalanceDisplay();
    document.getElementById('transactionDate').valueAsDate = new Date();
    
    // Set balance button
    document.getElementById('setBalanceBtn').addEventListener('click', setInitialBalance);

    // Transaction form
    document.getElementById('transactionForm').addEventListener('submit', addTransaction);

    // Delete transaction handler (event delegation)
    document.getElementById('transactionsTable').addEventListener('click', handleDelete);

    // Logout
    document.getElementById('logoutBtn').addEventListener('click', logout);

    // Initial table load
    updateTable();
});

function setInitialBalance() {
    const balanceInput = parseFloat(document.getElementById('initialBalance').value);
    if (!isNaN(balanceInput)) {
        currentBalance = balanceInput;
        localStorage.setItem('currentBalance', currentBalance);
        updateBalanceDisplay();
        document.getElementById('initialBalance').value = '';
    } else {
        alert('Please enter a valid amount!');
    }
}

function addTransaction(e) {
    e.preventDefault();
    
    const amount = parseFloat(document.getElementById('amount').value);
    const date = document.getElementById('transactionDate').value;
    const category = document.getElementById('category').value;
    const type = document.getElementById('transactionType').value;
    
    if (!amount || !date || !category) {
        alert('Please fill all fields!');
        return;
    }
    
    // Create transaction
    const transaction = {
        id: Date.now(),
        type,
        amount: type === 'Deposit' ? amount : -amount,
        category,
        date
    };
    
    // Update balance
    currentBalance += transaction.amount;
    
    // Save data
    transactions.push(transaction);
    saveData();
    
    // Update UI
    updateTable();
    updateBalanceDisplay();
    this.reset();
    document.getElementById('transactionDate').valueAsDate = new Date();
}

function handleDelete(e) {
    if (e.target.classList.contains('delete-btn')) {
        if (confirm('Are you sure you want to delete this transaction?')) {
            const transactionId = parseInt(e.target.dataset.id);
            const transactionIndex = transactions.findIndex(t => t.id === transactionId);
            
            if (transactionIndex > -1) {
                // Update balance
                currentBalance -= transactions[transactionIndex].amount;
                
                // Remove transaction
                transactions.splice(transactionIndex, 1);
                saveData();
                
                // Update UI
                updateTable();
                updateBalanceDisplay();
            }
        }
    }
}

function logout() {
    localStorage.removeItem('loggedIn');
    window.location.reload();
}

function updateBalanceDisplay() {
    const balanceElement = document.getElementById('currentBalance');
    balanceElement.textContent = `₹${currentBalance.toFixed(2)}`;
    balanceElement.className = currentBalance >= 0 ? 'deposit' : 'expense';
}

function updateTable() {
    const tbody = document.getElementById('transactionsTable').querySelector('tbody');
    tbody.innerHTML = '';
    
    transactions.forEach(transaction => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${transaction.type}</td>
            <td class="${transaction.type.toLowerCase()}">
                ${transaction.amount >= 0 ? '₹' : '-₹'}${Math.abs(transaction.amount).toFixed(2)}
            </td>
            <td>${transaction.category}</td>
            <td>${transaction.date}</td>
            <td><button class="delete-btn" data-id="${transaction.id}">Delete</button></td>
        `;
        tbody.appendChild(row);
    });
}

function saveData() {
    localStorage.setItem('currentBalance', currentBalance);
    localStorage.setItem('transactions', JSON.stringify(transactions));
}