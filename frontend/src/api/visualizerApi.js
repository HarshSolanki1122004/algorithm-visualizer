/**
 * Thin client for the Spring Boot engine. The frontend contains no algorithm
 * logic — it sends an array, the backend returns the full step trace, and we
 * play it back.
 *
 * The base URL can be overridden with the `VITE_API_BASE` env var; it defaults
 * to the local backend, whose CORS config already allows the Vite dev origin.
 */
const API_BASE =
  import.meta.env?.VITE_API_BASE ?? 'http://localhost:8080/api/visualize'

async function postJson(path, body) {
  let response
  try {
    response = await fetch(`${API_BASE}${path}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    })
  } catch {
    throw new Error('Could not reach the backend — is it running on :8080?')
  }

  const data = await response.json().catch(() => null)
  if (!response.ok) {
    // The backend's GlobalExceptionHandler returns { message, ... } on 400.
    throw new Error(data?.message ?? `Request failed (${response.status})`)
  }
  return data
}

/** POST /sort — returns the full sorting trace. */
export function runSort(algorithm, array) {
  return postJson('/sort', { algorithm, array })
}

/** POST /search — returns the full searching trace. */
export function runSearch(algorithm, array, target) {
  return postJson('/search', { algorithm, array, target })
}

/** GET /algorithms — the supported algorithms with their Big-O metadata. */
export async function fetchAlgorithms() {
  let response
  try {
    response = await fetch(`${API_BASE}/algorithms`)
  } catch {
    throw new Error('Could not reach the backend — is it running on :8080?')
  }
  if (!response.ok) throw new Error(`Could not load algorithms (${response.status})`)
  return response.json()
}
