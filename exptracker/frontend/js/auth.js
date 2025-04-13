document.addEventListener('DOMContentLoaded', function() {
    // Login functionality
    if (document.getElementById('loginForm')) {
        document.getElementById('loginForm').addEventListener('submit', function(e) {
            e.preventDefault();
            const email = document.getElementById('loginEmail').value;
            const password = document.getElementById('loginPassword').value;

            if (email && password) {
                localStorage.setItem('loggedIn', 'true');
                window.location.href = 'dashboard.html';
            } else {
                alert('Please enter email and password!');
            }
        });
    }

    // Signup functionality
    if (document.getElementById('signupForm')) {
        document.getElementById('signupForm').addEventListener('submit', function(e) {
            e.preventDefault();
            const name = document.getElementById('signupName').value.trim();
            const email = document.getElementById('signupEmail').value.trim();
            const password = document.getElementById('signupPassword').value;
            const confirmPassword = document.getElementById('signupConfirmPassword').value;
            const messageElement = document.getElementById('signupMessage');

            if (!name || !email || !password || !confirmPassword) {
                messageElement.textContent = 'Please fill all fields!';
                messageElement.className = 'signup-message error';
                return;
            }

            if (password.length < 6) {
                messageElement.textContent = 'Password must be at least 6 characters!';
                messageElement.className = 'signup-message error';
                return;
            }

            if (password !== confirmPassword) {
                messageElement.textContent = 'Passwords do not match!';
                messageElement.className = 'signup-message error';
                return;
            }

            messageElement.textContent = 'Signup successful! Redirecting to dashboard...';
            messageElement.className = 'signup-message success';
            setTimeout(() => {
                localStorage.setItem('loggedIn', 'true');
                window.location.href = 'dashboard.html';
                document.getElementById('signupForm').reset();
                messageElement.textContent = '';
            }, 2000);
        });
    }

    if (localStorage.getItem('loggedIn') === 'true') {
        window.location.href = 'dashboard.html';
    }
});
