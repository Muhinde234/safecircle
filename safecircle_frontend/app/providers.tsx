'use client';

import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import type { ReactNode } from 'react';
import { api } from '@/lib/api';
import type { BookmarkResponse } from '@/lib/api';

export type Language = 'en' | 'rw';

interface AppContextType {
  language: Language;
  setLanguage: (lang: Language) => void;
  isPrivateSession: boolean;
  togglePrivateSession: () => void;
  sessionId: string | null;
  nickname: string | null;
  bookmarks: BookmarkResponse[];
  addBookmark: (type: string, targetId: string) => Promise<void>;
  removeBookmark: (type: string, targetId: string) => Promise<void>;
  loadBookmarks: () => Promise<void>;
}

const AppContext = createContext<AppContextType>({
  language: 'en',
  setLanguage: () => {},
  isPrivateSession: false,
  togglePrivateSession: () => {},
  sessionId: null,
  nickname: null,
  bookmarks: [],
  addBookmark: async () => {},
  removeBookmark: async () => {},
  loadBookmarks: async () => {},
});

export function useApp() {
  return useContext(AppContext);
}

export function Providers({ children }: { children: ReactNode }) {
  const [language, setLanguageState] = useState<Language>(() => {
    if (typeof window !== 'undefined') {
      const storedLang = localStorage.getItem('sc-lang') as Language | null;
      if (storedLang === 'en' || storedLang === 'rw') return storedLang;
    }
    return 'en';
  });
  const [isPrivateSession, setIsPrivateSession] = useState(() => {
    if (typeof window !== 'undefined') {
      return !!sessionStorage.getItem('sc-private');
    }
    return false;
  });
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [nickname, setNickname] = useState<string | null>(null);
  const [bookmarks, setBookmarks] = useState<BookmarkResponse[]>([]);

  // Fetch bookmarks from backend
  const loadBookmarks = useCallback(async () => {
    const activeSessionId = sessionId || localStorage.getItem('sc-sessionId');
    if (!activeSessionId) return;
    try {
      const data = await api.getBookmarks(activeSessionId);
      setBookmarks(data || []);
    } catch (err) {
      console.error('Failed to load bookmarks:', err);
    }
  }, [sessionId]);

  // Initialize session on mount
  useEffect(() => {
    const initSession = async () => {
      let activeSessionId = localStorage.getItem('sc-sessionId');
      let activeNickname = localStorage.getItem('sc-nickname');

      if (!activeSessionId) {
        try {
          const res = await api.createAnonymousSession();
          activeSessionId = res.sessionId;
          activeNickname = res.nickname;
          localStorage.setItem('sc-sessionId', res.sessionId);
          localStorage.setItem('sc-nickname', res.nickname);
        } catch (err) {
          console.error('Failed to create anonymous session:', err);
        }
      }

      if (activeSessionId) {
        setSessionId(activeSessionId);
        setNickname(activeNickname);
      }
    };

    initSession();
  }, []);

  // Fetch bookmarks when sessionId is set
  useEffect(() => {
    if (sessionId) {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      loadBookmarks();
    }
  }, [sessionId, loadBookmarks]);

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

  const addBookmark = async (type: string, targetId: string) => {
    if (!sessionId) return;
    try {
      await api.addBookmark(sessionId, type, targetId);
      await loadBookmarks();
    } catch (err) {
      console.error('Failed to add bookmark:', err);
    }
  };

  const removeBookmark = async (type: string, targetId: string) => {
    if (!sessionId) return;
    try {
      await api.removeBookmark(sessionId, type, targetId);
      await loadBookmarks();
    } catch (err) {
      console.error('Failed to remove bookmark:', err);
    }
  };

  return (
    <AppContext.Provider
      value={{
        language,
        setLanguage,
        isPrivateSession,
        togglePrivateSession,
        sessionId,
        nickname,
        bookmarks,
        addBookmark,
        removeBookmark,
        loadBookmarks,
      }}
    >
      {children}
    </AppContext.Provider>
  );
}
