'use client';

import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  User, ShieldCheck, Info, Trash2,
  Lock, Bell, Type, Globe, ChevronRight,
} from 'lucide-react';
import Link from 'next/link';
import { useApp } from '@/app/providers';
import { translations } from '@/lib/translations';

/* ── animation ───────────────────────────────────────── */
const slideIn = {
  hidden: { opacity: 0, x: -10 },
  show: { opacity: 1, x: 0, transition: { duration: 0.25, ease: 'easeOut' } },
};
const stagger = {
  hidden: {},
  show: { transition: { staggerChildren: 0.07, delayChildren: 0.05 } },
};

/* ── toggle switch ───────────────────────────────────── */
function Toggle({
  on,
  onChange,
  label,
}: {
  on: boolean;
  onChange: (v: boolean) => void;
  label?: string;
}) {
  return (
    <button
      role="switch"
      aria-checked={on}
      aria-label={label}
      onClick={() => onChange(!on)}
      className={`relative w-12 h-7 rounded-full transition-colors duration-200 flex-none ${
        on ? 'bg-teal' : 'bg-slate-gray/25'
      }`}
    >
      <motion.span
        layout
        transition={{ type: 'spring', stiffness: 500, damping: 30 }}
        className={`absolute top-0.5 w-6 h-6 bg-white rounded-full shadow-sm ${
          on ? 'left-[22px]' : 'left-0.5'
        }`}
      />
    </button>
  );
}

/* ── section card ────────────────────────────────────── */
function SectionCard({ children }: { children: React.ReactNode }) {
  return (
    <div className="bg-white rounded-2xl shadow-sm border border-mint-teal/20 overflow-hidden divide-y divide-mint-teal/15">
      {children}
    </div>
  );
}

/* ── row ─────────────────────────────────────────────── */
function Row({ children }: { children: React.ReactNode }) {
  return <div className="flex items-center gap-3 px-4 py-3.5">{children}</div>;
}

/* ── erase confirmation modal ────────────────────────── */
function EraseModal({
  t,
  onConfirm,
  onCancel,
}: {
  t: typeof translations.en;
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
        initial={{ y: 48, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        exit={{ y: 48, opacity: 0 }}
        transition={{ duration: 0.22, ease: 'easeOut' }}
        className="w-full bg-white rounded-t-3xl px-6 pt-6 pb-12 max-w-md mx-auto"
      >
        <div className="w-10 h-1 bg-slate-gray/20 rounded-full mx-auto mb-5" />
        <div className="w-12 h-12 bg-coral/10 rounded-full flex items-center justify-center mx-auto mb-4">
          <Trash2 size={22} className="text-coral" strokeWidth={1.75} />
        </div>
        <h3 className="font-heading font-bold text-lg text-deep-navy text-center">
          {t.eraseData}
        </h3>
        <p className="font-body text-sm text-slate-gray text-center mt-2 leading-relaxed">
          {t.eraseConfirmBody}
        </p>
        <div className="flex flex-col gap-3 mt-6">
          <button
            onClick={onConfirm}
            className="w-full h-12 bg-coral text-white font-heading font-bold text-sm rounded-xl active:opacity-90 transition-opacity"
          >
            {t.eraseConfirmBtn}
          </button>
          <button
            onClick={onCancel}
            className="w-full h-12 border border-slate-gray/20 text-slate-gray font-heading font-semibold text-sm rounded-xl active:bg-slate-gray/5 transition-colors"
          >
            {t.cancelBtn}
          </button>
        </div>
      </motion.div>
    </motion.div>
  );
}

/* ── text size selector ──────────────────────────────── */
type TextSize = 'small' | 'medium' | 'large';

/* ── page ────────────────────────────────────────────── */
export default function ProfilePage() {
  const { language, setLanguage, isPrivateSession, togglePrivateSession } = useApp();
  const t = translations[language];

  const [notifications, setNotifications] = useState(false);
  const [autoDelete, setAutoDelete] = useState(true);
  const [textSize, setTextSize] = useState<TextSize>('medium');
  const [showEraseModal, setShowEraseModal] = useState(false);
  const [erased, setErased] = useState(false);

  const handleErase = () => {
    setShowEraseModal(false);
    setErased(true);
  };

  const textSizes: { key: TextSize; label: string }[] = [
    { key: 'small',  label: t.textSmall  },
    { key: 'medium', label: t.textMedium },
    { key: 'large',  label: t.textLarge  },
  ];

  return (
    <div className="min-h-screen bg-frost-white pb-6">

      {/* ── top bar ──────────────────────────────────── */}
      <header className="bg-white border-b border-mint-teal/40 px-5 py-3.5 sticky top-0 z-40">
        <h1 className="font-heading font-semibold text-lg text-deep-navy">
          {t.profileTitle}
        </h1>
      </header>

      {/* ── avatar + display name ─────────────────────── */}
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.25 }}
        className="flex flex-col items-center pt-6 pb-4 px-5"
      >
        <div className="w-20 h-20 rounded-full bg-mint-teal flex items-center justify-center shadow-sm">
          <User size={36} className="text-teal" strokeWidth={1.5} />
        </div>
        <p className="font-heading font-bold text-lg text-deep-navy mt-3">
          {t.displayName} #4829
        </p>
        <p className="font-body text-xs text-slate-gray mt-0.5">{t.memberSince}</p>
      </motion.div>

      <motion.div
        variants={stagger}
        initial="hidden"
        animate="show"
        className="px-4 space-y-5"
      >

        {/* ── Section 1: Preferences ───────────────────── */}
        <motion.div variants={slideIn}>
          <p className="font-heading font-semibold text-[11px] text-slate-gray uppercase tracking-widest mb-2 px-1">
            {t.preferencesTitle}
          </p>
          <SectionCard>

            {/* Language */}
            <Row>
              <Globe size={18} className="text-teal flex-none" strokeWidth={1.5} />
              <p className="font-body text-sm text-deep-navy flex-1">{t.settingLanguage}</p>
              <div className="flex items-center bg-mint-teal rounded-full p-0.5">
                {(['en', 'rw'] as const).map(lang => (
                  <button
                    key={lang}
                    onClick={() => setLanguage(lang)}
                    className={`px-3 py-1 rounded-full text-xs font-heading font-bold transition-all duration-150 ${
                      language === lang ? 'bg-teal text-white shadow-sm' : 'text-teal'
                    }`}
                  >
                    {lang.toUpperCase()}
                  </button>
                ))}
              </div>
            </Row>

            {/* Notifications */}
            <Row>
              <Bell size={18} className="text-teal flex-none" strokeWidth={1.5} />
              <div className="flex-1 min-w-0">
                <p className="font-body text-sm text-deep-navy">{t.settingNotifications}</p>
                <p className="font-body text-xs text-slate-gray mt-0.5 leading-tight">{t.settingNotifDesc}</p>
              </div>
              <Toggle on={notifications} onChange={setNotifications} label={t.settingNotifications} />
            </Row>

            {/* Text size */}
            <Row>
              <Type size={18} className="text-teal flex-none" strokeWidth={1.5} />
              <p className="font-body text-sm text-deep-navy flex-1">{t.settingTextSize}</p>
              <div className="flex gap-1">
                {textSizes.map(({ key, label }) => (
                  <button
                    key={key}
                    onClick={() => setTextSize(key)}
                    className={`px-2.5 py-1 rounded-lg font-body text-xs font-semibold transition-colors ${
                      textSize === key
                        ? 'bg-teal text-white'
                        : 'bg-mint-teal/40 text-teal'
                    }`}
                  >
                    {label}
                  </button>
                ))}
              </div>
            </Row>

          </SectionCard>
        </motion.div>

        {/* ── Section 2: Privacy & Safety ─────────────── */}
        <motion.div variants={slideIn}>
          <p className="font-heading font-semibold text-[11px] text-slate-gray uppercase tracking-widest mb-2 px-1">
            {t.privacySafetyTitle}
          </p>
          <SectionCard>

            {/* Private Session Mode */}
            <Row>
              <Lock size={18} className="text-teal flex-none" strokeWidth={1.5} />
              <div className="flex-1 min-w-0">
                <p className="font-body text-sm text-deep-navy">{t.settingPrivateMode}</p>
                <p className="font-body text-xs text-slate-gray mt-0.5">{t.settingPrivateModeDesc}</p>
              </div>
              <Toggle on={isPrivateSession} onChange={togglePrivateSession} label={t.settingPrivateMode} />
            </Row>

            {/* Auto-delete */}
            <Row>
              <Trash2 size={18} className="text-teal flex-none" strokeWidth={1.5} />
              <div className="flex-1 min-w-0">
                <p className="font-body text-sm text-deep-navy">{t.settingAutoDelete}</p>
                <p className="font-body text-xs text-slate-gray mt-0.5">{t.settingAutoDeleteDesc}</p>
              </div>
              <Toggle on={autoDelete} onChange={setAutoDelete} label={t.settingAutoDelete} />
            </Row>

            {/* Clear saved content */}
            <button className="flex items-center gap-3 px-4 py-3.5 w-full hover:bg-frost-white transition-colors active:bg-mint-teal/20">
              <Trash2 size={18} className="text-slate-gray flex-none" strokeWidth={1.5} />
              <p className="font-body text-sm text-slate-gray flex-1 text-left">{t.clearSaved}</p>
            </button>

          </SectionCard>
        </motion.div>

        {/* ── Section 3: About ─────────────────────────── */}
        <motion.div variants={slideIn}>
          <p className="font-heading font-semibold text-[11px] text-slate-gray uppercase tracking-widest mb-2 px-1">
            {t.aboutTitle}
          </p>
          <SectionCard>

            <Link
              href="/privacy"
              className="flex items-center gap-3 px-4 py-3.5 hover:bg-frost-white transition-colors active:bg-mint-teal/20"
            >
              <ShieldCheck size={18} className="text-teal flex-none" strokeWidth={1.5} />
              <p className="font-body text-sm text-deep-navy flex-1">{t.privacyTitle}</p>
              <ChevronRight size={16} className="text-slate-gray/50" strokeWidth={1.5} />
            </Link>

            <Link
              href="/sources"
              className="flex items-center gap-3 px-4 py-3.5 hover:bg-frost-white transition-colors active:bg-mint-teal/20"
            >
              <Info size={18} className="text-teal flex-none" strokeWidth={1.5} />
              <p className="font-body text-sm text-deep-navy flex-1">{t.healthSources}</p>
              <ChevronRight size={16} className="text-slate-gray/50" strokeWidth={1.5} />
            </Link>

            <Row>
              <p className="font-body text-xs text-slate-gray/60 w-full text-center py-0.5">
                {t.appVersion}
              </p>
            </Row>

          </SectionCard>
        </motion.div>

        {/* ── Danger Zone ───────────────────────────────── */}
        <motion.div variants={slideIn}>
          <div className="bg-blush rounded-2xl border border-coral/15 overflow-hidden">
            <p className="font-heading font-semibold text-[11px] text-coral/70 uppercase tracking-widest px-4 pt-3.5 pb-2">
              {t.dangerZoneTitle}
            </p>
            <button
              onClick={() => setShowEraseModal(true)}
              className="flex items-center gap-3 px-4 py-3.5 w-full hover:bg-coral/5 transition-colors active:bg-coral/10"
            >
              <Trash2 size={18} className="text-coral flex-none" strokeWidth={1.75} />
              <p className="font-body text-sm font-semibold text-coral flex-1 text-left">
                {t.eraseData}
              </p>
            </button>
          </div>
        </motion.div>

      </motion.div>

      {/* erase confirmation */}
      <AnimatePresence>
        {showEraseModal && (
          <EraseModal
            t={t}
            onConfirm={handleErase}
            onCancel={() => setShowEraseModal(false)}
          />
        )}
      </AnimatePresence>

      {/* erased confirmation */}
      <AnimatePresence>
        {erased && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0 }}
            className="fixed bottom-24 left-4 right-4 bg-deep-navy text-white font-body text-sm text-center px-4 py-3 rounded-xl shadow-lg z-50 max-w-md mx-auto"
          >
            {language === 'en' ? 'All data erased.' : 'Amakuru yose asibwe.'}
          </motion.div>
        )}
      </AnimatePresence>

    </div>
  );
}
