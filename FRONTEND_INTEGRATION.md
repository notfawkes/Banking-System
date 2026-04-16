# Next.js Integration Guide

This document outlines how to consume the Spring Boot Banking API directly from a React / Next.js frontend frontend.

## Global Fetch configuration Setup
Because the Java Backend uses JWT Authentication mapped strictly using a `Bearer` token inside the `Authorization` header, you will need to establish authorization state inside your Next.js frontend (e.g. saving the Token inside `localStorage`, `Zustand` or Next-Auth).

**Example API URL setup:**
```typescript
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";
```

A reusable wrapper for authenticated requests:
```typescript
const fetchWithAuth = async (endpoint: string, options: RequestInit = {}) => {
  const token = localStorage.getItem("jwt_token");
  return fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });
};
```

---

## 1. Authentication

### Register a User (`POST /auth/register`)
This endpoint accepts plain text JSON. It returns a string confirming registration or a 400 Bad Request error if validation fails or email exists.

```typescript
const registerUser = async (name, email, password) => {
  const res = await fetch(`${API_BASE_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, email, password }),
  });
  
  if (!res.ok) throw new Error("Registration Failed");
  return await res.text();
};
```

### Log in (`POST /auth/login`)
Logging in returns the critically required JWT authorization token.

```typescript
const loginUser = async (email, password) => {
  const res = await fetch(`${API_BASE_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });
  
  if (!res.ok) throw new Error("Invalid username/password");
  const data = await res.json();
  
  // IMMEDIATELY Save token safely.
  localStorage.setItem("jwt_token", data.token); 
  return data;
};
```

---

## 2. Profiles and Accounts

### Get Current User Profile (`GET /users/me`)

```typescript
const fetchMe = async () => {
  const res = await fetchWithAuth("/users/me");
  if (!res.ok) throw new Error("Not Authenticated");
  return await res.json(); // Returns { id, name, email, createdAt }
};
```

### Create a Bank Account (`POST /accounts`)
Accounts are created dynamically for the logged-in user. Every account begins with $0.00.

```typescript
const createAccount = async () => {
  const res = await fetchWithAuth("/accounts", { method: "POST" });
  if (!res.ok) throw new Error("Failed to create account");
  return await res.json();
};
```

### Get My Accounts (`GET /accounts`)

```typescript
const getAccounts = async () => {
  const res = await fetchWithAuth("/accounts");
  return await res.json(); // Returns Array of accounts objects
};
```

---

## 3. Financial Actions

### Deposit Money into Account (`POST /accounts/{id}/deposit`)

```typescript
const depositMoney = async (accountId, amount) => {
  const res = await fetchWithAuth(`/accounts/${accountId}/deposit`, {
    method: "POST",
    body: JSON.stringify({ amount }),
  });
  
  if (!res.ok) throw new Error("Deposit Failed");
  return await res.json(); // Returns the updated account object
};
```

### Withdraw Money from My Account (`POST /accounts/{id}/withdraw`)

```typescript
const withdrawMoney = async (accountId, amount) => {
  const res = await fetchWithAuth(`/accounts/${accountId}/withdraw`, {
    method: "POST",
    body: JSON.stringify({ amount }),
  });
  
  if (!res.ok) {
     const errorPayload = await res.json();
     throw new Error(errorPayload.message || "Withdrawal Failed");
  }
  return await res.json();
};
```

### Transfer Money to Another Account (`POST /transactions/transfer`)
Transfer internally queries two ID's and dynamically debits / credits while establishing a ledger Transaction record.

```typescript
const transferMoney = async (fromAccountId, toAccountId, amount) => {
  const res = await fetchWithAuth(`/transactions/transfer`, {
    method: "POST",
    body: JSON.stringify({ 
        fromAccountId, 
        toAccountId, 
        amount 
    }),
  });
  
  if (!res.ok) {
     const errorResponse = await res.json();
     throw new Error(errorResponse.message || "Transfer Failed");
  }
  return await res.json(); // Returns the executed transaction record
};
```

### View Transaction Ledger (`GET /transactions`)

```typescript
const getMyLedger = async () => {
  const res = await fetchWithAuth("/transactions");
  return await res.json(); // Array of all transactions involving YOUR accounts.
};
```
