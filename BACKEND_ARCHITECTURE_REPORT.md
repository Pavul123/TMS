# TransFlow TMS — Backend Architecture & Developer Onboarding Guide

**Version:** 1.0.0 (Enterprise Production)  
**Target Audience:** New Backend Developers, Full-Stack Engineers, System Architects  
**Tech Stack:** Java 17 LTS, Spring Boot 3.3.4, Spring Security 6, Spring Data JPA / Hibernate, PostgreSQL 17, Flyway, JJWT 0.12.6, Lombok, Swagger/OpenAPI 3  

---

## 1. Executive Summary & Business Context

### What is TransFlow TMS?
TransFlow TMS is an enterprise **Transport Management & Financial Control System** designed specifically for high-volume commercial transport/tipper fleet operations (e.g., aggregate material hauling, crushers, sand, boulders, infrastructure sites).

### The Core Problem It Solves:
Traditional transport operations rely on loose Excel sheets and manual registers, exposing the business to major risks:
1. **Rate & Quantity Manipulation:** Field workers altering trip quantities or freight amounts retroactively.
2. **Payment Embezzlement & Record Tampering:** Operators changing received cash amounts or silently deleting records.
3. **Delayed Payment Mismatches:** Trips occur today, invoices are billed weekly, and payments arrive weeks later with partial allocations.

### Architectural Solution:
TransFlow TMS enforces a **Controlled Transaction + 2-Person Approval + Immutable Ledger** architecture:
* **Frictionless CREATE:** Workers log trips rapidly with zero delays.
* **Controlled EDIT:** Once a record is posted, workers **cannot** edit it directly. Any edit requires submitting a `CorrectionRequest` (with before/after snapshot and mandatory reason).
* **2-Person Rule Approval:** MD or Operations Managers review and approve/reject corrections before database modification.
* **Strict Zero Financial Exposure for Workers:** Field workers never see freight rates, invoice amounts, customer balances, or revenue figures.
* **Immutable Financial Records:** Payments and financial transactions are never physically deleted (`DELETE` prohibited). Erroneous entries are neutralized via **Reversals** and contra entries.

---

## 2. High-Level System Architecture

```
                    ┌────────────────────────────────────────┐
                    │       Next.js 14 Frontend UI           │
                    │   (Portals: Worker, Accounts, Manager, │
                    │          MD Cockpit, Admin)            │
                    └───────────────────┬────────────────────┘
                                        │ REST API (JSON)
                                        │ JWT Bearer Tokens
                                        ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                    Spring Boot 3.3 REST API (Port 8080)                     │
│                                                                            │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                    Spring Security 6 & JWT Layer                     │  │
│  │  - JwtAuthenticationFilter  - CustomUserDetailsService                │  │
│  │  - Granular RBAC Permissions (@PreAuthorize)                          │  │
│  └──────────────────────────────────┬───────────────────────────────────┘  │
│                                     │                                      │
│  ┌──────────────────────────────────┴───────────────────────────────────┐  │
│  │                         Core Business Engines                        │  │
│  │                                                                      │  │
│  │  [ Trip Engine ]         [ Governance Engine ]    [ Finance Engine ] │  │
│  │  • 7-Step Trip Wizard    • Correction Requests    • Multi-Trip GST   │  │
│  │  • Rate Card Freezing    • 2-Person Approval        Invoicing        │  │
│  │  • Worker Sanitizer      • Audit Trail Logger     • Delayed Payments │  │
│  │  • No-Load Operations                             • Contra Transfers │  │
│  │                                                   • Driver Advances  │  │
│  │  [ Master Data ]         [ Dashboard Engine ]     • Diesel & Mileage │  │
│  │  • Customers (Phone Key) • MD Executive Cockpit   • Central Ledger   │  │
│  │  • Vehicles & Drivers    • Accounts Position                         │  │
│  │  • Crushers & Materials  • Fleet Dispatch KPIs                       │  │
│  └──────────────────────────────────┬───────────────────────────────────┘  │
│                                     │                                      │
│  ┌──────────────────────────────────┴───────────────────────────────────┐  │
│  │              Spring Data JPA 3 & Hibernate 6 ORM Layer               │  │
│  │  • Optimistic Locking (@Version)  • ACID Transaction Boundaries       │  │
│  └──────────────────────────────────┬───────────────────────────────────┘  │
└─────────────────────────────────────┼──────────────────────────────────────┘
                                      │
                                      ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                      PostgreSQL 17 Database (tms_db)                       │
│                                                                            │
│  • Flyway Migrations (V1 to V12 Schema Evolution)                          │
│  • Foreign Keys, CHECK constraints, Unique Phone Keys, Indexes             │
│  • Tables: users, roles, customers, vehicles, drivers, trips, invoices,    │
│    payments, allocations, contra_transfers, financial_transactions,        │
│    diesel_logs, worker_wages, correction_requests, audit_logs, settings    │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Database Schema & Flyway Migrations

The database is version-controlled via Flyway scripts located in `src/main/resources/db/migration/`:

| Migration Script | Table(s) Created | Purpose & Constraints |
| :--- | :--- | :--- |
| `V1__create_security_and_rbac.sql` | `users`, `roles`, `permissions`, `role_permissions` | RBAC tables, BCrypt hashed passwords, unique usernames. |
| `V2__create_masters.sql` | `customers`, `vehicles`, `drivers`, `workers`, `sources`, `materials`, `locations`, `fuel_stations` | Master catalogs; **`customers.phone` is unique lookup key** to prevent shadow duplicates. |
| `V3__create_rate_cards.sql` | `configured_rates` | Contracted customer and crusher rate cards (Per Ton / Fixed). |
| `V4__create_trips.sql` | `trips`, `trip_status_history` | 7-step fields, frozen rates, `is_no_load`, optimistic `@Version`. |
| `V5__create_invoices.sql` | `invoices`, `invoice_items` | GST tax invoices (`INV-xxxx`), subtotal, 5%/18% tax, line items. |
| `V6__create_payments_and_accounts.sql` | `cash_bank_accounts`, `payments`, `payment_allocations`, `contra_transfers` | Delayed customer payments (`PAY-xxxx`), multi-invoice allocation, contra bank transfers. |
| `V7__create_ledgers.sql` | `financial_transactions` | Central double-entry transaction ledger (`TXN-xxxxx`). |
| `V8__create_diesel_and_maintenance.sql` | `diesel_logs`, `vehicle_expenses` | Diesel receipts, mileage calculation, maintenance records. |
| `V9__create_wages.sql` | `worker_wages`, `wage_advances` | Driver cash advances, wage calculations (Base + Overtime - Advances). |
| `V10__create_corrections_and_approvals.sql` | `correction_requests`, `correction_request_items` | 2-person approval queue, field-level before/after diffs. |
| `V11__create_audit_logs.sql` | `audit_logs`, `company_settings` | Immutable audit trail, company GSTIN and numbering rules. |
| `V12__insert_initial_seed_data.sql` | *Seed Data* | Preloaded roles, permissions, demo users, vehicles, crushers, rate cards. |

---

## 4. Permanent Business Identifiers Format

The system generates consistent, human-readable permanent IDs across all modules via [IdGenerator.java](file:///c:/Users/ADMIN/OneDrive/Desktop/TMS/tms-backend/src/main/java/com/transport/tms/common/util/IdGenerator.java):

* `TRP-01484` — Daily Tipper Trips
* `INV-0001` — GST Invoices
* `PAY-0001` — Customer Payment Receipts
* `TXN-02481` — Central Ledger Financial Transactions
* `CRQ-0001` — Correction Requests
* `CUS-00124` — Customers
* `DRV-0012` — Drivers
* `WRK-0024` — Workers / Operators
* `SRC-001` — Quarries / Crushers
* `ACC-001` — Cash / Bank Accounts
* `TRF-0001` — Cash $\leftrightarrow$ Bank Contra Transfers
* `DSL-0001` — Diesel Fuel Logs

---

## 5. Security & RBAC Model

### Roles & Authority Hierarchy:
1. **`ROLE_ADMIN`:** Full governance, user provisioning, role assignments, system settings, full audit logs.
2. **`ROLE_MD`:** Executive oversight, high-level P&L dashboards, **2-Person Rule Approval Authority**.
3. **`ROLE_MANAGER`:** Fleet operations, vehicle/driver assignments, rate card configuration, operational corrections.
4. **`ROLE_ACCOUNTS`:** GST invoicing, payment recording, customer ledgers, diesel expenses, wage disbursement, contra transfers.
5. **`ROLE_WORKER`:** Daily trip logging, 7-step wizard, customer search. **Zero access to financials**.

### Granular Permission Matrix:
Methods in controllers use `@PreAuthorize("hasAnyAuthority('PERMISSION_NAME', 'ROLE_NAME')")`. Examples:
* `TRIP_CREATE`, `TRIP_VIEW`, `TRIP_EDIT_REQUEST`, `TRIP_APPROVE`
* `INVOICE_CREATE`, `INVOICE_VIEW`
* `PAYMENT_CREATE`, `PAYMENT_VIEW`, `PAYMENT_REVERSE`
* `CUSTOMER_VIEW`, `CUSTOMER_MANAGE`
* `CASHBANK_MANAGE`, `DIESEL_MANAGE`, `WAGE_MANAGE`, `AUDIT_VIEW`

### Default Demo Credentials:
| Username | Password | Role | Designated Landing Portal |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | `ADMIN` | Admin Control Center (`/admin/*`) |
| `md` | `md123` | `MD` | MD Cockpit & Approvals (`/md/*`) |
| `manager` | `manager123` | `MANAGER` | Fleet Operations (`/manager/*`) |
| `accounts` | `accounts123` | `ACCOUNTS` | Financial Hub (`/accounts/*`) |
| `worker` | `worker123` | `WORKER` | Trip Dispatcher (`/worker/*`) |

---

## 6. Detailed Business Engines & Code Walkthrough

### A. Frictionless CREATE vs. Controlled EDIT
1. **Create Flow:**
   * Worker calls `POST /api/v1/trips`.
   * [TripService.java](file:///c:/Users/ADMIN/OneDrive/Desktop/TMS/tms-backend/src/main/java/com/transport/tms/trip/service/TripService.java) validates customer, vehicle, driver.
   * If `isNoLoad = false`, it calls `RateCardService.resolveRate(...)` to find the active contract rate and **freezes** it on `trip.appliedRate`.
   * Saves trip with permanent ID `TRP-xxxxx`.
   * Returns `WorkerTripDto` (with **no rates or totals**) to the worker.
2. **Edit Flow:**
   * Direct PUT on posted trips is prohibited.
   * Worker calls `POST /api/v1/approvals/requests` with `entityType: TRIP`, `entityId`, `reason`, and list of `changes`.
   * [ApprovalService.java](file:///c:/Users/ADMIN/OneDrive/Desktop/TMS/tms-backend/src/main/java/com/transport/tms/governance/approval/service/ApprovalService.java) captures the existing field values (`oldValue`) from database and creates a `CorrectionRequest` in `PENDING` status.
   * MD / Manager views pending requests at `GET /api/v1/approvals/pending`.
   * Reviewer calls `POST /api/v1/approvals/{id}/approve`.
   * The service applies the new value, recalculates totals if quantity changed, updates the DB, and records a tamper-evident entry in `audit_logs`.

### B. Invoicing & Delayed Customer Payments
1. **Multi-Trip GST Invoicing:**
   * Accounts officer selects multiple completed unbilled trips for a customer.
   * Calls `POST /api/v1/invoices`.
   * [InvoiceService.java](file:///c:/Users/ADMIN/OneDrive/Desktop/TMS/tms-backend/src/main/java/com/transport/tms/finance/invoice/service/InvoiceService.java) calculates subtotal, tax (5% GTA / 18%), and generates `INV-xxxx`.
   * Links each trip to `invoiceId` (preventing duplicate billing).
   * Automatically posts a `RECEIVABLE` entry in `financial_transactions`.
2. **Payment Allocation:**
   * Customer pays later via Bank/NEFT/Cash.
   * Accounts officer calls `POST /api/v1/payments`.
   * [PaymentService.java](file:///c:/Users/ADMIN/OneDrive/Desktop/TMS/tms-backend/src/main/java/com/transport/tms/finance/payment/service/PaymentService.java) allocates the amount across invoice(s), reduces invoice `balanceAmount`, marks invoice `PAID` or `PARTIALLY_PAID`, credits the receiving `CashBankAccount`, and writes a `CUSTOMER_PAYMENT` ledger transaction.
3. **Payment Reversal (No Silent Deletions):**
   * If a payment was recorded erroneously, Accounts calls `POST /api/v1/payments/{id}/reverse`.
   * The service restores invoice balances, debits the bank account, marks payment `REVERSED`, and writes a compensating ledger entry.

### C. Cash & Bank Contra Transfers
* Corporate cash and bank accounts are tracked in `cash_bank_accounts`.
* When cash is deposited into the corporate bank account (or vice versa), `POST /api/v1/accounts/transfer` executes an atomic contra transfer in [CashBankService.java](file:///c:/Users/ADMIN/OneDrive/Desktop/TMS/tms-backend/src/main/java/com/transport/tms/finance/cashbank/service/CashBankService.java).

---

## 7. Package Structure

```
tms-backend/src/main/java/com/transport/tms/
│
├── TmsApplication.java                # Spring Boot Main Application Class
│
├── common/                            # Shared Utilities & Standard Responses
│   ├── exception/
│   │   ├── Exceptions.java            # Domain Exceptions (ResourceNotFound, BadRequest, etc.)
│   │   └── GlobalExceptionHandler.java# Central REST Controller Exception Handler
│   ├── response/
│   │   └── ApiResponse.java           # Standard API Response Envelope { success, message, data, timestamp }
│   └── util/
│       └── IdGenerator.java           # Atomic Permanent Sequence Number Generators
│
├── security/                          # Spring Security 6 & JWT Infrastructure
│   ├── CustomUserDetailsService.java  # User loader & authority mapper
│   ├── JwtAuthenticationFilter.java   # HTTP Request Bearer token interceptor
│   ├── JwtTokenProvider.java          # JJWT 0.12.6 Token encoder & validator
│   ├── SecurityConfig.java            # SecurityFilterChain, CORS, Stateless policy
│   └── UserPrincipal.java             # UserDetails implementation
│
├── auth/                              # Authentication & Profile APIs
│   ├── controller/AuthController.java # /api/v1/auth/login, /me, /roles
│   ├── dto/AuthDto.java               # LoginRequest, LoginResponse, UserDto
│   └── service/AuthService.java       # User authentication and token creation
│
├── user/                              # User & Role Administration
│   ├── controller/UserController.java # /api/v1/users
│   ├── entity/User.java, Role.java, Permission.java
│   ├── repository/UserRepository.java, RoleRepository.java, PermissionRepository.java
│   └── service/UserService.java
│
├── master/                            # Master Data Catalogs
│   ├── customer/                      # Customer Master (Phone duplicate protection)
│   ├── vehicle/                       # Fleet Tipper Vehicles (Registration key, Km, status)
│   ├── driver/                        # Drivers Master (License, advance balance)
│   ├── source/                        # Quarries, Crushers, Materials, Locations, Fuel Stations
│   └── rate/                          # Contracted Customer/Crusher Freight Rate Cards
│
├── trip/                              # Operational Trip Management
│   ├── controller/TripController.java # /api/v1/trips, /trips/{id}, /trips/unbilled
│   ├── dto/TripDto.java               # CreateTripRequest, FullTripDto, WorkerTripDto
│   ├── entity/Trip.java, TripStatusHistory.java
│   ├── repository/TripRepository.java, TripStatusHistoryRepository.java
│   └── service/TripService.java       # Rate lookup & freeze, Worker sanitization
│
├── governance/                        # Audit & 2-Person Approval Queue
│   ├── approval/                      # Approval Engine Controller & Service
│   ├── correction/                    # CorrectionRequest & Items Entities & Repositories
│   └── audit/                         # Immutable AuditLog Entity, Repository, Service, Controller
│
├── finance/                           # Financial Engine & Ledgers
│   ├── invoice/                       # Multi-Trip GST Invoicing (/api/v1/invoices)
│   ├── payment/                       # Delayed Payments & Reversals (/api/v1/payments)
│   ├── ledger/                        # Central Financial Ledger (/api/v1/ledger)
│   ├── diesel/                        # Diesel receipts & Mileage (/api/v1/fleet-expenses)
│   ├── wage/                          # Driver Advances & Wage Settlement (/api/v1/wages)
│   └── cashbank/                      # Cash/Bank Accounts & Contra Transfers (/api/v1/accounts)
│
└── dashboard/                         # Real-time Analytics & Executive KPIs
    ├── controller/DashboardController.java # /api/v1/dashboard/md, /accounts, /manager
    ├── dto/DashboardDtos.java
    └── service/DashboardService.java  # Derived KPIs & Truck-wise P&L calculation
```

---

## 8. REST API Endpoint Reference Catalog

### Authentication
* `POST /api/v1/auth/login` — Authenticate and obtain JWT token
* `GET  /api/v1/auth/me` — Get current logged-in user profile & permissions
* `GET  /api/v1/auth/roles` — List available system roles & permissions

### Trips (Operations & Worker)
* `GET  /api/v1/trips` — Get trips list (Worker receives sanitized DTO with zero rates)
* `GET  /api/v1/trips/{id}` — Get single trip details
* `GET  /api/v1/trips/unbilled` — Get unbilled trips available for invoicing
* `POST /api/v1/trips` — Log a new trip (Worker / Ops flow)
* `PATCH /api/v1/trips/{id}/status` — Transition operational status (`LOADED`, `RUNNING`, `DELIVERED`, etc.)

### Approvals & Governance (2-Person Rule)
* `POST /api/v1/approvals/requests` — Submit correction request for locked record
* `GET  /api/v1/approvals/pending` — View pending approval review queue (MD / Manager)
* `GET  /api/v1/approvals/{id}` — Get correction request details
* `POST /api/v1/approvals/{id}/approve` — Approve correction & apply DB update
* `POST /api/v1/approvals/{id}/reject` — Reject correction request
* `GET  /api/v1/audit-logs` — View system-wide audit trail
* `GET  /api/v1/audit-logs/{entityType}/{entityId}` — View entity-specific audit trail

### Master Data
* `GET / POST / PUT  /api/v1/customers` — Customer master
* `GET /api/v1/customers/by-phone/{phone}` — Phone-first duplicate lookup
* `GET / POST / PUT  /api/v1/vehicles` — Vehicle fleet master
* `GET / POST / PUT  /api/v1/drivers` — Drivers master
* `GET / POST        /api/v1/sources` — Quarries & Crushers
* `GET / POST        /api/v1/materials` — Aggregates & Raw stone materials
* `GET / POST        /api/v1/locations` — Site & Yard locations
* `GET / POST / PUT  /api/v1/rates` — Contracted Freight Rate Cards

### Finance & Invoicing
* `GET / POST /api/v1/invoices` — Generate GST Invoice from selected trips
* `GET /api/v1/invoices/{id}` — Get invoice with line items
* `GET / POST /api/v1/payments` — Record delayed payment with multi-invoice allocation
* `POST /api/v1/payments/{id}/reverse` — Reverse invalid payment record
* `GET  /api/v1/ledger/transactions` — View Central Financial Ledger
* `GET  /api/v1/ledger/customer/{id}` — View customer ledger statement
* `GET / POST /api/v1/accounts` — Cash & Bank accounts list
* `POST /api/v1/accounts/transfer` — Execute Cash $\leftrightarrow$ Bank Contra transfer
* `GET / POST /api/v1/fleet-expenses/diesel` — Diesel logs & mileage tracking
* `GET / POST /api/v1/fleet-expenses/maintenance` — Vehicle maintenance expenses
* `GET / POST /api/v1/wages/disburse` — Monthly wage settlement
* `GET / POST /api/v1/wages/advances` — Driver cash advances

### Executive Dashboards
* `GET /api/v1/dashboard/md` — MD Cockpit KPIs, Profitability, Truck P&L
* `GET /api/v1/dashboard/accounts` — Accounts Financial position & outstanding receivables
* `GET /api/v1/dashboard/manager` — Fleet dispatch KPIs

---

## 9. How to Run, Test, and Develop Locally

### Prerequisites:
1. **Java 17 LTS** installed.
2. **PostgreSQL 17** running locally at `localhost:5432` with user `postgres` and password `root`.
3. Standalone Maven binary is pre-bundled in `tools/apache-maven-3.9.6/`.

### Starting the Backend:
```bash
cd tms-backend
..\tools\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run
```
* Tomcat will start on **`http://localhost:8080`**.
* Flyway will automatically run all 12 database migrations against PostgreSQL `tms_db`.

### Running the End-to-End Test Suite:
A complete Python test suite verifying all 8 core fraud-prevention flows is available:
```bash
python test_e2e.py
```

### Accessing Swagger / OpenAPI Documentation:
* **Interactive UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **OpenAPI Specification:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
