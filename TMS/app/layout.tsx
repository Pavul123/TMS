import './globals.css';
import type { Metadata } from 'next';
export const metadata: Metadata = { title: 'TRANSLOGIX | Worker Portal', description: 'Operational trip workspace' };
export default function RootLayout({ children }: Readonly<{children: React.ReactNode}>) { return <html lang="en"><body>{children}</body></html>; }
