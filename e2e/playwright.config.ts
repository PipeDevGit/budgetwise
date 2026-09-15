import { defineConfig, devices } from '@playwright/test'

/**
 * Prueba E2E de la issue #18. Corre contra el docker-compose ya levantado:
 *
 *   docker compose -f infra/docker-compose.yml up --build -V
 *
 * No levanta la aplicacion por su cuenta, a proposito: lo que se prueba es el
 * sistema tal como corre en contenedores (frontend, API y Postgres reales),
 * no un servidor de desarrollo armado solo para la prueba.
 */
export default defineConfig({
  testDir: './tests',
  timeout: 60_000,
  retries: 0,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL: process.env.E2E_BASE_URL ?? 'http://localhost:5173',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    trace: 'retain-on-failure',
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
})
