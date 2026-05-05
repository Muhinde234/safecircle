'use client';

import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';

export type Language = 'en' | 'rw';

interface AppContextType {
  language: Language;
  setLanguage: (lang: Language) => void;
  isPrivateSession: boolean;
  togglePrivateSession: () => void;
}

const AppContext = createContext<AppContextType>({
  language: 'en',
  setLanguage: () => {},
  isPrivateSession: false,
  togglePrivateSession: () => {},
});

export function useApp() {
  return useContext(AppContext);
}

export function Providers({ children }: { children: ReactNode }) {
  const [language, setLanguageState] = useState<Language>('en');
  const [isPrivateSession, setIsPrivateSession] = useState(false);

  useEffect(() => {
    const storedLang = localStorage.getItem('sc-lang') as Language | null;
    if (storedLang === 'en' || storedLang === 'rw') setLanguageState(storedLang);
    if (sessionStorage.getItem('sc-private')) setIsPrivateSession(true);
  }, []);

  const setLanguage = (lang: Language) => {
    setLanguageState(lang);
    localStorage.setItem('sc-lang', lang);
  };

  const togglePrivateSession = () => {
    setIsPrivateSession(prev => {
      const next = !prev;
      if (next) sessionStorage.setItem('sc-private', '1');
      else sessionStorage.removeItem('sc-private');
      return next;
    });
  };

  return (
    <AppContext.Provider value={{ language, setLanguage, isPrivateSession, togglePrivateSession }}>
      {children}
    </AppContext.Provider>
  );
}
