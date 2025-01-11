document.addEventListener("DOMContentLoaded", () => {
    const sidebar = document.getElementById('sidebar');

    // Function to toggle the sidebar
    function toggleSidebar() {
        sidebar.classList.toggle('open');
    }

    // Attach the toggle function to the button
    const toggleButton = document.querySelector('.toggle-btn');
    toggleButton.addEventListener('click', toggleSidebar);

    const userDropdown = document.querySelector(".user-dropdown");

    // Toggle dropdown menu
    if (userDropdown) {
        userDropdown.addEventListener("click", () => {
            userDropdown.classList.toggle("open");
        });

        // Close dropdown when clicking outside
        document.addEventListener("click", (event) => {
            if (!userDropdown.contains(event.target)) {
                userDropdown.classList.remove("open");
            }
        });
    }
});
