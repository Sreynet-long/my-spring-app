// JavaScript Application
const API_BASE_URL = '/api/students';
let currentPage = 0;
let currentSize = 10;
let currentSort = 'id';
let currentSortDir = 'asc';

// Initialize application
document.addEventListener('DOMContentLoaded', function() {
  loadStudents();
  loadStatistics();
  setupEventListeners();
});

// Setup event listeners
function setupEventListeners() {
  // Form submissions
  document.getElementById('studentForm').addEventListener('submit', handleStudentFormSubmit);
  document.getElementById('editStudentForm').addEventListener('submit', handleEditStudentFormSubmit);
  document.getElementById('searchForm').addEventListener('submit', handleSearchFormSubmit);

  // Modal events
  window.addEventListener('click', function(event) {
    const modal = document.getElementById('editModal');
    if (event.target === modal) {
      closeModal();
    }
  });
}

// Show/Hide sections
function showSection(sectionName) {
  // Hide all sections
  const sections = document.querySelectorAll('.section');
  sections.forEach(section => section.classList.remove('active'));

  // Remove active class from all nav links
  const navLinks = document.querySelectorAll('nav a');
  navLinks.forEach(link => link.classList.remove('active'));

  // Show selected section
  document.getElementById(sectionName + '-section').classList.add('active');

  // Add active class to clicked nav link
  event.target.classList.add('active');

  // Load data based on section
  if (sectionName === 'list') {
    loadStudents();
  } else if (sectionName === 'stats') {
    loadStatistics();
  }
}

// API Functions
async function makeRequest(url, options = {}) {
  showLoading();
  try {
    const response = await fetch(API_BASE_URL + url, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
      ...options
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || 'មានបញ្ហាកើតឡើង');
    }

    return await response.json();
  } catch (error) {
    showNotification(error.message, 'error');
    throw error;
  } finally {
    hideLoading();
  }
}

// Load students with pagination
async function loadStudents(page = 0, size = 10, sortBy = 'id', sortDir = 'asc') {
  try {
    const data = await makeRequest(`/paginated?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`);

    currentPage = data.currentPage;
    currentSize = data.pageSize;
    currentSort = sortBy;
    currentSortDir = sortDir;

    displayStudents(data.students);
    displayPagination(data);
  } catch (error) {
    console.error('Error loading students:', error);
  }
}

// Display students in table
function displayStudents(students) {
  const tbody = document.querySelector('#studentsTable tbody');
  tbody.innerHTML = '';

  if (students.length === 0) {
    tbody.innerHTML = '<tr><td colspan="6" class="text-center">មិនមានទិន្នន័យ</td></tr>';
    return;
  }

  students.forEach(student => {
    const row = document.createElement('tr');
    row.innerHTML = `
          <td>${student.id}</td>
          <td>${student.name}</td>
          <td>${student.email}</td>
          <td>${student.phoneNumber || '-'}</td>
          <td>${student.major}</td>
          <td class="actions">
              <button onclick="editStudent(${student.id})" class="btn-edit" title="កែប្រែ">
                  <i class="fas fa-edit"></i>
              </button>
              <button onclick="deleteStudent(${student.id})" class="btn-delete" title="លុប">
                  <i class="fas fa-trash"></i>
              </button>
              <button onclick="viewStudent(${student.id})" class="btn-view" title="មើល">
                  <i class="fas fa-eye"></i>
              </button>
          </td>
      `;
    tbody.appendChild(row);
  });
}

// Display pagination
function displayPagination(data) {
  const pagination = document.getElementById('pagination');
  pagination.innerHTML = '';

  if (data.totalPages <= 1) return;

  // Previous button
  if (data.hasPrevious) {
    const prevBtn = document.createElement('button');
    prevBtn.innerHTML = '<i class="fas fa-chevron-left"></i> មុន';
    prevBtn.onclick = () => loadStudents(data.currentPage - 1, currentSize, currentSort, currentSortDir);
    pagination.appendChild(prevBtn);
  }

  // Page numbers
  const startPage = Math.max(0, data.currentPage - 2);
  const endPage = Math.min(data.totalPages - 1, data.currentPage + 2);

  for (let i = startPage; i <= endPage; i++) {
    const pageBtn = document.createElement('button');
    pageBtn.textContent = i + 1;
    pageBtn.className = i === data.currentPage ? 'active' : '';
    pageBtn.onclick = () => loadStudents(i, currentSize, currentSort, currentSortDir);
    pagination.appendChild(pageBtn);
  }

  // Next button
  if (data.hasNext) {
    const nextBtn = document.createElement('button');
    nextBtn.innerHTML = 'បន្ទាប់ <i class="fas fa-chevron-right"></i>';
    nextBtn.onclick = () => loadStudents(data.currentPage + 1, currentSize, currentSort, currentSortDir);
    pagination.appendChild(nextBtn);
  }

  // Page info
  const pageInfo = document.createElement('span');
  pageInfo.className = 'page-info';
  pageInfo.textContent = `ទំព័រ ${data.currentPage + 1} នៃ ${data.totalPages} (សរុប ${data.totalItems} ធាតុ)`;
  pagination.appendChild(pageInfo);
}

// Handle student form submission
async function handleStudentFormSubmit(event) {
  event.preventDefault();

  const formData = new FormData(event.target);
  const studentData = Object.fromEntries(formData.entries());

  try {
    await makeRequest('', {
      method: 'POST',
      body: JSON.stringify(studentData)
    });

    showNotification('បន្ថែមនិស្សិតបានជោគជ័យ', 'success');
    event.target.reset();
    loadStudents();
  } catch (error) {
    console.error('Error creating student:', error);
  }
}

// Edit student
async function editStudent(id) {
  try {
    const student = await makeRequest(`/${id}`);

    // Populate edit form
    document.getElementById('editId').value = student.id;
    document.getElementById('editName').value = student.name;
    document.getElementById('editEmail').value = student.email;
    document.getElementById('editPhoneNumber').value = student.phoneNumber || '';
    document.getElementById('editBirthDate').value = student.birthDate || '';
    document.getElementById('editMajor').value = student.major;
    document.getElementById('editAddress').value = student.address || '';

    // Show modal
    document.getElementById('editModal').style.display = 'block';
  } catch (error) {
    console.error('Error loading student for edit:', error);
  }
}

// Handle edit student form submission
async function handleEditStudentFormSubmit(event) {
  event.preventDefault();

  const formData = new FormData(event.target);
  const studentData = Object.fromEntries(formData.entries());
  const id = studentData.id;
  delete studentData.id;

  try {
    await makeRequest(`/${id}`, {
      method: 'PUT',
      body: JSON.stringify(studentData)
    });

    showNotification('កែប្រែនិស្សិតបានជោគជ័យ', 'success');
    closeModal();
    loadStudents(currentPage, currentSize, currentSort, currentSortDir);
  } catch (error) {
    console.error('Error updating student:', error);
  }
}

// Delete student
async function deleteStudent(id) {
  if (!confirm('តើអ្នកពិតជាចង់លុបនិស្សិតនេះមែនទេ?')) {
    return;
  }

  try {
    await makeRequest(`/${id}`, { method: 'DELETE' });
    showNotification('លុបនិស្សិតបានជោគជ័យ', 'success');
    loadStudents(currentPage, currentSize, currentSort, currentSortDir);
  } catch (error) {
    console.error('Error deleting student:', error);
  }
}

// View student details
async function viewStudent(id) {
  try {
    const student = await makeRequest(`/${id}`);

    let details = `
          <div class="student-details">
              <h3>ព័ត៌មានលម្អិតនិស្សិត</h3>
              <p><strong>ID:</strong> ${student.id}</p>
              <p><strong>ឈ្មោះ:</strong> ${student.name}</p>
              <p><strong>អ៊ីមែល:</strong> ${student.email}</p>
              <p><strong>លេខទូរសព្ទ:</strong> ${student.phoneNumber || 'មិនមាន'}</p>
              <p><strong>កាលបរិច្ឆេទកំណើត:</strong> ${student.birthDate || 'មិនមាន'}</p>
              <p><strong>ជំនាញ:</strong> ${student.major}</p>
              <p><strong>អាសយដ្ឋាន:</strong> ${student.address || 'មិនមាន'}</p>
              <p><strong>បង្កើតនៅ:</strong> ${new Date(student.createdAt).toLocaleDateString('km')}</p>
              <p><strong>ធ្វើបច្ចុប្បន្នភាព:</strong> ${new Date(student.updatedAt).toLocaleDateString('km')}</p>
          </div>
      `;

    showNotification(details, 'info', 10000);
  } catch (error) {
    console.error('Error viewing student:', error);
  }
}

// Handle search form
async function handleSearchFormSubmit(event) {
  event.preventDefault();

  const name = document.getElementById('searchName').value;
  const major = document.getElementById('searchMajor').value;
  const email = document.getElementById('searchEmail').value;

  try {
    const params = new URLSearchParams();
    if (name) params.append('name', name);
    if (major) params.append('major', major);
    if (email) params.append('email', email);
    params.append('page', '0');
    params.append('size', '20');

    const data = await makeRequest(`/search?${params}`);

    displaySearchResults(data);
  } catch (error) {
    console.error('Error searching students:', error);
  }
}

// Display search results
function displaySearchResults(data) {
  const resultsDiv = document.getElementById('searchResults');

  if (data.students.length === 0) {
    resultsDiv.innerHTML = '<p class="no-results">មិនមានលទ្ធផលស្វែងរក</p>';
    return;
  }

  let html = `
      <h3>លទ្ធផលស្វែងរក (${data.totalItems} ធាតុ)</h3>
      <div class="search-results-table">
          <table>
              <thead>
                  <tr>
                      <th>ID</th>
                      <th>ឈ្មោះ</th>
                      <th>អ៊ីមែល</th>
                      <th>ជំនាញ</th>
                      <th>សកម្មភាព</th>
                  </tr>
              </thead>
              <tbody>
  `;

  data.students.forEach(student => {
    html += `
          <tr>
              <td>${student.id}</td>
              <td>${student.name}</td>
              <td>${student.email}</td>
              <td>${student.major}</td>
              <td class="actions">
                  <button onclick="editStudent(${student.id})" class="btn-edit" title="កែប្រែ">
                      <i class="fas fa-edit"></i>
                  </button>
                  <button onclick="deleteStudent(${student.id})" class="btn-delete" title="លុប">
                      <i class="fas fa-trash"></i>
                  </button>
                  <button onclick="viewStudent(${student.id})" class="btn-view" title="មើល">
                      <i class="fas fa-eye"></i>
                  </button>
              </td>
          </tr>
      `;
  });

  html += `
              </tbody>
          </table>
      </div>
  `;

  resultsDiv.innerHTML = html;
}

// Clear search
function clearSearch() {
  document.getElementById('searchForm').reset();
  document.getElementById('searchResults').innerHTML = '';
}

// Load statistics
async function loadStatistics() {
  try {
    const stats = await makeRequest('/statistics');

    document.getElementById('totalStudents').textContent = stats.totalStudents;
    document.getElementById('newStudents').textContent = stats.recentStudents.length;

    // Count unique majors
    const majors = new Set();
    stats.recentStudents.forEach(student => majors.add(student.major));
    document.getElementById('majorsCount').textContent = majors.size;

    // Last updated
    document.getElementById('lastUpdated').textContent = new Date().toLocaleDateString('km');

    // Display recent students
    displayRecentStudents(stats.recentStudents);
  } catch (error) {
    console.error('Error loading statistics:', error);
  }
}

// Display recent students
function displayRecentStudents(students) {
  const container = document.getElementById('recentStudentsList');

  if (students.length === 0) {
    container.innerHTML = '<p>មិនមាននិស្សិតថ្មី</p>';
    return;
  }

  let html = '<div class="recent-students-grid">';

  students.forEach(student => {
    html += `
          <div class="recent-student-card">
              <div class="student-avatar">
                  <i class="fas fa-user-graduate"></i>
              </div>
              <div class="student-info">
                  <h4>${student.name}</h4>
                  <p>${student.major}</p>
                  <small>${new Date(student.createdAt).toLocaleDateString('km')}</small>
              </div>
          </div>
      `;
  });

  html += '</div>';
  container.innerHTML = html;
}

// Modal functions
function closeModal() {
  document.getElementById('editModal').style.display = 'none';
}

// Loading functions
function showLoading() {
  document.getElementById('loadingOverlay').style.display = 'flex';
}

function hideLoading() {
  document.getElementById('loadingOverlay').style.display = 'none';
}

// Notification functions
function showNotification(message, type = 'info', duration = 5000) {
  const notification = document.getElementById('notification');
  const messageSpan = document.getElementById('notificationMessage');

  messageSpan.innerHTML = message;
  notification.className = `notification ${type} show`;

  setTimeout(() => {
    hideNotification();
  }, duration);
}

function hideNotification() {
  document.getElementById('notification').classList.remove('show');
}

// Back to top functionality
window.addEventListener('scroll', function() {
  const backToTop = document.querySelector('.back-to-top');
  if (window.pageYOffset > 300) {
    backToTop.classList.add('visible');
  } else {
    backToTop.classList.remove('visible');
  }
});

// Smooth scroll to top
function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' });
}