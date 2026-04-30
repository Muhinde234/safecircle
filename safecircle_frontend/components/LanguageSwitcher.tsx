'use client';

import { useApp } from '@/app/providers';

export default function LanguageSwitcher() {
  const { language, setLanguage } = useApp();

  return (
    <button
      onClick={() => setLanguage(language === 'en' ? 'rw' : 'en')}
      className="px-3 py-1.5 rounded-full border border-teal/30 bg-teal/5 text-teal font-body text-xs font-semibold hover:bg-teal/10 active:bg-teal/15 transition-colors"
      aria-label="Switch language"
    >
      {language === 'en' ? 'RW' : 'EN'}
    </button>
  );
}
