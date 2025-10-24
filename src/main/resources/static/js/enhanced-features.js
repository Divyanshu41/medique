/**
 * Hospital Appointment System - Enhanced Frontend JavaScript
 * Includes: Loading indicators, Toast notifications, Client-side validation
 */

// ==================== LOADING INDICATOR ====================

/**
 * Show loading spinner overlay
 */
function showLoading(message = 'Loading...') {
    // Create loading overlay if it doesn't exist
    if (!document.getElementById('loadingOverlay')) {
        const overlay = document.createElement('div');
        overlay.id = 'loadingOverlay';
        overlay.innerHTML = `
            <div class="loading-spinner">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="loading-message mt-3">${message}</p>
            </div>
        `;
        document.body.appendChild(overlay);
    } else {
        document.getElementById('loadingOverlay').style.display = 'flex';
        document.querySelector('.loading-message').textContent = message;
    }
}

/**
 * Hide loading spinner overlay
 */
function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.style.display = 'none';
    }
}

// ==================== TOAST NOTIFICATIONS ====================

/**
 * Show toast notification
 * @param {string} message - Message to display
 * @param {string} type - success, error, warning, info
 * @param {number} duration - Duration in milliseconds (default 3000)
 */
function showToast(message, type = 'success', duration = 3000) {
    // Create toast container if it doesn't exist
    let toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toastContainer';
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        toastContainer.style.zIndex = '9999';
        document.body.appendChild(toastContainer);
    }

    // Icon mapping
    const icons = {
        success: '✓',
        error: '✗',
        warning: '⚠',
        info: 'ℹ'
    };

    // Color mapping
    const colors = {
        success: '#28a745',
        error: '#dc3545',
        warning: '#ffc107',
        info: '#17a2b8'
    };

    // Create toast element
    const toast = document.createElement('div');
    toast.className = 'toast align-items-center text-white border-0';
    toast.style.backgroundColor = colors[type];
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                <span class="toast-icon">${icons[type]}</span>
                <span class="ms-2">${message}</span>
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;

    toastContainer.appendChild(toast);

    // Show toast
    const bsToast = new bootstrap.Toast(toast, { autohide: true, delay: duration });
    bsToast.show();

    // Remove toast after animation
    toast.addEventListener('hidden.bs.toast', () => {
        toast.remove();
    });
}

// ==================== FORM VALIDATION ====================

/**
 * Validate appointment form
 */
function validateAppointmentForm() {
    const form = document.getElementById('appointmentForm');
    if (!form) return true;

    let isValid = true;
    const errors = [];

    // Validate doctor selection
    const doctorId = document.getElementById('doctorId') ? .value;
    if (!doctorId) {
        errors.push('Please select a doctor');
        isValid = false;
    }

    // Validate date
    const appointmentDate = document.getElementById('appointmentDate') ? .value;
    if (!appointmentDate) {
        errors.push('Please select an appointment date');
        isValid = false;
    } else {
        const selectedDate = new Date(appointmentDate);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (selectedDate < today) {
            errors.push('Appointment date cannot be in the past');
            isValid = false;
        }
    }

    // Validate time
    const appointmentTime = document.getElementById('appointmentTime') ? .value;
    if (!appointmentTime) {
        errors.push('Please select an appointment time');
        isValid = false;
    }

    // Show errors
    if (!isValid) {
        showToast(errors.join('<br>'), 'error', 5000);
    }

    return isValid;
}

/**
 * Validate registration form
 */
function validateRegistrationForm() {
    const form = document.getElementById('registerForm');
    if (!form) return true;

    let isValid = true;
    const errors = [];

    // Name validation
    const name = document.getElementById('name') ? .value;
    if (!name || name.trim().length < 2) {
        errors.push('Name must be at least 2 characters');
        isValid = false;
    }

    // Email validation
    const email = document.getElementById('email') ? .value;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email || !emailRegex.test(email)) {
        errors.push('Please enter a valid email address');
        isValid = false;
    }

    // Password validation
    const password = document.getElementById('password') ? .value;
    const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$/;
    if (!password || !passwordRegex.test(password)) {
        errors.push('Password must contain: 8+ characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character');
        isValid = false;
    }

    // Phone validation
    const phone = document.getElementById('phone') ? .value;
    const phoneRegex = /^[0-9]{10}$/;
    if (!phone || !phoneRegex.test(phone)) {
        errors.push('Phone number must be 10 digits');
        isValid = false;
    }

    // Age validation
    const age = document.getElementById('age') ? .value;
    if (age && (age < 0 || age > 150)) {
        errors.push('Please enter a valid age');
        isValid = false;
    }

    // Show errors
    if (!isValid) {
        showToast(errors.join('<br>'), 'error', 5000);
    }

    return isValid;
}

/**
 * Validate payment form
 */
function validatePaymentForm() {
    const form = document.getElementById('paymentForm');
    if (!form) return true;

    let isValid = true;
    const errors = [];

    // Amount validation
    const amount = document.getElementById('amount') ? .value;
    if (!amount || parseFloat(amount) <= 0) {
        errors.push('Amount must be greater than 0');
        isValid = false;
    }

    // Payment method validation
    const paymentMethod = document.getElementById('paymentMethod') ? .value;
    if (!paymentMethod) {
        errors.push('Please select a payment method');
        isValid = false;
    }

    // Show errors
    if (!isValid) {
        showToast(errors.join('<br>'), 'error', 5000);
    }

    return isValid;
}

// ==================== AJAX HELPERS ====================

/**
 * Make AJAX request with loading indicator
 */
async function makeRequest(url, options = {}) {
    showLoading();

    try {
        const response = await fetch(url, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        });

        const data = await response.json();

        hideLoading();

        if (!response.ok) {
            throw new Error(data.message || 'Request failed');
        }

        return data;
    } catch (error) {
        hideLoading();
        showToast(error.message, 'error');
        throw error;
    }
}

// ==================== CONFIRMATION DIALOGS ====================

/**
 * Show confirmation dialog
 */
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

/**
 * Cancel appointment with confirmation
 */
function cancelAppointment(appointmentId) {
    confirmAction('Are you sure you want to cancel this appointment?', async() => {
        try {
            showLoading('Cancelling appointment...');

            const response = await fetch(`/appointments/cancel/${appointmentId}`, {
                method: 'POST'
            });

            hideLoading();

            if (response.ok) {
                showToast('Appointment cancelled successfully', 'success');
                setTimeout(() => location.reload(), 1500);
            } else {
                showToast('Failed to cancel appointment', 'error');
            }
        } catch (error) {
            hideLoading();
            showToast('Error cancelling appointment', 'error');
        }
    });
}

// ==================== DATE/TIME HELPERS ====================

/**
 * Format date for display
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

/**
 * Format time for display
 */
function formatTime(timeString) {
    const [hours, minutes] = timeString.split(':');
    const hour = parseInt(hours);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour > 12 ? hour - 12 : (hour === 0 ? 12 : hour);
    return `${displayHour}:${minutes} ${ampm}`;
}

// ==================== REAL-TIME VALIDATION ====================

/**
 * Add real-time validation to input fields
 */
document.addEventListener('DOMContentLoaded', () => {
    // Password strength indicator
    const passwordInput = document.getElementById('password');
    if (passwordInput) {
        passwordInput.addEventListener('input', (e) => {
            const password = e.target.value;
            let strength = 0;

            if (password.length >= 8) strength++;
            if (/[a-z]/.test(password)) strength++;
            if (/[A-Z]/.test(password)) strength++;
            if (/[0-9]/.test(password)) strength++;
            if (/[@#$%^&+=]/.test(password)) strength++;

            const strengthIndicator = document.getElementById('passwordStrength');
            if (strengthIndicator) {
                const strengthTexts = ['Weak', 'Fair', 'Good', 'Strong', 'Very Strong'];
                const strengthColors = ['#dc3545', '#fd7e14', '#ffc107', '#28a745', '#20c997'];

                strengthIndicator.textContent = strengthTexts[strength - 1] || 'Too Weak';
                strengthIndicator.style.color = strengthColors[strength - 1] || '#dc3545';
            }
        });
    }

    // Email validation
    const emailInput = document.getElementById('email');
    if (emailInput) {
        emailInput.addEventListener('blur', (e) => {
            const email = e.target.value;
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

            if (email && !emailRegex.test(email)) {
                e.target.classList.add('is-invalid');
                showToast('Please enter a valid email address', 'warning', 2000);
            } else {
                e.target.classList.remove('is-invalid');
            }
        });
    }

    // Phone validation
    const phoneInput = document.getElementById('phone');
    if (phoneInput) {
        phoneInput.addEventListener('input', (e) => {
            e.target.value = e.target.value.replace(/[^0-9]/g, '').slice(0, 10);
        });
    }

    // Date picker - disable past dates
    const dateInput = document.getElementById('appointmentDate');
    if (dateInput) {
        const today = new Date().toISOString().split('T')[0];
        dateInput.setAttribute('min', today);
    }
});

// ==================== AUTO-SAVE DRAFT ====================

/**
 * Auto-save form data to localStorage
 */
function autoSaveForm(formId, interval = 5000) {
    const form = document.getElementById(formId);
    if (!form) return;

    setInterval(() => {
        const formData = new FormData(form);
        const data = {};
        formData.forEach((value, key) => {
            data[key] = value;
        });
        localStorage.setItem(`draft_${formId}`, JSON.stringify(data));
    }, interval);
}

/**
 * Restore form data from localStorage
 */
function restoreFormDraft(formId) {
    const form = document.getElementById(formId);
    if (!form) return;

    const draft = localStorage.getItem(`draft_${formId}`);
    if (draft) {
        const data = JSON.parse(draft);
        Object.keys(data).forEach(key => {
            const input = form.querySelector(`[name="${key}"]`);
            if (input) {
                input.value = data[key];
            }
        });
        showToast('Draft restored', 'info', 2000);
    }
}

/**
 * Clear form draft
 */
function clearFormDraft(formId) {
    localStorage.removeItem(`draft_${formId}`);
}

// ==================== EXPORT ====================

// Make functions globally available
window.showLoading = showLoading;
window.hideLoading = hideLoading;
window.showToast = showToast;
window.validateAppointmentForm = validateAppointmentForm;
window.validateRegistrationForm = validateRegistrationForm;
window.validatePaymentForm = validatePaymentForm;
window.makeRequest = makeRequest;
window.confirmAction = confirmAction;
window.cancelAppointment = cancelAppointment;
window.formatDate = formatDate;
window.formatTime = formatTime;
window.autoSaveForm = autoSaveForm;
window.restoreFormDraft = restoreFormDraft;
window.clearFormDraft = clearFormDraft;

console.log('✅ Enhanced frontend features loaded');