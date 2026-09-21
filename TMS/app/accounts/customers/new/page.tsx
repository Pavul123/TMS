'use client';

import React, { Suspense } from 'react';
import { AccountsCustomers } from '../../../../components/accounts/AccountsCustomers';

export default function AccountsNewCustomerPage() {
  return (
    <Suspense fallback={<div className="p-6 text-[#5A6E7F]">Loading new customer form...</div>}>
      <AccountsCustomers />
    </Suspense>
  );
}
