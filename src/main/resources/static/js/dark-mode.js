/**
 * Dark Mode Toggle Feature
 * Saves preference to localStorage
 */

(function() {
    'use strict';

    // Get stored theme or default to light
    const currentTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', currentTheme);

    // Create theme toggle button
    function createThemeToggle() {
        const toggleButton = document.createElement('button');
        toggleButton.className = 'theme-toggle';
        toggleButton.setAttribute('aria-label', 'Toggle dark mode');
        toggleButton.innerHTML = currentTheme === 'dark' ?
            '<i class="bi bi-sun-fill"></i>' :
            '<i class="bi bi-moon-fill"></i>';

        document.body.appendChild(toggleButton);

        toggleButton.addEventListener('click', toggleTheme);
    }

    // Toggle between light and dark themes
    function toggleTheme() {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';

        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);

        // Update button icon
        const button = document.querySelector('.theme-toggle');
        if (button) {
            button.innerHTML = newTheme === 'dark' ?
                '<i class="bi bi-sun-fill"></i>' :
                '<i class="bi bi-moon-fill"></i>';
        }

        // Show toast notification
        if (window.showToast) {
            showToast(`${newTheme === 'dark' ? 'Dark' : 'Light'} mode activated`, 'info', 2000);
        }
    }

    // Initialize on DOM load
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', createThemeToggle);
    } else {
        createThemeToggle();
    }

    // Make toggle function globally available
    window.toggleTheme = toggleTheme;
})();