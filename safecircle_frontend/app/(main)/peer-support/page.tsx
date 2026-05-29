'use client';

import { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ChevronLeft, Lock, Send, Star } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useApp } from '@/app/providers';
import { translations } from '@/lib/translations';

type SessionState = 'matching' | 'chat' | 'closing' | 'rated';

interface Message {
  id: string;
  role: 'user' | 'counsellor' | 'system';
  text: string;
}

/* ── spinner ─────────────────────────────────────────── */
function MatchingSpinner() {
  return (
    <svg
      className="w-12 h-12 animate-spin text-teal"
      viewBox="0 0 48 48"
      fill="none"
      aria-hidden="true"
    >
      <circle
        cx="24" cy="24" r="20"
        stroke="currentColor"
        strokeWidth="4"
        strokeOpacity="0.2"
      />
      <path
        d="M44 24A20 20 0 0 0 24 4"
        stroke="currentColor"
        strokeWidth="4"
        strokeLinecap="round"
      />
    </svg>
  );
}

/* ── chat bubble ─────────────────────────────────────── */
function Bubble({ msg }: { msg: Message }) {
  if (msg.role === 'system') {
    return (
      <div className="flex justify-center my-1">
        <span className="font-body text-[11px] text-slate-gray/70 bg-mint-teal/40 px-3 py-1 rounded-full">
          {msg.text}
        </span>
      </div>
    );
  }
  const isUser = msg.role === 'user';
  return (
    <div className={`flex ${isUser ? 'justify-end' : 'justify-start'}`}>
      <div
        className={`max-w-[75%] px-3.5 py-2.5 rounded-2xl font-body text-sm leading-relaxed ${
          isUser
            ? 'bg-teal text-white rounded-br-sm'
            : 'bg-white text-deep-navy shadow-sm border border-mint-teal/30 rounded-bl-sm'
        }`}
      >
        {msg.text}
      </div>
    </div>
  );
}

/* ── end session modal ───────────────────────────────── */
function EndModal({
  t,
  onConfirm,
  onCancel,
}: {
  t: typeof translations.en | typeof translations.rw;
  onConfirm: () => void;
  onCancel: () => void;
}) {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 z-50 bg-deep-navy/50 flex items-end"
    >
      <motion.div
        initial={{ y: 40, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        exit={{ y: 40, opacity: 0 }}
        transition={{ duration: 0.22, ease: 'easeOut' }}
        className="w-full bg-white rounded-t-3xl px-6 pt-6 pb-10 max-w-md mx-auto"
      >
        <div className="w-10 h-1 bg-slate-gray/20 rounded-full mx-auto mb-5" />
        <h3 className="font-heading font-bold text-lg text-deep-navy text-center">
          {t.peerEndTitle}
        </h3>
        <p className="font-body text-sm text-slate-gray text-center mt-2 leading-relaxed">
          {t.peerEndBody}
        </p>
        <div className="flex flex-col gap-3 mt-6">
          <button
            onClick={onConfirm}
            className="w-full h-12 bg-coral text-white font-heading font-bold text-sm rounded-xl active:opacity-90 transition-opacity"
          >
            {t.peerEndConfirm}
          </button>
          <button
            onClick={onCancel}
            className="w-full h-12 border border-teal/40 text-teal font-heading font-semibold text-sm rounded-xl active:bg-teal/5 transition-colors"
          >
            {t.peerKeepChatting}
          </button>
        </div>
      </motion.div>
    </motion.div>
  );
}

/* ── star rating ─────────────────────────────────────── */
function StarRating({ onRate }: { onRate: (n: number) => void }) {
  const [hovered, setHovered] = useState(0);
  const [selected, setSelected] = useState(0);

  const pick = (n: number) => {
    setSelected(n);
    setTimeout(() => onRate(n), 350);
  };

  return (
    <div className="flex gap-2 justify-center">
      {[1, 2, 3, 4, 5].map(n => (
        <button
          key={n}
          onMouseEnter={() => setHovered(n)}
          onMouseLeave={() => setHovered(0)}
          onClick={() => pick(n)}
          className="p-1 transition-transform active:scale-90"
          aria-label={`${n} star`}
        >
          <Star
            size={32}
            strokeWidth={1.5}
            className={
              n <= (hovered || selected)
                ? 'text-teal fill-teal'
                : 'text-slate-gray/30'
            }
          />
        </button>
      ))}
    </div>
  );
}

/* ── page ────────────────────────────────────────────── */
export default function PeerSupportPage() {
  const { language } = useApp();
  const t = translations[language];
  const router = useRouter();

  const [sessionState, setSessionState] = useState<SessionState>('matching');
  const [showEndModal, setShowEndModal] = useState(false);
  const [inputText, setInputText] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const bottomRef = useRef<HTMLDivElement>(null);

  /* simulate counsellor connecting after 4 s */
  useEffect(() => {
    if (sessionState !== 'matching') return;
    const timer = setTimeout(() => {
      setSessionState('chat');
      setMessages([
        { id: 'sys-1', role: 'system', text: t.peerSystemMsg },
        {
          id: 'c-1',
          role: 'counsellor',
          text: language === 'en'
            ? "Hello 👋 I'm here to listen. You can talk about anything — I won't judge."
            : "Muraho 👋 Ndiri hano kukumva. Ushobora kuvuga ikintu cyose — nta makemwa.",
        },
      ]);
    }, 4000);
    return () => clearTimeout(timer);
  }, [sessionState, t.peerSystemMsg, language]);

  /* auto-scroll */
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isTyping]);

  const sendMessage = () => {
    const text = inputText.trim();
    if (!text) return;
    const userMsg: Message = { id: `u-${Date.now()}`, role: 'user', text };
    setMessages(prev => [...prev, userMsg]);
    setInputText('');

    /* simulate counsellor reply */
    setIsTyping(true);
    setTimeout(() => {
      setIsTyping(false);
      const reply: Message = {
        id: `c-${Date.now()}`,
        role: 'counsellor',
        text: language === 'en'
          ? "I hear you. Thank you for sharing that with me. How does that make you feel?"
          : "Nakumvise. Urakoze kumbwira ibyo. Birakutereje iki kumva?",
      };
      setMessages(prev => [...prev, reply]);
    }, 2000);
  };

  const handleEndConfirm = () => {
    setShowEndModal(false);
    setSessionState('closing');
  };

  /* ── matching state ──────────────────────────────── */
  if (sessionState === 'matching') {
    return (
      <div className="min-h-screen bg-frost-white flex flex-col">
        {/* top bar */}
        <header className="sticky top-0 z-40 bg-white border-b border-mint-teal/40 px-4 py-3 flex items-center gap-3">
          <button
            onClick={() => router.back()}
            className="p-1.5 -ml-1 text-deep-navy hover:text-teal transition-colors rounded-xl"
            aria-label="Go back"
          >
            <ChevronLeft size={22} strokeWidth={1.5} />
          </button>
          <p className="font-heading font-semibold text-base text-deep-navy flex-1">{t.peerTitle}</p>
        </header>

        {/* anonymity banner */}
        <div className="mx-4 mt-4 bg-mint-teal rounded-xl px-4 py-3 flex items-start gap-3">
          <Lock size={16} className="text-teal flex-none mt-0.5" strokeWidth={1.75} />
          <p className="font-body text-sm text-teal leading-relaxed">{t.peerAnonBanner}</p>
        </div>

        {/* matching content */}
        <div className="flex-1 flex flex-col items-center justify-center px-6 text-center gap-5">
          <MatchingSpinner />
          <div>
            <p className="font-heading font-semibold text-base text-deep-navy">{t.peerFinding}</p>
            <p className="font-body text-sm text-slate-gray mt-1">{t.peerFindingSub}</p>
          </div>
          <button
            onClick={() => router.back()}
            className="px-6 py-2.5 border border-teal/35 text-teal font-heading font-semibold text-sm rounded-xl hover:bg-teal/5 transition-colors"
          >
            {t.peerCancel}
          </button>
          <p className="font-body text-xs text-slate-gray/60 max-w-64 leading-relaxed">
            {t.peerDisclaimer}
          </p>
        </div>
      </div>
    );
  }

  /* ── closing / rated state ───────────────────────── */
  if (sessionState === 'closing' || sessionState === 'rated') {
    return (
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        className="min-h-screen bg-frost-white flex flex-col items-center justify-center px-6 text-center gap-6"
      >
        <div className="w-20 h-20 rounded-full bg-teal/10 flex items-center justify-center">
          <Lock size={36} className="text-teal" strokeWidth={1.25} />
        </div>
        <div>
          <h2 className="font-heading font-bold text-xl text-deep-navy">{t.peerThankYou}</h2>
          {sessionState === 'closing' && (
            <p className="font-body text-sm text-slate-gray mt-2">{t.peerRateSub}</p>
          )}
        </div>

        {sessionState === 'closing' && (
          <div className="w-full max-w-xs">
            <p className="font-body text-sm text-slate-gray mb-3">{t.peerRateSession}</p>
            <StarRating onRate={() => setSessionState('rated')} />
          </div>
        )}

        <button
          onClick={() => {
            setSessionState('matching');
            setMessages([]);
          }}
          className="w-full max-w-xs h-13 bg-teal text-white font-heading font-bold text-sm rounded-xl shadow-sm active:opacity-90 transition-opacity"
        >
          {t.peerNewSession}
        </button>
      </motion.div>
    );
  }

  /* ── chat state ──────────────────────────────────── */
  return (
    <div className="min-h-screen bg-frost-white flex flex-col">

      {/* top bar */}
      <header className="sticky top-0 z-40 bg-white border-b border-mint-teal/40 px-4 py-3 flex items-center gap-3">
        <button
          onClick={() => router.back()}
          className="p-1.5 -ml-1 text-deep-navy hover:text-teal transition-colors rounded-xl"
          aria-label="Go back"
        >
          <ChevronLeft size={22} strokeWidth={1.5} />
        </button>
        <div className="flex-1 min-w-0">
          <p className="font-heading font-semibold text-base text-deep-navy leading-tight">
            {t.peerTitle}
          </p>
          <span className="inline-flex items-center gap-1 bg-health-green/15 text-health-green font-body text-[10px] font-semibold px-2 py-0.5 rounded-full mt-0.5">
            <span className="w-1.5 h-1.5 bg-health-green rounded-full" />
            {t.peerConnected}
          </span>
        </div>
        <button
          onClick={() => setShowEndModal(true)}
          className="flex-none px-3 py-1.5 border border-teal/30 text-teal font-heading font-semibold text-xs rounded-xl hover:bg-teal/5 transition-colors"
        >
          {t.peerEndChat}
        </button>
      </header>

      {/* anonymity banner */}
      <div className="mx-4 mt-3 bg-mint-teal rounded-xl px-4 py-2.5 flex items-start gap-2.5">
        <Lock size={14} className="text-teal flex-none mt-0.5" strokeWidth={1.75} />
        <p className="font-body text-xs text-teal leading-relaxed">{t.peerAnonBanner}</p>
      </div>

      {/* messages */}
      <div className="flex-1 overflow-y-auto px-4 py-4 space-y-3">
        <AnimatePresence initial={false}>
          {messages.map(msg => (
            <motion.div
              key={msg.id}
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.2 }}
            >
              {msg.role !== 'user' && msg.role !== 'system' && (
                <p className="font-body text-[10px] text-slate-gray/60 mb-1 ml-1">
                  {t.peerCounsellorName}
                </p>
              )}
              <Bubble msg={msg} />
            </motion.div>
          ))}
        </AnimatePresence>

        {/* typing indicator */}
        {isTyping && (
          <div className="flex flex-col gap-1">
            <p className="font-body text-[10px] text-slate-gray/60 ml-1 italic">
              {t.peerTypingIndicator}
            </p>
            <div className="flex items-center gap-1 bg-white border border-mint-teal/30 rounded-2xl rounded-bl-sm px-4 py-3 w-16 shadow-sm">
              {[0, 0.15, 0.3].map((d, i) => (
                <motion.span
                  key={i}
                  className="w-1.5 h-1.5 bg-slate-gray/40 rounded-full"
                  animate={{ y: [0, -4, 0] }}
                  transition={{ repeat: Infinity, duration: 0.7, delay: d }}
                />
              ))}
            </div>
          </div>
        )}

        <div ref={bottomRef} />
      </div>

      {/* input bar */}
      <div className="bg-white border-t border-mint-teal/40 px-4 py-3 pb-safe flex items-center gap-2.5">
        <input
          type="text"
          value={inputText}
          onChange={e => setInputText(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && sendMessage()}
          placeholder={t.chatPlaceholder}
          className="flex-1 px-4 py-2.5 bg-frost-white border border-mint-teal/60 rounded-full font-body text-sm text-deep-navy placeholder:text-slate-gray/50 focus:outline-none focus:ring-2 focus:ring-teal/30 focus:border-teal transition-all"
        />
        <button
          onClick={sendMessage}
          disabled={!inputText.trim()}
          className="w-10 h-10 bg-teal text-white rounded-full flex items-center justify-center flex-none shadow-sm disabled:opacity-40 active:opacity-90 transition-all"
          aria-label="Send"
        >
          <Send size={17} strokeWidth={2} />
        </button>
      </div>

      {/* end session modal */}
      <AnimatePresence>
        {showEndModal && (
          <EndModal
            t={t}
            onConfirm={handleEndConfirm}
            onCancel={() => setShowEndModal(false)}
          />
        )}
      </AnimatePresence>

    </div>
  );
}
