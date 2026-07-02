#!/usr/bin/env bash
set -e

BASE_URL="http://localhost:8081/api/employees"

create_employee() {
  curl -s -X POST "$BASE_URL" \
    -H "Content-Type: application/json" \
    -d "{
      \"firstName\": \"$1\",
      \"lastName\": \"$2\",
      \"email\": \"$3\",
      \"jobTitle\": \"$4\",
      \"hireDate\": \"$5\",
      \"departmentId\": $6
    }"
  echo
}

# --- Managers ---
create_employee "Karim"   "Bensalem"  "karim.bensalem@leavemanagement.com"   "Manager RH"          "2023-02-01" 2
create_employee "Sonia"   "Trabelsi"  "sonia.trabelsi@leavemanagement.com"   "Manager IT"          "2022-09-15" 3
create_employee "Walid"   "Hammami"   "walid.hammami@leavemanagement.com"    "Manager Finance"     "2021-06-10" 4
create_employee "Lamia"   "Cherif"    "lamia.cherif@leavemanagement.com"     "Manager Marketing"   "2023-04-20" 5

# --- Employees ---
create_employee "Yassine" "Gharbi"    "yassine.gharbi@leavemanagement.com"   "Développeur Backend" "2024-01-10" 3
create_employee "Amira"   "Belhadj"   "amira.belhadj@leavemanagement.com"    "Développeuse Frontend" "2024-02-15" 3
create_employee "Nour"    "Mejri"     "nour.mejri@leavemanagement.com"       "Comptable"           "2024-03-01" 4
create_employee "Anis"    "Khaldi"    "anis.khaldi@leavemanagement.com"      "Chargé RH"           "2024-01-20" 2
create_employee "Salma"   "Brahmi"    "salma.brahmi@leavemanagement.com"     "Chargée Marketing"   "2024-05-05" 5
create_employee "Mehdi"   "Saidi"     "mehdi.saidi@leavemanagement.com"      "Développeur Backend" "2024-06-01" 3
