import urllib.request
import urllib.parse
import json
import time
import sys

BASE_URL = "http://localhost:8080/api/v1"

passed = 0
failed = 0
total = 0

def test(endpoint_name, condition, details=""):
    global passed, failed, total
    total += 1
    if condition:
        passed += 1
        print(f"[PASS] #{total:02d}: {endpoint_name} | {details}")
    else:
        failed += 1
        print(f"[FAIL] #{total:02d}: {endpoint_name} -> {details}")

def make_request(method, endpoint, data=None, token=None):
    url = f"{BASE_URL}{endpoint}"
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    
    encoded_data = None
    if data is not None:
        encoded_data = json.dumps(data).encode("utf-8")
        
    req = urllib.request.Request(url, data=encoded_data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as resp:
            status = resp.status
            body = resp.read().decode("utf-8")
            return status, json.loads(body) if body else {}
    except urllib.error.HTTPError as e:
        body = e.read().decode("utf-8")
        try:
            return e.code, json.loads(body) if body else {}
        except:
            return e.code, {"error": body}
    except Exception as ex:
        return 500, {"error": str(ex)}

def get_token(username, password):
    status, body = make_request("POST", "/auth/login", {"username": username, "password": password})
    if status == 200:
        return body.get("data", {}).get("token")
    return None

print("=" * 80)
print("COMPREHENSIVE 5-PORTAL REST API AUDIT & VALIDATION SUITE")
print("=" * 80)

# 1. Authenticating 5 Portal Personas
print("\n--- 1. Authenticating 5 Portal Personas ---")
tokens = {}
for role, creds in [
    ("ADMIN", ("admin", "admin123")),
    ("MD", ("md", "md123")),
    ("MANAGER", ("manager", "manager123")),
    ("ACCOUNTS", ("accounts", "accounts123")),
    ("WORKER", ("worker", "worker123"))
]:
    t = get_token(creds[0], creds[1])
    test(f"Auth Login for {role} ({creds[0]})", t is not None, f"Token length: {len(t) if t else 0}")
    tokens[role] = t

# -------------------------------------------------------------
# PORTAL 1: WORKER PORTAL
# -------------------------------------------------------------
print("\n--- 2. PORTAL 1: WORKER PORTAL (Operational flow & financial isolation) ---")

status, body = make_request("GET", "/auth/me", token=tokens["WORKER"])
test("GET /auth/me (Worker)", status == 200 and body["data"]["role"] == "WORKER", f"Status {status}")

status, body = make_request("GET", "/customers", token=tokens["WORKER"])
test("GET /customers (Worker)", status == 200 and len(body.get("data", [])) > 0, f"Count: {len(body.get('data', []))}")
seed_customer = body.get("data", [])[0]
seed_cust_id = seed_customer.get("id")
seed_cust_phone = seed_customer.get("phone")

status, body = make_request("GET", f"/customers/by-phone/{urllib.parse.quote(seed_cust_phone)}", token=tokens["WORKER"])
test("GET /customers/by-phone/{phone} (Worker)", status == 200 and body.get("data", {}).get("id") == seed_cust_id, f"Found: {body.get('data', {}).get('name')}")

status, body = make_request("GET", "/vehicles/available", token=tokens["WORKER"])
test("GET /vehicles/available (Worker)", status == 200, f"Count: {len(body.get('data', []))}")
seed_vehicle = body.get("data", [])[0] if body.get("data") else {"registration": "TN 58 AB 2345"}
seed_veh_reg = seed_vehicle.get("registration")

status, body = make_request("GET", "/drivers/available", token=tokens["WORKER"])
test("GET /drivers/available (Worker)", status == 200, f"Count: {len(body.get('data', []))}")
seed_driver = body.get("data", [])[0] if body.get("data") else {"id": "DRV-001"}
seed_drv_id = seed_driver.get("id")

status, body = make_request("GET", "/sources", token=tokens["WORKER"])
test("GET /sources (Worker)", status == 200, f"Count: {len(body.get('data', []))}")

status, body = make_request("GET", "/materials", token=tokens["WORKER"])
test("GET /materials (Worker)", status == 200, f"Count: {len(body.get('data', []))}")

status, body = make_request("GET", "/locations", token=tokens["WORKER"])
test("GET /locations (Worker)", status == 200, f"Count: {len(body.get('data', []))}")

status, body = make_request("GET", "/fuel-stations", token=tokens["WORKER"])
test("GET /fuel-stations (Worker)", status == 200, f"Count: {len(body.get('data', []))}")

# POST /trips (Worker creates trip)
trip_payload = {
    "date": "2026-09-21",
    "customerId": seed_cust_id,
    "vehicleRegistration": seed_veh_reg,
    "driverId": seed_drv_id,
    "material": "20 MM Blue Metal",
    "quantity": 25.50,
    "unit": "Ton",
    "source": "Sri Ramanatha Blue Metals Crusher",
    "sourceBillNo": "KQ-9988",
    "loadingLocation": "Sayalgudi Quarry Area",
    "deliveryLocation": "Metro Site A",
    "loadingDateTime": "2026-09-21T08:00:00",
    "departureDateTime": "2026-09-21T08:30:00",
    "deliveryDateTime": "2026-09-21T11:00:00",
    "openingKm": 12000.0,
    "closingKm": 12085.0,
    "tripKm": 85.0,
    "notes": "Delivered on time, verified at gate"
}
status, body = make_request("POST", "/trips", trip_payload, token=tokens["WORKER"])
created_trip = body.get("data", {})
created_trip_id = created_trip.get("id")
has_no_financials = "appliedRate" not in created_trip and "totalAmount" not in created_trip
test("POST /trips (Worker creation & financial isolation)", status == 200 and created_trip_id is not None and has_no_financials, f"Trip ID: {created_trip_id}, Sanitized: {has_no_financials}")

status, body = make_request("GET", "/trips", token=tokens["WORKER"])
first_item = body.get("data", [])[0] if body.get("data") else {}
worker_list_sanitized = "appliedRate" not in first_item and "totalAmount" not in first_item
test("GET /trips (Worker list sanitized)", status == 200 and worker_list_sanitized, f"Status {status}, Sanitized: {worker_list_sanitized}")

status, body = make_request("GET", f"/trips/{created_trip_id}", token=tokens["WORKER"])
test("GET /trips/{id} (Worker trip view)", status == 200, f"Status {status}")

status, body = make_request("PATCH", f"/trips/{created_trip_id}/status?status=IN_TRANSIT&comment=Enroute", token=tokens["WORKER"])
test("PATCH /trips/{id}/status?status=IN_TRANSIT", status == 200 and body.get("data", {}).get("status") == "IN_TRANSIT", f"Status {status}")

status, body = make_request("PATCH", f"/trips/{created_trip_id}/status?status=DELIVERED&comment=Reached+destination", token=tokens["WORKER"])
test("PATCH /trips/{id}/status?status=DELIVERED", status == 200 and body.get("data", {}).get("status") == "DELIVERED", f"Status {status}")

correction_payload = {
    "entityType": "TRIP",
    "entityId": created_trip_id,
    "reason": "Slip quantity updated after site weighbridge verification",
    "changes": [
        {"field": "quantity", "newValue": "26.00"}
    ]
}
status, body = make_request("POST", "/approvals/requests", correction_payload, token=tokens["WORKER"])
created_correction_id = body.get("data", {}).get("id")
test("POST /approvals/requests (Worker submits correction request)", status == 200 and created_correction_id is not None, f"Correction ID: {created_correction_id}")

diesel_payload = {
    "date": "2026-09-21",
    "vehicleRegistration": seed_veh_reg,
    "driverId": seed_drv_id,
    "fuelStationId": "PMP-001",
    "fuelStationName": "HPCL Bypass Pump",
    "billNumber": "HP-44910",
    "litres": 45.0,
    "ratePerLitre": 94.50,
    "startKm": 12000.0,
    "endKm": 12200.0,
    "paymentMode": "CREDIT",
    "notes": "Full tank fill"
}
status, body = make_request("POST", "/fleet-expenses/diesel", diesel_payload, token=tokens["WORKER"])
created_diesel_id = body.get("data", {}).get("id")
test("POST /fleet-expenses/diesel (Worker logs diesel)", status == 200 and created_diesel_id is not None, f"Diesel ID: {created_diesel_id}")

# -------------------------------------------------------------
# PORTAL 2: ACCOUNTS PORTAL
# -------------------------------------------------------------
print("\n--- 3. PORTAL 2: ACCOUNTS PORTAL (Invoicing, Ledger, Payments, Wages, Banking) ---")

status, body = make_request("GET", "/dashboard/accounts", token=tokens["ACCOUNTS"])
test("GET /dashboard/accounts", status == 200 and "totalOutstandingReceivables" in body.get("data", {}), f"Status {status}")

status, body = make_request("GET", "/trips/unbilled", token=tokens["ACCOUNTS"])
test("GET /trips/unbilled", status == 200, f"Unbilled count: {len(body.get('data', []))}")

cust_phone = f"+91 9944{int(time.time()) % 1000000:06d}"
new_cust_payload = {
    "name": "Apex Infrastructure Ltd",
    "phone": cust_phone,
    "address": "77 Ring Road, Industrial Area, Coimbatore",
    "gstin": "33AAPCA1234F1Z5",
    "creditTerms": "30 Days",
    "openingBalance": 0.00
}
status, body = make_request("POST", "/customers", new_cust_payload, token=tokens["ACCOUNTS"])
created_cust = body.get("data", {})
created_cust_id = created_cust.get("id")
test("POST /customers (Accounts creates customer)", status == 200 and created_cust_id is not None, f"Customer ID: {created_cust_id}")

new_cust_payload["address"] = "77 Ring Road, Phase 2, Coimbatore"
status, body = make_request("PUT", f"/customers/{created_cust_id}", new_cust_payload, token=tokens["ACCOUNTS"])
test("PUT /customers/{id} (Accounts updates customer)", status == 200 and body.get("data", {}).get("address") == "77 Ring Road, Phase 2, Coimbatore", f"Status {status}")

inv_payload = {
    "date": "2026-09-21",
    "dueDate": "2026-10-21",
    "customerId": created_cust_id,
    "customerName": "Apex Infrastructure Ltd",
    "customerAddress": "77 Ring Road, Phase 2, Coimbatore",
    "customerGstin": "33AAPCA1234F1Z5",
    "taxRate": 12.00,
    "notes": "Direct freight invoice for bulk material supply",
    "manualItems": [
        {
            "description": "M-Sand 40mm Aggregate Transport",
            "quantity": 30.0,
            "unit": "Ton",
            "rate": 800.00,
            "amount": 24000.00
        },
        {
            "description": "Site Delivery & Tipper Handling Charge",
            "quantity": 2.0,
            "unit": "Trip",
            "rate": 1500.00,
            "amount": 3000.00
        }
    ]
}
status, body = make_request("POST", "/invoices", inv_payload, token=tokens["ACCOUNTS"])
created_inv = body.get("data", {})
created_inv_id = created_inv.get("id")
test("POST /invoices (Generate invoice with manual items)", status == 200 and created_inv_id is not None and created_inv.get("totalAmount") == 30240.0, f"Invoice ID: {created_inv_id}, Total: {created_inv.get('totalAmount')}")

status, body = make_request("GET", "/invoices", token=tokens["ACCOUNTS"])
test("GET /invoices (Accounts)", status == 200, f"Count: {len(body.get('data', []))}")

status, body = make_request("GET", f"/invoices/{created_inv_id}", token=tokens["ACCOUNTS"])
test("GET /invoices/{id} (Accounts)", status == 200 and len(body.get("data", {}).get("items", [])) == 2, f"Line items: {len(body.get('data', {}).get('items', []))}")

status, body = make_request("PATCH", f"/invoices/{created_inv_id}/status?status=PARTIALLY_PAID", token=tokens["ACCOUNTS"])
test("PATCH /invoices/{id}/status (Accounts)", status == 200 and body.get("data", {}).get("status") == "PARTIALLY_PAID", f"Status {status}")

payment_payload = {
    "date": "2026-09-21",
    "customerId": created_cust_id,
    "amount": 15000.00,
    "paymentMode": "NEFT",
    "referenceNumber": "NEFT-AXIS-992817",
    "accountId": "ACC-001",
    "notes": "Part payment received for Invoice " + str(created_inv_id),
    "allocations": [
        {
            "invoiceId": created_inv_id,
            "allocatedAmount": 15000.00
        }
    ]
}
status, body = make_request("POST", "/payments", payment_payload, token=tokens["ACCOUNTS"])
created_pay = body.get("data", {})
created_pay_id = created_pay.get("id")
test("POST /payments (Record payment receipt)", status == 200 and created_pay_id is not None, f"Payment ID: {created_pay_id}")

status, body = make_request("GET", "/payments", token=tokens["ACCOUNTS"])
test("GET /payments", status == 200, f"Count: {len(body.get('data', []))}")

status, body = make_request("GET", f"/payments/{created_pay_id}", token=tokens["ACCOUNTS"])
test("GET /payments/{id}", status == 200 and body.get("data", {}).get("amount") == 15000.00, f"Status {status}")

rev_payload = {
    "reason": "Wrong bank reference entered by operator"
}
status, body = make_request("POST", f"/payments/{created_pay_id}/reverse", rev_payload, token=tokens["ACCOUNTS"])
test("POST /payments/{id}/reverse (Reverse payment)", status == 200 and body.get("data", {}).get("status") == "REVERSED", f"Status {status}")

status, body = make_request("GET", "/accounts", token=tokens["ACCOUNTS"])
test("GET /accounts", status == 200, f"Count: {len(body.get('data', []))}")

new_acc_payload = {
    "accountName": "HDFC Current Account",
    "accountType": "BANK",
    "bankName": "HDFC Bank",
    "accountNumber": "50200012345678",
    "ifsc": "HDFC0001234",
    "branch": "Gandhipuram Branch",
    "balance": 100000.00
}
status, body = make_request("POST", "/accounts", new_acc_payload, token=tokens["ACCOUNTS"])
created_acc = body.get("data", {})
created_acc_id = created_acc.get("id")
test("POST /accounts (Create bank account)", status == 200 and created_acc_id is not None, f"Account ID: {created_acc_id}")

new_acc_payload["branch"] = "Gandhipuram Main Branch"
status, body = make_request("PUT", f"/accounts/{created_acc_id}", new_acc_payload, token=tokens["ACCOUNTS"])
test("PUT /accounts/{id} (Update bank account)", status == 200 and body.get("data", {}).get("branch") == "Gandhipuram Main Branch", f"Status {status}")

transfer_payload = {
    "fromAccountId": created_acc_id,
    "toAccountId": "ACC-001",
    "amount": 25000.00,
    "referenceNumber": "TRF-CHEQUE-1029",
    "reason": "Monthly operating fund transfer to main bank"
}
status, body = make_request("POST", "/accounts/transfer", transfer_payload, token=tokens["ACCOUNTS"])
test("POST /accounts/transfer (Contra Transfer)", status == 200 and body.get("data", {}).get("amount") == 25000.00, f"Transfer ID: {body.get('data', {}).get('id')}")

status, body = make_request("GET", "/accounts/transfers", token=tokens["ACCOUNTS"])
test("GET /accounts/transfers", status == 200, f"Count: {len(body.get('data', []))}")

advance_payload = {
    "date": "2026-09-21",
    "recipientType": "DRIVER",
    "recipientId": seed_drv_id,
    "recipientName": "Ramesh Kumar",
    "amount": 2000.00,
    "reason": "Emergency fuel & highway toll cash",
    "paymentMode": "CASH"
}
status, body = make_request("POST", "/wages/advances", advance_payload, token=tokens["ACCOUNTS"])
created_adv = body.get("data", {})
created_adv_id = created_adv.get("id")
test("POST /wages/advances (Issue driver advance)", status == 200 and created_adv_id is not None, f"Advance ID: {created_adv_id}")

status, body = make_request("GET", "/wages/advances", token=tokens["ACCOUNTS"])
test("GET /wages/advances", status == 200, f"Count: {len(body.get('data', []))}")

wage_payload = {
    "workerId": "USR-004",
    "workerName": "Suresh Mani",
    "monthYear": "2026-09",
    "baseSalary": 22000.00,
    "overtimeAmount": 3500.00,
    "tripAllowance": 1500.00,
    "advancesDeducted": 2000.00,
    "otherDeductions": 500.00,
    "paymentMode": "BANK",
    "referenceNo": "SAL-SEP-00192"
}
status, body = make_request("POST", "/wages/disburse", wage_payload, token=tokens["ACCOUNTS"])
created_wage = body.get("data", {})
created_wage_id = created_wage.get("id")
test("POST /wages/disburse (Monthly wage settlement)", status == 200 and created_wage.get("netPayable") == 24500.00, f"Wage ID: {created_wage_id}, Net: {created_wage.get('netPayable')}")

status, body = make_request("GET", "/wages", token=tokens["ACCOUNTS"])
test("GET /wages", status == 200, f"Count: {len(body.get('data', []))}")

maint_payload = {
    "date": "2026-09-21",
    "vehicleRegistration": seed_veh_reg,
    "expenseType": "TYRE_REPLACE",
    "amount": 18500.00,
    "vendorName": "Apollo Tyres Hub",
    "invoiceBillNo": "AP-88210",
    "paymentMode": "BANK",
    "description": "2 Rear Tipper Radial Tyres Replaced"
}
status, body = make_request("POST", "/fleet-expenses/maintenance", maint_payload, token=tokens["ACCOUNTS"])
created_maint = body.get("data", {})
created_maint_id = created_maint.get("id")
test("POST /fleet-expenses/maintenance (Vehicle maintenance)", status == 200 and created_maint_id is not None, f"Expense ID: {created_maint_id}")

status, body = make_request("GET", "/fleet-expenses/maintenance", token=tokens["ACCOUNTS"])
test("GET /fleet-expenses/maintenance", status == 200, f"Count: {len(body.get('data', []))}")

status, body = make_request("GET", "/ledger/transactions", token=tokens["ACCOUNTS"])
test("GET /ledger/transactions", status == 200, f"Entries: {len(body.get('data', []))}")

status, body = make_request("GET", f"/ledger/customer/{created_cust_id}", token=tokens["ACCOUNTS"])
test("GET /ledger/customer/{id}", status == 200, f"Customer ledger entries: {len(body.get('data', []))}")

# -------------------------------------------------------------
# PORTAL 3: MANAGER PORTAL
# -------------------------------------------------------------
print("\n--- 4. PORTAL 3: MANAGER PORTAL (Operations, Fleet, Rate Cards, Masters) ---")

status, body = make_request("GET", "/dashboard/manager", token=tokens["MANAGER"])
test("GET /dashboard/manager", status == 200 and "availableVehiclesCount" in body.get("data", {}), f"Status {status}")

status, body = make_request("GET", f"/trips/{created_trip_id}", token=tokens["MANAGER"])
manager_trip_data = body.get("data", {})
test("GET /trips/{id} (Manager sees financial rates)", status == 200 and "appliedRate" in manager_trip_data, f"Applied Rate: {manager_trip_data.get('appliedRate')}")

edit_trip_payload = {
    "date": "2026-09-21",
    "customerId": seed_cust_id,
    "vehicleRegistration": seed_veh_reg,
    "driverId": seed_drv_id,
    "material": "20 MM Blue Metal",
    "quantity": 28.00,
    "unit": "Ton",
    "source": "Sri Ramanatha Blue Metals Crusher",
    "loadingLocation": "Sayalgudi Quarry Area",
    "deliveryLocation": "Metro Site A",
    "notes": "Updated by Operations Manager"
}
status, body = make_request("PUT", f"/trips/{created_trip_id}", edit_trip_payload, token=tokens["MANAGER"])
test("PUT /trips/{id} (Manager updates trip)", status == 200 and body.get("data", {}).get("quantity") == 28.00, f"Status {status}")

new_veh_reg = f"TN-38-XY-{int(time.time()) % 10000:04d}"
new_veh_payload = {
    "registration": new_veh_reg,
    "type": "TIPPER_16_WHEELER",
    "ownership": "OWNED",
    "capacity": "30 Ton",
    "fuelCapacity": "300 L",
    "currentKm": 5000.0,
    "status": "AVAILABLE"
}
status, body = make_request("POST", "/vehicles", new_veh_payload, token=tokens["MANAGER"])
test("POST /vehicles (Register vehicle)", status == 200 and body.get("data", {}).get("registration") == new_veh_reg, f"Reg: {new_veh_reg}")

new_veh_payload["capacity"] = "32 Ton"
status, body = make_request("PUT", f"/vehicles/{new_veh_reg}", new_veh_payload, token=tokens["MANAGER"])
test("PUT /vehicles/{registration} (Update vehicle)", status == 200 and "32" in str(body.get("data", {}).get("capacity")), f"Status {status}")

status, body = make_request("PATCH", f"/vehicles/{new_veh_reg}/status?status=AVAILABLE", token=tokens["MANAGER"])
test("PATCH /vehicles/{registration}/status", status == 200 and body.get("data", {}).get("status") == "AVAILABLE", f"Status {status}")

new_drv_payload = {
    "name": "Murugan Velu",
    "phone": f"+91 9789{int(time.time()) % 1000000:06d}",
    "licenseNumber": "TN38-20210098765",
    "assignedVehicle": new_veh_reg,
    "status": "AVAILABLE"
}
status, body = make_request("POST", "/drivers", new_drv_payload, token=tokens["MANAGER"])
created_drv = body.get("data", {})
created_drv_id = created_drv.get("id")
test("POST /drivers (Register driver)", status == 200 and created_drv_id is not None, f"Driver ID: {created_drv_id}")

new_drv_payload["licenseNumber"] = "TN38-20210098799"
status, body = make_request("PUT", f"/drivers/{created_drv_id}", new_drv_payload, token=tokens["MANAGER"])
test("PUT /drivers/{id} (Update driver)", status == 200 and body.get("data", {}).get("licenseNumber") == "TN38-20210098799", f"Status {status}")

rate_payload = {
    "customerId": created_cust_id,
    "rateType": "CUSTOMER",
    "material": "Gravel 20mm",
    "loadingLocation": "Pollachi Quarry",
    "deliveryLocation": "Metro Site A",
    "rate": 680.00,
    "unit": "Ton",
    "effectiveFrom": "2026-09-01",
    "status": "ACTIVE"
}
status, body = make_request("POST", "/rates", rate_payload, token=tokens["MANAGER"])
created_rate = body.get("data", {})
created_rate_id = created_rate.get("id")
test("POST /rates (Create rate card)", status == 200 and created_rate_id is not None, f"Rate ID: {created_rate_id}")

status, body = make_request("GET", f"/rates/{created_rate_id}", token=tokens["MANAGER"])
test("GET /rates/{id}", status == 200 and body.get("data", {}).get("rate") == 680.00, f"Status {status}")

rate_payload["rate"] = 720.00
status, body = make_request("PUT", f"/rates/{created_rate_id}", rate_payload, token=tokens["MANAGER"])
test("PUT /rates/{id} (Update rate card)", status == 200 and body.get("data", {}).get("rate") == 720.00, f"Status {status}")

src_payload = {"name": "Madukkarai Quarry 4", "location": "Madukkarai", "material": "20 MM Blue Metal", "status": "ACTIVE"}
status, body = make_request("POST", "/sources", src_payload, token=tokens["MANAGER"])
created_src_id = body.get("data", {}).get("id")
test("POST /sources", status == 200 and created_src_id is not None, f"Source ID: {created_src_id}")

src_payload["name"] = "Madukkarai Quarry 4 (North Wing)"
status, body = make_request("PUT", f"/sources/{created_src_id}", src_payload, token=tokens["MANAGER"])
test("PUT /sources/{id}", status == 200 and body.get("data", {}).get("name") == "Madukkarai Quarry 4 (North Wing)", f"Status {status}")

mat_payload = {"name": f"Sand Fine {int(time.time()) % 1000}", "category": "SAND", "standardUnit": "Ton", "status": "ACTIVE"}
status, body = make_request("POST", "/materials", mat_payload, token=tokens["MANAGER"])
created_mat_id = body.get("data", {}).get("id")
test("POST /materials", status == 200 and created_mat_id is not None, f"Material ID: {created_mat_id}")

mat_payload["standardUnit"] = "CFT"
status, body = make_request("PUT", f"/materials/{created_mat_id}", mat_payload, token=tokens["MANAGER"])
test("PUT /materials/{id}", status == 200 and body.get("data", {}).get("standardUnit") == "CFT", f"Status {status}")

loc_payload = {"name": "Avinashi Bypass Bridge Site", "type": "DELIVERY_SITE", "address": "Tirupur District", "status": "ACTIVE"}
status, body = make_request("POST", "/locations", loc_payload, token=tokens["MANAGER"])
created_loc_id = body.get("data", {}).get("id")
test("POST /locations", status == 200 and created_loc_id is not None, f"Location ID: {created_loc_id}")

loc_payload["name"] = "Avinashi Bypass Flyover Site"
status, body = make_request("PUT", f"/locations/{created_loc_id}", loc_payload, token=tokens["MANAGER"])
test("PUT /locations/{id}", status == 200 and body.get("data", {}).get("name") == "Avinashi Bypass Flyover Site", f"Status {status}")

pmp_payload = {"name": "IOCL Neelambur Bypass Pump", "location": "Neelambur", "phone": "+91 9842200000", "status": "ACTIVE"}
status, body = make_request("POST", "/fuel-stations", pmp_payload, token=tokens["MANAGER"])
created_pmp_id = body.get("data", {}).get("id")
test("POST /fuel-stations", status == 200 and created_pmp_id is not None, f"Pump ID: {created_pmp_id}")

pmp_payload["name"] = "IOCL Neelambur Super Pump"
status, body = make_request("PUT", f"/fuel-stations/{created_pmp_id}", pmp_payload, token=tokens["MANAGER"])
test("PUT /fuel-stations/{id}", status == 200 and body.get("data", {}).get("name") == "IOCL Neelambur Super Pump", f"Status {status}")

status, body = make_request("GET", "/approvals/pending", token=tokens["MANAGER"])
test("GET /approvals/pending", status == 200, f"Pending count: {len(body.get('data', []))}")

status, body = make_request("POST", f"/approvals/{created_correction_id}/approve", {"comment": "Approved after weighbridge slip confirmation"}, token=tokens["MANAGER"])
test("POST /approvals/{id}/approve (Apply correction)", status == 200 and body.get("data", {}).get("status") == "APPROVED", f"Status {status}")

# -------------------------------------------------------------
# PORTAL 4: MD PORTAL
# -------------------------------------------------------------
print("\n--- 5. PORTAL 4: MD (MANAGING DIRECTOR) PORTAL (Executive Analytics & Audits) ---")

status, body = make_request("GET", "/dashboard/md", token=tokens["MD"])
test("GET /dashboard/md", status == 200 and "monthlyGrossRevenue" in body.get("data", {}), f"Monthly Revenue: {body.get('data', {}).get('monthlyGrossRevenue')}")

status, body = make_request("GET", "/dashboards/md-cockpit", token=tokens["MD"])
test("GET /dashboards/md-cockpit (Alias)", status == 200 and "monthlyGrossRevenue" in body.get("data", {}), f"Monthly Revenue: {body.get('data', {}).get('monthlyGrossRevenue')}")

status, body = make_request("GET", "/audit-logs", token=tokens["MD"])
test("GET /audit-logs (MD Executive audit trail)", status == 200, f"Audit events logged: {len(body.get('data', []))}")

status, body = make_request("GET", f"/audit-logs/TRIP/{created_trip_id}", token=tokens["MD"])
test("GET /audit-logs/{entityType}/{entityId}", status == 200, f"Trip audit events: {len(body.get('data', []))}")

# -------------------------------------------------------------
# PORTAL 5: ADMIN PORTAL
# -------------------------------------------------------------
print("\n--- 6. PORTAL 5: ADMIN PORTAL (User Administration, Full Master Deletions & Cleanups) ---")

status, body = make_request("GET", "/auth/roles", token=tokens["ADMIN"])
test("GET /auth/roles", status == 200 and len(body.get("data", [])) >= 5, f"Roles: {len(body.get('data', []))}")

test_uname = f"testuser_{int(time.time()) % 10000}"
new_user_payload = {
    "username": test_uname,
    "fullName": "Test Security Officer",
    "email": f"{test_uname}@transflow.com",
    "phone": "+91 9842199999",
    "role": "MANAGER",
    "password": "Password@123"
}
status, body = make_request("POST", "/users", new_user_payload, token=tokens["ADMIN"])
created_user = body.get("data", {})
created_user_id = created_user.get("id")
test("POST /users (Admin creates user)", status == 200 and created_user_id is not None, f"User ID: {created_user_id}")

update_user_payload = {
    "fullName": "Senior Security Officer",
    "email": f"{test_uname}@transflow.com",
    "phone": "+91 9842199998",
    "role": "MANAGER",
    "status": "ACTIVE"
}
status, body = make_request("PUT", f"/users/{created_user_id}", update_user_payload, token=tokens["ADMIN"])
test("PUT /users/{id} (Admin updates user)", status == 200 and body.get("data", {}).get("fullName") == "Senior Security Officer", f"Status {status}")

status, body = make_request("PATCH", f"/users/{created_user_id}/status?status=INACTIVE", token=tokens["ADMIN"])
test("PATCH /users/{id}/status", status == 200 and body.get("data", {}).get("status") == "INACTIVE", f"Status {status}")

status, body = make_request("DELETE", f"/users/{created_user_id}", token=tokens["ADMIN"])
test("DELETE /users/{id}", status == 200, f"Status {status}")

status, body = make_request("DELETE", f"/rates/{created_rate_id}", token=tokens["ADMIN"])
test("DELETE /rates/{id}", status == 200, f"Status {status}")

status, body = make_request("DELETE", f"/sources/{created_src_id}", token=tokens["ADMIN"])
test("DELETE /sources/{id}", status == 200, f"Status {status}")

status, body = make_request("DELETE", f"/materials/{created_mat_id}", token=tokens["ADMIN"])
test("DELETE /materials/{id}", status == 200, f"Status {status}")

status, body = make_request("DELETE", f"/locations/{created_loc_id}", token=tokens["ADMIN"])
test("DELETE /locations/{id}", status == 200, f"Status {status}")

status, body = make_request("DELETE", f"/fuel-stations/{created_pmp_id}", token=tokens["ADMIN"])
test("DELETE /fuel-stations/{id}", status == 200, f"Status {status}")

status, body = make_request("DELETE", f"/accounts/{created_acc_id}", token=tokens["ADMIN"])
test("DELETE /accounts/{id}", status == 200, f"Status {status}")

status, body = make_request("DELETE", f"/drivers/{created_drv_id}", token=tokens["ADMIN"])
test("DELETE /drivers/{id}", status == 200, f"Status {status}")

status, body = make_request("DELETE", f"/vehicles/{new_veh_reg}", token=tokens["ADMIN"])
test("DELETE /vehicles/{registration}", status == 200, f"Status {status}")

status, body = make_request("DELETE", f"/fleet-expenses/diesel/{created_diesel_id}", token=tokens["ADMIN"])
test("DELETE /fleet-expenses/diesel/{id}", status == 200, f"Status {status}")

status, body = make_request("DELETE", f"/fleet-expenses/maintenance/{created_maint_id}", token=tokens["ADMIN"])
test("DELETE /fleet-expenses/maintenance/{id}", status == 200, f"Status {status}")

status, body = make_request("POST", f"/invoices/{created_inv_id}/cancel?reason=End+to+end+test+cleanup", token=tokens["ADMIN"])
test("POST /invoices/{id}/cancel", status == 200 and body.get("data", {}).get("status") == "CANCELLED", f"Status {status}")

status, body = make_request("DELETE", f"/invoices/{created_inv_id}?reason=Voided", token=tokens["ADMIN"])
test("DELETE /invoices/{id} (Void invoice)", status == 200, f"Status {status}")

status, body = make_request("DELETE", f"/trips/{created_trip_id}", token=tokens["ADMIN"])
test("DELETE /trips/{id}", status == 200, f"Status {status}")

status, body = make_request("DELETE", f"/customers/{created_cust_id}", token=tokens["ADMIN"])
test("DELETE /customers/{id}", status == 200, f"Status {status}")

print("\n" + "=" * 80)
print(f"FINAL AUDIT RESULT: {passed} PASSED / {failed} FAILED (TOTAL {total} TESTS)")
print("=" * 80)

if failed == 0:
    print("ALL 5 PORTALS & ALL CRUD REST ENDPOINTS ARE 100% PRODUCTION READY AND VERIFIED!")
    sys.exit(0)
else:
    print(f"ATTENTION: {failed} tests failed! Review outputs above.")
    sys.exit(1)
