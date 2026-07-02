#!/usr/bin/env bash

BASE_URL="http://localhost:8081/api/employees"
TMP_FILE="$(mktemp)"
trap 'rm -f "$TMP_FILE"' EXIT

create_employee() {
  local first_name="$1" last_name="$2" email="$3" job_title="$4" hire_date="$5" department_id="$6"

  printf '{"firstName":"%s","lastName":"%s","email":"%s","jobTitle":"%s","hireDate":"%s","departmentId":%s}' \
    "$first_name" "$last_name" "$email" "$job_title" "$hire_date" "$department_id" > "$TMP_FILE"

  local response http_code body
  response=$(curl -s -w '\n%{http_code}' -X POST "$BASE_URL" \
    -H "Content-Type: application/json; charset=utf-8" \
    --data-binary @"$TMP_FILE")
  http_code=$(echo "$response" | tail -n1)
  body=$(echo "$response" | sed '$d')

  case "$http_code" in
    201)
      echo "[OK]      $first_name $last_name ($email)"
      ;;
    400)
      echo "[SKIP]    $first_name $last_name ($email) - $body"
      ;;
    *)
      echo "[FAIL $http_code] $first_name $last_name ($email) - $body"
      ;;
  esac
}

# --- Managers ---
create_employee "Karim"   "Bensalem"  "karim.bensalem@leavemanagement.com"   "Manager RH"            "2023-02-01" 2
create_employee "Sonia"   "Trabelsi"  "sonia.trabelsi@leavemanagement.com"   "Manager IT"            "2022-09-15" 3
create_employee "Walid"   "Hammami"   "walid.hammami@leavemanagement.com"    "Manager Finance"       "2021-06-10" 4
create_employee "Lamia"   "Cherif"    "lamia.cherif@leavemanagement.com"     "Manager Marketing"     "2023-04-20" 5

# --- Employees ---
create_employee "Yassine" "Gharbi"    "yassine.gharbi@leavemanagement.com"   "Développeur Backend"   "2024-01-10" 3
create_employee "Amira"   "Belhadj"   "amira.belhadj@leavemanagement.com"    "Développeuse Frontend" "2024-02-15" 3
create_employee "Anis"    "Khaldi"    "anis.khaldi@leavemanagement.com"      "Chargé RH"             "2024-01-20" 2
create_employee "Salma"   "Brahmi"    "salma.brahmi@leavemanagement.com"     "Chargée Marketing"     "2024-05-05" 5
create_employee "Mehdi"   "Saidi"     "mehdi.saidi@leavemanagement.com"      "Développeur Backend"   "2024-06-01" 3
