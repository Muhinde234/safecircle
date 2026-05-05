'use client';

import { Lock } from 'lucide-react';
import { useApp } from '@/app/providers';
import { translations } from '@/lib/translations';

export default function PrivateSessionBanner() {
  const { isPrivateSession, togglePrivateSession, language } = useApp();
  const t = translations[language];

  if (!isPrivateSession) return null;

  return (
    <div className="bg-deep-navy text-white px-4 py-2 flex items-center justify-between gap-3">
      <div className="flex items-center gap-2">
        <Lock size={13} strokeWidth={2} className="text-mint-teal flex-none" />
        <span className="font-body text-xs font-semibold">{t.privateActive}</span>
        <span className="font-body text-xs text-white/50">{t.privateActiveDesc}</span>
      </div>
      <button
        onClick={togglePrivateSession}
        className="font-body text-xs font-bold text-coral hover:text-coral/80 transition-colors flex-none"
      >
        {t.exitPrivate}
      </button>
    </div>
  );
}
