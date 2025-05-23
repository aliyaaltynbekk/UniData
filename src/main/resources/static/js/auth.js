document.addEventListener('DOMContentLoaded', function() {
    initializeLoginForm();

    initializeRegisterForm();

    initializeForgotPasswordForm();
});

function initializeLoginForm() {
    const loginForm = document.getElementById('login-form');
    if (!loginForm) return;

    loginForm.addEventListener('submit', function(event) {
        let hasError = false;

        const username = document.getElementById('username');
        if (!username.value.trim()) {
            markInvalid(username, 'Пайдаланушы аты міндетті өріс');
            hasError = true;
        } else {
            markValid(username);
        }

        const password = document.getElementById('password');
        if (!password.value.trim()) {
            markInvalid(password, 'Құпия сөз міндетті өріс');
            hasError = true;
        } else {
            markValid(password);
        }

        if (hasError) {
            event.preventDefault();
        }
    });
}

function initializeRegisterForm() {
    const registerForm = document.getElementById('register-form');
    if (!registerForm) return;

    const passwordInput = document.getElementById('password');
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            const password = this.value;
            const strengthIndicator = document.getElementById('password-strength');

            if (!strengthIndicator) return;

            let strength = 0;

            if (password.length >= 8) strength += 1;

            if (password.match(/[a-z]/) && password.match(/[A-Z]/)) strength += 1;
            if (password.match(/\d+/)) strength += 1;
            if (password.match(/[^a-zA-Z0-9]/)) strength += 1;

            switch (strength) {
                case 0:
                case 1:
                    strengthIndicator.textContent = 'Әлсіз';
                    strengthIndicator.className = 'password-strength-weak';
                    break;
                case 2:
                    strengthIndicator.textContent = 'Орташа';
                    strengthIndicator.className = 'password-strength-medium';
                    break;
                case 3:
                    strengthIndicator.textContent = 'Күшті';
                    strengthIndicator.className = 'password-strength-strong';
                    break;
                case 4:
                    strengthIndicator.textContent = 'Өте күшті';
                    strengthIndicator.className = 'password-strength-very-strong';
                    break;
            }
        });
    }

    registerForm.addEventListener('submit', function(event) {
        let hasError = false;

        const username = document.getElementById('username');
        if (!username.value.trim()) {
            markInvalid(username, 'Пайдаланушы аты міндетті өріс');
            hasError = true;
        } else {
            markValid(username);
        }

        const fullName = document.getElementById('fullName');
        if (!fullName.value.trim()) {
            markInvalid(fullName, 'Толық аты-жөні міндетті өріс');
            hasError = true;
        } else {
            markValid(fullName);
        }

        const email = document.getElementById('email');
        if (!email.value.trim()) {
            markInvalid(email, 'Email міндетті өріс');
            hasError = true;
        } else if (!isValidEmail(email.value)) {
            markInvalid(email, 'Email форматы дұрыс емес');
            hasError = true;
        } else {
            markValid(email);
        }

        const password = document.getElementById('password');
        if (!password.value.trim()) {
            markInvalid(password, 'Құпия сөз міндетті өріс');
            hasError = true;
        } else if (password.value.length < 8) {
            markInvalid(password, 'Құпия сөз кемінде 8 таңбадан тұруы керек');
            hasError = true;
        } else {
            markValid(password);
        }

        const confirmPassword = document.getElementById('confirmPassword');
        if (!confirmPassword.value.trim()) {
            markInvalid(confirmPassword, 'Құпия сөзді қайталау міндетті');
            hasError = true;
        } else if (confirmPassword.value !== password.value) {
            markInvalid(confirmPassword, 'Құпия сөздер сәйкес келмейді');
            hasError = true;
        } else {
            markValid(confirmPassword);
        }

        if (hasError) {
            event.preventDefault();
            showAlert('Форманы толтыру кезінде қателер табылды', 'danger');
        }
    });
}

function initializeForgotPasswordForm() {
    const forgotPasswordForm = document.getElementById('forgot-password-form');
    if (!forgotPasswordForm) return;

    forgotPasswordForm.addEventListener('submit', function(event) {
        let hasError = false;

        const email = document.getElementById('email');
        if (!email.value.trim()) {
            markInvalid(email, 'Email міндетті өріс');
            hasError = true;
        } else if (!isValidEmail(email.value)) {
            markInvalid(email, 'Email форматы дұрыс емес');
            hasError = true;
        } else {
            markValid(email);
        }

        if (hasError) {
            event.preventDefault();
        }
    });
}

function isValidEmail(email) {
    const re = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return re.test(email.toLowerCase());
}

function markInvalid(element, message) {
    element.classList.add('is-invalid');

    let feedbackElement = element.nextElementSibling;
    if (!feedbackElement || !feedbackElement.classList.contains('invalid-feedback')) {
        feedbackElement = document.createElement('div');
        feedbackElement.className = 'invalid-feedback';
        element.parentNode.insertBefore(feedbackElement, element.nextSibling);
    }

    feedbackElement.textContent = message;
    feedbackElement.style.display = 'block';
    feedbackElement.style.color = '#e74c3c';
    feedbackElement.style.fontSize = '14px';
    feedbackElement.style.marginTop = '5px';
}

function markValid(element) {
    element.classList.remove('is-invalid');

    const feedbackElement = element.nextElementSibling;
    if (feedbackElement && feedbackElement.classList.contains('invalid-feedback')) {
        feedbackElement.style.display = 'none';
    }
}

function showAlert(message, type) {
    const alertContainer = document.getElementById('auth-alert-container');
    if (!alertContainer) return;

    alertContainer.innerHTML = '';

    const alertElement = document.createElement('div');
    alertElement.className = `alert alert-${type}`;
    alertElement.textContent = message;

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
        alertElement.remove();
    });

    alertElement.insertBefore(closeButton, alertElement.firstChild);
    alertContainer.appendChild(alertElement);
}