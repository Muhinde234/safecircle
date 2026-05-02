'use client';

import { motion } from 'framer-motion';
import {
  Lock, Clock, BellOff, ShieldCheck, CheckCircle,
  Award, Eye, Database,
} from 'lucide-react';
import type { ElementType } from 'react';
import Header from '@/components/Header';
import SafeCircleLogo from '@/components/SafeCircleLogo';
import TrustBadge from '@/components/TrustBadge';
import { useApp } from '@/app/providers';
import { translations } from '@/lib/translations';

interface PrivacyPoint {
  icon: ElementType;
  title: string;
  body: string;
  color: string;
  bg: string;
}

export default function PrivacyPage() {
  const { language, isPrivateSession, togglePrivateSession } = useApp();
  const t = translations[language];

  const points: PrivacyPoint[] = [
    { icon: Lock, title: t.privacyPoint1Title, body: t.privacyPoint1, color: 'text-teal', bg: 'bg-teal/10' },
    { icon: Clock, title: t.privacyPoint2Title, body: t.privacyPoint2, color: 'text-health-green', bg: 'bg-health-green/10' },
    { icon: BellOff, title: t.privacyPoint3Title, body: t.privacyPoint3, color: 'text-coral', bg: 'bg-coral/10' },
    { icon: Eye, title: t.privacyPoint4Title, body: t.privacyPoint4, color: 'text-deep-navy', bg: 'bg-deep-navy/10' },
    { icon: CheckCircle, title: t.privacyPoint5Title, body: t.privacyPoint5, color: 'text-health-green', bg: 'bg-health-green/10' },
    { icon: Award, title: t.privacyPoint6Title, body: t.privacyPoint6, color: 'text-teal', bg: 'bg-teal/10' },
  ];

  return (
    <div className="bg-frost-white min-h-screen">
      <Header title={t.privacyTitle} subtitle={t.privacyPageSubtitle} />

      {/* Hero */}
      <motion.div
        initial={{ opacity: 0, y: 12 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.35 }}
        className="mx-4 mt-4 bg-teal rounded-2xl p-5 flex flex-col items-center text-center relative overflow-hidden"
      >
        <div className="absolute -top-8 -right-8 w-32 h-32 rounded-full bg-white/10 pointer-events-none" />
        <div className="absolute -bottom-4 -left-4 w-20 h-20 rounded-full bg-white/5 pointer-events-none" />
        <div className="relative z-10">
          <SafeCircleLogo size={56} className="text-white mx-auto" />
          <h2 className="font-heading font-bold text-xl text-white mt-3">{t.privacyTitle}</h2>
          <p className="font-body text-sm text-mint-teal mt-1 leading-relaxed">{t.privacyPageSubtitle}</p>
          <div className="flex gap-2 justify-center mt-3 flex-wrap">
            <TrustBadge variant="moh" size="md" label={t.mohApproved} />
            <TrustBadge variant="clinical" size="md" label={t.clinicallyVerified} />
          </div>
        </div>
      </motion.div>

      {/* Privacy points */}
      <div className="px-4 mt-5 space-y-3">
        {points.map(({ icon: Icon, title, body, color, bg }, i) => (
          <motion.div
            key={title}
            initial={{ opacity: 0, x: -8 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.25, delay: i * 0.06 }}
            className="bg-white rounded-2xl p-4 flex gap-4 shadow-sm border border-mint-teal/20"
          >
            <div className={`w-10 h-10 rounded-xl ${bg} flex items-center justify-center flex-none`}>
              <Icon size={18} className={color} strokeWidth={1.5} />
            </div>
            <div className="flex-1 min-w-0">
              <p className="font-heading font-semibold text-sm text-deep-navy">{title}</p>
              <p className="font-body text-xs text-slate-gray mt-1 leading-relaxed">{body}</p>
            </div>
          </motion.div>
        ))}
      </div>

      {/* Private session toggle */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.4 }}
        className="mx-4 mt-5"
      >
        <button
          onClick={togglePrivateSession}
          className={`w-full flex items-center justify-between gap-3 p-4 rounded-2xl border-2 transition-all ${
            isPrivateSession
              ? 'bg-deep-navy border-deep-navy text-white'
              : 'bg-white border-deep-navy/20 hover:border-deep-navy/40 text-deep-navy'
          }`}
        >
          <div className="flex items-center gap-3">
            <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${isPrivateSession ? 'bg-white/15' : 'bg-deep-navy/10'}`}>
              <Lock size={18} className={isPrivateSession ? 'text-white' : 'text-deep-navy'} strokeWidth={1.5} />
            </div>
            <div className="text-left">
              <p className="font-heading font-semibold text-sm">
                {isPrivateSession ? t.disablePrivate : t.enablePrivate}
              </p>
              <p className={`font-body text-[11px] mt-0.5 ${isPrivateSession ? 'text-white/70' : 'text-slate-gray'}`}>
                {isPrivateSession ? t.privateActiveDesc.replace('— ', '') : t.privateDesc}
              </p>
            </div>
          </div>
          {/* Toggle pill */}
          <div className={`w-12 h-7 rounded-full flex items-center transition-all px-0.5 ${isPrivateSession ? 'bg-white/20 justify-end' : 'bg-slate-gray/20 justify-start'}`}>
            <div className="w-6 h-6 rounded-full bg-white shadow-sm" />
          </div>
        </button>
      </motion.div>

      {/* Data pledge */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.5 }}
        className="mx-4 mt-3 mb-4 bg-mint-teal/40 rounded-2xl p-4 flex items-start gap-3"
      >
        <Database size={16} className="text-teal flex-none mt-0.5" strokeWidth={1.5} />
        <div>
          <p className="font-body text-xs font-semibold text-teal">
            {language === 'en' ? 'Our Data Pledge' : "Isezerano ryacu ry'amakuru"}
          </p>
          <p className="font-body text-xs text-deep-navy mt-0.5 leading-relaxed">{t.dataNote}</p>
        </div>
      </motion.div>

      {/* MoH seal */}
      <div className="flex justify-center pb-4">
        <div className="flex items-center gap-1.5 font-body text-[11px] text-slate-gray/70">
          <ShieldCheck size={12} className="text-teal" strokeWidth={1.5} />
          {t.mohNote}
        </div>
      </div>
    </div>
  );
}
