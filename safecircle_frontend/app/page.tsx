'use client';

import { motion } from 'framer-motion';
import Link from 'next/link';
import { ShieldCheck, Lock, HeartPulse } from 'lucide-react';
import type { LucideIcon } from 'lucide-react';
import SafeCircleLogo from '@/components/SafeCircleLogo';
import { useApp } from './providers';
import { translations } from '@/lib/translations';

const container = {
  hidden: {},
  show: { transition: { staggerChildren: 0.15, delayChildren: 0.2 } },
};

const item = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0, transition: { duration: 0.2, ease: 'easeOut' } },
};

function LangPill() {
  const { language, setLanguage } = useApp();
  return (
    <div className="flex items-center bg-mint-teal rounded-full p-0.5">
      <button
        onClick={() => setLanguage('en')}
        aria-label="Switch to English"
        className={`px-3 py-1 rounded-full text-xs font-heading font-bold transition-all duration-150 ${
          language === 'en' ? 'bg-teal text-white shadow-sm' : 'text-teal'
        }`}
      >
        EN
      </button>
      <button
        onClick={() => setLanguage('rw')}
        aria-label="Switch to Kinyarwanda"
        className={`px-3 py-1 rounded-full text-xs font-heading font-bold transition-all duration-150 ${
          language === 'rw' ? 'bg-teal text-white shadow-sm' : 'text-teal'
        }`}
      >
        RW
      </button>
    </div>
  );
}

function TrustBullet({ icon: Icon, text }: { icon: LucideIcon; text: string }) {
  return (
    <div className="flex items-center gap-3">
      <span className="flex-none w-9 h-9 rounded-full bg-mint-teal flex items-center justify-center">
        <Icon size={18} className="text-teal" strokeWidth={2} />
      </span>
      <p className="font-body text-sm text-deep-navy leading-snug">{text}</p>
    </div>
  );
}

export default function WelcomePage() {
  const { language } = useApp();
  const t = translations[language];

  return (
    <div className="relative min-h-dvh bg-frost-white flex flex-col max-w-md mx-auto overflow-hidden">

      {/* Organic mint-teal blob — top right */}
      <svg
        className="absolute -top-24 -right-24 w-80 h-80 opacity-70 pointer-events-none"
        viewBox="0 0 200 200"
        xmlns="http://www.w3.org/2000/svg"
        aria-hidden="true"
      >
        <path
          fill="#CCFBF1"
          d="M42.7,-54.3C54.7,-46.2,63.1,-31.7,67.2,-15.6C71.3,0.5,71,18.2,64.4,33.3C57.7,48.4,44.6,60.9,29.6,67.5C14.6,74.1,-2.3,74.8,-18.8,70.3C-35.3,65.8,-51.5,56.1,-61.2,42.1C-70.9,28.1,-74.1,9.8,-71.5,-7.1C-68.9,-24,-60.5,-39.5,-48.4,-47.6C-36.3,-55.7,-20.5,-56.4,-4,-52.4C12.5,-48.4,30.7,-62.4,42.7,-54.3Z"
          transform="translate(100 100)"
        />
      </svg>

      {/* Language pill — top right */}
      <div className="absolute top-5 right-5 z-10">
        <LangPill />
      </div>

      {/* Main animated content */}
      <motion.div
        variants={container}
        initial="hidden"
        animate="show"
        className="flex-1 flex flex-col px-6 pt-20 pb-0"
      >
        {/* Centered content block */}
        <div className="flex-1 flex flex-col items-center justify-center gap-7">

          {/* Logo mark + wordmark */}
          <motion.div className="flex flex-col items-center gap-2">
            <SafeCircleLogo size={88} className="text-teal" />
            <p className="font-heading font-bold text-[26px] text-deep-navy tracking-tight mt-1">
              Safe Circle
            </p>
          </motion.div>

          {/* Tagline */}
          <motion.p
          
            className="font-body text-base text-deep-navy/80 text-center leading-relaxed max-w-65"
          >
            {t.tagline}
          </motion.p>

          {/* Trust bullets */}
          <motion.div className="w-full flex flex-col gap-3.5">
            <TrustBullet icon={ShieldCheck} text={t.trust1} />
            <TrustBullet icon={Lock}        text={t.trust2} />
            <TrustBullet icon={HeartPulse}  text={t.trust3} />
          </motion.div>

          {/* CTA buttons */}
          <motion.div  className="w-full flex flex-col gap-3 mt-1">
            <Link
              href="/explore"
              className="flex items-center justify-center w-full h-14 bg-teal text-white font-heading font-bold text-base rounded-xl shadow-sm active:opacity-90 transition-opacity"
            >
              {t.getStarted}
            </Link>

            <Link
              href="/explore"
              className="flex items-center justify-center w-full h-12 border border-teal/40 text-teal font-heading font-semibold text-sm rounded-xl active:bg-teal/5 transition-colors"
            >
              {t.haveAccount}
            </Link>
          </motion.div>
        </div>

        {/* Footer tagline */}
        <motion.p
       
          className="text-center font-body text-xs text-slate-gray pb-safe py-6"
        >
          {t.footerSlogan}
        </motion.p>
      </motion.div>
    </div>
  );
}
