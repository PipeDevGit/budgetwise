import { expect, test, type Locator } from '@playwright/test'

/**
 * El unico flujo E2E del proyecto (issue #18), de punta a punta contra la
 * aplicacion real: registrarse, iniciar sesion, crear un gasto y ver el saldo
 * actualizado. Cada corrida usa un correo nuevo, asi no depende de lo que haya
 * dejado la anterior en la base.
 */

/**
 * El saldo se muestra con formato es-CR ("92 499,75"). El separador de miles
 * puede ser un espacio fino, asi que se limpia antes de comparar como numero:
 * comparar el texto exacto rompe la prueba si cambia la configuracion regional.
 */
async function montoDe(locator: Locator): Promise<number> {
  const texto = (await locator.textContent()) ?? ''
  const limpio = texto.replace(/[\s  ]/g, '').replace('−', '-').replace(',', '.')
  return Number(limpio)
}

test('registrarse, iniciar sesion, crear un gasto y ver el saldo actualizado', async ({ page }) => {
  const correo = `e2e.${Date.now()}@budgetwise.dev`
  const clave = 'clave-e2e-1234'
  const saldo = page.locator('.saldo')

  await test.step('registrarse', async () => {
    await page.goto('/')
    await page.getByRole('button', { name: 'Registrate' }).click()
    await page.getByLabel('Nombre').fill('Prueba E2E')
    await page.getByLabel('Correo').fill(correo)
    await page.getByLabel('Contrasena').fill(clave)
    await page.getByRole('button', { name: 'Crear cuenta' }).click()
    await expect(page.getByText('Saldo disponible')).toBeVisible()
  })

  await test.step('cerrar sesion e iniciar sesion con la cuenta nueva', async () => {
    await page.getByRole('button', { name: 'Cerrar sesion' }).click()
    await page.getByLabel('Correo').fill(correo)
    await page.getByLabel('Contrasena').fill(clave)
    await page.getByRole('button', { name: 'Entrar' }).click()
    await expect(page.getByText('Saldo disponible')).toBeVisible()
    await expect.poll(() => montoDe(saldo)).toBe(0)
  })

  await test.step('registrar un ingreso', async () => {
    await page.getByLabel('Tipo').selectOption('INGRESO')
    await page.getByLabel('Monto').fill('100000')
    await page.getByLabel('Categoria').selectOption({ label: 'Otros' })
    await page.getByLabel('Descripcion (opcional)').fill('Salario')
    await page.getByRole('button', { name: 'Agregar' }).click()
    await expect.poll(() => montoDe(saldo)).toBe(100000)
  })

  await test.step('crear un gasto y ver el saldo actualizado', async () => {
    await page.getByLabel('Tipo').selectOption('GASTO')
    await page.getByLabel('Monto').fill('7500.25')
    await page.getByLabel('Categoria').selectOption({ label: 'Comida' })
    await page.getByLabel('Descripcion (opcional)').fill('Supermercado')
    await page.getByRole('button', { name: 'Agregar' }).click()

    await expect.poll(() => montoDe(saldo)).toBe(92499.75)
    await expect(page.getByRole('row', { name: /Supermercado/ })).toContainText('Comida')
  })

  // La captura final es la que va a la filmina de resultados (#21).
  await page.screenshot({ path: 'test-results/flujo-principal.png', fullPage: true })
})
