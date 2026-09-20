/**
 * TransFlow TMS - Unified Backend API Client
 * Connects Next.js Frontend to Spring Boot REST API (http://localhost:8080/api/v1)
 */

export const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';
export const TOKEN_KEY = 'tms_jwt_token';

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
  error?: string;
}

export function getAuthToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem(TOKEN_KEY);
}

export function setAuthToken(token: string): void {
  if (typeof window === 'undefined') return;
  localStorage.setItem(TOKEN_KEY, token);
}

export function removeAuthToken(): void {
  if (typeof window === 'undefined') return;
  localStorage.removeItem(TOKEN_KEY);
}

async function fetchWithAuth<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const token = getAuthToken();
  const headers = new Headers(options.headers || {});
  headers.set('Content-Type', 'application/json');
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), 2500);

  let response: Response;
  try {
    response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers,
      signal: options.signal || controller.signal,
    });
  } finally {
    clearTimeout(timeoutId);
  }

  if (response.status === 401) {
    removeAuthToken();
  }

  if (!response.ok) {
    const errorBody = await response.text();
    let errorMessage = `HTTP Error ${response.status}: ${response.statusText}`;
    try {
      const parsed = JSON.parse(errorBody);
      errorMessage = parsed.message || parsed.error || errorMessage;
    } catch {
      if (errorBody) errorMessage = errorBody;
    }
    throw new Error(errorMessage);
  }

  // If 204 No Content
  if (response.status === 204) {
    return {} as T;
  }

  return response.json() as Promise<T>;
}

export const apiClient = {
  // Authentication
  auth: {
    login: async (usernameOrEmail: string, password: string) => {
      const data = await fetchWithAuth<{
        token: string;
        tokenType: string;
        username: string;
        fullName: string;
        email: string;
        role: string;
        employeeId: string;
      }>('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ usernameOrEmail, password }),
      });
      if (data && data.token) {
        setAuthToken(data.token);
      }
      return data;
    },
    getCurrentUser: () => fetchWithAuth<any>('/auth/me'),
    getRoles: () => fetchWithAuth<any[]>('/auth/roles'),
  },

  // Master Data
  master: {
    getCustomers: () => fetchWithAuth<any[]>('/master/customers'),
    createCustomer: (data: any) => fetchWithAuth<any>('/master/customers', { method: 'POST', body: JSON.stringify(data) }),
    getVehicles: () => fetchWithAuth<any[]>('/master/vehicles'),
    createVehicle: (data: any) => fetchWithAuth<any>('/master/vehicles', { method: 'POST', body: JSON.stringify(data) }),
    getDrivers: () => fetchWithAuth<any[]>('/master/drivers'),
    createDriver: (data: any) => fetchWithAuth<any>('/master/drivers', { method: 'POST', body: JSON.stringify(data) }),
    getSources: () => fetchWithAuth<any[]>('/master/sources'),
    getMaterials: () => fetchWithAuth<any[]>('/master/materials'),
    getLocations: () => fetchWithAuth<any[]>('/master/locations'),
    getRates: () => fetchWithAuth<any[]>('/master/rates'),
    createRate: (data: any) => fetchWithAuth<any>('/master/rates', { method: 'POST', body: JSON.stringify(data) }),
  },

  // Trips
  trips: {
    getAll: () => fetchWithAuth<any[]>('/trips'),
    getById: (id: string) => fetchWithAuth<any>(`/trips/${id}`),
    create: (tripData: any) => fetchWithAuth<any>('/trips', { method: 'POST', body: JSON.stringify(tripData) }),
  },

  // Governance & Approvals
  governance: {
    requestCorrection: (correctionData: any) =>
      fetchWithAuth<any>('/approvals/correction-requests', { method: 'POST', body: JSON.stringify(correctionData) }),
    getPendingCorrections: () => fetchWithAuth<any[]>('/approvals/pending'),
    approveCorrection: (id: string, comments: string = 'Approved') =>
      fetchWithAuth<any>(`/approvals/${id}/approve`, { method: 'POST', body: JSON.stringify({ comments }) }),
    rejectCorrection: (id: string, comments: string) =>
      fetchWithAuth<any>(`/approvals/${id}/reject`, { method: 'POST', body: JSON.stringify({ comments }) }),
  },

  // Finance & Ledger
  finance: {
    getInvoices: () => fetchWithAuth<any[]>('/finance/invoices'),
    generateInvoice: (invoiceData: any) =>
      fetchWithAuth<any>('/finance/invoices', { method: 'POST', body: JSON.stringify(invoiceData) }),
    getPayments: () => fetchWithAuth<any[]>('/finance/payments'),
    recordPayment: (paymentData: any) =>
      fetchWithAuth<any>('/finance/payments', { method: 'POST', body: JSON.stringify(paymentData) }),
    reversePayment: (id: string, reason: string) =>
      fetchWithAuth<any>(`/finance/payments/${id}/reverse`, { method: 'POST', body: JSON.stringify({ reason }) }),
    getLedger: () => fetchWithAuth<any[]>('/finance/ledger'),
    getAccounts: () => fetchWithAuth<any[]>('/finance/accounts'),
    createContraTransfer: (transferData: any) =>
      fetchWithAuth<any>('/finance/contra-transfer', { method: 'POST', body: JSON.stringify(transferData) }),
    getDieselRecords: () => fetchWithAuth<any[]>('/finance/diesel'),
    recordDiesel: (dieselData: any) =>
      fetchWithAuth<any>('/finance/diesel', { method: 'POST', body: JSON.stringify(dieselData) }),
  },

  // Dashboards & Analytics
  dashboard: {
    getMdCockpit: () => fetchWithAuth<any>('/dashboards/md-cockpit'),
    getTruckProfitability: () => fetchWithAuth<any[]>('/dashboards/truck-profitability'),
    getAccountsPosition: () => fetchWithAuth<any>('/dashboards/accounts-position'),
  },

  // Audit Logs
  audit: {
    getLogs: () => fetchWithAuth<any[]>('/audit-logs'),
  },
};
