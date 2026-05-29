'use client';

import { motion } from 'framer-motion';
import Link from 'next/link';
import {
  MessageCircle, MapPin, BookOpen, HeartPulse,
  Bell, User, Lock, Phone, ChevronRight, ShieldCheck
} from 'lucide-react';
import type { LucideIcon } from 'lucide-react';
import SafeCircleLogo from '@/components/SafeCircleLogo';
import { useApp } from '@/app/providers';
import { translations } from '@/lib/translations';

/* ── animation variants ─────────────────────────────── */
const stagger = {
  hidden: {},
  show: { transition: { staggerChildren: 0.08, delayChildren: 0.12 } },
} as const;

const fadeUp = {
  hidden: { opacity: 0, y: 12 },
  show: { opacity: 1, y: 0, transition: { duration: 0.25, ease: 'easeOut' } },
} as const;

/* ── quick-action card ───────────────────────────────── */
type ActionCardProps = {
  href: string;
  icon: LucideIcon;
  title: string;
  desc: string;
};

function ActionCard({ href, icon: Icon, title, desc }: ActionCardProps) {
  return (
    <Link
      href={href}
      className="block bg-mint-teal rounded-xl p-4 hover:scale-[1.02] active:scale-[0.98] transition-transform duration-150"
    >
      <div className="w-10 h-10 bg-white/70 rounded-xl flex items-center justify-center mb-3">
        <Icon size={20} className="text-teal" strokeWidth={1.75} />
      </div>
      <p className="font-heading font-semibold text-sm text-deep-navy leading-snug">
        {title}
      </p>
      <p className="font-body text-[11px] text-slate-gray mt-0.5 leading-tight">
        {desc}
      </p>
    </Link>
  );
}

/* ── page ────────────────────────────────────────────── */
export default function DashboardPage() {
  const { language, isPrivateSession, togglePrivateSession } = useApp();
  const t = translations[language];

  const actions: ActionCardProps[] = [
    { href: '/chat',         icon: MessageCircle, title: t.askAnything,   desc: t.askAnythingDesc  },
    { href: '/clinics',      icon: MapPin,         title: t.findClinic,    desc: t.clinicDesc       },
    { href: '/feed',         icon: BookOpen,       title: t.healthTips,    desc: t.healthTipsDesc   },
    { href: '/peer-support', icon: HeartPulse,     title: t.talkToSomeone, desc: t.talkToSomeoneDesc},
  ];

  return (
    <div className="min-h-screen bg-frost-white">

      {/* ── top navigation bar ─────────────────────────── */}
      <header className="sticky top-0 z-40 bg-white border-b border-mint-teal/50">
        <div className="flex items-center px-5 py-3 max-w-md mx-auto">

          {/* Logo + wordmark */}
          <div className="flex items-center gap-2">
            <SafeCircleLogo size={26} className="text-teal" />
            <span className="font-heading font-bold text-[15px] text-deep-navy">
              Safe Circle
            </span>
          </div>

          <div className="flex-1" />

          {/* Right actions */}
          <div className="flex items-center gap-0.5">

            {/* Notification bell */}
            <button
              className="relative p-2 rounded-xl hover:bg-frost-white transition-colors"
              aria-label="Notifications"
            >
              <Bell size={20} className="text-slate-gray" strokeWidth={1.5} />
              {/* Unread indicator */}
              <span className="absolute top-2 right-2 w-1.5 h-1.5 bg-coral rounded-full pointer-events-none" />
            </button>

            {/* Anonymous avatar */}
            <button
              className="p-2 rounded-xl hover:bg-frost-white transition-colors"
              aria-label="Profile"
            >
              <User size={20} className="text-slate-gray" strokeWidth={1.5} />
            </button>

            {/* Private session toggle */}
            <button
              onClick={togglePrivateSession}
              aria-label={isPrivateSession ? 'Disable private session' : 'Enable private session'}
              className={`flex items-center gap-1 px-2.5 py-1.5 rounded-xl transition-all duration-150 ${
                isPrivateSession
                  ? 'bg-amber-100 text-amber-600'
                  : 'hover:bg-frost-white text-slate-gray'
              }`}
            >
              <Lock size={18} strokeWidth={isPrivateSession ? 2 : 1.5} />
              {isPrivateSession && (
                <span className="font-heading font-bold text-[11px] text-amber-600">
                  {t.privateMode}
                </span>
              )}
            </button>

          </div>
        </div>
      </header>

      {/* ── scrollable content ─────────────────────────── */}
      <div className="max-w-md mx-auto px-5 pb-6">

        {/* Greeting */}
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.25, ease: 'easeOut' }}
          className="pt-5 pb-4"
        >
          <h1 className="font-heading font-bold text-[26px] text-deep-navy leading-tight">
            {t.dashHello} 👋
          </h1>
          <p className="font-body text-sm text-slate-gray mt-1">
            {t.dashSubtitle}
          </p>
        </motion.div>

        {/* Quick action 2×2 grid */}
        <motion.div
          variants={stagger}
          initial="hidden"
          animate="show"
          className="grid grid-cols-2 gap-3"
        >
          {actions.map((action) => (
            <motion.div key={action.href} variants={fadeUp}>
              <ActionCard {...action} />
            </motion.div>
          ))}
        </motion.div>

        {/* Health Tip of the Day */}
        <motion.div
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.28, ease: 'easeOut', delay: 0.38 }}
          className="mt-5 bg-white rounded-xl shadow-sm overflow-hidden flex"
        >
          {/* Left teal accent bar */}
          <div className="w-1 bg-teal flex-none" />

          <div className="p-4 flex-1 min-w-0">
            <p className="font-heading font-semibold text-[10px] text-teal uppercase tracking-wider">
              {t.tipOfDay}
            </p>
            <p className="font-body text-sm text-deep-navy leading-relaxed mt-1.5">
              {t.tipText}
            </p>
            {/* Ghost "Read More" button */}
            <Link
              href="/learn"
              className="mt-3 inline-flex items-center gap-1 font-body text-xs text-teal font-semibold hover:text-teal/75 transition-colors"
            >
              {t.readMore}
              <ChevronRight size={12} strokeWidth={2.5} />
            </Link>
          </div>
        </motion.div>

        {/* Risk Assessment Banner */}
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.42 }}
          className="mt-4 bg-teal/5 rounded-2xl p-4 flex items-center gap-3 border border-teal/15"
        >
          <div className="w-10 h-10 rounded-xl bg-teal/10 flex items-center justify-center flex-none">
            <ShieldCheck size={18} className="text-teal" strokeWidth={1.5} />
          </div>
          <div className="flex-1 min-w-0">
            <p className="font-heading font-semibold text-sm text-deep-navy">
              {language === 'en' ? 'Unsure about an exposure?' : 'Ufite impungenge ku gikorwa cyabaye?'}
            </p>
            <p className="font-body text-[11px] text-slate-gray mt-0.5">
              {language === 'en' ? 'Take our confidential risk assessment.' : 'Kora isuzuma ryacu ry\'ibanga.'}
            </p>
          </div>
          <Link
            href="/risk"
            className="flex-none px-3.5 py-2 bg-teal text-white font-heading font-bold text-xs rounded-xl shadow-sm hover:opacity-90 active:scale-95 transition-all"
          >
            {language === 'en' ? 'Check Now' : 'Genzura'}
          </Link>
        </motion.div>

        {/* Emergency banner */}
        <motion.a
          href="tel:08007233"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.48 }}
          className="mt-4 flex items-center gap-3 bg-coral rounded-xl px-4 py-3.5 active:opacity-90 transition-opacity"
        >
          <div className="w-9 h-9 bg-white/20 rounded-xl flex items-center justify-center flex-none">
            <Phone size={18} className="text-white" strokeWidth={2} />
          </div>

          <div className="flex-1 min-w-0">
            <p className="font-heading font-bold text-sm text-white">
              {t.urgentHelp}
            </p>
            <p className="font-body text-xs text-white/85 mt-0.5">
              {t.callFreeNumber}
            </p>
          </div>

          <ChevronRight size={16} className="text-white/60 flex-none" strokeWidth={2} />
        </motion.a>

      </div>
    </div>
  );
}
