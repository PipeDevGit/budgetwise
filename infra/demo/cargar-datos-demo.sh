#!/usr/bin/env bash
# Carga una cuenta de demostracion con datos que muestran las ocho funcionalidades:
# movimientos de este mes y del anterior, una categoria propia, un presupuesto
# excedido (alerta), otro sin exceder y una meta de ahorro con progreso.
#
# Las fechas son relativas al dia en que se corre: el dia de la demo la alerta y
# la comparacion con el mes pasado siguen funcionando.
#
# Uso, con el stack levantado (docker compose up):
#   bash infra/demo/cargar-datos-demo.sh
#   API_URL=http://otra-maquina:8080 bash infra/demo/cargar-datos-demo.sh
#
# Solo datos inventados: con la capa gratuita de Gemini no se usan datos reales (D-12).
set -euo pipefail

API_URL="${API_URL:-http://localhost:8080}"
CORREO="${DEMO_CORREO:-demo.$(date +%Y%m%d%H%M%S)@budgetwise.dev}"
CLAVE="${DEMO_CLAVE:-clave-demo-1234}"

MES=$(date +%Y-%m)
MES_PASADO=$(date -d "${MES}-01 -1 month" +%Y-%m)
FECHA_META=$(date -d "${MES}-01 +3 months" +%Y-%m-20)

# El dia del mes para los gastos: nunca en el futuro, asi valen aunque se corra el dia 1.
dia() { local d=$1; local hoy=$(date +%-d); [ "$d" -gt "$hoy" ] && d=$hoy; printf '%s-%02d' "$MES" "$d"; }

falla() { echo "ERROR: $*" >&2; exit 1; }

# post RUTA CUERPO [METODO] -> imprime el cuerpo; falla si el codigo no es 2xx.
llamar() {
  local metodo=${3:-POST} salida codigo
  salida=$(curl -s -w $'\n%{http_code}' -X "$metodo" "$API_URL$1" \
    -H 'Content-Type: application/json' ${TOKEN:+-H "Authorization: Bearer $TOKEN"} -d "$2") \
    || falla "no se pudo conectar con $API_URL. Esta levantado el stack?"
  codigo=${salida##*$'\n'}
  [[ $codigo == 2* ]] || falla "$metodo $1 respondio $codigo: ${salida%$'\n'*}"
  printf '%s' "${salida%$'\n'*}"
}

# Lee un campo de un JSON por stdin. Node ya esta instalado por el frontend.
campo() { node -e "let s='';process.stdin.on('data',d=>s+=d).on('end',()=>console.log(JSON.parse(s)[process.argv[1]]))" "$1"; }

echo "Creando la cuenta $CORREO ..."
TOKEN=""
llamar /api/auth/register "{\"name\":\"Cuenta Demo\",\"email\":\"$CORREO\",\"password\":\"$CLAVE\"}" >/dev/null
TOKEN=$(llamar /api/auth/login "{\"email\":\"$CORREO\",\"password\":\"$CLAVE\"}" | campo token)

CATEGORIAS=$(curl -s "$API_URL/api/categories" -H "Authorization: Bearer $TOKEN")
id_de() { printf '%s' "$CATEGORIAS" | node -e "let s='';process.stdin.on('data',d=>s+=d).on('end',()=>console.log(JSON.parse(s).find(c=>c.name===process.argv[1]).id))" "$1"; }
COMIDA=$(id_de Comida); TRANSPORTE=$(id_de Transporte); OCIO=$(id_de Ocio); OTROS=$(id_de Otros)
MASCOTAS=$(llamar /api/categories '{"name":"Mascotas"}' | campo id)

movimiento() { llamar /api/transactions "{\"amount\":$1,\"type\":\"$2\",\"date\":\"$3\",\"categoryId\":$4,\"description\":\"$5\"}" >/dev/null; }

echo "Cargando movimientos de $MES_PASADO y $MES ..."
movimiento 450000 INGRESO "${MES_PASADO}-01" "$OTROS"      "Salario"
movimiento 40000  GASTO   "${MES_PASADO}-08" "$COMIDA"     "Supermercado"
movimiento 12000  GASTO   "${MES_PASADO}-15" "$TRANSPORTE" "Bus"
movimiento 450000 INGRESO "$(dia 1)"          "$OTROS"      "Salario"
movimiento 62000  GASTO   "$(dia 5)"          "$COMIDA"     "Supermercado"
movimiento 18500  GASTO   "$(dia 10)"         "$TRANSPORTE" "Bus y taxi"
movimiento 25000  GASTO   "$(dia 12)"         "$OCIO"       "Cine"
movimiento 15000  GASTO   "$(dia 14)"         "$MASCOTAS"   "Veterinaria"

echo "Definiendo presupuestos y la meta ..."
llamar /api/budgets "{\"categoryId\":$COMIDA,\"monthlyLimit\":50000}" PUT >/dev/null
llamar /api/budgets "{\"categoryId\":$OCIO,\"monthlyLimit\":40000}" PUT >/dev/null
META=$(llamar /api/goals "{\"name\":\"Viaje\",\"targetAmount\":300000,\"targetDate\":\"$FECHA_META\"}" | campo id)
llamar "/api/goals/$META/savings" '{"savedAmount":120000}' PUT >/dev/null

echo
echo "Listo. Iniciar sesion en http://localhost:5173 con:"
echo "  correo: $CORREO"
echo "  clave:  $CLAVE"
echo
echo "Saldo esperado: 727 500,00 · Comida excedida (62 000 de 50 000) · Meta Viaje al 40 %"
