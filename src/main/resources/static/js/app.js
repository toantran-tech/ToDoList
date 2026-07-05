const appData       = document.getElementById('app-data');
let currentPage     = parseInt(appData?.dataset.currentPage  ?? '0');
let currentSize     = parseInt(appData?.dataset.currentSize  ?? '9');
let currentSearch   = appData?.dataset.currentSearch   ?? '';
let currentStatus   = appData?.dataset.currentStatus   ?? '';
let currentPriority = appData?.dataset.currentPriority ?? '';
let currentSort     = appData?.dataset.currentSort     ?? 'createdAt';
let currentSortDir  = appData?.dataset.currentSortDir  ?? 'desc';

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('btn-open-add')?.addEventListener('click', openAddModal);
    document.getElementById('btn-empty-add')?.addEventListener('click', openAddModal);
    document.getElementById('todo-form')?.addEventListener('submit', handleFormSubmit);
    document.getElementById('btn-confirm-delete')?.addEventListener('click', confirmDelete);

    document.querySelectorAll('.modal-backdrop').forEach(backdrop => {
        backdrop.addEventListener('click', (e) => {
            if (e.target === backdrop) closeModal(backdrop.id);
        });
    });

    document.querySelectorAll('[data-close-modal]').forEach(btn => {
        btn.addEventListener('click', () => closeModal(btn.dataset.closeModal));
    });

    document.querySelectorAll('.stat-card[data-filter-status]').forEach(card => {
        card.addEventListener('click', () => {
            const status = card.dataset.filterStatus;
            document.getElementById('filter-status').value = status;
            currentStatus = status;
            currentPage   = 0;
            reloadPage();
        });
    });

    document.getElementById('todo-grid')?.addEventListener('click', (e) => {
        const btn  = e.target.closest('[data-action]');
        if (!btn) return;
        const card = btn.closest('.todo-card');
        if (!card) return;
        const id     = card.dataset.id;
        const action = btn.dataset.action;
        if      (action === 'toggle') toggleTodo(id);
        else if (action === 'edit')   openEditModal(card);
        else if (action === 'delete') openDeleteModal(id, card.dataset.title);
    });

    document.querySelector('.pagination-wrapper')?.addEventListener('click', (e) => {
        const btn = e.target.closest('[data-page]');
        if (!btn || btn.disabled) return;
        goToPage(parseInt(btn.dataset.page));
    });

    document.getElementById('filter-status')?.addEventListener('change',   applyFilters);
    document.getElementById('filter-priority')?.addEventListener('change', applyFilters);
    document.getElementById('filter-sort')?.addEventListener('change',     applyFilters);

    let searchTimeout;
    document.getElementById('search-input')?.addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            currentSearch = e.target.value.trim();
            currentPage   = 0;
            applyFilters();
        }, 400);
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            document.querySelectorAll('.modal-backdrop.open').forEach(m => closeModal(m.id));
        }
    });
});

function openModal(id) {
    const modal = document.getElementById(id);
    if (!modal) return;
    modal.classList.add('open');
    document.body.style.overflow = 'hidden';
    setTimeout(() => modal.querySelector('input, textarea, select, button')?.focus(), 300);
}

function closeModal(id) {
    const modal = document.getElementById(id);
    if (!modal) return;
    modal.classList.remove('open');
    document.body.style.overflow = '';
}

function openAddModal() {
    document.getElementById('todo-form').reset();
    document.getElementById('todo-id').value              = '';
    document.getElementById('modal-title').textContent    = 'Thêm công việc';
    document.getElementById('btn-submit-text').textContent = 'Lưu công việc';
    clearFormErrors();
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(23, 59, 0, 0);
    document.getElementById('input-deadline').value = formatDatetimeLocal(tomorrow);
    openModal('todo-modal');
}

function openEditModal(card) {
    document.getElementById('todo-id').value              = card.dataset.id;
    document.getElementById('input-title').value           = card.dataset.title       || '';
    document.getElementById('input-description').value     = card.dataset.description || '';
    document.getElementById('input-status').value          = card.dataset.status      || 'TODO';
    document.getElementById('input-priority').value        = card.dataset.priorityVal || 'MEDIUM';
    document.getElementById('input-deadline').value        = card.dataset.deadline    || '';
    document.getElementById('modal-title').textContent     = 'Chỉnh sửa công việc';
    document.getElementById('btn-submit-text').textContent = 'Cập nhật';
    clearFormErrors();
    openModal('todo-modal');
}

async function handleFormSubmit(e) {
    e.preventDefault();
    if (!validateForm()) return;

    const id          = document.getElementById('todo-id').value;
    const title       = document.getElementById('input-title').value.trim();
    const description = document.getElementById('input-description').value.trim();
    const status      = document.getElementById('input-status').value;
    const priority    = document.getElementById('input-priority').value;
    const deadlineStr = document.getElementById('input-deadline').value;

    const payload = {
        title,
        description: description || null,
        status,
        priority,
        deadline: deadlineStr ? new Date(deadlineStr).toISOString().slice(0, 19) : null
    };

    const btn = document.getElementById('btn-submit');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span> Đang lưu...';

    try {
        if (id) {
            await apiPut(`/api/todos/${id}`, payload);
            showToast('Đã cập nhật công việc', 'success');
        } else {
            await apiPost('/api/todos', payload);
            showToast('Đã thêm công việc mới', 'success');
        }
        closeModal('todo-modal');
        reloadPage();
    } catch (err) {
        showToast(err.message || 'Đã xảy ra lỗi, vui lòng thử lại', 'error');
        btn.disabled = false;
        btn.innerHTML = `<span id="btn-submit-text">${id ? 'Cập nhật' : 'Lưu công việc'}</span>`;
    }
}

async function toggleTodo(id) {
    try {
        const updated = await apiPatch(`/api/todos/${id}/toggle`);
        const labels  = { TODO: 'Chưa làm', IN_PROGRESS: 'Đang làm', DONE: 'Hoàn thành' };
        showToast(`Trạng thái: ${labels[updated.status] || updated.status}`, 'success');
        reloadPage();
    } catch {
        showToast('Không thể cập nhật trạng thái', 'error');
    }
}

function openDeleteModal(id, title) {
    document.getElementById('delete-todo-id').value = id;
    document.getElementById('delete-confirm-desc').textContent =
        `Bạn có chắc muốn xóa "${title}"? Hành động này không thể hoàn tác.`;
    openModal('delete-modal');
}

async function confirmDelete() {
    const id  = document.getElementById('delete-todo-id').value;
    const btn = document.getElementById('btn-confirm-delete');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span> Đang xóa...';

    try {
        await apiDelete(`/api/todos/${id}`);
        showToast('Đã xóa công việc', 'success');
        closeModal('delete-modal');
        reloadPage();
    } catch {
        showToast('Không thể xóa công việc', 'error');
        btn.disabled = false;
        btn.textContent = 'Xóa';
    }
}

function applyFilters() {
    currentSearch   = document.getElementById('search-input')?.value.trim()  ?? '';
    currentStatus   = document.getElementById('filter-status')?.value        ?? '';
    currentPriority = document.getElementById('filter-priority')?.value      ?? '';

    const sortVal  = (document.getElementById('filter-sort')?.value ?? 'createdAt_desc').split('_');
    currentSort    = sortVal[0];
    currentSortDir = sortVal[1] || 'desc';
    currentPage    = 0;

    reloadPage();
}

function goToPage(page) {
    currentPage = page;
    reloadPage();
}

function reloadPage() {
    const params = new URLSearchParams();
    if (currentSearch)   params.set('search',   currentSearch);
    if (currentStatus)   params.set('status',   currentStatus);
    if (currentPriority) params.set('priority', currentPriority);
    params.set('page',    currentPage);
    params.set('size',    currentSize);
    params.set('sortBy',  currentSort);
    params.set('sortDir', currentSortDir);
    window.location.href = '/?' + params.toString();
}

function validateForm() {
    clearFormErrors();
    let valid = true;

    const title = document.getElementById('input-title').value.trim();
    if (!title) {
        showFieldError('input-title', 'error-title', 'Tiêu đề không được để trống');
        valid = false;
    } else if (title.length > 255) {
        showFieldError('input-title', 'error-title', 'Tiêu đề không được vượt quá 255 ký tự');
        valid = false;
    }

    const deadlineStr = document.getElementById('input-deadline').value;
    if (deadlineStr) {
        const deadline = new Date(deadlineStr);
        if (isNaN(deadline.getTime())) {
            showFieldError('input-deadline', 'error-deadline', 'Deadline không hợp lệ');
            valid = false;
        } else if (deadline < new Date()) {
            showFieldError('input-deadline', 'error-deadline', 'Deadline đã qua, công việc sẽ được đánh dấu quá hạn');
        }
    }
    return valid;
}

function showFieldError(inputId, errorId, msg) {
    document.getElementById(inputId)?.classList.add('error');
    const el = document.getElementById(errorId);
    if (el) { el.textContent = msg; el.classList.add('visible'); }
}

function clearFormErrors() {
    document.querySelectorAll('.form-control.error').forEach(el => el.classList.remove('error'));
    document.querySelectorAll('.form-error').forEach(el => {
        el.classList.remove('visible');
        el.textContent = '';
    });
}

function showToast(message, type = 'info', duration = 3000) {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `<span class="toast-dot"></span><span class="toast-text">${message}</span>`;
    document.getElementById('toast-container').appendChild(toast);
    requestAnimationFrame(() => requestAnimationFrame(() => toast.classList.add('show')));
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 350);
    }, duration);
}

async function apiFetch(url, options = {}) {
    const res = await fetch(url, {
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json', ...options.headers },
        ...options
    });
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.message || `Lỗi HTTP ${res.status}`);
    }
    return res.json();
}

const apiGet    = url         => apiFetch(url);
const apiPost   = (url, body) => apiFetch(url, { method: 'POST',   body: JSON.stringify(body) });
const apiPut    = (url, body) => apiFetch(url, { method: 'PUT',    body: JSON.stringify(body) });
const apiPatch  = url         => apiFetch(url, { method: 'PATCH' });
const apiDelete = url         => apiFetch(url, { method: 'DELETE' });

function formatDatetimeLocal(date) {
    const p = n => String(n).padStart(2, '0');
    return `${date.getFullYear()}-${p(date.getMonth()+1)}-${p(date.getDate())}T${p(date.getHours())}:${p(date.getMinutes())}`;
}
