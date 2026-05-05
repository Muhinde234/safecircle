'use client';

import { ChevronLeft } from 'lucide-react';
import { useRouter } from 'next/navigation';
import type { ReactNode } from 'react';
import LanguageSwitcher from './LanguageSwitcher';

interface HeaderProps {
  title: string;
  subtitle?: string;
  showBack?: boolean;
  showLanguage?: boolean;
  right?: ReactNode;
}

export default function Header({
  title,
  subtitle,
  showBack = false,
  showLanguage = true,
  right,
}: HeaderProps) {
  const router = useRouter();

  return (
    <header className="bg-white border-b border-mint-teal/60 px-4 py-3 flex items-center gap-3 sticky top-0 z-40">
      {showBack && (
        <button
          onClick={() => router.back()}
          className="p-1.5 -ml-1.5 text-deep-navy hover:text-teal transition-colors rounded-xl"
          aria-label="Go back"
        >
          <ChevronLeft size={22} strokeWidth={1.5} />
        </button>
      )}

      <div className="flex-1 min-w-0">
        <h1 className="font-heading font-semibold text-lg text-deep-navy leading-tight truncate">
          {title}
        </h1>
        {subtitle && (
          <p className="font-body text-xs text-slate-gray truncate">{subtitle}</p>
        )}
      </div>

      <div className="flex items-center gap-2 flex-none">
        {right}
        {showLanguage && <LanguageSwitcher />}
      </div>
    </header>
  );
}
