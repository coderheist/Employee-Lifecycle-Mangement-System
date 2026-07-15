// ========== CONFIG ==========
const API_BASE_URL = 'http://localhost:8080/api/recruitment';

// editing-state per page (null = "add" mode, set = "edit" mode)
let editingCandidateId = null;
let editingRequisitionId = null;
let editingInterviewId = null;
let editingOfferId = null;


// Field maps: how each form's inputs map onto the API's JSON keys.
// {el: <input id>, key: <API field name>, type: 'int' | 'float' (optional, default text)}
const CANDIDATE_FIELDS = [
  { el: 'fullName', key: 'fullName' },
  { el: 'appliedRole', key: 'appliedRole' },
  { el: 'experienceYears', key: 'experienceYears', type: 'int' },
  { el: 'interviewStage', key: 'interviewStage' },
  { el: 'candidateStatus', key: 'candidateStatus' },
];

const REQUISITION_FIELDS = [
  { el: 'jobTitle', key: 'jobTitle' },
  { el: 'department', key: 'department' },
  { el: 'numberOfPositions', key: 'numberOfPositions', type: 'int' },
  { el: 'priority', key: 'priority' },
  { el: 'status', key: 'status' },
  { el: 'requisitionDate', key: 'requisitionDate' },
  { el: 'description', key: 'description' },
];

const INTERVIEW_FIELDS = [
  { el: 'interviewCandidateId', key: 'candidateId', type: 'int' },
  { el: 'interviewerName', key: 'interviewerName' },
  { el: 'interviewDateTime', key: 'interviewDateTime' },
  { el: 'interviewRound', key: 'interviewRound' },
  { el: 'interviewMode', key: 'interviewMode' },
  { el: 'interviewStatus', key: 'interviewStatus' },
  { el: 'location', key: 'location' },
  { el: 'meetingLink', key: 'meetingLink' },
  { el: 'interviewRemarks', key: 'remarks' },
];

const OFFER_FIELDS = [
  { el: 'offerCandidateId', key: 'candidateId', type: 'int' },
  { el: 'positionOffered', key: 'positionOffered' },
  { el: 'offerDepartment', key: 'department' },
  { el: 'salaryOffered', key: 'salaryOffered', type: 'float' },
  { el: 'offerStatus', key: 'offerStatus' },
  { el: 'offerDate', key: 'offerDate' },
  { el: 'joiningDate', key: 'joiningDate' },
  { el: 'additionalBenefits', key: 'additionalBenefits' },
  { el: 'offerRemarks', key: 'remarks' },
];

// ========== UTILITIES ==========
function showToast(message) {
  const toast = document.getElementById('toastBox');
  if (!toast) return;
  toast.textContent = message;
  toast.style.display = 'block';
  setTimeout(() => { toast.style.display = 'none'; }, 3000);
}

function formatStatus(status) {
  return status.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
}

function formatDate(dateStr) {
  if (!dateStr) return 'N/A';
  return new Date(dateStr).toLocaleDateString('en-IN', { year: 'numeric', month: 'short', day: 'numeric' });
}

function formatDateTime(dateTimeStr) {
  if (!dateTimeStr) return 'N/A';
  return new Date(dateTimeStr).toLocaleString('en-IN', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
}

function formatCurrency(amount) {
  if (!amount) return '0.00';
  return amount.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function getAuthenticatedUser() {
  return {
    username: sessionStorage.getItem('currentUser') || 'HR Manager',
    role: sessionStorage.getItem('userRole') || 'Recruiter'
  };
}

function renderUserChip() {
  const { username, role } = getAuthenticatedUser();
  const userNameEl = document.getElementById('userName');
  const userRoleEl = document.getElementById('userRole');
  const userAvatarEl = document.getElementById('userAvatar');

  if (userNameEl) userNameEl.textContent = username;
  if (userRoleEl) userRoleEl.textContent = role;
  if (userAvatarEl) {
    const initials = username
      .split(' ')
      .filter(Boolean)
      .map(part => part[0])
      .join('')
      .slice(0, 2)
      .toUpperCase();
    userAvatarEl.textContent = initials || 'HR';
  }
}

// Read a form's inputs into a plain data object, using a field map.
function buildFormData(fields) {
  const data = {};
  fields.forEach(f => {
    const raw = document.getElementById(f.el).value;
    if (f.type === 'int') data[f.key] = parseInt(raw);
    else if (f.type === 'float') data[f.key] = parseFloat(raw);
    else data[f.key] = raw;
  });
  return data;
}

// Write a record's values into a form, using the same field map.
function populateForm(fields, record) {
  fields.forEach(f => {
    document.getElementById(f.el).value = record[f.key] ?? '';
  });
}

// Reveal a hidden form card on page layout
function showForm(cardId, formId, titleId, titleText, hiddenIdField) {
  document.getElementById(titleId).textContent = titleText;
  document.getElementById(formId).reset();
  if (hiddenIdField) document.getElementById(hiddenIdField).value = '';
  const card = document.getElementById(cardId);
  card.style.display = 'block';
  card.scrollIntoView({ behavior: 'smooth' });
}

function cancelForm(cardId, formId) {
  document.getElementById(cardId).style.display = 'none';
  document.getElementById(formId).reset();
}

// ========== API LAYER ==========
const api = {
  async get(path) {
    const res = await fetch(`${API_BASE_URL}/${path}`, {
      method: 'GET',
      credentials: 'include' // Added credentials here
    });
    return res.json();
  },
  async post(path, data) {
    return fetch(`${API_BASE_URL}/${path}`, {
      method: 'POST', 
      headers: { 'Content-Type': 'application/json' }, 
      body: JSON.stringify(data),
      credentials: 'include' // Added credentials here
    });
  },
  async put(path, data) {
    return fetch(`${API_BASE_URL}/${path}`, {
      method: 'PUT', 
      headers: { 'Content-Type': 'application/json' }, 
      body: JSON.stringify(data),
      credentials: 'include' // Added credentials here
    });
  },
  async delete(path) {
    return fetch(`${API_BASE_URL}/${path}`, { 
      method: 'DELETE',
      credentials: 'include' // Added credentials here
    });
  }
};

async function checkBackendConnection() {
  try {
    const res = await fetch(`${API_BASE_URL}/health`, {
      method: 'GET',
      credentials: 'include' // Added credentials here
    });
    await res.json();
    return true;
  } catch (error) {
    console.error('Backend connection failed:', error);
    showToast('Cannot connect to backend. Please ensure the Spring Boot application is running on port 8080.');
    return false;
  }
}

// ========== GENERIC CRUD HELPERS ==========
// Fetch a record by id, populate its form, and reveal the form card.
async function editRecord(path, id, fields, hiddenIdField, formCard, formTitle, titleText) {
  try {
    const record = await api.get(`${path}/${id}`);
    document.getElementById(formTitle).textContent = titleText;
    document.getElementById(hiddenIdField).value = record[hiddenIdField];
    populateForm(fields, record);
    const card = document.getElementById(formCard);
    card.style.display = 'block';
    card.scrollIntoView({ behavior: 'smooth' });
    return record;
  } catch (error) {
    console.error(`Error loading ${path}:`, error);
    showToast('Error loading details');
  }
}

// Confirm, delete, toast, reload.
async function deleteRecord(path, id, confirmMsg, successMsg, errorMsg, reloadFn) {
  if (!confirm(confirmMsg)) return;
  try {
    const res = await api.delete(`${path}/${id}`);
    if (res.ok) { showToast(successMsg); reloadFn(); }
    else showToast(errorMsg);
  } catch (error) {
    console.error(`Error deleting ${path}:`, error);
    showToast(errorMsg);
  }
}

// POST if adding, PUT if editing; toast, close form, reload.
async function saveRecord(path, id, data, labels, cancelFn, reloadFns) {
  try {
    const res = id ? await api.put(`${path}/${id}`, data) : await api.post(path, data);
    if (res.ok) {
      showToast(id ? labels.updated : labels.created);
      cancelFn();
      reloadFns.forEach(fn => fn());
    } else {
      const err = await res.json();
      showToast(err.error || labels.error);
    }
  } catch (error) {
    console.error(`Error saving ${path}:`, error);
    showToast(labels.error);
  }
}

// ========== DASHBOARD ==========
async function loadDashboard() {
  try {
    const data = await api.get('dashboard');
    document.getElementById('candidateCount').textContent = data.candidateCount || 0;
    document.getElementById('requisitionCount').textContent = data.requisitionCount || 0;
    document.getElementById('interviewCount').textContent = data.interviewCount || 0;
    document.getElementById('offerCount').textContent = data.offerCount || 0;
  } catch (error) {
    console.error('Error loading dashboard:', error);
    showToast('Error loading dashboard data');
  }
}

// ========== CANDIDATES ==========
async function loadCandidates() {
  try {
    const candidates = await api.get('candidates');
    const tbody = document.getElementById('candidateTableBody');
    if (!tbody) return;
    if (candidates.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" class="text-center">No candidates found</td></tr>';
      return;
    }
    tbody.innerHTML = candidates.map(c => `
      <tr>
        <td>${c.candidateId}</td>
        <td>${c.fullName}</td>
        <td>${c.appliedRole}</td>
        <td>${c.experienceYears} years</td>
        <td>${c.interviewStage}</td>
        <td><span class="pill pill-${c.candidateStatus.toLowerCase()}">${formatStatus(c.candidateStatus)}</span></td>
        <td>
          <button class="btn-action" onclick="editCandidate(${c.candidateId})" title="Edit"><i class="fas fa-edit"></i></button>
          <button class="btn-action delete" onclick="deleteCandidate(${c.candidateId})" title="Delete"><i class="fas fa-trash"></i></button>
        </td>
      </tr>
    `).join('');
  } catch (error) {
    console.error('Error loading candidates:', error);
    showToast('Error loading candidates');
  }
}

function showAddCandidateForm() {
  editingCandidateId = null;
  showForm('candidateFormCard', 'candidateForm', 'candidateFormTitle', 'Add New Candidate', 'candidateId');
}

function cancelCandidateForm() {
  cancelForm('candidateFormCard', 'candidateForm');
  editingCandidateId = null;
}

async function editCandidate(id) {
  editingCandidateId = id;
  await editRecord('candidates', id, CANDIDATE_FIELDS, 'candidateId', 'candidateFormCard', 'candidateFormTitle', 'Edit Candidate');
}

function deleteCandidate(id) {
  deleteRecord('candidates', id, 'Are you sure you want to delete this candidate?',
    'Candidate deleted successfully', 'Error deleting candidate', loadCandidates);
}

function saveCandidateForm(e) {
  e.preventDefault();
  const data = buildFormData(CANDIDATE_FIELDS);
  saveRecord('candidates', editingCandidateId, data,
    { created: 'Candidate added successfully', updated: 'Candidate updated successfully', error: 'Error saving candidate' },
    cancelCandidateForm, [loadCandidates]);
}

// Populate the "Applied Role" dropdown from open/in-progress requisitions.
async function loadRolesForCandidateDropdown() {
  try {
    const requisitions = await api.get('job-requisitions');
    const roleSelect = document.getElementById('appliedRole');
    if (!roleSelect || roleSelect.tagName !== 'SELECT') return;
    const openRoles = requisitions.filter(r => r.status === 'OPEN' || r.status === 'IN_PROGRESS');
    const options = openRoles.map(r => `<option value="${r.jobTitle}">${r.jobTitle} - ${r.department}</option>`).join('');
    roleSelect.innerHTML = '<option value="">Select Role</option>' + options;
  } catch (error) { console.error('Error loading roles for dropdown:', error); }
}

// ========== JOB REQUISITIONS ==========
async function loadRequisitions() {
  try {
    const requisitions = await api.get('job-requisitions');
    const tbody = document.getElementById('requisitionTableBody');
    if (!tbody) return;
    if (requisitions.length === 0) {
      tbody.innerHTML = '<tr><td colspan="8" class="text-center">No requisitions found</td></tr>';
      return;
    }
    tbody.innerHTML = requisitions.map(r => `
      <tr>
        <td>${r.requisitionId}</td>
        <td>${r.jobTitle}</td>
        <td>${r.department}</td>
        <td>${r.numberOfPositions}</td>
        <td><span class="pill pill-${r.priority.toLowerCase()}">${r.priority}</span></td>
        <td><span class="pill pill-${r.status.toLowerCase()}">${formatStatus(r.status)}</span></td>
        <td>${formatDate(r.requisitionDate)}</td>
        <td>
          <button class="btn-action" onclick="editRequisition(${r.requisitionId})" title="Edit"><i class="fas fa-edit"></i></button>
          <button class="btn-action delete" onclick="deleteRequisition(${r.requisitionId})" title="Delete"><i class="fas fa-trash"></i></button>
        </td>
      </tr>
    `).join('');
  } catch (error) {
    console.error('Error loading requisitions:', error);
    showToast('Error loading requisitions');
  }
}

function showAddRequisitionForm() {
  editingRequisitionId = null;
  showForm('requisitionFormCard', 'requisitionForm', 'requisitionFormTitle', 'Create Job Requisition', 'requisitionId');
}

function cancelRequisitionForm() {
  cancelForm('requisitionFormCard', 'requisitionForm');
  editingRequisitionId = null;
}

async function editRequisition(id) {
  editingRequisitionId = id;
  await editRecord('job-requisitions', id, REQUISITION_FIELDS, 'requisitionId', 'requisitionFormCard', 'requisitionFormTitle', 'Edit Job Requisition');
}

function deleteRequisition(id) {
  deleteRecord('job-requisitions', id, 'Are you sure you want to delete this job requisition?',
    'Job requisition deleted successfully', 'Error deleting job requisition', loadRequisitions);
}

function saveRequisitionForm(e) {
  e.preventDefault();
  const data = buildFormData(REQUISITION_FIELDS);
  saveRecord('job-requisitions', editingRequisitionId, data,
    { created: 'Requisition created successfully', updated: 'Requisition updated successfully', error: 'Error saving requisition' },
    cancelRequisitionForm, [loadRequisitions]);
}

// ========== INTERVIEWS ==========
async function loadCandidatesForDropdown() {
  try {
    const candidates = await api.get('candidates');
    const select = document.getElementById('interviewCandidateId');
    if (!select) return;
    const options = candidates.map(c => `<option value="${c.candidateId}">${c.fullName} - ${c.appliedRole}</option>`).join('');
    select.innerHTML = '<option value="">Select Candidate</option>' + options;
  } catch (error) { console.error('Error loading candidates for dropdown:', error); }
}

async function loadInterviews() {
  try {
    const [interviews, candidates] = await Promise.all([api.get('interviews'), api.get('candidates')]);
    const candidateMap = {};
    candidates.forEach(c => candidateMap[c.candidateId] = c.fullName);

    const tbody = document.getElementById('interviewTableBody');
    if (!tbody) return;
    if (interviews.length === 0) {
      tbody.innerHTML = '<tr><td colspan="8" class="text-center">No interviews scheduled</td></tr>';
      return;
    }
    tbody.innerHTML = interviews.map(i => `
      <tr>
        <td>${i.interviewId}</td>
        <td>${candidateMap[i.candidateId] || 'N/A'}</td>
        <td>${i.interviewerName}</td>
        <td>${formatDateTime(i.interviewDateTime)}</td>
        <td>${i.interviewRound}</td>
        <td>${i.interviewMode}</td>
        <td><span class="pill pill-${i.interviewStatus.toLowerCase()}">${formatStatus(i.interviewStatus)}</span></td>
        <td>
          <button class="btn-action" onclick="editInterview(${i.interviewId})" title="Edit"><i class="fas fa-edit"></i></button>
          <button class="btn-action delete" onclick="deleteInterview(${i.interviewId})" title="Delete"><i class="fas fa-trash"></i></button>
        </td>
      </tr>
    `).join('');
  } catch (error) {
    console.error('Error loading interviews:', error);
    showToast('Error loading interviews');
  }
}

function showScheduleInterviewForm() {
  editingInterviewId = null;
  showForm('interviewFormCard', 'interviewForm', 'interviewFormTitle', 'Schedule Interview', 'interviewId');
}

function cancelInterviewForm() {
  cancelForm('interviewFormCard', 'interviewForm');
  editingInterviewId = null;
}

async function editInterview(id) {
  editingInterviewId = id;
  await editRecord('interviews', id, INTERVIEW_FIELDS, 'interviewId', 'interviewFormCard', 'interviewFormTitle', 'Edit Interview');
}

function deleteInterview(id) {
  deleteRecord('interviews', id, 'Are you sure you want to delete this interview?',
    'Interview deleted successfully', 'Error deleting interview', loadInterviews);
}

function saveInterviewForm(e) {
  e.preventDefault();
  const data = buildFormData(INTERVIEW_FIELDS);
  saveRecord('interviews', editingInterviewId, data,
    { created: 'Interview scheduled successfully', updated: 'Interview updated successfully', error: 'Error scheduling interview' },
    cancelInterviewForm, [loadInterviews]);
}

// ========== OFFERS ==========
// Only candidates with a completed interview, not already offered/hired, are eligible.
async function loadCandidatesForOfferDropdown() {
  try {
    const [candidates, interviews] = await Promise.all([api.get('candidates'), api.get('interviews')]);
    const completedIds = new Set(interviews.filter(i => i.interviewStatus === 'COMPLETED').map(i => i.candidateId));
    const eligible = candidates.filter(c =>
      completedIds.has(c.candidateId) && c.candidateStatus !== 'OFFERED' && c.candidateStatus !== 'HIRED'
    );
    const select = document.getElementById('offerCandidateId');
    if (!select) return;
    const options = eligible.map(c => `<option value="${c.candidateId}">${c.fullName} - ${c.appliedRole}</option>`).join('');
    select.innerHTML = '<option value="">Select Candidate</option>' + options;
  } catch (error) { console.error('Error loading candidates for offer dropdown:', error); }
}

async function loadOffers() {
  try {
    const [offers, candidates] = await Promise.all([api.get('offers'), api.get('candidates')]);
    const candidateMap = {};
    candidates.forEach(c => candidateMap[c.candidateId] = c.fullName);

    const tbody = document.getElementById('offerTableBody');
    if (!tbody) return;
    if (offers.length === 0) {
      tbody.innerHTML = '<tr><td colspan="9" class="text-center">No offers found</td></tr>';
      return;
    }
    tbody.innerHTML = offers.map(o => `
      <tr>
        <td>${o.offerId}</td>
        <td>${candidateMap[o.candidateId] || 'N/A'}</td>
        <td>${o.positionOffered}</td>
        <td>${o.department}</td>
        <td>₹${formatCurrency(o.salaryOffered)}</td>
        <td>${formatDate(o.offerDate)}</td>
        <td>${formatDate(o.joiningDate)}</td>
        <td><span class="pill pill-${o.offerStatus.toLowerCase()}">${formatStatus(o.offerStatus)}</span></td>
        <td>
          <button class="btn-action" onclick="editOffer(${o.offerId})" title="Edit"><i class="fas fa-edit"></i></button>
          <button class="btn-action delete" onclick="deleteOffer(${o.offerId})" title="Delete"><i class="fas fa-trash"></i></button>
        </td>
      </tr>
    `).join('');
  } catch (error) {
    console.error('Error loading offers:', error);
    showToast('Error loading offers');
  }
}

function showRolloutOfferForm() {
  editingOfferId = null;
  showForm('offerFormCard', 'offerForm', 'offerFormTitle', 'Rollout Offer Letter', 'offerId');
}

function cancelOfferForm() {
  cancelForm('offerFormCard', 'offerForm');
  editingOfferId = null;
}

async function editOffer(id) {
  editingOfferId = id;
  // Ensure the candidate dropdown is populated before attempting to set the value
  await loadCandidatesForOfferDropdown();

  try {
    const record = await api.get(`offers/${id}`);

    // Prepare the form card
    document.getElementById('offerFormTitle').textContent = 'Edit Offer';
    document.getElementById('offerForm').reset();
    document.getElementById('offerId').value = record.offerId ?? '';

    // Populate remaining fields
    populateForm(OFFER_FIELDS, record);

    // Ensure candidate select has an option for this candidate and select it
    const sel = document.getElementById('offerCandidateId');
    if (sel && record.candidateId != null) {
      const match = Array.from(sel.options).find(o => o.value == record.candidateId);
      if (match) {
        sel.value = match.value;
      } else {
        // Option missing — fetch the candidate and append an option
        try {
          const candidate = await api.get(`candidates/${record.candidateId}`);
          const option = document.createElement('option');
          option.value = candidate.candidateId;
          option.text = `${candidate.fullName} - ${candidate.appliedRole || ''}`.trim();
          sel.appendChild(option);
          sel.value = option.value;
        } catch (err) {
          console.error('Failed to load candidate for offer edit:', err);
        }
      }
    }

    // Reveal form card
    const card = document.getElementById('offerFormCard');
    card.style.display = 'block';
    card.scrollIntoView({ behavior: 'smooth' });
    return record;
  } catch (error) {
    console.error('Error loading offer:', error);
    showToast('Error loading offer details');
  }
}

function deleteOffer(id) {
  deleteRecord('offers', id, 'Are you sure you want to delete this offer?',
    'Offer deleted successfully', 'Error deleting offer', loadOffers);
}

function saveOfferForm(e) {
  e.preventDefault();
  (async () => {
    const data = buildFormData(OFFER_FIELDS);
    try {
      const res = editingOfferId ? await api.put(`offers/${editingOfferId}`, data) : await api.post('offers', data);
      if (res.ok) {
        showToast(editingOfferId ? 'Offer updated successfully' : 'Offer rolled out successfully');
        cancelOfferForm();
        await loadOffers();
        await loadCandidatesForOfferDropdown();

        // If candidate accepted the offer, mark candidate as HIRED
        try {
          if (data.offerStatus === 'ACCEPTED' && data.candidateId) {
            const cand = await api.get(`candidates/${data.candidateId}`);
            if (cand) {
              cand.candidateStatus = 'HIRED';
              const upd = await api.put(`candidates/${data.candidateId}`, cand);
              if (upd.ok) {
                showToast('Candidate status updated to Hired');
                // reload candidate list if present on this page
                if (typeof loadCandidates === 'function') await loadCandidates();
              } else {
                console.error('Failed to update candidate status to HIRED');
              }
            }
          }
        } catch (err) {
          console.error('Error updating candidate status after offer acceptance:', err);
        }
      } else {
        const err = await res.json().catch(() => ({}));
        showToast(err.error || 'Error rolling out offer');
      }
    } catch (error) {
      console.error('Error saving offer:', error);
      showToast('Error rolling out offer');
    }
  })();
}


// ========== FORM DATE MINIMUMS ==========
function setMinDatesOnForms() {
  const today = new Date().toISOString().split('T')[0];
  const now = new Date();
  const nowLocal = now.getFullYear() + '-' +
    String(now.getMonth() + 1).padStart(2, '0') + '-' +
    String(now.getDate()).padStart(2, '0') + 'T' +
    String(now.getHours()).padStart(2, '0') + ':' +
    String(now.getMinutes()).padStart(2, '0');

  ['requisitionDate', 'offerDate', 'joiningDate'].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.setAttribute('min', today);
  });
  const interviewDateTime = document.getElementById('interviewDateTime');
  if (interviewDateTime) interviewDateTime.setAttribute('min', nowLocal);
}

// ========== AUTH (placeholder) ==========
function handleLogin() { console.log('Login clicked'); }
function handleLogout() { console.log('Logout clicked'); }

// ========== EVENT REGISTRATION & INITIALIZATION ==========
document.addEventListener('DOMContentLoaded', async function () {
  console.log('HRMS Page Loading...');
  renderUserChip();
  const isConnected = await checkBackendConnection();
  if (!isConnected) return;

  // Auto-detect which page we're on and load the matching data + listeners
  if (document.getElementById('candidateCount')) {
    loadDashboard();
  }
  if (document.getElementById('candidateTableBody')) {
    loadCandidates();
    loadRolesForCandidateDropdown();
    document.getElementById('candidateForm')?.addEventListener('submit', saveCandidateForm);
  }
  if (document.getElementById('requisitionTableBody')) {
    loadRequisitions();
    document.getElementById('requisitionForm')?.addEventListener('submit', saveRequisitionForm);
  }
  if (document.getElementById('interviewTableBody')) {
    loadInterviews();
    loadCandidatesForDropdown();
    document.getElementById('interviewForm')?.addEventListener('submit', saveInterviewForm);
  }
  if (document.getElementById('offerTableBody')) {
    loadOffers();
    loadCandidatesForOfferDropdown();
    document.getElementById('offerForm')?.addEventListener('submit', saveOfferForm);
  }

  setMinDatesOnForms();
});

async function handleLogout() {
    try {
        const response = await fetch('http://localhost:8080/api/auth/logout', {
            method: 'POST', // Spring Security prefers POST requests for mutations like logout
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include' // CRITICAL: Sends the JSESSIONID cookie so the server knows which session to kill
        });
 
        if (response.ok) {
            // 1. Clear any frontend cached variables
          localStorage.removeItem("user_roles");
          sessionStorage.removeItem("currentUser");
          sessionStorage.removeItem("userRole");
 
            // 2. Redirect back to the login page
            window.location.href = "../html/login.html";
        } else {
            console.error("Backend failed to clear session context.");
            // Fallback safety redirect
            window.location.href = "../html/login.html";
        }
    } catch (error) {
        console.error("Network error during logout operation:", error);
        // Force redirect to login even if the network fails
        window.location.href = "../html/login.html";
    }
}