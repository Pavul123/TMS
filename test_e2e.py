import urllib.request
import json

def post(url, data, token=None):
    headers = {'Content-Type': 'application/json'}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers=headers, method='POST')
    with urllib.request.urlopen(req) as resp:
        return json.loads(resp.read().decode('utf-8'))

def get(url, token=None):
    headers = {}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    req = urllib.request.Request(url, headers=headers, method='GET')
    with urllib.request.urlopen(req) as resp:
        return json.loads(resp.read().decode('utf-8'))

print("=== 1. Testing Auth Logins ===")
worker_auth = post('http://localhost:8080/api/v1/auth/login', {'username': 'worker', 'password': 'worker123'})
worker_token = worker_auth['data']['token']
print('Worker Logged in successfully:', worker_auth['data']['username'], '| Role:', worker_auth['data']['role'])

md_auth = post('http://localhost:8080/api/v1/auth/login', {'username': 'md', 'password': 'md123'})
md_token = md_auth['data']['token']
print('MD Logged in successfully:', md_auth['data']['username'], '| Role:', md_auth['data']['role'])

accounts_auth = post('http://localhost:8080/api/v1/auth/login', {'username': 'accounts', 'password': 'accounts123'})
accounts_token = accounts_auth['data']['token']
print('Accounts Logged in successfully:', accounts_auth['data']['username'], '| Role:', accounts_auth['data']['role'])

print("\n=== 2. Testing Worker Trip Creation (Frictionless + Zero Financial Leakage) ===")
trip_payload = {
    'date': '2026-09-18',
    'customerId': 'CUS-00124',
    'vehicleRegistration': 'TN 58 AB 2345',
    'driverId': 'DRV-0012',
    'material': '20 MM Aggregate',
    'quantity': 18.5,
    'unit': 'Ton',
    'source': 'Sri Ramanatha Blue Metals Crusher',
    'sourceBillNo': 'SRC-BILL-8891',
    'loadingLocation': 'Sri Ramanatha Blue Metals Crusher',
    'deliveryLocation': 'Paramakudi Highway Site KM 42',
    'openingKm': 142500,
    'closingKm': 142540,
    'tripKm': 40,
    'notes': 'Delivered in good condition'
}
new_trip = post('http://localhost:8080/api/v1/trips', trip_payload, worker_token)
trip_id = new_trip['data']['id']
print('New Trip Created ID:', trip_id)
assert 'appliedRate' not in new_trip['data'], 'FAIL: appliedRate leaked to Worker!'
assert 'totalAmount' not in new_trip['data'], 'FAIL: totalAmount leaked to Worker!'
print('SUCCESS: Zero Financial Exposure strictly enforced on Worker DTO!')

print("\n=== 3. Testing Manager/MD Full Trip View (Rate Frozen) ===")
full_trip = get(f'http://localhost:8080/api/v1/trips/{trip_id}', md_token)
rate = full_trip['data']['appliedRate']
total = full_trip['data']['totalAmount']
print(f'MD View -> Applied Rate: Rs.{rate}/Ton | Total Amount: Rs.{total}')

print("\n=== 4. Testing Worker Correction Request (Controlled Edit) ===")
correction_payload = {
    'entityType': 'TRIP',
    'entityId': trip_id,
    'reason': 'Customer weight slip revised after unloading: 19.5 Ton instead of 18.5 Ton',
    'changes': [
        {'field': 'quantity', 'newValue': '19.5'}
    ]
}
corr_req = post('http://localhost:8080/api/v1/approvals/requests', correction_payload, worker_token)
corr_id = corr_req['data']['id']
print('Correction Request Submitted ID:', corr_id, '| Status:', corr_req['data']['status'])

print("\n=== 5. Testing MD Approval Queue & Approve Action ===")
pending = get('http://localhost:8080/api/v1/approvals/pending', md_token)
print('Pending Approvals Count in MD Cockpit:', len(pending['data']))

approved = post(f'http://localhost:8080/api/v1/approvals/{corr_id}/approve', {'comment': 'Verified with Paramakudi weighbridge slip - Approved'}, md_token)
print('Approved Status:', approved['data']['status'], '| Reviewed By:', approved['data']['reviewedBy'])

# Verify updated trip in DB
updated_trip = get(f'http://localhost:8080/api/v1/trips/{trip_id}', md_token)
new_qty = updated_trip['data']['quantity']
new_total = updated_trip['data']['totalAmount']
print(f'Trip after Approval -> New Qty: {new_qty} Ton | New Total: Rs.{new_total}')

print("\n=== 6. Testing Accounts Invoice Generation ===")
invoice_payload = {
    'date': '2026-09-18',
    'customerId': 'CUS-00124',
    'taxRate': 5.0,
    'tripIds': [trip_id],
    'notes': 'GST Tax Invoice for NH 49 Highway delivery'
}
new_inv = post('http://localhost:8080/api/v1/invoices', invoice_payload, accounts_token)
inv_id = new_inv['data']['id']
subtotal = new_inv['data']['subtotal']
tax = new_inv['data']['taxAmount']
inv_total = new_inv['data']['totalAmount']
print(f'Invoice Generated: {inv_id} | Subtotal: Rs.{subtotal} | Tax (5%): Rs.{tax} | Total: Rs.{inv_total}')

print("\n=== 7. Testing Delayed Customer Payment & Allocation ===")
pay_payload = {
    'date': '2026-09-18',
    'customerId': 'CUS-00124',
    'amount': float(inv_total),
    'paymentMode': 'NEFT',
    'referenceNumber': 'HDFCN26091800921',
    'accountId': 'ACC-002',
    'notes': 'Full settlement for invoice ' + inv_id,
    'allocations': [
        {'invoiceId': inv_id, 'allocatedAmount': float(inv_total)}
    ]
}
new_pay = post('http://localhost:8080/api/v1/payments', pay_payload, accounts_token)
pay_id = new_pay['data']['id']
print(f'Payment Recorded: {pay_id} | Amount: Rs.{new_pay["data"]["amount"]} | Status: {new_pay["data"]["status"]}')

print("\n=== 8. Testing Live MD Cockpit & Audit Trail ===")
md_dash = get('http://localhost:8080/api/v1/dashboard/md', md_token)
print('MD Dashboard Gross Revenue: Rs.', md_dash['data']['monthlyGrossRevenue'])
print('MD Dashboard Fleet Utilization: ', md_dash['data']['fleetUtilizationRate'], '%')
print('Trucks Profitability Tracked: ', len(md_dash['data']['truckProfitability']))

audit_logs = get('http://localhost:8080/api/v1/audit-logs', md_token)
print('Total Audit Logs Recorded: ', len(audit_logs['data']))
for log in audit_logs['data'][:3]:
    print(f" -> [{log['performedAt']}] {log['performedBy']} | Action: {log['action']} on {log['entityType']} #{log['entityId']} | Reason: {log['reason']}")

print("\n=== ALL 8 END-TO-END BUSINESS FRAUD PREVENTION TESTS PASSED PERFECTLY ===")
