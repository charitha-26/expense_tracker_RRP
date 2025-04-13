// Auth Module
document.addEventListener('DOMContentLoaded', function() {
    const authContainer = document.getElementById('authContainer');
    const appContainer = document.getElementById('appContainer');
    
    // Show login form by default
    authContainer.innerHTML = `
        <div class="auth-tabs">
            <button class="active" id="loginTab">Login</button>
            <button id="signupTab">Sign Up</button>
        </div>
        
        <div class="auth-form active" id="loginForm">
            <h2>Login</h2>
            <input type="email" id="loginEmail" placeholder="Email" required>
            <input type="password" id="loginPassword" placeholder="Password" required>
            <button id="loginBtn">Login</button>
        </div>
        
        <div class="auth-form" id="signupForm">
            <h2>Sign Up</h2>
            <input type="text" id="signupName" placeholder="Name" required>
            <input type="email" id="signupEmail" placeholder="Email" required>
            <input type="password" id="signupPassword" placeholder="Password" required>
            <button id="signupBtn">Sign Up</button>
        </div>
    `;

    // Tab switching
    document.getElementById('loginTab').addEventListener('click', () => {
        document.getElementById('loginForm').classList.add('active');
        document.getElementById('signupForm').classList.remove('active');
        document.getElementById('loginTab').classList.add('active');
        document.getElementById('signupTab').classList.remove('active');
    });

    document.getElementById('signupTab').addEventListener('click', () => {
        document.getElementById('signupForm').classList.add('active');
        document.getElementById('loginForm').classList.remove('active');
        document.getElementById('signupTab').classList.add('active');
        document.getElementById('loginTab').classList.remove('active');
    });

    // Mock authentication
    document.getElementById('loginBtn').addEventListener('click', function() {
        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;
        
        if (email && password) {
            authContainer.style.display = 'none';
            appContainer.style.display = 'block';
            localStorage.setItem('loggedIn', 'true');
        } else {
            alert('Please enter email and password!');
        }
    });

    document.getElementById('signupBtn').addEventListener('click', function() {
        const name = document.getElementById('signupName').value;
        const email = document.getElementById('signupEmail').value;
        const password = document.getElementById('signupPassword').value;
        
        if (name && email && password) {
            alert('Signup successful! Please login.');
            document.getElementById('loginTab').click();
        } else {
            alert('Please fill all fields!');
        }
    });

    // Check if already logged in
    if (localStorage.getItem('loggedIn') === 'true') {
        authContainer.style.display = 'none';
        appContainer.style.display = 'block';
    }
});