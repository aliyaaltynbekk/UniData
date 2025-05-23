document.addEventListener('DOMContentLoaded', function() {
    initializeAdminDashboard();

    initializeStatCounters();

    initializeUsersTable();

    initializeSearch();
});

function initializeAdminDashboard() {
    const currentPath = window.location.pathname;
    const sidebarLinks = document.querySelectorAll('.admin-sidebar-link');

    sidebarLinks.forEach(link => {
        const href = link.getAttribute('href');

        if (currentPath.includes(href) && href !== '/admin') {
            link.classList.add('active');
        } else if (currentPath === '/admin' && href === '/admin') {
            link.classList.add('active');
        }
    });
}

function initializeStatCounters() {
    const statValues = document.querySelectorAll('.stat-value');

    statValues.forEach(element => {
        const targetValue = parseInt(element.getAttribute('data-value'));

        if (!isNaN(targetValue)) {
            animateCounter(element, targetValue);
        }
    });
}

function animateCounter(element, targetValue) {
    let startValue = 0;
    const duration = 2000; // миллисекунд
    const startTime = performance.now();

    function updateCounter(currentTime) {
        const elapsedTime = currentTime - startTime;

        if (elapsedTime < duration) {
            const progress = elapsedTime / duration;
            const currentValue = Math.floor(progress * targetValue);
            element.textContent = currentValue;
            requestAnimationFrame(updateCounter);
        } else {
            element.textContent = targetValue;
        }
    }

    requestAnimationFrame(updateCounter);
}

function initializeUsersTable() {
    const userActionButtons = document.querySelectorAll('.user-action-btn');

    userActionButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault();

            const userId = this.getAttribute('data-user-id');
            const action = this.getAttribute('data-action');

            if (action === 'delete') {
                if (confirm('Пайдаланушыны жою керек пе?')) {
                    deleteUser(userId);
                }
            } else if (action === 'activate') {
                updateUserStatus(userId, true);
            } else if (action === 'deactivate') {
                updateUserStatus(userId, false);
            } else if (action === 'edit') {
                window.location.href = `/admin/users/${userId}/edit`;
            }
        });
    });
}

function deleteUser(userId) {
    fetch(`/admin/users/${userId}/delete`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content')
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Сервер қатесі');
        })
        .then(data => {
            if (data.success) {
                const userRow = document.querySelector(`tr[data-user-id="${userId}"]`);
                if (userRow) {
                    userRow.remove();
                }
                showAlert('Пайдаланушы сәтті жойылды', 'success');
            } else {
                showAlert(data.message || 'Пайдаланушыны жою кезінде қате пайда болды', 'danger');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('Қате пайда болды: ' + error.message, 'danger');
        });
}

function updateUserStatus(userId, isActive) {
    fetch(`/admin/users/${userId}/status`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content')
        },
        body: JSON.stringify({ active: isActive })
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Сервер қатесі');
        })
        .then(data => {
            if (data.success) {
                // Пайдаланушы мәртебесін кестеде жаңарту
                const statusCell = document.querySelector(`tr[data-user-id="${userId}"] .status-badge`);
                if (statusCell) {
                    statusCell.textContent = isActive ? 'Белсенді' : 'Белсенді емес';
                    statusCell.className = `status-badge ${isActive ? 'status-active' : 'status-inactive'}`;
                }

                const activateBtn = document.querySelector(`tr[data-user-id="${userId}"] .user-action-btn[data-action="activate"]`);
                const deactivateBtn = document.querySelector(`tr[data-user-id="${userId}"] .user-action-btn[data-action="deactivate"]`);

                if (activateBtn && deactivateBtn) {
                    activateBtn.style.display = isActive ? 'none' : 'inline-block';
                    deactivateBtn.style.display = isActive ? 'inline-block' : 'none';
                }

                showAlert(`Пайдаланушы мәртебесі ${isActive ? 'белсенді' : 'белсенді емес'} етіп өзгертілді`, 'success');
            } else {
                showAlert(data.message || 'Мәртебені өзгерту кезінде қате пайда болды', 'danger');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('Қате пайда болды: ' + error.message, 'danger');
        });
}

function initializeSearch() {
    const searchInput = document.getElementById('admin-search-input');
    if (!searchInput) return;

    searchInput.addEventListener('keyup', function() {
        const searchTerm = this.value.toLowerCase();
        const table = document.querySelector('.table');

        if (!table) return;

        const rows = table.querySelectorAll('tbody tr');

        rows.forEach(row => {
            const textContent = row.textContent.toLowerCase();
            row.style.display = textContent.includes(searchTerm) ? '' : 'none';
        });
    });
}

function showAlert(message, type = 'info') {
    const alertContainer = document.getElementById('admin-alert-container');

    if (!alertContainer) return;

    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.innerHTML = message;

    const closeButton = document.createElement('button');
    closeButton.className = 'close';
    closeButton.innerHTML = '&times;';
    closeButton.style.float = 'right';
    closeButton.style.background = 'none';
    closeButton.style.border = 'none';
    closeButton.style.fontSize = '20px';
    closeButton.style.cursor = 'pointer';
    closeButton.style.padding = '0 5px';

    closeButton.addEventListener('click', function() {
        fadeOut(alert);
    });

    alert.insertBefore(closeButton, alert.firstChild);
    alertContainer.appendChild(alert);

    setTimeout(() => {
        fadeOut(alert);
    }, 5000);
}

function fadeOut(element) {
    element.style.opacity = '1';

    (function fade() {
        if ((element.style.opacity -= 0.1) < 0) {
            element.style.display = 'none';
        } else {
            requestAnimationFrame(fade);
        }
    })();
}

function addRoleToUser(userId, roleType) {
    fetch(`/admin/users/${userId}/roles/${roleType}/add`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showAlert('success', data.message);
                setTimeout(() => window.location.reload(), 1000);
            } else {
                showAlert('danger', data.message);
            }
        })
        .catch(error => {
            showAlert('danger', 'Қате орын алды');
            console.error('Error:', error);
        });
}

function removeRoleFromUser(userId, roleType) {
    fetch(`/admin/users/${userId}/roles/${roleType}/remove`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showAlert('success', data.message);
                setTimeout(() => window.location.reload(), 1000);
            } else {
                showAlert('danger', data.message);
            }
        })
        .catch(error => {
            showAlert('danger', 'Қате орын алды');
            console.error('Error:', error);
        });
}

function showAlert(type, message) {
    const alertContainer = document.getElementById('admin-alert-container');

    while (alertContainer.firstChild) {
        alertContainer.removeChild(alertContainer.firstChild);
    }

    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;

    alertContainer.appendChild(alert);

    setTimeout(() => {
        alert.style.opacity = '0';
        alert.style.transition = 'opacity 0.5s';
        setTimeout(() => alertContainer.removeChild(alert), 500);
    }, 5000);
}
