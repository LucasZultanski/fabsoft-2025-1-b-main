const screens = ['homeScreen', 'checkinScreen', 'avaliacaoScreen', 'notificacaoScreen', 'adminLoginScreen', 'adminDashboardScreen'];
const backButton = document.getElementById('backButton');
const messageModal = document.getElementById('messageModal');
const modalTitle = document.getElementById('modalTitle');
const modalMessage = document.getElementById('modalMessage');

const submitCheckinBtn = document.getElementById('submitCheckinBtn');
const submitAvaliacaoBtn = document.getElementById('submitAvaliacaoBtn');
const submitNotificacaoBtn = document.getElementById('submitNotificacaoBtn');
const loginAdminBtn = document.getElementById('loginAdminBtn');

// Novos elementos para Check-in
const checkinStateInput = document.getElementById('checkinStateInput');
const checkinCityInput = document.getElementById('checkinCityInput');
const stateOptionsDatalist = document.getElementById('stateOptions');
const cityOptionsDatalist = document.getElementById('cityOptions');

// Variável para controlar a tela de retorno do modal em caso de erro
let lastOperationWasErrorScreenId = null;

// --- Modal de seleção para Estado, Cidade e Gênero ---
const selectModal = document.getElementById('selectModal');
const selectModalTitle = document.getElementById('selectModalTitle');
const modalSearch = document.getElementById('modalSearch');
const modalOptions = document.getElementById('modalOptions');
const checkinGenderInput = document.getElementById('checkinGender');
const checkinStateInput2 = document.getElementById('checkinStateInput');
const checkinCityInput2 = document.getElementById('checkinCityInput');

let currentSelectField = null;
let currentOptions = [];

const generoOptions = [
  'Feminino',
  'Masculino',
  'Outro',
  'Prefiro não informar'
];

let estadosCache = [];
let cidadesCache = [];

const otherGenderContainer = document.getElementById('otherGenderContainer');
const otherGenderInput = document.getElementById('otherGenderInput');

// Variáveis globais para filtros do dashboard
let dashboardFilterStartDate = null;
let dashboardFilterEndDate = null;
let dashboardFilterStartDateISO = null;
let dashboardFilterEndDateISO = null;

// --- DateTime Picker Modal Logic ---
let currentDateTimeTargetInput = null;

function openDateTimePickerModal(targetInputId) {
    currentDateTimeTargetInput = document.getElementById(targetInputId);
    if (!currentDateTimeTargetInput) return;
    const modal = document.getElementById('dateTimePickerModal');
    const pickerInput = document.getElementById('dateTimePickerInput');
    const title = document.getElementById('dateTimePickerTitle');
    // Define valor atual se houver
    pickerInput.value = currentDateTimeTargetInput.value || '';
    title.textContent = targetInputId === 'filterStartDate' ? 'Selecionar Data/Hora Inicial' : 'Selecionar Data/Hora Final';
    // Força exibição do modal
    modal.style.display = 'flex';
    modal.style.zIndex = 9999;
    modal.style.opacity = 1;
    modal.style.visibility = 'visible';
    console.log('[DEBUG] Modal de data/hora exibido (display=flex, zIndex=9999, opacity=1, visibility=visible)');
    setTimeout(() => { pickerInput.focus(); }, 100);
}

function closeDateTimePickerModal() {
    const modal = document.getElementById('dateTimePickerModal');
    modal.style.display = 'none';
    currentDateTimeTargetInput = null;
}

window.addEventListener('DOMContentLoaded', function() {
    document.getElementById('filterStartDate').addEventListener('click', function(e) {
        console.log('[DEBUG] Clicou no campo filterStartDate');
        try {
            e.preventDefault();
            openDateTimePickerModal('filterStartDate');
            console.log('[DEBUG] Chamou openDateTimePickerModal para filterStartDate');
        } catch (err) {
            alert('Erro ao abrir o seletor de data/hora: ' + err);
            console.error('[ERRO] Falha ao abrir modal de data/hora:', err);
        }
    });
    document.getElementById('filterEndDate').addEventListener('click', function(e) {
        console.log('[DEBUG] Clicou no campo filterEndDate');
        try {
            e.preventDefault();
            openDateTimePickerModal('filterEndDate');
            console.log('[DEBUG] Chamou openDateTimePickerModal para filterEndDate');
        } catch (err) {
            alert('Erro ao abrir o seletor de data/hora: ' + err);
            console.error('[ERRO] Falha ao abrir modal de data/hora:', err);
        }
    });
    document.getElementById('dateTimePickerConfirmBtn').addEventListener('click', function() {
        const pickerInput = document.getElementById('dateTimePickerInput');
        const isoValue = pickerInput.value;
        const displayValue = formatDateTimeForDisplay(isoValue);
        const now = new Date();
        let errorMsg = '';
        if (currentDateTimeTargetInput) {
            if (currentDateTimeTargetInput.id === 'filterStartDate') {
                // Validação: não pode ser no futuro
                if (isoValue && new Date(isoValue) > now) {
                    errorMsg = 'A data/hora inicial não pode ser no futuro.';
                }
                // Se já existe data final, não pode ser maior que ela
                if (isoValue && dashboardFilterEndDateISO && new Date(isoValue) > new Date(dashboardFilterEndDateISO)) {
                    errorMsg = 'A data/hora inicial não pode ser maior que a data/hora final.';
                }
            } else if (currentDateTimeTargetInput.id === 'filterEndDate') {
                // Se já existe data inicial, não pode ser menor que ela
                if (isoValue && dashboardFilterStartDateISO && new Date(isoValue) < new Date(dashboardFilterStartDateISO)) {
                    errorMsg = 'A data/hora final não pode ser anterior à data/hora inicial.';
                }
            }
            if (!isoValue) {
                errorMsg = 'Selecione uma data/hora válida.';
            }
            if (errorMsg) {
                showModal('Erro de Data/Hora', errorMsg);
                return; // Não fecha o modal
            }
            currentDateTimeTargetInput.value = displayValue;
            if (currentDateTimeTargetInput.id === 'filterStartDate') {
                dashboardFilterStartDateISO = isoValue;
            } else if (currentDateTimeTargetInput.id === 'filterEndDate') {
                dashboardFilterEndDateISO = isoValue;
            }
            // Dispara evento de input para atualizar filtros se necessário
            currentDateTimeTargetInput.dispatchEvent(new Event('input'));
        }
        closeDateTimePickerModal();
    });
    document.getElementById('dateTimePickerCancelBtn').addEventListener('click', function() {
        closeDateTimePickerModal();
    });
    const btnHoje = document.getElementById('btnHoje');
    const btnMes = document.getElementById('btnMes');
    const btnAno = document.getElementById('btnAno');
    if (btnHoje) btnHoje.addEventListener('click', setFilterToToday);
    if (btnMes) btnMes.addEventListener('click', setFilterToMonth);
    if (btnAno) btnAno.addEventListener('click', setFilterToYear);
    document.getElementById('filterStartDate').addEventListener('input', updateDateDisplays);
    document.getElementById('filterEndDate').addEventListener('input', updateDateDisplays);
    updateDateDisplays();
});
// Fecha modal ao clicar fora do conteúdo
window.addEventListener('mousedown', function(e) {
    const modal = document.getElementById('dateTimePickerModal');
    if (modal && modal.style.display === 'flex' && !modal.querySelector('.modal-content').contains(e.target)) {
        closeDateTimePickerModal();
    }
});

function formatDateTimeForDisplay(isoString) {
    if (!isoString) return '';
    const d = new Date(isoString);
    if (isNaN(d.getTime())) return '';
    return d.toLocaleString('pt-BR', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
}

function padDateTimeToSeconds(dtStr) {
    // dtStr pode ser '2025-06-29T03:00' ou '2025-06-29T03:00:00'
    if (dtStr && dtStr.length === 16) return dtStr + ':00';
    return dtStr;
}

function getDashboardFilterParams(endpoint) {
    const params = [];
    if (dashboardFilterStartDateISO) params.push(`startDate=${encodeURIComponent(padDateTimeToSeconds(dashboardFilterStartDateISO))}`);
    if (dashboardFilterEndDateISO) params.push(`endDate=${encodeURIComponent(padDateTimeToSeconds(dashboardFilterEndDateISO))}`);
    if (params.length === 0) return '';
    return (endpoint.includes('?') ? '&' : '?') + params.join('&');
}

function showScreen(screenId) {
    console.log("Ativando tela:", screenId);
    screens.forEach(id => {
        const screen = document.getElementById(id);
        if (screen) {
            if (id === screenId) {
                screen.classList.add('active');
            } else {
                screen.classList.remove('active');
            }
        }
    });

    // Limpa campos do login administrativo ao sair da tela
    if (screenId !== 'adminLoginScreen') {
        const usernameInput = document.getElementById('adminUsername');
        const passwordInput = document.getElementById('adminPassword');
        if (usernameInput) usernameInput.value = '';
        if (passwordInput) passwordInput.value = '';
    }

    if (screenId === 'homeScreen') {
        backButton.classList.add('hidden');
    } else {
        backButton.classList.remove('hidden');
    }

    if (screenId === 'checkinScreen') {
        updateCheckinDateTime();
        initBrazilianStates(); // Inicializa os estados ao entrar na tela de check-in
        checkinCityInput.disabled = true; // Desabilita a cidade por padrão
        checkinCityInput.innerHTML = '<option value="">Selecione a Cidade...</option>'; // Limpa cidades
    }

    if (screenId === 'adminDashboardScreen') {
        fetchAdminData('checkins', 'month');
        fetchAdminData('evaluations', 'month');
        fetchAllEvaluationsForTable();
        fetchAllUsersForTable();
        renderCheckinsByGenderChart();
        renderCheckinsByStateChart();
        renderCheckinsByAgeGroupChart();
        renderCheckinsByCityChart();
    }
}

function showModal(title, message) {
    modalTitle.textContent = title;
    modalMessage.innerHTML = message;
    const modalErrorIcon = document.getElementById('modalErrorIcon');
    const modalSuccessIcon = document.getElementById('modalSuccessIcon');
    const errorXIcon = document.getElementById('errorXIcon');
    // Exibe o X vermelho animado apenas em erros de check-in
    if (title && title.toLowerCase().includes('erro')) {
        modalErrorIcon.style.display = 'flex';
        if (modalSuccessIcon) modalSuccessIcon.style.display = 'none';
        errorXIcon.classList.remove('animate-shake');
        void errorXIcon.offsetWidth;
        errorXIcon.classList.add('animate-shake');
    } else {
        modalErrorIcon.style.display = 'none';
        errorXIcon.classList.remove('animate-shake');
        if (modalSuccessIcon) modalSuccessIcon.style.display = 'flex';
    }
    messageModal.classList.add('active');
    setTimeout(() => {
        const modalContent = messageModal.querySelector('.modal-content');
        if (modalContent) {
            modalContent.style.opacity = '1';
            modalContent.style.transform = 'translateY(0)';
        }
    }, 10);
}

function closeModal() {
    const modalContent = messageModal.querySelector('.modal-content');
    if (modalContent) {
        modalContent.style.opacity = '0';
        modalContent.style.transform = 'translateY(-20px)';
    }
    messageModal.classList.remove('active');
    const modalSuccessIcon = document.getElementById('modalSuccessIcon');
    if (modalSuccessIcon) modalSuccessIcon.style.display = 'none';

    // Verifica se a última operação resultou em um erro em uma tela específica.
    // Se sim, volta para essa tela. Caso contrário, volta para a tela inicial.
    if (lastOperationWasErrorScreenId) {
        showScreen(lastOperationWasErrorScreenId); // Retorna para a tela que gerou o erro
        lastOperationWasErrorScreenId = null; // Reseta a flag após retornar
    } else {
        showScreen('homeScreen'); // Retorna para a tela inicial para sucesso ou saída geral
    }
}

function setLoading(isLoading, targetButton, originalHtml) {
    if (targetButton) {
        if (isLoading) {
            targetButton.setAttribute('disabled', 'true');
            targetButton.style.opacity = 0.7;
            targetButton.innerHTML = `<i class="fas fa-spinner fa-spin mr-3"></i> Carregando...`;
        } else {
            targetButton.removeAttribute('disabled');
            targetButton.style.opacity = 1;
            targetButton.innerHTML = originalHtml;
        }
    }
}

const checkinDateTimeElement = document.getElementById('checkinDateTime');
function updateCheckinDateTime() {
    if (checkinDateTimeElement) {
        const now = new Date();
        const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit' };
        checkinDateTimeElement.textContent = `Data e Hora: ${now.toLocaleDateString('pt-BR', options)}`;
    }
}

// Helper para limpar erros visuais de campos
function clearFieldErrors(elements) {
    elements.forEach(el => {
        if (el) el.classList.remove('error');
    });
}

// Helper para marcar campo com erro visual
function markFieldError(element) {
    if (element) element.classList.add('error');
}


// Função para inicializar o dropdown de estados a partir da API
async function initBrazilianStates() {
    if (!checkinStateInput || !stateOptionsDatalist) return;
    stateOptionsDatalist.innerHTML = '';
    checkinStateInput.value = '';
    checkinStateInput.disabled = true;
    try {
        const response = await fetch('/api/locations/states');
        if (response.ok) {
            const states = await response.json();
            stateOptionsDatalist.innerHTML = '';
            states.forEach(state => {
                const option = document.createElement('option');
                option.value = state.sigla;
                option.label = state.nome;
                stateOptionsDatalist.appendChild(option);
            });
            checkinStateInput.disabled = false;
        } else {
            stateOptionsDatalist.innerHTML = '';
        }
    } catch (error) {
        stateOptionsDatalist.innerHTML = '';
    }
}

// Função para preencher as cidades com base no estado selecionado a partir da API
async function populateCities() {
    if (!checkinStateInput || !checkinCityInput || !cityOptionsDatalist) return;
    const selectedStateUF = checkinStateInput.value;
    cityOptionsDatalist.innerHTML = '';
    checkinCityInput.value = '';
    checkinCityInput.disabled = true;
    if (selectedStateUF) {
        try {
            const response = await fetch(`/api/locations/states/${selectedStateUF}/cities`);
            if (response.ok) {
                const cities = await response.json();
                cityOptionsDatalist.innerHTML = '';
                cities.forEach(city => {
                    const option = document.createElement('option');
                    option.value = city.nome;
                    cityOptionsDatalist.appendChild(option);
                });
                checkinCityInput.disabled = false;
            } else {
                cityOptionsDatalist.innerHTML = '';
            }
        } catch (error) {
            cityOptionsDatalist.innerHTML = '';
        }
    } else {
        cityOptionsDatalist.innerHTML = '';
    }
}

// Adiciona listener para mudança no dropdown de estados
if (checkinStateInput) {
    checkinStateInput.addEventListener('input', populateCities);
}

if (checkinCityInput) {
    checkinCityInput.addEventListener('input', () => checkinCityInput.classList.remove('error'));
}


async function submitCheckin() {
    const checkinNameInput = document.getElementById('checkinName');
    const checkinAgeInput = document.getElementById('checkinAge');
    const checkinGenderInput = document.getElementById('checkinGender');
    const checkinEmailInput = document.getElementById('checkinEmail');
    const checkinPhoneInput = document.getElementById('checkinPhone');
    const notifCheckinEmail = document.getElementById('notifCheckinEmail');
    const notifCheckinPhone = document.getElementById('notifCheckinPhone');

    // Limpa erros visuais anteriores
    clearFieldErrors([checkinNameInput, checkinAgeInput, checkinGenderInput, checkinStateInput, checkinCityInput, checkinEmailInput, checkinPhoneInput]);

    let isValid = true;
    const email = checkinEmailInput.value.trim();
    const phone = checkinPhoneInput.value.trim();
    const emailRegex = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
    const phoneRegex = /^\(?\d{2}\)?\s?\d{4,5}[- ]?\d{4}$/;

    if (!checkinNameInput.value.trim()) {
        markFieldError(checkinNameInput);
        isValid = false;
    }
    if (!checkinAgeInput.value.trim()) {
        markFieldError(checkinAgeInput);
        isValid = false;
    }
    if (!checkinGenderInput.value.trim()) {
        markFieldError(checkinGenderInput);
        isValid = false;
    }
    if (!checkinStateInput.value.trim()) {
        markFieldError(checkinStateInput);
        isValid = false;
    }
    if (!checkinCityInput.value.trim()) {
        markFieldError(checkinCityInput);
        isValid = false;
    }
    // Pelo menos um dos dois deve ser preenchido
    if (!email && !phone) {
        markFieldError(checkinEmailInput);
        markFieldError(checkinPhoneInput);
        isValid = false;
    }
    if (email && !emailRegex.test(email)) {
        markFieldError(checkinEmailInput);
        isValid = false;
    }
    if (phone && !phoneRegex.test(phone)) {
        markFieldError(checkinPhoneInput);
        isValid = false;
    }

    if (!isValid) {
        lastOperationWasErrorScreenId = 'checkinScreen';
        showModal('Erro de Preenchimento', 'Por favor, preencha todos os campos obrigatórios corretamente para realizar o check-in.');
        return;
    }

    const nome = checkinNameInput.value.trim();
    const idade = checkinAgeInput.value.trim() !== '' ? parseInt(checkinAgeInput.value) : null;
    const genero = checkinGenderInput.value;
    const estado = checkinStateInput.value.trim();
    const cidade = checkinCityInput.value.trim();
    const currentDateTime = new Date().toISOString();

    const notificacoesExposicoes = notifCheckinEmail && notifCheckinEmail.checked;
    const notificacoesEventos = notifCheckinPhone && notifCheckinPhone.checked;

    const originalButtonHtml = submitCheckinBtn ? submitCheckinBtn.innerHTML : '';
    setLoading(true, submitCheckinBtn, originalButtonHtml);

    try {
        const response = await fetch('/api/v1/checkups', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: email,
                telefone: phone,
                dataHora: currentDateTime,
                local: "Museu Principal",
                nome: nome,
                genero: genero,
                idade: idade,
                estado: estado,
                cidade: cidade,
                notificacoesExposicoes: notificacoesExposicoes,
                notificacoesEventos: notificacoesEventos
            }),
        });

        if (response.ok) {
            lastOperationWasErrorScreenId = null;
            showModal('Check-in Realizado!', 'Bem-vindo(a) ao museu! Esperamos que tenha uma ótima visita.');
            checkinNameInput.value = '';
            checkinAgeInput.value = '';
            checkinGenderInput.value = '';
            checkinStateInput.value = '';
            checkinCityInput.value = '';
            checkinCityInput.disabled = true;
            checkinEmailInput.value = '';
            checkinPhoneInput.value = '';
            if (notifCheckinEmail) notifCheckinEmail.checked = false;
            if (notifCheckinPhone) notifCheckinPhone.checked = false;
            launchConfetti();
            showVerifiedBadge();
        } else {
            lastOperationWasErrorScreenId = 'checkinScreen';
            let errorData = { message: 'Ocorreu um erro desconhecido.' };
            try {
                if (response.headers.get('content-type')?.includes('application/json')) {
                    errorData = await response.json();
                } else {
                    errorData.message = await response.text();
                }
            } catch (jsonError) {
                console.warn("Could not parse error response as JSON:", jsonError);
            }
            showModal('Erro no Check-in', (errorData.message || 'Ocorreu um erro ao realizar o check-in. Tente novamente.').replace(/\n/g, '<br>'));
        }
    } catch (error) {
        lastOperationWasErrorScreenId = 'checkinScreen';
        console.error('Erro ao enviar check-in:', error);
        showModal('Erro de Conexão', 'Não foi possível conectar ao servidor. Por favor, verifique sua conexão ou tente novamente mais tarde.');
    } finally {
        setLoading(false, submitCheckinBtn, originalButtonHtml);
    }
}

const ratingInput = document.getElementById('ratingInput');
const currentRatingDisplay = document.getElementById('currentRating');

if (ratingInput && currentRatingDisplay) {
    ratingInput.oninput = function() {
        currentRatingDisplay.textContent = this.value;
    };
}


async function submitAvaliacao() {
    const rating = ratingInput ? parseInt(ratingInput.value) : 0;
    const avaliacaoEmailInput = document.getElementById('avaliacaoEmail');
    
    // Limpa erros visuais anteriores
    clearFieldErrors([avaliacaoEmailInput]);

    let isValid = true;
    if (!avaliacaoEmailInput || !avaliacaoEmailInput.value.trim()) {
        markFieldError(avaliacaoEmailInput);
        isValid = false;
    }

    if (!isValid) {
        // Se a validação local falhar, define a tela de retorno para a tela de avaliação
        lastOperationWasErrorScreenId = 'avaliacaoScreen';
        showModal('Erro de Preenchimento', 'Por favor, insira seu e-mail ou telefone para vincular sua avaliação.');
        return;
    }

    const emailOrPhone = avaliacaoEmailInput.value.trim();
    const currentDateTime = new Date().toISOString();

    const originalButtonHtml = submitAvaliacaoBtn ? submitAvaliacaoBtn.innerHTML : '';
    setLoading(true, submitAvaliacaoBtn, originalButtonHtml);

    try {
        const response = await fetch('/api/v1/avaliacoes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                nota: rating,
                usuarioIdentifier: emailOrPhone,
                dataHora: currentDateTime
            }),
        });

        if (response.ok) {
            // Se a operação for um sucesso, não há tela de erro para retornar
            lastOperationWasErrorScreenId = null;
            showModal('Avaliação Enviada!', 'Obrigado(a) pela sua avaliação! Sua opinião é muito importante para nós.');
            avaliacaoEmailInput.value = '';
            if (ratingInput) ratingInput.value = 5;
            if (currentRatingDisplay) currentRatingDisplay.textContent = 5;
        } else {
            // Se houver um erro da API, define a tela de retorno para a tela de avaliação
            lastOperationWasErrorScreenId = 'avaliacaoScreen';
            let errorData = { message: 'Ocorreu um erro desconhecido.' };
            try {
                if (response.headers.get('content-type')?.includes('application/json')) {
                    errorData = await response.json();
                } else {
                    errorData.message = await response.text();
                }
            } catch (jsonError) {
                console.warn("Could not parse error response as JSON:", jsonError);
            }
            showModal('Erro na Avaliação', errorData.message || 'Ocorreu um erro ao enviar sua avaliação. Tente novamente.');
        }
    } catch (error) {
        // Se houver um erro de conexão, define a tela de retorno para a tela de avaliação
        lastOperationWasErrorScreenId = 'avaliacaoScreen';
        console.error('Erro ao enviar avaliação:', error);
        showModal('Erro de Conexão', 'Não foi possível conectar ao servidor. Por favor, verifique sua conexão ou tente novamente mais tarde.');
    } finally {
        setLoading(false, submitAvaliacaoBtn, originalButtonHtml);
    }
}

async function submitNotificacao() {
    const signupEmailInput = document.getElementById('signupEmail');
    const signupPhoneInput = document.getElementById('signupPhone');
    const notifExposicoes = document.getElementById('notifExposicoes');
    const notifEventos = document.getElementById('notifEventos');
    const notifNovidades = document.getElementById('notifNovidades');

    // Limpa erros visuais anteriores
    clearFieldErrors([signupEmailInput]);

    let isValid = true;
    if (!signupEmailInput || !signupEmailInput.value.trim()) {
        markFieldError(signupEmailInput);
        isValid = false;
    }
    
    // Validação de formato de e-mail (além do `required` do HTML)
    if (signupEmailInput && signupEmailInput.value.trim() && !signupEmailInput.checkValidity()) {
        markFieldError(signupEmailInput);
        lastOperationWasErrorScreenId = 'notificacaoScreen';
        showModal('Erro de Validação', 'Por favor, insira um e-mail válido.');
        return; // Interrompe imediatamente se o e-mail for inválido
    }

    if (!notifExposicoes.checked && !notifEventos.checked && !notifNovidades.checked) {
        isValid = false;
    }

    if (!isValid) {
        // Se a validação local falhar, define a tela de retorno para a tela de notificação
        lastOperationWasErrorScreenId = 'notificacaoScreen';
        showModal('Erro de Preenchimento', 'Por favor, insira um e-mail e selecione pelo menos um tipo de notificação.');
        return;
    }

    const email = signupEmailInput.value.trim();
    const telefone = signupPhoneInput ? signupPhoneInput.value.trim() : '';
    const notificacoesExposicoes = notifExposicoes.checked;
    const notificacoesEventos = notifEventos.checked;
    const notificacoesNovidades = notifNovidades.checked;

    const originalButtonHtml = submitNotificacaoBtn ? submitNotificacaoBtn.innerHTML : '';
    setLoading(true, submitNotificacaoBtn, originalButtonHtml);

    try {
        const response = await fetch('/api/v1/usuarios', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: email,
                telefone: telefone || null,
                notificacoesExposicoes: notificacoesExposicoes,
                notificacoesEventos: notificacoesEventos,
                notificacoesNovidades: notificacoesNovidades
            }),
        });

        if (response.ok) {
            // Se a operação for um sucesso, não há tela de erro para retornar
            lastOperationWasErrorScreenId = null;
            showModal('Cadastro Realizado!', 'Você foi cadastrado com sucesso para receber nossas atualizações!');
            signupEmailInput.value = '';
            if (signupPhoneInput) signupPhoneInput.value = '';
            notifExposicoes.checked = true;
            notifEventos.checked = true;
            notifNovidades.checked = true;
        } else {
            // Se houver um erro da API, define a tela de retorno para a tela de notificação
            lastOperationWasErrorScreenId = 'notificacaoScreen';
            let errorData = { message: 'Ocorreu um erro desconhecido.' };
            try {
                if (response.headers.get('content-type')?.includes('application/json')) {
                    errorData = await response.json();
                } else {
                    errorData.message = await response.text();
                }
            } catch (jsonError) {
                console.warn("Could not parse error response as JSON:", jsonError);
            }
            showModal('Erro no Cadastro', errorData.message || 'Ocorreu um erro ao realizar o cadastro. Tente novamente.');
        }
    } catch (error) {
        // Se houver um erro de conexão, define a tela de retorno para a tela de notificação
        lastOperationWasErrorScreenId = 'notificacaoScreen';
        console.error('Erro ao enviar cadastro de notificação:', error);
        showModal('Erro de Conexão', 'Não foi possível conectar ao servidor. Por favor, verifique sua conexão ou tente novamente mais tarde.');
    } finally {
        setLoading(false, submitNotificacaoBtn, originalButtonHtml);
    }
}

// Funções Administrativas
async function loginAdmin() {
    const usernameInput = document.getElementById('adminUsername');
    const passwordInput = document.getElementById('adminPassword');

    // Limpa erros visuais anteriores
    clearFieldErrors([usernameInput, passwordInput]);

    if (!usernameInput || !passwordInput) {
        console.error("Elementos de login (adminUsername ou adminPassword) não encontrados.");
        return;
    }

    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();

    // Simples autenticação para demonstração
    if (username === 'admin' && password === 'admin') {
        // Sucesso no login: não há tela de erro para retornar
        lastOperationWasErrorScreenId = null;
        showScreen('adminDashboardScreen');
        usernameInput.value = '';
        passwordInput.value = '';
    } else {
        // Erro no login: define a tela de retorno para a tela de login administrativo
        markFieldError(usernameInput);
        markFieldError(passwordInput);
        lastOperationWasErrorScreenId = 'adminLoginScreen';
        showModal('Erro de Login', 'Usuário ou senha inválidos. Por favor, tente novamente.');
    }
}

function getDashboardPeriodText(period) {
    if (dashboardFilterStartDateISO && dashboardFilterEndDateISO) {
        const start = formatDateTimeForDisplay(dashboardFilterStartDateISO).split(',')[0];
        const end = formatDateTimeForDisplay(dashboardFilterEndDateISO).split(',')[0];
        return `(${start} a ${end})`;
    }
    switch (period) {
        case 'month': return '(Último Mês)';
        case 'week': return '(Última Semana)';
        case 'year': return '(Último Ano)';
        default: return '(Todos os períodos)';
    }
}

async function fetchAdminData(type, period) {
    let endpoint = `/api/admin/reports/${type}?period=${period}`;
    endpoint += getDashboardFilterParams(endpoint);
    let displayElementId = '';
    let titleElementId = '';
    if (type === 'checkins') {
        displayElementId = 'totalCheckins';
        titleElementId = 'totalCheckinsTitle';
    } else if (type === 'evaluations') {
        displayElementId = 'averageRating';
        titleElementId = 'averageRatingTitle'; // Caso queira adicionar para avaliações
    }
    if (titleElementId && document.getElementById(titleElementId)) {
        document.getElementById(titleElementId).textContent = (type === 'checkins' ? 'Total de Check-ins ' : 'Avaliação Média ') + getDashboardPeriodText(period);
    }
    const displayElement = document.getElementById(displayElementId);
    if (displayElement) {
        displayElement.textContent = 'Carregando...';
    } else {
        console.warn(`Elemento de exibição para ${displayElementId} não encontrado.`);
    }

    try {
        const response = await fetch(endpoint);
        if (response.ok) {
            const data = await response.json();
            if (displayElement) {
                if (type === 'checkins') {
                    displayElement.textContent = data.count;
                } else if (type === 'evaluations') {
                    displayElement.textContent = data.averageRating ? data.averageRating.toFixed(1) : 'N/A';
                }
            }
        } else {
            const errorText = await response.text();
            console.error(`Erro ao buscar dados de ${type}:`, response.status, errorText);
            if (displayElement) {
                displayElement.textContent = 'Erro!';
            }
            showModal('Erro no Dashboard', `Não foi possível carregar os dados de ${type}.`);
        }
    } catch (error) {
        console.error(`Erro de conexão ao buscar dados de ${type}:`, error);
        if (displayElement) {
            displayElement.textContent = 'Erro!';
        }
        showModal('Erro de Conexão', 'Não foi possível conectar ao servidor para obter dados administrativos.');
    }
}

async function exportDataToPowerBI() {
    showModal('Exportando Dados', 'A exportação para Power BI é uma simulação. Em breve, você verá uma mensagem de sucesso.');
    try {
        const response = await fetch('/api/admin/export-powerbi?period=all');
        if (response.ok) {
            const result = await response.json();
            showModal('Exportação Concluída!', result.message || 'Dados exportados com sucesso para Power BI (simulado)!');
        } else {
            const errorText = await response.text();
            showModal('Erro na Exportação', `Falha ao exportar dados: ${errorText}`);
        }
    } catch (error) {
        showModal('Erro de Conexão', 'Não foi possível conectar ao servidor para exportar dados.');
    }
}

async function fetchAllUsersForTable() {
    const usersTableBody = document.getElementById('usersTableBody');
    if (!usersTableBody) return;
    usersTableBody.innerHTML = '<tr><td colspan="7" class="text-center py-4">Carregando...</td></tr>';
    let endpoint = '/api/admin/users';
    endpoint += getDashboardFilterParams(endpoint);
    try {
        const response = await fetch(endpoint);
        if (response.ok) {
            const users = await response.json();
            if (users.length === 0) {
                usersTableBody.innerHTML = '<tr><td colspan="7" class="text-center py-4">Nenhum usuário para exibir.</td></tr>';
            } else {
                usersTableBody.innerHTML = users.map(user => `
                    <tr>
                        <td class="py-2 px-4">${user.nome || ''}</td>
                        <td class="py-2 px-4">${user.email || ''}</td>
                        <td class="py-2 px-4">${user.telefone || ''}</td>
                        <td class="py-2 px-4">${user.idade != null ? user.idade : ''}</td>
                        <td class="py-2 px-4">${user.genero || ''}</td>
                        <td class="py-2 px-4">${user.estado || ''}</td>
                        <td class="py-2 px-4">${user.cidade || ''}</td>
                    </tr>
                `).join('');
            }
        } else {
            usersTableBody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-red-600">Erro ao carregar usuários.</td></tr>';
        }
    } catch (error) {
        usersTableBody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-red-600">Erro de conexão ao buscar usuários.</td></tr>';
    }
}

async function fetchAllEvaluationsForTable() {
    const evaluationsTableBody = document.getElementById('evaluationsTableBody');
    if (!evaluationsTableBody) return;
    evaluationsTableBody.innerHTML = '<tr><td colspan="3" class="text-center py-4">Carregando...</td></tr>';
    let endpoint = '/api/v1/avaliacoes';
    endpoint += getDashboardFilterParams(endpoint);
    try {
        const response = await fetch(endpoint);
        if (response.ok) {
            const evaluations = await response.json();
            if (evaluations.length === 0) {
                evaluationsTableBody.innerHTML = '<tr><td colspan="3" class="text-center py-4">Nenhuma avaliação para exibir.</td></tr>';
            } else {
                evaluationsTableBody.innerHTML = evaluations.map(avaliacao => `
                    <tr>
                        <td class="py-2 px-4">${avaliacao.nota != null ? avaliacao.nota : ''}</td>
                        <td class="py-2 px-4">${avaliacao.usuario && avaliacao.usuario.email ? avaliacao.usuario.email : (avaliacao.usuario && avaliacao.usuario.telefone ? avaliacao.usuario.telefone : '')}</td>
                        <td class="py-2 px-4">${avaliacao.dataHora ? avaliacao.dataHora.replace('T', ' ').substring(0, 16) : ''}</td>
                    </tr>
                `).join('');
            }
        } else {
            evaluationsTableBody.innerHTML = '<tr><td colspan="3" class="text-center py-4 text-red-600">Erro ao carregar avaliações.</td></tr>';
        }
    } catch (error) {
        evaluationsTableBody.innerHTML = '<tr><td colspan="3" class="text-center py-4 text-red-600">Erro de conexão ao buscar avaliações.</td></tr>';
    }
}

async function renderCheckinsByGenderChart(period = 'month') {
    const ctx = document.getElementById('checkinsByGenderChart').getContext('2d');
    if (window.checkinsByGenderChartInstance) window.checkinsByGenderChartInstance.destroy();
    let endpoint = '/api/admin/reports/checkins-by-gender';
    endpoint += getDashboardFilterParams(endpoint);
    if (document.getElementById('checkinsByGenderTitle')) {
        document.getElementById('checkinsByGenderTitle').textContent = 'Check-ins por Gênero ' + getDashboardPeriodText(period);
    }
    try {
        const response = await fetch(endpoint);
        if (response.ok) {
            const data = await response.json();
            const labels = Object.keys(data);
            const values = Object.values(data);
            window.checkinsByGenderChartInstance = new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: labels,
                    datasets: [{
                        data: values,
                        backgroundColor: [
                            '#36A2EB', '#FF6384', '#FFCE56', '#AAAAAA', '#8B5CF6', '#10B981'
                        ]
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'bottom',
                        },
                        title: {
                            display: false
                        }
                    }
                }
            });
        } else {
            ctx.font = '16px Arial';
            ctx.fillText('Erro ao carregar gráfico.', 10, 50);
        }
    } catch (error) {
        ctx.font = '16px Arial';
        ctx.fillText('Erro de conexão ao buscar gráfico.', 10, 50);
    }
}

async function renderCheckinsByStateChart(period = 'month') {
    const ctx = document.getElementById('checkinsByStateChart').getContext('2d');
    if (window.checkinsByStateChartInstance) window.checkinsByStateChartInstance.destroy();
    let endpoint = '/api/admin/reports/checkins-by-state';
    endpoint += getDashboardFilterParams(endpoint);
    if (document.getElementById('checkinsByStateTitle')) {
        document.getElementById('checkinsByStateTitle').textContent = 'Check-ins por Estado ' + getDashboardPeriodText(period);
    }
    try {
        const response = await fetch(endpoint);
        if (response.ok) {
            const data = await response.json();
            const labels = Object.keys(data);
            const values = Object.values(data);
            window.checkinsByStateChartInstance = new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: labels,
                    datasets: [{
                        data: values,
                        backgroundColor: ['#36A2EB', '#FF6384', '#FFCE56', '#AAAAAA', '#8B5CF6', '#10B981', '#F59E42', '#F472B6']
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { position: 'bottom' }
                    }
                }
            });
        } else {
            ctx.font = '16px Arial';
            ctx.fillText('Erro ao carregar gráfico.', 10, 50);
        }
    } catch (error) {
        ctx.font = '16px Arial';
        ctx.fillText('Erro de conexão ao buscar gráfico.', 10, 50);
    }
}

async function renderCheckinsByAgeGroupChart(period = 'month') {
    const ctx = document.getElementById('checkinsByAgeGroupChart').getContext('2d');
    if (window.checkinsByAgeGroupChartInstance) window.checkinsByAgeGroupChartInstance.destroy();
    let endpoint = '/api/admin/reports/checkins-by-age-group';
    endpoint += getDashboardFilterParams(endpoint);
    if (document.getElementById('checkinsByAgeGroupTitle')) {
        document.getElementById('checkinsByAgeGroupTitle').textContent = 'Check-ins por Faixa Etária ' + getDashboardPeriodText(period);
    }
    try {
        const response = await fetch(endpoint);
        if (response.ok) {
            const data = await response.json();
            const labels = Object.keys(data);
            const values = Object.values(data);
            window.checkinsByAgeGroupChartInstance = new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: labels,
                    datasets: [{
                        data: values,
                        backgroundColor: ['#FBBF24', '#10B981', '#6366F1', '#AAAAAA']
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { position: 'bottom' }
                    }
                }
            });
        } else {
            ctx.font = '16px Arial';
            ctx.fillText('Erro ao carregar gráfico.', 10, 50);
        }
    } catch (error) {
        ctx.font = '16px Arial';
        ctx.fillText('Erro de conexão ao buscar gráfico.', 10, 50);
    }
}

async function renderCheckinsByCityChart(period = 'month') {
    const ctx = document.getElementById('checkinsByCityChart').getContext('2d');
    if (window.checkinsByCityChartInstance) window.checkinsByCityChartInstance.destroy();
    let endpoint = '/api/admin/reports/checkins-by-city';
    endpoint += getDashboardFilterParams(endpoint);
    if (document.getElementById('checkinsByCityTitle')) {
        document.getElementById('checkinsByCityTitle').textContent = 'Check-ins por Cidade ' + getDashboardPeriodText(period);
    }
    try {
        const response = await fetch(endpoint);
        if (response.ok) {
            const data = await response.json();
            const labels = Object.keys(data);
            const values = Object.values(data);
            window.checkinsByCityChartInstance = new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: labels,
                    datasets: [{
                        data: values,
                        backgroundColor: [
                            '#F472B6', '#36A2EB', '#FF6384', '#FFCE56', '#AAAAAA', '#8B5CF6', '#10B981', '#F59E42'
                        ]
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { position: 'bottom' }
                    }
                }
            });
        } else {
            ctx.font = '16px Arial';
            ctx.fillText('Erro ao carregar gráfico.', 10, 50);
        }
    } catch (error) {
        ctx.font = '16px Arial';
        ctx.fillText('Erro de conexão ao buscar gráfico.', 10, 50);
    }
}

// Remover erro visual ao preencher campos do check-in
function addCheckinFieldListeners() {
    const checkinNameInput = document.getElementById('checkinName');
    const checkinAgeInput = document.getElementById('checkinAge');
    const checkinGenderInput = document.getElementById('checkinGender');
    const checkinStateInput = document.getElementById('checkinStateInput');
    const checkinCityInput = document.getElementById('checkinCityInput');
    const checkinEmailInput = document.getElementById('checkinEmail');
    const checkinPhoneInput = document.getElementById('checkinPhone');
    if (checkinNameInput) checkinNameInput.addEventListener('input', () => checkinNameInput.classList.remove('error'));
    if (checkinAgeInput) {
        checkinAgeInput.addEventListener('input', () => checkinAgeInput.classList.remove('error'));
        checkinAgeInput.addEventListener('keydown', function(e) {
            if (e.key === 'e' || e.key === 'E' || e.key === '+' || e.key === '-') {
                e.preventDefault();
            }
        });
    }
    if (checkinGenderInput) checkinGenderInput.addEventListener('change', () => checkinGenderInput.classList.remove('error'));
    if (checkinStateInput) checkinStateInput.addEventListener('change', () => checkinStateInput.classList.remove('error'));
    if (checkinCityInput) checkinCityInput.addEventListener('change', () => checkinCityInput.classList.remove('error'));
    if (checkinEmailInput) checkinEmailInput.addEventListener('input', () => checkinEmailInput.classList.remove('error'));
    if (checkinPhoneInput) checkinPhoneInput.addEventListener('input', () => checkinPhoneInput.classList.remove('error'));
}

document.addEventListener('DOMContentLoaded', () => {
    showScreen('homeScreen');
    addCheckinFieldListeners();
    // Toggle visualizar senha admin (interruptor)
    const adminPassword = document.getElementById('adminPassword');
    const toggleAdminPasswordSwitch = document.getElementById('toggleAdminPasswordSwitch');
    if (toggleAdminPasswordSwitch && adminPassword) {
        toggleAdminPasswordSwitch.addEventListener('change', () => {
            adminPassword.type = toggleAdminPasswordSwitch.checked ? 'text' : 'password';
        });
    }
    // A função initBrazilianStates() será chamada dentro de showScreen('checkinScreen')
});

async function openSelectModal(field) {
  currentSelectField = field;
  selectModal.classList.add('active');
  modalSearch.value = '';
  modalOptions.innerHTML = '<li>Carregando...</li>';
  if (field === 'genero') {
    selectModalTitle.textContent = 'Selecione o Gênero';
    currentOptions = generoOptions;
    renderModalOptions('');
    modalSearch.style.display = 'none'; // Esconde o campo de busca para gênero
  } else if (field === 'estado') {
    selectModalTitle.textContent = 'Selecione o Estado';
    if (estadosCache.length === 0) {
      try {
        const response = await fetch('/api/locations/states');
        if (response.ok) {
          const states = await response.json();
          estadosCache = states.map(s => ({ value: s.sigla, label: s.nome }));
        }
      } catch {}
    }
    currentOptions = estadosCache.map(s => s.label + ' (' + s.value + ')');
    renderModalOptions('');
    modalSearch.style.display = '';
  } else if (field === 'cidade') {
    selectModalTitle.textContent = 'Selecione a Cidade';
    const estadoSelecionado = checkinStateInput2.value;
    if (!estadoSelecionado) {
      modalOptions.innerHTML = '<li>Selecione o estado primeiro</li>';
      return;
    }
    if (!cidadesCache[estadoSelecionado]) {
      try {
        const response = await fetch(`/api/locations/states/${estadoSelecionado}/cities`);
        if (response.ok) {
          const cities = await response.json();
          cidadesCache[estadoSelecionado] = cities.map(c => c.nome);
        }
      } catch {}
    }
    currentOptions = cidadesCache[estadoSelecionado] || [];
    renderModalOptions('');
    modalSearch.style.display = '';
  }
  modalSearch.oninput = (e) => renderModalOptions(e.target.value);
}

function renderModalOptions(search) {
  const filtered = currentOptions.filter(opt => opt.toLowerCase().includes(search.toLowerCase()));
  if (filtered.length === 0) {
    modalOptions.innerHTML = '<li>Nenhuma opção encontrada</li>';
    return;
  }
  modalOptions.innerHTML = filtered.map(opt => {
    const safeOpt = opt.replace(/'/g, "&#39;").replace(/\\/g, "\\\\");
    return `<li class="cursor-pointer py-2 px-3 hover:bg-blue-100 rounded" data-value="${safeOpt}">${opt}</li>`;
  }).join('');
  Array.from(modalOptions.querySelectorAll('li[data-value]')).forEach(li => {
    li.onclick = () => window.selectOptionModal(li.textContent);
  });
}

window.selectOptionModal = function(opt) {
  if (currentSelectField === 'genero') {
    checkinGenderInput.value = opt;
    if (opt === 'Outro') {
      otherGenderContainer.style.display = '';
      otherGenderInput.value = '';
      otherGenderInput.focus();
    } else {
      otherGenderContainer.style.display = 'none';
      otherGenderInput.value = '';
    }
  } else if (currentSelectField === 'estado') {
    // Extrai sigla do estado
    const sigla = opt.match(/\(([^)]+)\)$/);
    checkinStateInput2.value = sigla ? sigla[1] : opt;
    checkinCityInput2.value = '';
    checkinCityInput2.disabled = false;
  } else if (currentSelectField === 'cidade') {
    checkinCityInput2.value = opt;
  }
  closeSelectModal();
}

function closeSelectModal() {
  selectModal.classList.remove('active');
}

function launchConfetti() {
  const confettiContainer = document.getElementById('confetti-container');
  if (!confettiContainer) return;
  confettiContainer.innerHTML = '';
  confettiContainer.style.display = 'block';
  const colors = ['#2563eb', '#fbbf24', '#10b981', '#ef4444', '#a21caf', '#f472b6'];
  const numConfetti = 80;
  for (let i = 0; i < numConfetti; i++) {
    const conf = document.createElement('div');
    const size = Math.random() * 10 + 8;
    conf.style.position = 'absolute';
    conf.style.width = `${size}px`;
    conf.style.height = `${size * 0.4}px`;
    conf.style.background = colors[Math.floor(Math.random() * colors.length)];
    conf.style.left = `${Math.random() * 100}%`;
    conf.style.top = '-20px';
    conf.style.opacity = 0.85;
    conf.style.borderRadius = `${Math.random() * 8}px`;
    conf.style.transform = `rotate(${Math.random() * 360}deg)`;
    conf.style.transition = 'top 1.6s cubic-bezier(.4,2,.6,1), opacity 0.7s';
    confettiContainer.appendChild(conf);
    setTimeout(() => {
      conf.style.top = `${80 + Math.random() * 15}%`;
      conf.style.opacity = 0;
    }, 10 + Math.random() * 200);
  }
  setTimeout(() => {
    confettiContainer.style.display = 'none';
    confettiContainer.innerHTML = '';
  }, 2000);
}

function showVerifiedBadge() {
  const badge = document.getElementById('verified-badge');
  if (!badge) return;
  badge.style.display = 'block';
  badge.classList.add('animate-fade-in');
  setTimeout(() => {
    badge.classList.remove('animate-fade-in');
    badge.style.display = 'none';
  }, 1800);
}

function applyDashboardFilters() {
    if (dashboardFilterStartDateISO && dashboardFilterEndDateISO) {
        if (new Date(dashboardFilterStartDateISO) > new Date(dashboardFilterEndDateISO)) {
            showModal('Erro de Data/Hora', 'A data/hora inicial não pode ser maior que a data/hora final.');
            return;
        }
    }
    fetchAdminData('checkins', 'custom');
    fetchAdminData('evaluations', 'custom');
    fetchAllEvaluationsForTable();
    fetchAllUsersForTable();
    renderCheckinsByGenderChart('custom');
    renderCheckinsByStateChart('custom');
    renderCheckinsByAgeGroupChart('custom');
    renderCheckinsByCityChart('custom');
}

function setFilterToToday() {
    const now = new Date();
    const start = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0);
    const end = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59);
    document.getElementById('filterStartDate').value = start.toISOString().slice(0, 16);
    document.getElementById('filterEndDate').value = end.toISOString().slice(0, 16);
    dashboardFilterStartDateISO = start.toISOString().slice(0, 19);
    dashboardFilterEndDateISO = end.toISOString().slice(0, 19);
    updateDateDisplays();
    applyDashboardFilters();
}

function setFilterToMonth() {
    const now = new Date();
    const start = new Date(now.getFullYear(), now.getMonth(), 1, 0, 0, 0);
    const end = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59);
    document.getElementById('filterStartDate').value = start.toISOString().slice(0, 16);
    document.getElementById('filterEndDate').value = end.toISOString().slice(0, 16);
    dashboardFilterStartDateISO = start.toISOString().slice(0, 19);
    dashboardFilterEndDateISO = end.toISOString().slice(0, 19);
    updateDateDisplays();
    applyDashboardFilters();
}

function setFilterToYear() {
    const now = new Date();
    const start = new Date(now.getFullYear(), 0, 1, 0, 0, 0);
    const end = new Date(now.getFullYear(), 11, 31, 23, 59, 59);
    document.getElementById('filterStartDate').value = start.toISOString().slice(0, 16);
    document.getElementById('filterEndDate').value = end.toISOString().slice(0, 16);
    dashboardFilterStartDateISO = start.toISOString().slice(0, 19);
    dashboardFilterEndDateISO = end.toISOString().slice(0, 19);
    updateDateDisplays();
    applyDashboardFilters();
}

function updateDateDisplays() {
    const startInput = document.getElementById('filterStartDate');
    const endInput = document.getElementById('filterEndDate');
    const startDisplay = document.getElementById('filterStartDateDisplay');
    const endDisplay = document.getElementById('filterEndDateDisplay');
    if (startDisplay) startDisplay.textContent = startInput.value ? formatDateTimeForDisplay(startInput.value) : '';
    if (endDisplay) endDisplay.textContent = endInput.value ? formatDateTimeForDisplay(endInput.value) : '';
}

function exportUsersTableToExcel() {
    // Cabeçalhos personalizados
    const headers = [
        "Nome", "E-mail", "Telefone", "Idade", "Gênero", "Estado", "Cidade"
    ];
    // Pega as linhas da tabela
    const rows = Array.from(document.querySelectorAll("#usersTableBody tr"));
    const data = rows.map(tr => Array.from(tr.children).map(td => td.textContent.trim()));
    // Remove linhas de erro ou aviso (ex: "Nenhum usuário para exibir.")
    const filteredData = data.filter(row => row.length === headers.length);
    // Monta a planilha
    const ws_data = [headers, ...filteredData];
    const ws = XLSX.utils.aoa_to_sheet(ws_data);
    // Ajusta largura das colunas
    ws['!cols'] = [
        { wch: 20 }, // Nome
        { wch: 28 }, // E-mail
        { wch: 16 }, // Telefone
        { wch: 8 },  // Idade
        { wch: 12 }, // Gênero
        { wch: 10 }, // Estado
        { wch: 20 }  // Cidade
    ];
    // Adiciona filtro automático
    ws['!autofilter'] = { ref: XLSX.utils.encode_range({s: {c:0, r:0}, e: {c:headers.length-1, r:filteredData.length}}) };
    // Cria o arquivo
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Usuários");
    XLSX.writeFile(wb, 'usuarios.xlsx');
}
