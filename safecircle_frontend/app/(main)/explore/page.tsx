'use client';

import { motion } from 'framer-motion';
import Link from 'next/link';
import {
  MessageCircle, MapPin, BookOpen, Lock,
  Phone, ChevronRight, ShieldCheck,
} from 'lucide-react';
import LanguageSwitcher from '@/components/LanguageSwitcher';
import { useApp } from '@/app/providers';
import { translations } from '@/lib/translations';

const stagger = {
  hidden: {},
  show: { transition: { staggerChildren: 0.07, delayChildren: 0.05 } },
} as const;
const fadeUp = {
  hidden: { opacity: 0, y: 10 },
  show: { opacity: 1, y: 0, transition: { duration: 0.28, ease: 'easeOut' } },
} as const;

type ActionCardProps = {
  href?: string;
  onClick?: () => void;
  icon: React.ElementType;
  title: string;
  desc: string;
  iconBg: string;
  cardBorder: string;
};

function ActionCard({ href, onClick, icon: Icon, title, desc, iconBg, cardBorder }: ActionCardProps) {
  const inner = (
    <>
      <div className={`w-12 h-12 rounded-xl ${iconBg} flex items-center justify-center flex-none`}>
        <Icon size={22} strokeWidth={1.5} className="text-white" />
      </div>
      <div className="mt-3">
        <p className="font-heading font-semibold text-sm text-deep-navy leading-snug">{title}</p>
        <p className="font-body text-[11px] text-slate-gray mt-0.5 leading-tight">{desc}</p>
      </div>
    </>
  );

  const cls = `block p-4 bg-white rounded-2xl shadow-sm hover:shadow-md active:scale-[0.98] transition-all border ${cardBorder} w-full text-left`;
  return href
    ? <Link href={href} className={cls}>{inner}</Link>
    : <button onClick={onClick} className={cls}>{inner}</button>;
}

export default function ExplorePage() {
  const { language, isPrivateSession, togglePrivateSession } = useApp();
  const t = translations[language];

  const actions: ActionCardProps[] = [
    { href: '/chat', icon: MessageCircle, title: t.chatWithSira, desc: t.chatDesc, iconBg: 'bg-teal', cardBorder: 'border-teal/10' },
    { href: '/clinics', icon: MapPin, title: t.findClinic, desc: t.clinicDesc, iconBg: 'bg-health-green', cardBorder: 'border-health-green/10' },
    { href: '/learn', icon: BookOpen, title: t.learnExplore, desc: t.learnDesc, iconBg: 'bg-coral', cardBorder: 'border-coral/10' },
    {
      onClick: togglePrivateSession,
      icon: Lock,
      title: t.privateSession,
      desc: isPrivateSession ? t.privateActive : t.privateDesc,
      iconBg: isPrivateSession ? 'bg-deep-navy' : 'bg-slate-gray',
      cardBorder: 'border-deep-navy/10',
    },
  ];

  const topics = language === 'en'
    ? ['HIV Prevention', 'Contraception', 'STI Testing', 'Mental Health', 'Relationships']
    : ['Kwirinda HIV', 'Contraception', 'Isuzuma rya STI', 'Ubuzima bwo mu Mutwe', 'Imibanire'];

  const topicColors = [
    'bg-teal/10 text-teal border-teal/20',
    'bg-health-green/10 text-health-green border-health-green/20',
    'bg-coral/10 text-coral border-coral/20',
    'bg-deep-navy/10 text-deep-navy border-deep-navy/20',
    'bg-slate-gray/10 text-slate-gray border-slate-gray/20',
  ];

  return (
    <div className="bg-frost-white min-h-screen">
      {/* Teal hero banner */}
      <div className="bg-teal relative overflow-hidden">
        <div className="absolute -top-10 -right-10 w-44 h-44 rounded-full bg-white/10 pointer-events-none" />
        <div className="absolute top-8 right-4 w-14 h-14 rounded-full bg-white/5 pointer-events-none" />
        <div className="absolute -bottom-6 left-6 w-24 h-24 rounded-full bg-white/5 pointer-events-none" />

        <div className="relative z-10 px-5 pt-12 pb-14 flex items-start justify-between gap-4">
          <div>
            <h1 className="font-heading font-bold text-3xl text-white">
              {t.hello} 👋
            </h1>
            <p className="font-body text-sm text-mint-teal mt-1 max-w-[220px] leading-relaxed">
              {t.taglineExplore}
            </p>
          </div>
          <div className="flex items-center gap-2 pt-1 flex-none">
            {isPrivateSession && (
              <span className="flex items-center gap-1 bg-white/15 text-white/90 text-[10px] font-body font-semibold px-2 py-1 rounded-full">
                <Lock size={9} /> Private
              </span>
            )}
            <LanguageSwitcher />
          </div>
        </div>

        <div className="absolute bottom-0 left-0 right-0 h-8 bg-frost-white rounded-t-[32px]" />
      </div>

      {/* Quick actions */}
      <div className="px-4 relative z-10">
        <p className="font-heading font-semibold text-[11px] text-slate-gray uppercase tracking-widest mb-3">
          {t.quickActions}
        </p>
        <motion.div
          variants={stagger}
          initial="hidden"
          animate="show"
          className="grid grid-cols-2 gap-3"
        >
          {actions.map((a, i) => (
            <motion.div key={i} variants={fadeUp}>
              <ActionCard {...a} />
            </motion.div>
          ))}
        </motion.div>
      </div>

      {/* Featured topics */}
      <div className="mt-6 px-4">
        <div className="flex items-center justify-between mb-3">
          <h2 className="font-heading font-semibold text-[11px] text-slate-gray uppercase tracking-widest">
            {t.featuredTopics}
          </h2>
          <Link href="/learn" className="flex items-center gap-0.5 font-body text-xs text-teal">
            {language === 'en' ? 'See all' : 'Reba byose'} <ChevronRight size={12} />
          </Link>
        </div>
        <div className="flex gap-2 overflow-x-auto pb-2 -mx-1 px-1">
          {topics.map((label, i) => (
            <Link
              key={label}
              href="/learn"
              className={`flex-none px-3 py-1.5 rounded-full border font-body text-xs font-medium hover:opacity-80 transition-opacity ${topicColors[i]}`}
            >
              {label}
            </Link>
          ))}
        </div>
      </div>

      {/* Emergency strip */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.4 }}
        className="mx-4 mt-5 bg-blush rounded-2xl p-4 flex items-center gap-3 border border-coral/10"
      >
        <div className="w-10 h-10 rounded-xl bg-coral/15 flex items-center justify-center flex-none">
          <Phone size={18} className="text-coral" strokeWidth={1.5} />
        </div>
        <div className="flex-1 min-w-0">
          <p className="font-heading font-semibold text-sm text-deep-navy">{t.emergencyHelp}</p>
          <p className="font-body text-[11px] text-slate-gray mt-0.5">{t.emergencyDesc}</p>
        </div>
        <a
          href="tel:3029"
          className="flex-none px-3 py-2 bg-coral text-white font-heading font-bold text-xs rounded-xl shadow-sm"
        >
          {t.callLine}
        </a>
      </motion.div>

      {/* MoH note */}
      <div className="flex justify-center pb-4 mt-4">
        <div className="flex items-center gap-1.5 font-body text-[11px] text-slate-gray/70">
          <ShieldCheck size={12} className="text-teal" strokeWidth={1.5} />
          {t.mohApproved}
        </div>
      </div>
    </div>
  );
}
