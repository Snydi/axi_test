<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'

const clients = ref([])
const loading = ref(true)
const error = ref('')
const nameQuery = ref('')
const phoneQuery = ref('')
const passportQuery = ref('')
const page = ref(0)
const pageSize = 25
const totalElements = ref(0)
const totalPages = ref(0)
let searchTimer
let activeRequest

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

function currentEmployment(client) {
  return client.employments?.find((employment) => !employment.employedTo) ?? client.employments?.[0]
}

async function loadClients(targetPage = page.value) {
  activeRequest?.abort()
  const request = new AbortController()
  activeRequest = request
  loading.value = true
  error.value = ''

  try {
    const params = new URLSearchParams({
      page: targetPage.toString(),
      size: pageSize.toString(),
    })
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
    page.value = result.page
    totalElements.value = result.totalElements
    totalPages.value = result.totalPages
  } catch (requestError) {
    if (requestError.name === 'AbortError') return
    error.value = 'Не удалось загрузить список клиентов. Попробуйте ещё раз.'
    console.error(requestError)
  } finally {
    if (activeRequest === request) loading.value = false
  }
}

watch([nameQuery, phoneQuery, passportQuery], () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadClients(0), 300)
})

onMounted(loadClients)
onBeforeUnmount(() => {
  clearTimeout(searchTimer)
  activeRequest?.abort()
})
</script>

<template>
  <main class="page">
    <header class="page-header">
      <div>
        <h1>Клиенты</h1>
      </div>
      <span v-if="!loading && !error" class="count">Всего: {{ totalElements }}</span>
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

      <div v-if="loading" class="state" role="status">Загрузка клиентов…</div>
      <div v-else-if="error" class="state error" role="alert">
        <p>{{ error }}</p>
        <button type="button" @click="loadClients">Повторить</button>
      </div>
      <div v-else-if="clients.length === 0" class="state">
        {{ nameQuery || phoneQuery || passportQuery ? 'По вашему запросу клиенты не найдены.' : 'Клиенты ещё не добавлены.' }}
      </div>

      <div v-else class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>ФИО</th>
              <th>Телефон</th>
              <th>Паспорт</th>
              <th>Пол</th>
              <th>Семейное положение</th>
              <th>Должность</th>
              <th>Организация</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="client in clients" :key="client.id">
              <td>
                <strong>{{ client.fullName }}</strong>
                <small>#{{ client.id }}</small>
              </td>
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

      <footer v-if="!loading && !error && totalPages > 1" class="pagination">
        <button type="button" :disabled="page === 0" @click="loadClients(page - 1)">Назад</button>
        <span>Страница {{ page + 1 }} из {{ totalPages }}</span>
        <button type="button" :disabled="page + 1 >= totalPages" @click="loadClients(page + 1)">Вперёд</button>
      </footer>
    </section>
  </main>
</template>

<style>
:root {
  font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  color: #1d2939;
  background: #f5f7fa;
  font-synthesis: none;
}

* { box-sizing: border-box; }
body { margin: 0; min-width: 320px; }
button, input { font: inherit; }

.page { width: min(1440px, calc(100% - 32px)); margin: 0 auto; padding: 64px 0; }
.page-header { display: flex; align-items: flex-end; justify-content: space-between; gap: 24px; margin-bottom: 32px; }
.eyebrow { margin: 0 0 8px; color: #175cd3; font-size: 13px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }
h1 { margin: 0; color: #101828; font-size: clamp(32px, 5vw, 48px); line-height: 1.1; }
.subtitle { margin: 10px 0 0; color: #667085; }
.count { padding: 7px 12px; border-radius: 999px; color: #175cd3; background: #eff8ff; font-size: 14px; font-weight: 600; white-space: nowrap; }
.panel { overflow: hidden; border: 1px solid #e4e7ec; border-radius: 14px; background: white; box-shadow: 0 8px 24px rgb(16 24 40 / 5%); }
.toolbar { display: flex; align-items: center; justify-content: space-between; gap: 20px; padding: 20px 24px; border-bottom: 1px solid #eaecf0; }
h2 { margin: 0; color: #101828; font-size: 18px; }
.filters { display: flex; gap: 10px; }
.search input { width: min(210px, 18vw); padding: 10px 14px; border: 1px solid #d0d5dd; border-radius: 8px; color: #101828; outline: none; }
.search input:focus { border-color: #84adff; box-shadow: 0 0 0 3px #d1e0ff; }
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; text-align: left; }
th { padding: 12px 24px; color: #667085; background: #f9fafb; font-size: 12px; font-weight: 600; letter-spacing: .04em; text-transform: uppercase; }
td { padding: 17px 24px; border-top: 1px solid #eaecf0; color: #475467; font-size: 14px; white-space: nowrap; }
td:first-child { color: #101828; }
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
  .page { width: min(100% - 20px, 1440px); padding: 32px 0; }
  .page-header { align-items: flex-start; }
  .toolbar { align-items: stretch; flex-direction: column; }
  .filters { flex-direction: column; }
  .search input { width: 100%; }
  th, td { padding-left: 16px; padding-right: 16px; }
}
</style>
