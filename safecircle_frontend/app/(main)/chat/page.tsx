'use client';

import { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Send, Lock, Info } from 'lucide-react';
import LanguageSwitcher from '@/components/LanguageSwitcher';
import TrustBadge from '@/components/TrustBadge';
import { useApp } from '@/app/providers';
import { translations } from '@/lib/translations';
import { api } from '@/lib/api';

type Message = { role: 'user' | 'assistant'; content: string };

export default function ChatPage() {
  const { language, isPrivateSession, sessionId } = useApp();
  const t = translations[language];

  const [messages, setMessages] = useState<Message[]>([
    { role: 'assistant', content: t.siraWelcome },
  ]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const bottomRef = useRef<HTMLDivElement>(null);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isLoading]);

  useEffect(() => {
    const el = textareaRef.current;
    if (el) {
      el.style.height = 'auto';
      el.style.height = `${Math.min(el.scrollHeight, 112)}px`;
    }
  }, [input]);

  // Load chat history from backend
  useEffect(() => {
    if (!sessionId) return;
    const loadHistory = async () => {
      try {
        const history = await api.getChatHistory(sessionId);
        if (history && history.length > 0) {
          const mapped = history.map(h => ({
            role: h.role === 'user' ? ('user' as const) : ('assistant' as const),
            content: h.message,
          }));
          setMessages(mapped);
        } else {
          setMessages([
            { role: 'assistant', content: t.siraWelcome },
          ]);
        }
      } catch (err) {
        console.error('Failed to load chat history:', err);
      }
    };
    loadHistory();
  }, [sessionId, t.siraWelcome]);

  const sendMessage = async (content: string) => {
    if (!content.trim() || isLoading || !sessionId) return;
    const userMsg: Message = { role: 'user', content: content.trim() };
    const next = [...messages, userMsg];
    setMessages(next);
    setInput('');
    setIsLoading(true);

    try {
      const response = await api.sendChatMessage(sessionId, content.trim(), language);
      setIsLoading(false);
      setMessages(prev => [
        ...prev,
        { role: 'assistant', content: response.reply },
      ]);
    } catch {
      setIsLoading(false);
      setMessages(prev => [
        ...prev,
        {
          role: 'assistant',
          content: language === 'en'
            ? "I'm having a bit of trouble connecting. Please try again in a moment."
            : "Mfite ikibazo cy'ihuzanye nonaha. Gerageza nyuma y'akanya gato.",
        },
      ]);
    }
  };

  const hasUserMessages = messages.some(m => m.role === 'user');

  return (
    <div className="flex flex-col bg-frost-white" style={{ minHeight: 'calc(100svh - 80px)' }}>
      {/* Chat header */}
      <div className="bg-white border-b border-mint-teal/60 px-4 pt-3 pb-2 sticky top-0 z-40">
        <div className="flex items-center justify-between gap-3">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-teal flex items-center justify-center flex-none shadow-sm">
              <span className="font-heading font-bold text-white text-lg">S</span>
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="font-heading font-semibold text-base text-deep-navy">{t.chatTitle}</h1>
                {isPrivateSession && (
                  <span className="flex items-center gap-0.5 bg-deep-navy text-white text-[10px] font-body font-semibold px-1.5 py-0.5 rounded-full">
                    <Lock size={8} className="mr-0.5" /> {t.privateMode}
                  </span>
                )}
              </div>
              <p className="font-body text-[11px] text-slate-gray">{t.chatSubtitle}</p>
            </div>
          </div>
          <LanguageSwitcher />
        </div>

        <div className="flex items-center gap-1.5 mt-2 bg-mint-teal/40 rounded-xl px-3 py-1.5">
          <Info size={11} className="text-teal flex-none" />
          <p className="font-body text-[11px] text-teal">{t.autoDelete}</p>
        </div>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto px-4 py-4">
        <div className="max-w-lg mx-auto space-y-4">
          <AnimatePresence initial={false}>
            {messages.map((msg, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0, y: 8 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.22 }}
                className={`flex gap-2 ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                {msg.role === 'assistant' && (
                  <div className="w-7 h-7 rounded-full bg-teal flex items-center justify-center flex-none mt-0.5 shadow-sm">
                    <span className="font-heading font-bold text-white text-xs">S</span>
                  </div>
                )}
                <div
                  className={`flex flex-col gap-1.5 max-w-[78%] ${
                    msg.role === 'user' ? 'items-end' : 'items-start'
                  }`}
                >
                  <div
                    className={`px-4 py-3 rounded-2xl font-body text-sm leading-relaxed ${
                      msg.role === 'user'
                        ? 'bg-teal text-white rounded-tr-sm'
                        : 'bg-white text-deep-navy border border-mint-teal/60 rounded-tl-sm shadow-sm'
                    }`}
                  >
                    {msg.content || <span className="opacity-40 italic text-xs">...</span>}
                  </div>

                  {msg.role === 'assistant' &&
                    i === messages.length - 1 &&
                    !isLoading &&
                    msg.content && (
                      <div className="flex gap-1.5 flex-wrap">
                        <TrustBadge variant="clinical" />
                        <TrustBadge variant="moh" />
                      </div>
                    )}
                </div>
              </motion.div>
            ))}
          </AnimatePresence>

          {/* Typing indicator */}
          {isLoading && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="flex items-center gap-2"
            >
              <div className="w-7 h-7 rounded-full bg-teal flex items-center justify-center flex-none shadow-sm">
                <span className="font-heading font-bold text-white text-xs">S</span>
              </div>
              <div className="bg-white border border-mint-teal/60 rounded-2xl rounded-tl-sm px-4 py-3 flex gap-1 shadow-sm">
                {[0, 1, 2].map(i => (
                  <motion.span
                    key={i}
                    className="w-1.5 h-1.5 bg-teal rounded-full block"
                    animate={{ opacity: [0.3, 1, 0.3] }}
                    transition={{ duration: 1, repeat: Infinity, delay: i * 0.2 }}
                  />
                ))}
              </div>
            </motion.div>
          )}
          <div ref={bottomRef} />
        </div>
      </div>

      {/* Suggested questions */}
      {!hasUserMessages && (
        <div className="bg-white border-t border-mint-teal/30 px-4 pt-2 pb-2">
          <p className="font-body text-[11px] text-slate-gray mb-2">{t.suggestedTitle}</p>
          <div className="flex gap-2 overflow-x-auto pb-1">
            {[t.suggested1, t.suggested2, t.suggested3, t.suggested4].map(q => (
              <button
                key={q}
                onClick={() => sendMessage(q)}
                className="flex-none px-3 py-1.5 rounded-full border border-teal/30 bg-teal/5 text-teal font-body text-xs whitespace-nowrap hover:bg-teal/10 active:bg-teal/15 transition-colors"
              >
                {q}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Disclaimer */}
      <div className="bg-white px-5 pt-1.5 pb-0.5">
        <p className="font-body text-[10px] text-slate-gray/60 text-center leading-tight">
          {t.chatWarning}
        </p>
      </div>

      {/* Input bar */}
      <div className="bg-white border-t border-mint-teal/60 px-4 py-3">
        <div className="flex gap-2 items-end max-w-lg mx-auto">
          <textarea
            ref={textareaRef}
            value={input}
            onChange={e => setInput(e.target.value)}
            onKeyDown={e => {
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage(input);
              }
            }}
            placeholder={t.chatPlaceholder}
            rows={1}
            className="flex-1 resize-none bg-frost-white border border-mint-teal/60 rounded-2xl px-4 py-2.5 font-body text-sm text-deep-navy placeholder:text-slate-gray/50 focus:outline-none focus:ring-2 focus:ring-teal/30 focus:border-teal/50 transition-all leading-relaxed"
          />
          <button
            onClick={() => sendMessage(input)}
            disabled={!input.trim() || isLoading}
            className="w-11 h-11 rounded-full bg-teal text-white flex items-center justify-center flex-none disabled:opacity-40 hover:bg-teal/90 active:scale-95 transition-all shadow-sm"
            aria-label="Send message"
          >
            <Send size={17} strokeWidth={2} />
          </button>
        </div>
      </div>
    </div>
  );
}
