// Enhanced login page with Dark Mode, OTP, and Social Login
document.addEventListener('DOMContentLoaded', function() {

    // ============ DARK MODE TOGGLE ============
    const themeToggle = document.getElementById('themeToggle');
    const htmlElement = document.documentElement;

    // Check for saved theme preference or default to 'light'
    const currentTheme = localStorage.getItem('theme') || 'light';
    htmlElement.setAttribute('data-theme', currentTheme);
    updateThemeIcon(currentTheme);

    if (themeToggle) {
        themeToggle.addEventListener('click', function() {
            const currentTheme = htmlElement.getAttribute('data-theme');
            const newTheme = currentTheme === 'light' ? 'dark' : 'light';

            htmlElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            updateThemeIcon(newTheme);

            // Add animation
            this.style.transform = 'rotate(360deg)';
            setTimeout(() => {
                this.style.transform = '';
            }, 300);
        });
    }

    function updateThemeIcon(theme) {
        if (themeToggle) {
            const icon = themeToggle.querySelector('i');
            if (theme === 'dark') {
                icon.className = 'bi bi-sun-fill';
            } else {
                icon.className = 'bi bi-moon-stars-fill';
            }
        }
    }

    // ============ LOGIN METHOD TABS ============
    const loginTabs = document.querySelectorAll('.login-tab');
    const emailForm = document.getElementById('loginForm');
    const otpForm = document.getElementById('otpForm');

    loginTabs.forEach(function(tab) {
        tab.addEventListener('click', function() {
            // Remove active from all tabs
            loginTabs.forEach(t => t.classList.remove('active'));
            // Add active to clicked tab
            this.classList.add('active');

            const tabType = this.getAttribute('data-tab');

            if (tabType === 'email') {
                emailForm.style.display = 'block';
                otpForm.style.display = 'none';
            } else if (tabType === 'otp') {
                emailForm.style.display = 'none';
                otpForm.style.display = 'block';
            }
        });
    });

    // ============ OTP FUNCTIONALITY ============
    const phoneInput = document.getElementById('phoneNumber');
    const sendOtpBtn = document.getElementById('sendOtpBtn');
    const phoneStep = document.getElementById('phoneStep');
    const otpStep = document.getElementById('otpStep');
    const phoneDisplay = document.getElementById('phoneDisplay');
    const otpBoxes = document.querySelectorAll('.otp-box');
    const verifyOtpBtn = document.getElementById('verifyOtpBtn');
    const resendOtpBtn = document.getElementById('resendOtpBtn');
    const changeNumberBtn = document.getElementById('changeNumberBtn');
    const otpTimer = document.getElementById('otpTimer');

    let timerInterval;
    let timeLeft = 60;

    // Send OTP
    if (sendOtpBtn) {
        sendOtpBtn.addEventListener('click', function() {
            const phoneNumber = phoneInput.value.trim();
            if (!phoneNumber) {
                alert('Please enter your phone number');
                return;
            }

            // Send OTP via API
            this.disabled = true;
            this.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Sending...';

            fetch('/api/otp/send', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ phoneNumber: phoneNumber })
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        phoneStep.style.display = 'none';
                        otpStep.style.display = 'block';
                        phoneDisplay.textContent = phoneNumber;
                        startOtpTimer();
                        otpBoxes[0].focus();
                    } else {
                        alert('Failed to send OTP: ' + data.message);
                        sendOtpBtn.disabled = false;
                        sendOtpBtn.innerHTML = '<i class="bi bi-send me-2"></i>Send OTP';
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Failed to send OTP. Please try again.');
                    sendOtpBtn.disabled = false;
                    sendOtpBtn.innerHTML = '<i class="bi bi-send me-2"></i>Send OTP';
                });
        });
    }

    // OTP Box Navigation
    otpBoxes.forEach(function(box, index) {
        box.addEventListener('input', function(e) {
            const value = this.value;

            if (value.length === 1 && index < otpBoxes.length - 1) {
                otpBoxes[index + 1].focus();
            }

            // Auto-verify when all boxes filled
            const allFilled = Array.from(otpBoxes).every(b => b.value.length === 1);
            if (allFilled) {
                verifyOtpBtn.disabled = false;
            }
        });

        box.addEventListener('keydown', function(e) {
            if (e.key === 'Backspace' && this.value === '' && index > 0) {
                otpBoxes[index - 1].focus();
            }
        });

        // Allow only numbers
        box.addEventListener('keypress', function(e) {
            if (!/[0-9]/.test(e.key)) {
                e.preventDefault();
            }
        });
    });

    // Verify OTP
    if (verifyOtpBtn) {
        verifyOtpBtn.addEventListener('click', function() {
            const otp = Array.from(otpBoxes).map(b => b.value).join('');
            const phoneNumber = phoneDisplay.textContent;

            if (otp.length !== 6) {
                alert('Please enter complete OTP');
                return;
            }

            this.disabled = true;
            this.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Verifying...';

            fetch('/api/otp/verify', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        phoneNumber: phoneNumber,
                        otp: otp
                    })
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('✓ OTP Verified Successfully! Logging in...');
                        window.location.href = '/dashboard';
                    } else {
                        alert('✗ Invalid or expired OTP. Please try again.');
                        verifyOtpBtn.disabled = false;
                        verifyOtpBtn.innerHTML = '<i class="bi bi-shield-check me-2"></i>Verify OTP';
                        otpBoxes.forEach(b => b.value = '');
                        otpBoxes[0].focus();
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Failed to verify OTP. Please try again.');
                    verifyOtpBtn.disabled = false;
                    verifyOtpBtn.innerHTML = '<i class="bi bi-shield-check me-2"></i>Verify OTP';
                });
        });
    }

    // Resend OTP
    if (resendOtpBtn) {
        resendOtpBtn.addEventListener('click', function() {
            const phoneNumber = phoneDisplay.textContent;
            clearInterval(timerInterval);
            this.disabled = true;
            this.innerHTML = '<span class="spinner-border spinner-border-sm"></span>';

            fetch('/api/otp/resend', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ phoneNumber: phoneNumber })
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        timeLeft = 60;
                        startOtpTimer();
                        alert('OTP resent successfully!');
                    } else {
                        alert('Failed to resend OTP: ' + data.message);
                        resendOtpBtn.disabled = false;
                        resendOtpBtn.innerHTML = '<i class="bi bi-arrow-clockwise me-1"></i>Resend OTP';
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Failed to resend OTP. Please try again.');
                    resendOtpBtn.disabled = false;
                    resendOtpBtn.innerHTML = '<i class="bi bi-arrow-clockwise me-1"></i>Resend OTP';
                });
        });
    }

    // Change Number
    if (changeNumberBtn) {
        changeNumberBtn.addEventListener('click', function() {
            clearInterval(timerInterval);
            otpStep.style.display = 'none';
            phoneStep.style.display = 'block';
            sendOtpBtn.disabled = false;
            sendOtpBtn.innerHTML = '<i class="bi bi-send me-2"></i>Send OTP';
            otpBoxes.forEach(b => b.value = '');
        });
    }

    function startOtpTimer() {
        resendOtpBtn.disabled = true;
        timerInterval = setInterval(function() {
            if (timeLeft > 0) {
                timeLeft--;
                otpTimer.textContent = `(${timeLeft}s)`;
            } else {
                clearInterval(timerInterval);
                resendOtpBtn.disabled = false;
                resendOtpBtn.innerHTML = '<i class="bi bi-arrow-clockwise me-1"></i>Resend OTP';
                otpTimer.textContent = '';
            }
        }, 1000);
    }

    // ============ SOCIAL LOGIN ============
    const googleLogin = document.getElementById('googleLogin');
    const facebookLogin = document.getElementById('facebookLogin');

    if (googleLogin) {
        googleLogin.addEventListener('click', function() {
            // Redirect to OAuth2 authorization endpoint
            window.location.href = '/oauth2/authorization/google';
        });
    }

    if (facebookLogin) {
        facebookLogin.addEventListener('click', function() {
            // Redirect to OAuth2 authorization endpoint
            window.location.href = '/oauth2/authorization/facebook';
        });
    }

    // ============ PASSWORD TOGGLE ============
    var toggle = document.getElementById('togglePw');
    var pw = document.getElementById('password');

    if (toggle && pw) {
        toggle.addEventListener('click', function(e) {
            e.preventDefault();
            if (pw.type === 'password') {
                pw.type = 'text';
                toggle.innerHTML = '<i class="bi bi-eye-slash text-muted"></i>';
            } else {
                pw.type = 'password';
                toggle.innerHTML = '<i class="bi bi-eye text-muted"></i>';
            }
        });
    }

    // ============ FORM SUBMISSION ============
    var form = document.getElementById('loginForm');
    var submitBtn = document.getElementById('submitBtn');

    if (form && submitBtn) {
        var spinner = submitBtn.querySelector('.btn-spinner');
        var btnText = submitBtn.querySelector('.btn-text');

        form.addEventListener('submit', function() {
            submitBtn.setAttribute('disabled', 'disabled');
            if (spinner && btnText) {
                spinner.classList.remove('visually-hidden');
                btnText.classList.add('visually-hidden');
            }
        });
    }

    // ============ INPUT FOCUS ANIMATION ============
    var inputs = document.querySelectorAll('.form-control');
    inputs.forEach(function(input) {
        input.addEventListener('focus', function() {
            if (this.parentElement.parentElement) {
                this.parentElement.parentElement.classList.add('input-focused');
            }
        });
        input.addEventListener('blur', function() {
            if (this.parentElement.parentElement) {
                this.parentElement.parentElement.classList.remove('input-focused');
            }
        });
    });

});