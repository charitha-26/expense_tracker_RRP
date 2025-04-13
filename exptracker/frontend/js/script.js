let transactions = JSON.parse(localStorage.getItem('transactions')) || [];
let currentBalance = parseFloat(localStorage.getItem('currentBalance')) || 0;

document.addEventListener('DOMContentLoaded', () => {
    if (!localStorage.getItem('loggedIn')) window.location.href = 'login.html';
    
    updateBalanceDisplay();
    document.getElementById('transactionDate').valueAsDate = new Date();
    
    document.getElementById('setBalanceBtn').addEventListener('click', setInitialBalance);
    document.getElementById('addExpense').addEventListener('click', () => addTransaction('Expense'));
    document.getElementById('addDeposit').addEventListener('click', () => addTransaction('Deposit'));
    document.getElementById('clearAll').addEventListener('click', clearAll);
    document.getElementById('logoutBtn').addEventListener('click', logout);

    document.getElementById('transactionsTable').addEventListener('click', handleTableActions);

    updateTable();
});

function setInitialBalance() {
    const balanceInput = parseFloat(document.getElementById('initialBalance').value);
    if (!isNaN(balanceInput) && balanceInput >= 0) {
        currentBalance = balanceInput;
        localStorage.setItem('currentBalance', currentBalance);
        updateBalanceDisplay();
        document.getElementById('initialBalance').value = '';
    } else {
        alert('Please enter a valid positive amount!');
    }
}

function addTransaction(type) {
    const amountField = type === 'Expense' ? 'expenseAmount' : 'depositAmount';
    const amount = parseFloat(document.getElementById(amountField).value);
    const date = document.getElementById('transactionDate').value;
    const category = document.getElementById('category').value;

    if (!amount || amount <= 0 || !date || !category) {
        alert('Please enter a valid amount greater than 0 and fill all fields!');
        return;
    }

    const transaction = {
        id: Date.now(),
        type,
        amount: type === 'Expense' ? -amount : amount, // Expense subtracts, Deposit adds
        category,
        date
    };

    currentBalance += transaction.amount;
    transactions.push(transaction);
    saveData();

    updateTable();
    updateBalanceDisplay();
    document.getElementById(amountField).value = '';
}

function clearAll() {
    if (confirm('Are you sure you want to clear all transactions?')) {
        transactions = [];
        currentBalance = 0;
        saveData();
        updateTable();
        updateBalanceDisplay();
    }
}

function handleTableActions(e) {
    if (e.target.classList.contains('delete-btn')) {
        if (confirm('Are you sure you want to delete this transaction?')) {
            const transactionId = parseInt(e.target.dataset.id);
            const transactionIndex = transactions.findIndex(t => t.id === transactionId);
            if (transactionIndex > -1) {
                currentBalance -= transactions[transactionIndex].amount; // Adjust balance
                transactions.splice(transactionIndex, 1); // Remove transaction
                saveData();
                updateTable();
                updateBalanceDisplay();
            }
        }
    }
}

function logout() {
    localStorage.removeItem('loggedIn');
    window.location.href = 'login.html';
}

function updateBalanceDisplay() {
    const balanceElement = document.querySelector('#currentBalanceDisplay .balance-value');
    if (balanceElement) {
        balanceElement.textContent = `₹${currentBalance.toFixed(2)}`;
        balanceElement.className = `balance-value ${currentBalance >= 0 ? 'deposit' : 'expense'}`;
    }
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
