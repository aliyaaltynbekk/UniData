document.addEventListener('DOMContentLoaded', function() {
    initializeAlerts();

    initializeTables();

    displayCurrentDate();
});

function initializeAlerts() {
    const alerts = document.querySelectorAll('.alert');

    alerts.forEach(alert => {
        setTimeout(() => {
            fadeOut(alert);
        }, 5000);

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
    });
}

function initializeTables() {
    const tables = document.querySelectorAll('.table');

    tables.forEach(table => {
        const headers = table.querySelectorAll('th[data-sort]');

        headers.forEach(header => {
            header.style.cursor = 'pointer';
            header.addEventListener('click', function() {
                const sortBy = this.getAttribute('data-sort');
                sortTable(table, sortBy, this);
            });
        });
    });
}

function displayCurrentDate() {
    const dateElements = document.querySelectorAll('.current-date');

    if (dateElements.length > 0) {
        const now = new Date();
        const options = { day: 'numeric', month: 'long', year: 'numeric' };
        const formattedDate = now.toLocaleDateString('kk-KZ', options);

        dateElements.forEach(element => {
            element.textContent = formattedDate;
        });
    }
}

function sortTable(table, sortBy, header) {
    const direction = header.classList.contains('sort-asc') ? 'desc' : 'asc';
    const headers = table.querySelectorAll('th');

    headers.forEach(h => {
        h.classList.remove('sort-asc', 'sort-desc');
    });

    header.classList.add(`sort-${direction}`);

    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));

    rows.sort((a, b) => {
        const aValue = a.querySelector(`td[data-${sortBy}]`)?.getAttribute(`data-${sortBy}`) ||
            a.querySelector(`td:nth-child(${Array.from(headers).indexOf(header) + 1})`)?.textContent;
        const bValue = b.querySelector(`td[data-${sortBy}]`)?.getAttribute(`data-${sortBy}`) ||
            b.querySelector(`td:nth-child(${Array.from(headers).indexOf(header) + 1})`)?.textContent;

        if (direction === 'asc') {
            return aValue.localeCompare(bValue, 'kk');
        } else {
            return bValue.localeCompare(aValue, 'kk');
        }
    });

    rows.forEach(row => {
        tbody.appendChild(row);
    });
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

function showAlert(message, type = 'info') {
    const alertContainer = document.getElementById('alert-container');

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

    // 5 секундтан кейін хабарламаны жабу
    setTimeout(() => {
        fadeOut(alert);
    }, 5000);
}