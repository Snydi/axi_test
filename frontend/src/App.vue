<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'

function pageFromHash() {
  if (window.location.hash === '#applications') return 'applications'
  if (window.location.hash === '#agreements') return 'agreements'
  return 'clients'
}

const activePage = ref(pageFromHash())

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

    <template v-if="activePage === 'clients'">
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
button, input { font: inherit; }
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
.sr-only { position: absolute; width: 1px; height: 1px; padding: 0; overflow: hidden; clip: rect(0, 0, 0, 0); white-space: nowrap; border: 0; }
@media (max-width: 640px) {
  .page { width: min(100% - 20px, 1440px); padding: 20px 0 32px; }
  .page-wide { width: calc(100% - 20px); }
  .navigation { width: 100%; margin-bottom: 32px; }
  .navigation button { flex: 1; }
  .page-header { align-items: flex-start; }
  .toolbar { align-items: stretch; flex-direction: column; }
  .filters { flex-direction: column; }
  .search input { width: 100%; }
  th, td { padding-left: 16px; padding-right: 16px; }
}
</style>
