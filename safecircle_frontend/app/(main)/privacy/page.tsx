'use client';

import { motion } from 'framer-motion';
import { useRouter } from 'next/navigation';
import {
  ShieldCheck, Lock, EyeOff, Phone,
  Trash2, WifiOff, X,
} from 'lucide-react';
import type { LucideIcon } from 'lucide-react';
import Header from '@/components/Header';
import { useApp } from '@/app/providers';
import { translations } from '@/lib/translations';

/* ── animation variants ─────────────────────────────── */
const stagger = {
  hidden: {},
  show: { transition: { staggerChildren: 0.08, delayChildren: 0.15 } },
} as const;
const fadeUp = {
  hidden: { opacity: 0, y: 14 },
  show: { opacity: 1, y: 0, transition: { duration: 0.28, ease: 'easeOut' } },
} as const;

/* ── explainer card ──────────────────────────────────── */
type CardProps = { icon: LucideIcon; title: string; body: string; index: number };

function ExplainerCard({ icon: Icon, title, body }: CardProps) {
  return (
    <motion.div
      variants={fadeUp}
      className="bg-white rounded-xl shadow-sm border border-mint-teal/20 p-4 flex gap-4"
    >
      <div className="w-11 h-11 rounded-xl bg-teal/10 flex items-center justify-center flex-none">
        <Icon size={20} className="text-teal" strokeWidth={1.75} />
      </div>
      <div className="flex-1 min-w-0">
        <p className="font-heading font-semibold text-sm text-deep-navy leading-snug">{title}</p>
        <p className="font-body text-sm text-slate-gray mt-1 leading-relaxed">{body}</p>
      </div>
    </motion.div>
  );
}

/* ── page ────────────────────────────────────────────── */
export default function PrivacyPage() {
  const { language } = useApp();
  const t = translations[language];
  const router = useRouter();

  type CardDef = { icon: LucideIcon; title: string; body: string };

  const cards: CardDef[] = [
    { icon: Lock,       title: t.privC1Title, body: t.privC1Body },
    { icon: EyeOff,     title: t.privC2Title, body: t.privC2Body },
    { icon: Phone,      title: t.privC3Title, body: t.privC3Body },
    { icon: ShieldCheck,title: t.privC4Title, body: t.privC4Body },
    { icon: Trash2,     title: t.privC5Title, body: t.privC5Body },
    { icon: WifiOff,    title: t.privC6Title, body: t.privC6Body },
  ];

  const notCollected = [t.dataI1, t.dataI2, t.dataI3, t.dataI4];

  return (
    <div className="min-h-screen bg-frost-white">

      <Header title={t.privacyTitle} showBack />

      {/* ── hero ─────────────────────────────────────── */}
      <motion.div
        initial={{ opacity: 0, y: 12 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
        className="mx-4 mt-4 bg-white rounded-2xl shadow-sm border border-mint-teal/30 p-6 flex flex-col items-center text-center"
      >
        <div className="w-16 h-16 rounded-full bg-teal/10 flex items-center justify-center mb-4">
          <ShieldCheck size={32} className="text-teal" strokeWidth={1.5} />
        </div>
        <h2 className="font-heading font-bold text-xl text-deep-navy leading-snug">
          {t.privSacred}
        </h2>
        <p className="font-body text-base text-slate-gray mt-2 leading-relaxed max-w-72">
          {t.privSacredSub}
        </p>
      </motion.div>

      {/* ── explainer cards ───────────────────────────── */}
      <motion.div
        variants={stagger}
        initial="hidden"
        animate="show"
        className="px-4 mt-5 space-y-3"
      >
        {cards.map((card, i) => (
          <ExplainerCard key={card.title} {...card} index={i} />
        ))}
      </motion.div>

      {/* ── data not collected ───────────────────────── */}
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.28, ease: 'easeOut', delay: 0.55 }}
        className="mx-4 mt-5 bg-white rounded-xl shadow-sm border border-mint-teal/20 p-4"
      >
        <p className="font-heading font-semibold text-sm text-deep-navy mb-3">
          {t.dataNotTitle}
        </p>
        <div className="space-y-2.5">
          {notCollected.map(item => (
            <div key={item} className="flex items-center gap-3">
              <div className="w-5 h-5 rounded-full bg-coral/15 flex items-center justify-center flex-none">
                <X size={11} className="text-coral" strokeWidth={2.5} />
              </div>
              <p className="font-body text-sm text-deep-navy">{item}</p>
            </div>
          ))}
        </div>
      </motion.div>

      {/* ── bottom CTA ───────────────────────────────── */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.65 }}
        className="px-4 mt-6 pb-8 flex flex-col items-center gap-3"
      >
        <button
          onClick={() => router.back()}
          className="w-full h-14 bg-teal text-white font-heading font-bold text-base rounded-xl shadow-sm active:opacity-90 transition-opacity"
        >
          {t.privUnderstood}
        </button>

        <p className="font-body text-xs text-slate-gray/60 text-center">
          {t.privQuestion}
        </p>
      </motion.div>

    </div>
  );
}
