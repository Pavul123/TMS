'use client';

import React, { Suspense } from 'react';
import { AccountsCustomers } from '../../../../components/accounts/AccountsCustomers';

export default function AccountsSearchCustomerPage() {
  return (
    <Suspense fallback={<div className="p-6 text-[#5A6E7F]">Loading customer search...</div>}>
      <AccountsCustomers />
    </Suspense>
  );
}
