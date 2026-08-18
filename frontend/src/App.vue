<script setup>
import { nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'

function pageFromHash() {
  if (window.location.hash === '#new-application') return 'new-application'
  if (window.location.hash === '#applications') return 'applications'
  if (window.location.hash === '#agreements') return 'agreements'
  return 'clients'
}

const activePage = ref(pageFromHash())

const emptyApplicationForm = () => ({
  lastName: '',
  firstName: '',
  middleName: '',
  passportSeries: '',
  passportNumber: '',
  passportDepartmentCode: '',
  passportIssuedBy: '',
  passportIssueDate: '',
  gender: '',
  maritalStatus: '',
  residentialAddress: '',
  registrationAddress: '',
  phone: '',
  employedFrom: '',
  employedTo: '',
  position: '',
  organizationName: '',
  requestedAmount: '',
  purpose: '',
})
const applicationForm = reactive(emptyApplicationForm())
const applicationSubmitting = ref(false)
const applicationSubmitError = ref('')
const applicationSubmitSuccess = ref('')
const applicationValidationErrors = ref({})
const applicationDecision = ref(null)
const today = new Date().toISOString().slice(0, 10)

const clients = ref([])
const clientsLoading = ref(false)
const clientsError = ref('')
const nameQuery = ref('')
const phoneQuery = ref('')
const passportQuery = ref('')
const clientsPage = ref(0)
const clientsTotalElements = ref(0)
const clientsTotalPages = ref(0)

const applications = ref([])
const applicationsLoading = ref(false)
const applicationsError = ref('')
const applicationsPage = ref(0)
const applicationsTotalElements = ref(0)
const applicationsTotalPages = ref(0)

const agreements = ref([])
const agreementsLoading = ref(false)
const agreementsError = ref('')
const agreementsPage = ref(0)
const agreementsTotalElements = ref(0)
const agreementsTotalPages = ref(0)

const pageSize = 25
let searchTimer
let clientsRequest
let applicationsRequest
let agreementsRequest

const genderLabels = {
  MALE: 'Мужской',
  FEMALE: 'Женский',
  OTHER: 'Другой',
}

const maritalStatusLabels = {
  SINGLE: 'Не состоит в браке',
  MARRIED: 'Состоит в браке',
  DIVORCED: 'В разводе',
  WIDOWED: 'Вдовец / вдова',
  OTHER: 'Другое',
}

const decisionStatusLabels = {
  PENDING: 'На рассмотрении',
  APPROVED: 'Одобрено',
  DENIED: 'Отказано',
}

const signatureStatusLabels = {
  SIGNED: 'Подписан',
  UNSIGNED: 'Не подписан',
}

const moneyFormatter = new Intl.NumberFormat('ru-RU', {
  style: 'currency',
  currency: 'RUB',
  maximumFractionDigits: 0,
})
const dateFormatter = new Intl.DateTimeFormat('ru-RU', {
  day: '2-digit',
  month: '2-digit',
  year: 'numeric',
})

function currentEmployment(client) {
  return client.employments?.find((employment) => !employment.employedTo) ?? client.employments?.[0]
}

function formatMoney(value) {
  return moneyFormatter.format(Number(value))
}

function formatDate(value) {
  return value ? dateFormatter.format(new Date(value)) : '—'
}

function openPage(pageName) {
  activePage.value = pageName
  window.location.hash = pageName
}

function validationMessage(field) {
  return applicationValidationErrors.value[field]
}

async function focusFirstInvalidField() {
  await nextTick()
  const firstField = Object.keys(applicationValidationErrors.value)[0]
  if (!firstField) return
  const targetField = firstField === 'employmentPeriodValid' ? 'employedTo' : firstField
  const control = document.querySelector(`[data-field="${targetField}"] input, [data-field="${targetField}"] select, [data-field="${targetField}"] textarea`)
  control?.focus()
  control?.scrollIntoView({ behavior: 'smooth', block: 'center' })
}

function clearFieldError(event) {
  const field = event.target.closest('[data-field]')?.dataset.field
  if (!field) return
  delete applicationValidationErrors.value[field]
  if (field === 'employedTo' || field === 'employedFrom') {
    delete applicationValidationErrors.value.employmentPeriodValid
  }
  if (Object.keys(applicationValidationErrors.value).length === 0) {
    applicationSubmitError.value = ''
  }
}

function randomItem(items) {
  return items[Math.floor(Math.random() * items.length)]
}

function randomDigits(length) {
  const values = new Uint32Array(length)
  crypto.getRandomValues(values)
  return Array.from(values, (value) => value % 10).join('')
}

function fillRandomApplication() {
  const profile = randomItem([
    { lastName: 'Смирнов', firstName: 'Александр', middleName: 'Игоревич', gender: 'MALE' },
    { lastName: 'Кузнецов', firstName: 'Максим', middleName: 'Андреевич', gender: 'MALE' },
    { lastName: 'Попова', firstName: 'Екатерина', middleName: 'Сергеевна', gender: 'FEMALE' },
    { lastName: 'Васильева', firstName: 'Мария', middleName: 'Олеговна', gender: 'FEMALE' },
  ])
  const city = randomItem(['Москва', 'Санкт-Петербург', 'Казань', 'Самара', 'Новосибирск'])
  const street = randomItem(['ул. Ленина', 'ул. Центральная', 'проспект Мира', 'ул. Молодёжная'])
  const organization = randomItem(['ООО «Вектор»', 'АО «Альфа»', 'ООО «Север»', 'АО «Городские системы»'])

  Object.assign(applicationForm, {
    ...profile,
    maritalStatus: randomItem(['SINGLE', 'MARRIED', 'DIVORCED']),
    passportSeries: `${Math.floor(Math.random() * 8) + 1}${randomDigits(3)}`,
    passportNumber: randomDigits(6),
    passportDepartmentCode: `${randomDigits(3)}-${randomDigits(3)}`,
    passportIssuedBy: `ГУ МВД России по г. ${city}`,
    passportIssueDate: randomItem(['2014-03-12', '2016-07-21', '2018-11-05', '2020-02-17']),
    phone: `+7 9${randomDigits(2)} ${randomDigits(3)}-${randomDigits(2)}-${randomDigits(2)}`,
    registrationAddress: `${city}, ${street}, д. ${Math.floor(Math.random() * 90) + 1}`,
    residentialAddress: `${city}, ${street}, д. ${Math.floor(Math.random() * 90) + 1}, кв. ${Math.floor(Math.random() * 200) + 1}`,
    employedFrom: randomItem(['2018-04-10', '2019-09-16', '2021-02-01', '2022-06-20']),
    employedTo: '',
    position: randomItem(['Инженер', 'Менеджер проектов', 'Финансовый аналитик', 'Ведущий специалист']),
    organizationName: organization,
    requestedAmount: randomItem([150000, 250000, 400000, 600000, 850000]),
    purpose: randomItem(['Ремонт квартиры', 'Покупка автомобиля', 'Оплата обучения', 'Медицинские услуги']),
  })
  applicationValidationErrors.value = {}
  applicationSubmitError.value = ''
  applicationSubmitSuccess.value = ''
  applicationDecision.value = null
}

async function submitApplication() {
  applicationSubmitting.value = true
  applicationSubmitError.value = ''
  applicationSubmitSuccess.value = ''
  applicationValidationErrors.value = {}
  applicationDecision.value = null

  try {
    const response = await fetch('/api/applications', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        ...applicationForm,
        requestedAmount: Number(applicationForm.requestedAmount),
        employedTo: applicationForm.employedTo || null,
        middleName: applicationForm.middleName || null,
      }),
    })
    const result = await response.json().catch(() => ({}))
    if (!response.ok) {
      const rawErrors = result.errors
      applicationValidationErrors.value = Array.isArray(rawErrors)
        ? Object.fromEntries(rawErrors.map((error) => [error.field || error.property || 'form', error.message || error.defaultMessage]))
        : rawErrors && typeof rawErrors === 'object' ? rawErrors : {}
      applicationSubmitError.value = result.message || result.detail || `Сервер отклонил заявку (код ${response.status}).`
      await focusFirstInvalidField()
      return
    }

    applicationDecision.value = result
    applicationSubmitSuccess.value = result.message
    applications.value = []
    agreements.value = []
    Object.assign(applicationForm, emptyApplicationForm())
  } catch (requestError) {
    applicationSubmitError.value = 'Не удалось отправить заявку. Проверьте соединение и попробуйте ещё раз.'
    console.error(requestError)
  } finally {
    applicationSubmitting.value = false
  }
}

async function loadAgreements(targetPage = agreementsPage.value) {
  agreementsRequest?.abort()
  const request = new AbortController()
  agreementsRequest = request
  agreementsLoading.value = true
  agreementsError.value = ''

  try {
    const params = new URLSearchParams({ page: targetPage.toString(), size: pageSize.toString() })
    const response = await fetch(`/api/agreements?${params}`, { signal: request.signal })
    if (!response.ok) throw new Error(`Ошибка запроса. Статус: ${response.status}`)
    const result = await response.json()
    agreements.value = result.content
    agreementsPage.value = result.page
    agreementsTotalElements.value = result.totalElements
    agreementsTotalPages.value = result.totalPages
  } catch (requestError) {
    if (requestError.name === 'AbortError') return
    agreementsError.value = 'Не удалось загрузить список договоров. Попробуйте ещё раз.'
    console.error(requestError)
  } finally {
    if (agreementsRequest === request) agreementsLoading.value = false
  }
}

async function loadClients(targetPage = clientsPage.value) {
  clientsRequest?.abort()
  const request = new AbortController()
  clientsRequest = request
  clientsLoading.value = true
  clientsError.value = ''

  try {
    const params = new URLSearchParams({ page: targetPage.toString(), size: pageSize.toString() })
    const filters = {
      name: nameQuery.value.trim(),
      phone: phoneQuery.value.trim(),
      passport: passportQuery.value.trim(),
    }
    Object.entries(filters).forEach(([key, value]) => {
      if (value) params.set(key, value)
    })

    const response = await fetch(`/api/clients?${params}`, { signal: request.signal })
    if (!response.ok) throw new Error(`Ошибка запроса. Статус: ${response.status}`)
    const result = await response.json()
    clients.value = result.content
    clientsPage.value = result.page
    clientsTotalElements.value = result.totalElements
    clientsTotalPages.value = result.totalPages
  } catch (requestError) {
    if (requestError.name === 'AbortError') return
    clientsError.value = 'Не удалось загрузить список клиентов. Попробуйте ещё раз.'
    console.error(requestError)
  } finally {
    if (clientsRequest === request) clientsLoading.value = false
  }
}

async function loadApplications(targetPage = applicationsPage.value) {
  applicationsRequest?.abort()
  const request = new AbortController()
  applicationsRequest = request
  applicationsLoading.value = true
  applicationsError.value = ''

  try {
    const params = new URLSearchParams({ page: targetPage.toString(), size: pageSize.toString() })
    const response = await fetch(`/api/applications?${params}`, { signal: request.signal })
    if (!response.ok) throw new Error(`Ошибка запроса. Статус: ${response.status}`)
    const result = await response.json()
    applications.value = result.content
    applicationsPage.value = result.page
    applicationsTotalElements.value = result.totalElements
    applicationsTotalPages.value = result.totalPages
  } catch (requestError) {
    if (requestError.name === 'AbortError') return
    applicationsError.value = 'Не удалось загрузить список заявок. Попробуйте ещё раз.'
    console.error(requestError)
  } finally {
    if (applicationsRequest === request) applicationsLoading.value = false
  }
}

function handleHashChange() {
  activePage.value = pageFromHash()
}

watch([nameQuery, phoneQuery, passportQuery], () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadClients(0), 300)
})

watch(activePage, (pageName) => {
  if (pageName === 'applications' && applications.value.length === 0) loadApplications(0)
  if (pageName === 'agreements' && agreements.value.length === 0) loadAgreements(0)
  if (pageName === 'clients' && clients.value.length === 0) loadClients(0)
})

onMounted(() => {
  window.addEventListener('hashchange', handleHashChange)
  if (activePage.value === 'applications') loadApplications(0)
  else if (activePage.value === 'agreements') loadAgreements(0)
  else loadClients(0)
})

onBeforeUnmount(() => {
  clearTimeout(searchTimer)
  clientsRequest?.abort()
  applicationsRequest?.abort()
  agreementsRequest?.abort()
  window.removeEventListener('hashchange', handleHashChange)
})
</script>

<template>
  <main class="page" :class="{ 'page-wide': activePage === 'applications' || activePage === 'agreements' }">
    <nav class="navigation" aria-label="Основная навигация">
      <button :class="{ active: activePage === 'new-application' }" type="button" @click="openPage('new-application')">
        Оформление заявки
      </button>
      <button :class="{ active: activePage === 'clients' }" type="button" @click="openPage('clients')">
        Клиенты
      </button>
      <button :class="{ active: activePage === 'applications' }" type="button" @click="openPage('applications')">
        Заявки
      </button>
      <button :class="{ active: activePage === 'agreements' }" type="button" @click="openPage('agreements')">
        Договоры
      </button>
    </nav>

    <template v-if="activePage === 'new-application'">
      <header class="page-header">
        <div>
          <h1>Оформление заявки</h1>
          <p class="subtitle">Заполните сведения о клиенте и желаемом кредите</p>
        </div>
      </header>

      <form class="panel application-form" @submit.prevent="submitApplication" @input="clearFieldError">
        <section class="form-section" aria-labelledby="personal-heading">
          <div class="section-heading">
            <span>1</span>
            <div>
              <h2 id="personal-heading">Личные данные</h2>
              <p>Основные сведения о клиенте</p>
            </div>
          </div>
          <div class="form-grid form-grid-three">
            <label class="field" :class="{ 'field-invalid': validationMessage('lastName') }" data-field="lastName">
              <span>Фамилия *</span>
              <input v-model.trim="applicationForm.lastName" autocomplete="family-name" required />
              <small v-if="validationMessage('lastName')" class="field-error">{{ validationMessage('lastName') }}</small>
            </label>
            <label class="field" :class="{ 'field-invalid': validationMessage('firstName') }" data-field="firstName">
              <span>Имя *</span>
              <input v-model.trim="applicationForm.firstName" autocomplete="given-name" required />
              <small v-if="validationMessage('firstName')" class="field-error">{{ validationMessage('firstName') }}</small>
            </label>
            <label class="field" :class="{ 'field-invalid': validationMessage('middleName') }" data-field="middleName">
              <span>Отчество</span>
              <input v-model.trim="applicationForm.middleName" autocomplete="additional-name" />
              <small v-if="validationMessage('middleName')" class="field-error">{{ validationMessage('middleName') }}</small>
            </label>
            <label class="field" :class="{ 'field-invalid': validationMessage('gender') }" data-field="gender">
              <span>Пол *</span>
              <select v-model="applicationForm.gender" required>
                <option disabled value="">Выберите пол</option>
                <option value="MALE">Мужской</option>
                <option value="FEMALE">Женский</option>
                <option value="OTHER">Другой</option>
              </select>
              <small v-if="validationMessage('gender')" class="field-error">{{ validationMessage('gender') }}</small>
            </label>
            <label class="field" :class="{ 'field-invalid': validationMessage('maritalStatus') }" data-field="maritalStatus">
              <span>Семейное положение *</span>
              <select v-model="applicationForm.maritalStatus" required>
                <option disabled value="">Выберите статус</option>
                <option value="SINGLE">Не состоит в браке</option>
                <option value="MARRIED">Состоит в браке</option>
                <option value="DIVORCED">В разводе</option>
                <option value="WIDOWED">Вдовец / вдова</option>
                <option value="OTHER">Другое</option>
              </select>
              <small v-if="validationMessage('maritalStatus')" class="field-error">{{ validationMessage('maritalStatus') }}</small>
            </label>
            <label class="field" :class="{ 'field-invalid': validationMessage('phone') }" data-field="phone">
              <span>Телефон *</span>
              <input v-model.trim="applicationForm.phone" type="tel" inputmode="tel" autocomplete="tel" placeholder="+7 999 123-45-67" required />
              <small v-if="validationMessage('phone')" class="field-error">{{ validationMessage('phone') }}</small>
            </label>
          </div>
        </section>

        <section class="form-section" aria-labelledby="passport-heading">
          <div class="section-heading">
            <span>2</span>
            <div>
              <h2 id="passport-heading">Паспорт и адреса</h2>
              <p>Документ и адресные данные клиента</p>
            </div>
          </div>
          <div class="form-grid">
            <label class="field" :class="{ 'field-invalid': validationMessage('passportSeries') }" data-field="passportSeries">
              <span>Серия паспорта *</span>
              <input v-model.trim="applicationForm.passportSeries" inputmode="numeric" maxlength="4" pattern="[0-9]{4}" placeholder="1234" required />
              <small v-if="validationMessage('passportSeries')" class="field-error">{{ validationMessage('passportSeries') }}</small>
            </label>
            <label class="field" :class="{ 'field-invalid': validationMessage('passportNumber') }" data-field="passportNumber">
              <span>Номер паспорта *</span>
              <input v-model.trim="applicationForm.passportNumber" inputmode="numeric" maxlength="6" pattern="[0-9]{6}" placeholder="567890" required />
              <small v-if="validationMessage('passportNumber')" class="field-error">{{ validationMessage('passportNumber') }}</small>
            </label>
            <label class="field" :class="{ 'field-invalid': validationMessage('passportDepartmentCode') }" data-field="passportDepartmentCode">
              <span>Код подразделения *</span>
              <input v-model.trim="applicationForm.passportDepartmentCode" inputmode="numeric" maxlength="7" pattern="[0-9]{3}-[0-9]{3}" placeholder="770-001" required />
              <small v-if="validationMessage('passportDepartmentCode')" class="field-error">{{ validationMessage('passportDepartmentCode') }}</small>
            </label>
            <label class="field" :class="{ 'field-invalid': validationMessage('passportIssueDate') }" data-field="passportIssueDate">
              <span>Дата выдачи *</span>
              <input v-model="applicationForm.passportIssueDate" type="date" :max="today" required />
              <small v-if="validationMessage('passportIssueDate')" class="field-error">{{ validationMessage('passportIssueDate') }}</small>
            </label>
            <label class="field field-wide" :class="{ 'field-invalid': validationMessage('passportIssuedBy') }" data-field="passportIssuedBy">
              <span>Кем выдан *</span>
              <input v-model.trim="applicationForm.passportIssuedBy" maxlength="255" required />
              <small v-if="validationMessage('passportIssuedBy')" class="field-error">{{ validationMessage('passportIssuedBy') }}</small>
            </label>
            <label class="field field-wide" :class="{ 'field-invalid': validationMessage('registrationAddress') }" data-field="registrationAddress">
              <span>Адрес регистрации *</span>
              <input v-model.trim="applicationForm.registrationAddress" autocomplete="address-line1" required />
              <small v-if="validationMessage('registrationAddress')" class="field-error">{{ validationMessage('registrationAddress') }}</small>
            </label>
            <label class="field field-wide" :class="{ 'field-invalid': validationMessage('residentialAddress') }" data-field="residentialAddress">
              <span>Адрес проживания *</span>
              <input v-model.trim="applicationForm.residentialAddress" autocomplete="street-address" required />
              <small v-if="validationMessage('residentialAddress')" class="field-error">{{ validationMessage('residentialAddress') }}</small>
            </label>
          </div>
        </section>

        <section class="form-section" aria-labelledby="employment-heading">
          <div class="section-heading">
            <span>3</span>
            <div>
              <h2 id="employment-heading">Занятость</h2>
              <p>Текущее место и период работы</p>
            </div>
          </div>
          <div class="form-grid">
            <label class="field field-wide" :class="{ 'field-invalid': validationMessage('organizationName') }" data-field="organizationName">
              <span>Название организации *</span>
              <input v-model.trim="applicationForm.organizationName" autocomplete="organization" required />
              <small v-if="validationMessage('organizationName')" class="field-error">{{ validationMessage('organizationName') }}</small>
            </label>
            <label class="field field-wide" :class="{ 'field-invalid': validationMessage('position') }" data-field="position">
              <span>Должность *</span>
              <input v-model.trim="applicationForm.position" autocomplete="organization-title" required />
              <small v-if="validationMessage('position')" class="field-error">{{ validationMessage('position') }}</small>
            </label>
            <label class="field" :class="{ 'field-invalid': validationMessage('employedFrom') }" data-field="employedFrom">
              <span>Дата начала работы *</span>
              <input v-model="applicationForm.employedFrom" type="date" :max="today" required />
              <small v-if="validationMessage('employedFrom')" class="field-error">{{ validationMessage('employedFrom') }}</small>
            </label>
            <label class="field" :class="{ 'field-invalid': validationMessage('employedTo') || validationMessage('employmentPeriodValid') }" data-field="employedTo">
              <span>Дата окончания</span>
              <input v-model="applicationForm.employedTo" type="date" :min="applicationForm.employedFrom" :max="today" />
              <small v-if="validationMessage('employedTo') || validationMessage('employmentPeriodValid')" class="field-error">
                {{ validationMessage('employedTo') || validationMessage('employmentPeriodValid') }}
              </small>
              <small v-else>Оставьте пустым, если клиент работает сейчас</small>
            </label>
          </div>
        </section>

        <section class="form-section" aria-labelledby="loan-heading">
          <div class="section-heading">
            <span>4</span>
            <div>
              <h2 id="loan-heading">Параметры кредита</h2>
              <p>Запрашиваемая сумма и цель</p>
            </div>
          </div>
          <div class="form-grid">
            <label class="field" :class="{ 'field-invalid': validationMessage('requestedAmount') }" data-field="requestedAmount">
              <span>Желаемая сумма, ₽ *</span>
              <input v-model="applicationForm.requestedAmount" type="number" inputmode="decimal" min="1000" step="1000" placeholder="300 000" required />
              <small v-if="validationMessage('requestedAmount')" class="field-error">{{ validationMessage('requestedAmount') }}</small>
            </label>
            <label class="field field-wide" :class="{ 'field-invalid': validationMessage('purpose') }" data-field="purpose">
              <span>Цель кредита *</span>
              <textarea v-model.trim="applicationForm.purpose" rows="3" maxlength="500" placeholder="Например, ремонт квартиры" required></textarea>
              <small v-if="validationMessage('purpose')" class="field-error">{{ validationMessage('purpose') }}</small>
            </label>
          </div>
        </section>

        <div class="form-footer">
          <div v-if="applicationSubmitError" class="form-message form-message-error" role="alert">
            <strong>{{ applicationSubmitError }}</strong>
            <ul v-if="Object.keys(applicationValidationErrors).length">
              <li v-for="(message, field) in applicationValidationErrors" :key="field">{{ message }}</li>
            </ul>
          </div>
          <div class="form-actions">
            <button class="secondary-button" type="button" :disabled="applicationSubmitting" @click="fillRandomApplication">
              Заполнить случайными данными
            </button>
            <button class="primary-button" type="submit" :disabled="applicationSubmitting">
              {{ applicationSubmitting ? 'Отправка…' : 'Отправить заявку' }}
            </button>
          </div>
        </div>
      </form>

      <section v-if="applicationDecision" class="decision-card" :class="`decision-${applicationDecision.status.toLowerCase()}`" role="status">
        <div>
          <span class="status-badge" :class="`status-${applicationDecision.status.toLowerCase()}`">
            {{ decisionStatusLabels[applicationDecision.status] }}
          </span>
          <h2>{{ applicationSubmitSuccess }}</h2>
          <p>Заявка № {{ applicationDecision.applicationId }}</p>
        </div>
        <dl v-if="applicationDecision.status === 'APPROVED'" class="decision-details">
          <div><dt>Одобренная сумма</dt><dd>{{ formatMoney(applicationDecision.approvedAmount) }}</dd></div>
          <div><dt>Срок</dt><dd>{{ applicationDecision.termMonths }} мес.</dd></div>
          <div><dt>Договор</dt><dd>{{ applicationDecision.agreementNumber }}</dd></div>
        </dl>
      </section>
    </template>

    <template v-else-if="activePage === 'clients'">
      <header class="page-header">
        <div>
          <h1>Клиенты</h1>
          <p class="subtitle">Клиентская база и сведения о занятости</p>
        </div>
        <span v-if="!clientsLoading && !clientsError" class="count">Всего: {{ clientsTotalElements }}</span>
      </header>

      <section class="panel" aria-labelledby="clients-heading">
        <div class="toolbar">
          <h2 id="clients-heading">Список клиентов</h2>
          <div class="filters">
            <label class="search">
              <span class="sr-only">Поиск по ФИО</span>
              <input v-model="nameQuery" type="search" placeholder="ФИО" />
            </label>
            <label class="search">
              <span class="sr-only">Поиск по телефону</span>
              <input v-model="phoneQuery" type="search" inputmode="tel" placeholder="Телефон" />
            </label>
            <label class="search">
              <span class="sr-only">Поиск по паспорту</span>
              <input v-model="passportQuery" type="search" placeholder="Паспорт" />
            </label>
          </div>
        </div>

        <div v-if="clientsLoading" class="state" role="status">Загрузка клиентов…</div>
        <div v-else-if="clientsError" class="state error" role="alert">
          <p>{{ clientsError }}</p>
          <button type="button" @click="loadClients()">Повторить</button>
        </div>
        <div v-else-if="clients.length === 0" class="state">
          {{ nameQuery || phoneQuery || passportQuery ? 'По вашему запросу клиенты не найдены.' : 'Клиенты ещё не добавлены.' }}
        </div>

        <div v-else class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ФИО</th><th>Телефон</th><th>Паспорт</th><th>Пол</th>
                <th>Семейное положение</th><th>Должность</th><th>Организация</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="client in clients" :key="client.id">
                <td><strong>{{ client.fullName }}</strong><small>#{{ client.id }}</small></td>
                <td>{{ client.phone }}</td>
                <td>{{ client.passport || '—' }}</td>
                <td>{{ genderLabels[client.gender] || 'Не указано' }}</td>
                <td>{{ maritalStatusLabels[client.maritalStatus] || 'Не указано' }}</td>
                <td>{{ currentEmployment(client)?.position || '—' }}</td>
                <td>{{ currentEmployment(client)?.organizationName || '—' }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <footer v-if="!clientsLoading && !clientsError && clientsTotalPages > 1" class="pagination">
          <button type="button" :disabled="clientsPage === 0" @click="loadClients(clientsPage - 1)">Назад</button>
          <span>Страница {{ clientsPage + 1 }} из {{ clientsTotalPages }}</span>
          <button type="button" :disabled="clientsPage + 1 >= clientsTotalPages" @click="loadClients(clientsPage + 1)">Вперёд</button>
        </footer>
      </section>
    </template>

    <template v-else-if="activePage === 'applications'">
      <header class="page-header">
        <div>
          <h1>Заявки на кредит</h1>
          <p class="subtitle">Все решения: одобренные, отклонённые и находящиеся на рассмотрении</p>
        </div>
        <span v-if="!applicationsLoading && !applicationsError" class="count">
          Всего: {{ applicationsTotalElements }}
        </span>
      </header>

      <section class="panel" aria-labelledby="applications-heading">
        <div class="toolbar">
          <h2 id="applications-heading">Все решения по заявкам</h2>
        </div>

        <div v-if="applicationsLoading" class="state" role="status">Загрузка заявок…</div>
        <div v-else-if="applicationsError" class="state error" role="alert">
          <p>{{ applicationsError }}</p>
          <button type="button" @click="loadApplications()">Повторить</button>
        </div>
        <div v-else-if="applications.length === 0" class="state">Заявок пока нет.</div>

        <div v-else class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Заявка</th><th>Клиент</th><th>Телефон</th><th>Цель кредита</th>
                <th>Запрошено</th><th>Одобрено</th><th>Срок</th><th>Дата решения</th><th>Статус</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="application in applications" :key="application.id">
                <td><strong>№ {{ application.id }}</strong><small>от {{ formatDate(application.createdAt) }}</small></td>
                <td>{{ application.clientFullName }}</td>
                <td>{{ application.clientPhone }}</td>
                <td>{{ application.purpose }}</td>
                <td>{{ formatMoney(application.requestedAmount) }}</td>
                <td><strong>{{ application.approvedAmount ? formatMoney(application.approvedAmount) : '—' }}</strong></td>
                <td>{{ application.termMonths ? `${application.termMonths} мес.` : '—' }}</td>
                <td>{{ formatDate(application.decidedAt) }}</td>
                <td>
                  <span class="status-badge" :class="`status-${application.status.toLowerCase()}`">
                    {{ decisionStatusLabels[application.status] || application.status }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <footer v-if="!applicationsLoading && !applicationsError && applicationsTotalPages > 1" class="pagination">
          <button type="button" :disabled="applicationsPage === 0" @click="loadApplications(applicationsPage - 1)">Назад</button>
          <span>Страница {{ applicationsPage + 1 }} из {{ applicationsTotalPages }}</span>
          <button type="button" :disabled="applicationsPage + 1 >= applicationsTotalPages" @click="loadApplications(applicationsPage + 1)">Вперёд</button>
        </footer>
      </section>
    </template>

    <template v-else>
      <header class="page-header">
        <div>
          <h1>Кредитные договоры</h1>
          <p class="subtitle">Все кредитные договоры и их актуальный статус подписания</p>
        </div>
        <span v-if="!agreementsLoading && !agreementsError" class="count">
          Всего: {{ agreementsTotalElements }}
        </span>
      </header>

      <section class="panel" aria-labelledby="agreements-heading">
        <div class="toolbar">
          <h2 id="agreements-heading">Все кредитные договоры</h2>
        </div>

        <div v-if="agreementsLoading" class="state" role="status">Загрузка договоров…</div>
        <div v-else-if="agreementsError" class="state error" role="alert">
          <p>{{ agreementsError }}</p>
          <button type="button" @click="loadAgreements()">Повторить</button>
        </div>
        <div v-else-if="agreements.length === 0" class="state">Кредитных договоров пока нет.</div>

        <div v-else class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Договор</th><th>Клиент</th><th>Телефон</th><th>Паспорт</th><th>Цель кредита</th>
                <th>Запрошено</th><th>Одобрено</th><th>Срок</th><th>Дата подписания</th><th>Статус подписи</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="agreement in agreements" :key="agreement.id">
                <td><strong>{{ agreement.agreementNumber }}</strong><small>Заявка № {{ agreement.applicationId }}</small></td>
                <td>{{ agreement.clientFullName }}</td>
                <td>{{ agreement.clientPhone }}</td>
                <td>{{ agreement.clientPassport || '—' }}</td>
                <td>{{ agreement.purpose }}</td>
                <td>{{ formatMoney(agreement.requestedAmount) }}</td>
                <td><strong>{{ formatMoney(agreement.approvedAmount) }}</strong></td>
                <td>{{ agreement.termMonths }} мес.</td>
                <td>{{ formatDate(agreement.signedAt) }}</td>
                <td>
                  <span class="status-badge" :class="`signature-${agreement.signatureStatus.toLowerCase()}`">
                    {{ signatureStatusLabels[agreement.signatureStatus] || agreement.signatureStatus }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <footer v-if="!agreementsLoading && !agreementsError && agreementsTotalPages > 1" class="pagination">
          <button type="button" :disabled="agreementsPage === 0" @click="loadAgreements(agreementsPage - 1)">Назад</button>
          <span>Страница {{ agreementsPage + 1 }} из {{ agreementsTotalPages }}</span>
          <button type="button" :disabled="agreementsPage + 1 >= agreementsTotalPages" @click="loadAgreements(agreementsPage + 1)">Вперёд</button>
        </footer>
      </section>
    </template>
  </main>
</template>

<style>
:root { font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; color: #1d2939; background: #f5f7fa; font-synthesis: none; }
* { box-sizing: border-box; }
body { margin: 0; min-width: 320px; }
button, input, select, textarea { font: inherit; }
.page { width: min(1440px, calc(100% - 32px)); margin: 0 auto; padding: 32px 0 64px; }
.page-wide { width: min(1800px, calc(100% - 32px)); }
.navigation { display: flex; gap: 6px; margin-bottom: 48px; padding: 5px; width: fit-content; border: 1px solid #e4e7ec; border-radius: 10px; background: white; }
.navigation button { padding: 9px 16px; border: 0; border-radius: 7px; color: #667085; background: transparent; cursor: pointer; font-weight: 600; }
.navigation button.active { color: #175cd3; background: #eff8ff; }
.page-header { display: flex; align-items: flex-end; justify-content: space-between; gap: 24px; margin-bottom: 32px; }
h1 { margin: 0; color: #101828; font-size: clamp(32px, 5vw, 48px); line-height: 1.1; }
.subtitle { margin: 10px 0 0; color: #667085; }
.count { padding: 7px 12px; border-radius: 999px; color: #175cd3; background: #eff8ff; font-size: 14px; font-weight: 600; white-space: nowrap; }
.status-badge { color: #344054; background: #f2f4f7; }
.status-approved { color: #067647; background: #ecfdf3; }
.status-denied { color: #b42318; background: #fef3f2; }
.status-pending { color: #b54708; background: #fffaeb; }
.signature-signed { color: #067647; background: #ecfdf3; }
.signature-unsigned { color: #b54708; background: #fffaeb; }
.panel { overflow: hidden; border: 1px solid #e4e7ec; border-radius: 14px; background: white; box-shadow: 0 8px 24px rgb(16 24 40 / 5%); }
.toolbar { display: flex; align-items: center; justify-content: space-between; gap: 20px; padding: 20px 24px; border-bottom: 1px solid #eaecf0; }
h2 { margin: 0; color: #101828; font-size: 18px; }
.filters { display: flex; gap: 10px; }
.search input { width: min(210px, 18vw); padding: 10px 14px; border: 1px solid #d0d5dd; border-radius: 8px; color: #101828; outline: none; }
.search input:focus { border-color: #84adff; box-shadow: 0 0 0 3px #d1e0ff; }
.status-badge { display: inline-block; padding: 4px 9px; border-radius: 999px; font-size: 12px; font-weight: 700; white-space: nowrap; }
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; text-align: left; }
th { padding: 12px 24px; color: #667085; background: #f9fafb; font-size: 12px; font-weight: 600; letter-spacing: .04em; text-transform: uppercase; }
td { padding: 17px 24px; border-top: 1px solid #eaecf0; color: #475467; font-size: 14px; white-space: nowrap; }
td:first-child, td strong { color: #101828; }
td small { display: block; margin-top: 4px; color: #98a2b3; }
.state { padding: 64px 24px; color: #667085; text-align: center; }
.state p { margin: 0 0 16px; }
.state button { padding: 9px 15px; border: 0; border-radius: 8px; color: white; background: #175cd3; cursor: pointer; }
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 16px; padding: 16px 24px; border-top: 1px solid #eaecf0; color: #667085; font-size: 14px; }
.pagination button { padding: 8px 13px; border: 1px solid #d0d5dd; border-radius: 8px; color: #344054; background: white; cursor: pointer; }
.pagination button:disabled { color: #98a2b3; background: #f9fafb; cursor: not-allowed; }
.error { color: #b42318; }
.application-form { overflow: visible; }
.form-section { display: grid; grid-template-columns: 230px minmax(0, 1fr); gap: 40px; padding: 28px 32px; border-bottom: 1px solid #eaecf0; }
.section-heading { display: flex; align-items: flex-start; gap: 12px; }
.section-heading > span { display: grid; flex: 0 0 30px; height: 30px; place-items: center; border-radius: 50%; color: #175cd3; background: #eff8ff; font-size: 13px; font-weight: 700; }
.section-heading h2 { margin-top: 3px; }
.section-heading p { margin: 5px 0 0; color: #667085; font-size: 13px; line-height: 1.45; }
.form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 20px; }
.form-grid-three { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.field { display: flex; flex-direction: column; gap: 7px; color: #344054; font-size: 14px; font-weight: 600; }
.field-wide { grid-column: span 2; }
.field input, .field select, .field textarea { width: 100%; min-height: 44px; padding: 10px 12px; border: 1px solid #d0d5dd; border-radius: 8px; color: #101828; background: white; outline: none; font-weight: 400; }
.field textarea { resize: vertical; }
.field input:focus, .field select:focus, .field textarea:focus { border-color: #84adff; box-shadow: 0 0 0 3px #d1e0ff; }
.field small { color: #667085; font-size: 12px; font-weight: 400; }
.field-invalid > span { color: #b42318; }
.field-invalid input, .field-invalid select, .field-invalid textarea { border-color: #f04438; background: #fffafa; }
.field-invalid input:focus, .field-invalid select:focus, .field-invalid textarea:focus { border-color: #f04438; box-shadow: 0 0 0 3px #fee4e2; }
.field .field-error { color: #b42318; font-weight: 500; }
.form-footer { display: flex; align-items: center; justify-content: flex-end; gap: 20px; padding: 24px 32px; }
.form-message { margin: 0 auto 0 0; font-size: 14px; }
.form-message strong { display: block; }
.form-message ul { margin: 6px 0 0; padding-left: 18px; }
.form-message-error { color: #b42318; }
.form-message-success { color: #067647; }
.form-actions { display: flex; gap: 12px; }
.secondary-button { padding: 10px 16px; border: 1px solid #d0d5dd; border-radius: 8px; color: #344054; background: white; cursor: pointer; font-weight: 600; }
.secondary-button:hover { background: #f9fafb; }
.secondary-button:disabled { opacity: .65; cursor: wait; }
.primary-button { padding: 11px 18px; border: 0; border-radius: 8px; color: white; background: #175cd3; cursor: pointer; font-weight: 600; }
.primary-button:hover { background: #1849a9; }
.primary-button:disabled { opacity: .65; cursor: wait; }
.decision-card { display: flex; align-items: center; justify-content: space-between; gap: 32px; margin-top: 24px; padding: 24px 28px; border: 1px solid #a6f4c5; border-radius: 14px; background: #ecfdf3; }
.decision-denied { border-color: #fecdca; background: #fef3f2; }
.decision-card h2 { margin-top: 12px; }
.decision-card p { margin: 5px 0 0; color: #667085; font-size: 14px; }
.decision-details { display: flex; gap: 36px; margin: 0; }
.decision-details div { min-width: 130px; }
.decision-details dt { margin-bottom: 5px; color: #667085; font-size: 12px; }
.decision-details dd { margin: 0; color: #101828; font-size: 15px; font-weight: 700; }
.sr-only { position: absolute; width: 1px; height: 1px; padding: 0; overflow: hidden; clip: rect(0, 0, 0, 0); white-space: nowrap; border: 0; }
@media (max-width: 900px) {
  .form-section { grid-template-columns: 1fr; gap: 24px; }
  .form-grid-three { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}
@media (max-width: 640px) {
  .page { width: min(100% - 20px, 1440px); padding: 20px 0 32px; }
  .page-wide { width: calc(100% - 20px); }
  .navigation { flex-wrap: wrap; width: 100%; margin-bottom: 32px; }
  .navigation button { flex: 1 1 40%; }
  .page-header { align-items: flex-start; }
  .toolbar { align-items: stretch; flex-direction: column; }
  .filters { flex-direction: column; }
  .search input { width: 100%; }
  .form-section { padding: 24px 18px; }
  .form-grid, .form-grid-three { grid-template-columns: 1fr; }
  .field-wide { grid-column: auto; }
  .form-footer { align-items: stretch; flex-direction: column; padding: 20px 18px; }
  .form-actions { flex-direction: column; }
  .primary-button, .secondary-button { width: 100%; }
  .decision-card, .decision-details { align-items: stretch; flex-direction: column; }
  .decision-details { gap: 16px; }
  th, td { padding-left: 16px; padding-right: 16px; }
}
</style>
