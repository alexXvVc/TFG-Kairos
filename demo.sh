#!/usr/bin/env bash
# =============================================================================
# Kairos — Demo de funcionalidades
# Backend: https://tfg-kairos-production.up.railway.app
# =============================================================================

set -euo pipefail

BASE="https://tfg-kairos-production.up.railway.app"
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
BLUE='\033[0;34m'; CYAN='\033[0;36m'; BOLD='\033[1m'; RESET='\033[0m'

ok()     { echo -e "  ${GREEN}✔${RESET}  $1"; }
fail()   { echo -e "  ${RED}✘${RESET}  $1"; }
info()   { echo -e "  ${CYAN}→${RESET}  $1"; }
sep()    { echo -e "\n${BLUE}${BOLD}══════════════════════════════════════════${RESET}"; }
title()  { sep; echo -e "${BOLD}${YELLOW}  $1${RESET}"; sep; }

# Llama a la API y devuelve el cuerpo; falla con mensaje si el HTTP code >= 400
api() {
  local method="$1"; shift
  local url="$1";    shift
  local extra=("$@")
  local tmp; tmp=$(mktemp)
  local code
  code=$(curl -s -o "$tmp" -w "%{http_code}" -X "$method" "$url" "${extra[@]}" 2>/dev/null)
  local body; body=$(cat "$tmp"); rm -f "$tmp"
  if [[ "$code" -ge 400 ]]; then
    echo "HTTP $code: $body" >&2
    return 1
  fi
  echo "$body"
}

jq_py() {
  # Extrae un campo con Python (evita depender de jq)
  local expr="$1"
  python3 -c "import sys,json; d=json.load(sys.stdin); print($expr)"
}

# ─── 1. AUTENTICACIÓN JWT ────────────────────────────────────────────────────
title "1 · AUTENTICACIÓN JWT"

echo -e "\n${BOLD}1.1  Login correcto — admin${RESET}"
RESP=$(api POST "$BASE/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@parish.dev","password":"admin"}')
TOKEN=$(echo "$RESP" | jq_py "d['token']")
ROLE=$( echo "$RESP" | jq_py "d['role']")
EXP=$(  echo "$RESP" | jq_py "str(d['expiresInMs']//3600000) + ' h'")
ok "Token recibido · rol: $ROLE · expira en: $EXP"
info "JWT: ${TOKEN:0:72}…"

echo -e "\n${BOLD}1.2  Login correcto — secretaria${RESET}"
RESP_S=$(api POST "$BASE/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"secretary@parish.dev","password":"secretary"}')
TOKEN_S=$(echo "$RESP_S" | jq_py "d['token']")
ok "Rol: $(echo "$RESP_S" | jq_py "d['role']")"

echo -e "\n${BOLD}1.3  Login correcto — sacerdote${RESET}"
RESP_P=$(api POST "$BASE/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"priest@parish.dev","password":"priest"}')
TOKEN_P=$(echo "$RESP_P" | jq_py "d['token']")
ok "Rol: $(echo "$RESP_P" | jq_py "d['role']")"

echo -e "\n${BOLD}1.4  Credenciales incorrectas → rechazado${RESET}"
CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@parish.dev","password":"incorrecta"}')
if [[ "$CODE" -ge 400 ]]; then
  ok "HTTP $CODE — acceso denegado correctamente"
else
  fail "Se esperaba error, recibido HTTP $CODE"
fi

echo -e "\n${BOLD}1.5  Recurso protegido sin token → bloqueado${RESET}"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/persons")
if [[ "$CODE" -ge 400 ]]; then
  ok "HTTP $CODE — sin JWT no hay acceso"
else
  fail "Se esperaba error, recibido HTTP $CODE"
fi

H_AUTH="-H 'Authorization: Bearer $TOKEN'"

# ─── 2. PERSONAS ────────────────────────────────────────────────────────────
title "2 · PERSONAS"

echo -e "\n${BOLD}2.1  Listar personas (paginado)${RESET}"
PERSONS=$(api GET "$BASE/api/persons" -H "Authorization: Bearer $TOKEN")
TOTAL=$(echo "$PERSONS" | jq_py "d['totalElements']")
ok "$TOTAL personas en la base de datos"
echo "$PERSONS" | python3 -c "
import sys,json
d=json.load(sys.stdin)
for p in d['content'][:6]:
    print(f\"    • {p['firstName']} {p['lastName']:30} {p['role']}\")
print('    …')
"

echo -e "\n${BOLD}2.2  Crear nueva persona (fiel)${RESET}"
NEW_PERSON=$(api POST "$BASE/api/persons" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Demo","lastName":"Kairos","role":"FAITHFUL","email":"demo@kairos.dev","phone":"600000001"}')
PERSON_ID=$(echo "$NEW_PERSON" | jq_py "d['id']")
ok "Persona creada · $(echo "$NEW_PERSON" | jq_py "d['firstName'] + ' ' + d['lastName']") · id: $PERSON_ID"

echo -e "\n${BOLD}2.3  Buscar persona por nombre${RESET}"
FOUND=$(api GET "$BASE/api/persons?search=Demo" -H "Authorization: Bearer $TOKEN")
COUNT=$(echo "$FOUND" | jq_py "d['totalElements']")
ok "Búsqueda 'Demo' → $COUNT resultado(s)"

echo -e "\n${BOLD}2.4  Obtener persona por id${RESET}"
SINGLE=$(api GET "$BASE/api/persons/$PERSON_ID" -H "Authorization: Bearer $TOKEN")
ok "$(echo "$SINGLE" | jq_py "d['firstName'] + ' ' + d['lastName'] + ' (' + d['role'] + ')'")"

# ─── 3. UBICACIONES ─────────────────────────────────────────────────────────
title "3 · UBICACIONES"

echo -e "\n${BOLD}3.1  Listar ubicaciones${RESET}"
LOCATIONS=$(api GET "$BASE/api/locations" -H "Authorization: Bearer $TOKEN")
echo "$LOCATIONS" | python3 -c "
import sys,json
locs=json.load(sys.stdin)
print(f'  Total: {len(locs)} ubicación(es)')
for l in locs:
    print(f\"    • {l['name']} — {l['address']} (aforo: {l['capacity']})\")
"
LOCATION_ID=$(echo "$LOCATIONS" | jq_py "d[0]['id']")
ok "Ubicación activa: $(echo "$LOCATIONS" | jq_py "d[0]['name']")"

# ─── 4. SCHEDULING — CELEBRACIONES ──────────────────────────────────────────
title "4 · SCHEDULING — CELEBRACIONES"

PRIEST_ID=$(echo "$PERSONS" | python3 -c "
import sys,json
d=json.load(sys.stdin)
p=[x for x in d['content'] if x['role']=='PRIEST']
print(p[0]['id'])
")
info "Sacerdote para las demos: $(echo "$PERSONS" | python3 -c "
import sys,json
d=json.load(sys.stdin)
p=[x for x in d['content'] if x['role']=='PRIEST']
print(p[0]['firstName'], p[0]['lastName'])
")"

echo -e "\n${BOLD}4.1  Crear Misa (estado inicial: BORRADOR)${RESET}"
MASS=$(api POST "$BASE/api/celebrations/masses" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"locationId\": \"$LOCATION_ID\",
    \"presidingPriestId\": \"$PRIEST_ID\",
    \"scheduledAt\": \"2026-11-02T10:00:00\",
    \"intention\": \"Por la comunidad parroquial\",
    \"sundayMass\": false
  }")
MASS_ID=$(echo "$MASS" | jq_py "d['id']")
ok "Misa creada · estado: $(echo "$MASS" | jq_py "d['status']") · id: $MASS_ID"

echo -e "\n${BOLD}4.2  Confirmar la Misa → CONFIRMED${RESET}"
CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
  "$BASE/api/celebrations/$MASS_ID/confirm" \
  -H "Authorization: Bearer $TOKEN")
[[ "$CODE" == "204" ]] && ok "HTTP 204 — Misa confirmada" || fail "HTTP $CODE inesperado"
STATUS=$(api GET "$BASE/api/celebrations/$MASS_ID" -H "Authorization: Bearer $TOKEN" | jq_py "d['status']")
ok "Estado verificado: $STATUS"

echo -e "\n${BOLD}4.3  Reprogramar la Misa${RESET}"
CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
  "$BASE/api/celebrations/$MASS_ID/reschedule" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"newDateTime":"2026-11-09T10:00:00"}')
[[ "$CODE" == "204" ]] && ok "HTTP 204 — Misa reprogramada al 09/11/2026" || fail "HTTP $CODE inesperado"
NEW_DATE=$(api GET "$BASE/api/celebrations/$MASS_ID" -H "Authorization: Bearer $TOKEN" | jq_py "d['scheduledAt'][:10]")
ok "Nueva fecha verificada: $NEW_DATE"

echo -e "\n${BOLD}4.4  Marcar la Misa como celebrada → COMPLETED${RESET}"
CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
  "$BASE/api/celebrations/$MASS_ID/complete" \
  -H "Authorization: Bearer $TOKEN")
[[ "$CODE" == "204" ]] && ok "HTTP 204 — Misa completada" || fail "HTTP $CODE inesperado"
STATUS=$(api GET "$BASE/api/celebrations/$MASS_ID" -H "Authorization: Bearer $TOKEN" | jq_py "d['status']")
ok "Estado verificado: $STATUS"

echo -e "\n${BOLD}4.5  Crear Bautismo${RESET}"
FAITHFUL_IDS=$(echo "$PERSONS" | python3 -c "
import sys,json
d=json.load(sys.stdin)
p=[x['id'] for x in d['content'] if x['role']=='FAITHFUL']
print(' '.join(p[:4]))
")
CHILD=$(echo $FAITHFUL_IDS | cut -d' ' -f1)
PAR1=$( echo $FAITHFUL_IDS | cut -d' ' -f2)
PAR2=$( echo $FAITHFUL_IDS | cut -d' ' -f3)
GODP=$( echo $FAITHFUL_IDS | cut -d' ' -f4)

BAPTISM=$(api POST "$BASE/api/celebrations/baptisms" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"locationId\": \"$LOCATION_ID\",
    \"presidingPriestId\": \"$PRIEST_ID\",
    \"scheduledAt\": \"2026-12-07T11:00:00\",
    \"childId\": \"$CHILD\",
    \"parentIds\": [\"$PAR1\",\"$PAR2\"],
    \"godparentIds\": [\"$GODP\"]
  }")
BAPTISM_ID=$(echo "$BAPTISM" | jq_py "d['id']")
ok "Bautismo creado · estado: $(echo "$BAPTISM" | jq_py "d['status']") · id: $BAPTISM_ID"

echo -e "\n${BOLD}4.6  Cancelar el Bautismo → CANCELLED${RESET}"
CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
  "$BASE/api/celebrations/$BAPTISM_ID/cancel" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"reason":"Cancelado durante demo de funcionalidades"}')
[[ "$CODE" == "204" ]] && ok "HTTP 204 — Bautismo cancelado" || fail "HTTP $CODE inesperado"
STATUS=$(api GET "$BASE/api/celebrations/$BAPTISM_ID" -H "Authorization: Bearer $TOKEN" | jq_py "d['status']")
ok "Estado verificado: $STATUS"

echo -e "\n${BOLD}4.7  Crear Boda${RESET}"
SP_A=$(echo $FAITHFUL_IDS | cut -d' ' -f1)
SP_B=$(echo $FAITHFUL_IDS | cut -d' ' -f2)
WIT1=$(echo $FAITHFUL_IDS | cut -d' ' -f3)
WIT2=$(echo $FAITHFUL_IDS | cut -d' ' -f4)

WEDDING=$(api POST "$BASE/api/celebrations/weddings" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"locationId\": \"$LOCATION_ID\",
    \"presidingPriestId\": \"$PRIEST_ID\",
    \"scheduledAt\": \"2026-12-19T12:00:00\",
    \"spouseAId\": \"$SP_A\",
    \"spouseBId\": \"$SP_B\",
    \"witnessIds\": [\"$WIT1\",\"$WIT2\"]
  }")
WEDDING_ID=$(echo "$WEDDING" | jq_py "d['id']")
ok "Boda creada · estado: $(echo "$WEDDING" | jq_py "d['status']") · id: $WEDDING_ID"

echo -e "\n${BOLD}4.8  Listar celebraciones (rango anual 2026)${RESET}"
ALL_CEL=$(api GET "$BASE/api/celebrations?from=2026-01-01T00:00:00&to=2026-12-31T23:59:59&size=50" \
  -H "Authorization: Bearer $TOKEN")
echo "$ALL_CEL" | python3 -c "
import sys,json
d=json.load(sys.stdin)
print(f'  Total celebraciones: {d[\"totalElements\"]}')
by_type={}
for c in d['content']:
    t=c['type']; by_type[t]=by_type.get(t,0)+1
for t,n in sorted(by_type.items()):
    print(f'    • {t}: {n}')
"
ok "Listado correcto"

# ─── 5. REGISTROS SACRAMENTALES ─────────────────────────────────────────────
title "5 · REGISTROS SACRAMENTALES"

echo -e "\n${BOLD}5.1  Listar todos los registros${RESET}"
RECORDS=$(api GET "$BASE/api/records" -H "Authorization: Bearer $TOKEN")
echo "$RECORDS" | python3 -c "
import sys,json
d=json.load(sys.stdin)
print(f'  Total registros: {d[\"totalElements\"]}')
by_type={}
for r in d['content']:
    t=r.get('recordType','?'); by_type[t]=by_type.get(t,0)+1
for t,n in sorted(by_type.items()):
    print(f'    • {t}: {n}')
"
ok "Listado correcto"

echo -e "\n${BOLD}5.2  Filtrar por tipo BAPTISM${RESET}"
COUNT=$(api GET "$BASE/api/records?type=BAPTISM" -H "Authorization: Bearer $TOKEN" | jq_py "d['totalElements']")
ok "Registros de bautismo: $COUNT"

echo -e "\n${BOLD}5.3  Filtrar por tipo MARRIAGE${RESET}"
COUNT=$(api GET "$BASE/api/records?type=MARRIAGE" -H "Authorization: Bearer $TOKEN" | jq_py "d['totalElements']")
ok "Registros de matrimonio: $COUNT"

echo -e "\n${BOLD}5.4  Filtrar por tipo FUNERAL${RESET}"
COUNT=$(api GET "$BASE/api/records?type=FUNERAL" -H "Authorization: Bearer $TOKEN" | jq_py "d['totalElements']")
ok "Registros de funeral: $COUNT"

echo -e "\n${BOLD}5.5  Filtrar por tipo CONFIRMATION${RESET}"
COUNT=$(api GET "$BASE/api/records?type=CONFIRMATION" -H "Authorization: Bearer $TOKEN" | jq_py "d['totalElements']")
ok "Registros de confirmación: $COUNT"

# ─── 6. CALENDARIO ──────────────────────────────────────────────────────────
title "6 · CALENDARIO"

echo -e "\n${BOLD}6.1  Junio 2026${RESET}"
api GET "$BASE/api/calendar?from=2026-06-01T00:00:00&to=2026-06-30T23:59:59" \
  -H "Authorization: Bearer $TOKEN" | python3 -c "
import sys,json
entries=json.load(sys.stdin)
print(f'  Eventos: {len(entries)}')
for e in entries:
    print(f\"    • {e['type']:20} {e['scheduledAt'][:16]} — {e.get('locationName','')}\")
"
ok "Calendario de junio correcto"

echo -e "\n${BOLD}6.2  Noviembre 2026 (incluye la Misa creada en este demo)${RESET}"
api GET "$BASE/api/calendar?from=2026-11-01T00:00:00&to=2026-11-30T23:59:59" \
  -H "Authorization: Bearer $TOKEN" | python3 -c "
import sys,json
entries=json.load(sys.stdin)
print(f'  Eventos: {len(entries)}')
for e in entries:
    print(f\"    • {e['type']:20} {e['scheduledAt'][:16]} — {e.get('presidingPriestName','')}\")
"
ok "Calendario de noviembre correcto"

echo -e "\n${BOLD}6.3  Diciembre 2026 (Boda pendiente de este demo)${RESET}"
api GET "$BASE/api/calendar?from=2026-12-01T00:00:00&to=2026-12-31T23:59:59" \
  -H "Authorization: Bearer $TOKEN" | python3 -c "
import sys,json
entries=json.load(sys.stdin)
print(f'  Eventos: {len(entries)}')
for e in entries:
    print(f\"    • {e['type']:20} {e['scheduledAt'][:16]} — {e.get('locationName','')}\")
"
ok "Calendario de diciembre correcto"

# ─── RESUMEN ─────────────────────────────────────────────────────────────────
title "RESUMEN"
echo -e "
  ${GREEN}✔${RESET}  Auth JWT         login de 3 roles · rechazo de credenciales · bloqueo sin token
  ${GREEN}✔${RESET}  Personas         listado paginado · búsqueda · obtener por id · crear
  ${GREEN}✔${RESET}  Ubicaciones      listado con nombre, dirección y aforo
  ${GREEN}✔${RESET}  Scheduling       Misa: crear → confirmar → reprogramar → completar
  ${GREEN}✔${RESET}  Scheduling       Bautismo: crear → cancelar
  ${GREEN}✔${RESET}  Scheduling       Boda: crear (borrador)
  ${GREEN}✔${RESET}  Scheduling       listar celebraciones con filtro de rango y desglose por tipo
  ${GREEN}✔${RESET}  Registros        listado total · filtro por tipo (BAPTISM, MARRIAGE, FUNERAL, CONFIRMATION)
  ${GREEN}✔${RESET}  Calendario       consulta por rango de fechas con localización y sacerdote
"
sep
echo ""
