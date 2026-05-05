'use client';

import { useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Navigation, ChevronDown, MapPin, Phone,
  Clock, ShieldCheck, Lock,
} from 'lucide-react';
import Header from '@/components/Header';
import { useApp } from '@/app/providers';
import { translations } from '@/lib/translations';
import { clinics } from '@/lib/data';
import type { Clinic } from '@/lib/data';

type Mode = 'idle' | 'nearme' | 'district';

/* ── skeleton card ───────────────────────────────────── */
function ClinicCardSkeleton() {
  return (
    <div className="bg-white rounded-xl shadow-sm overflow-hidden flex animate-pulse">
      <div className="w-1 bg-mint-teal flex-none" />
      <div className="p-4 flex-1 space-y-3">
        <div className="flex items-center justify-between gap-3">
          <div className="h-4 bg-mint-teal/50 rounded-full w-44" />
          <div className="h-5 bg-mint-teal/40 rounded-full w-14 flex-none" />
        </div>
        <div className="flex gap-1.5">
          <div className="h-5 bg-mint-teal/35 rounded-full w-24" />
          <div className="h-5 bg-mint-teal/35 rounded-full w-20" />
          <div className="h-5 bg-mint-teal/35 rounded-full w-16" />
        </div>
        <div className="h-3 bg-mint-teal/25 rounded-full w-36" />
        <div className="h-3 bg-health-green/20 rounded-full w-40" />
        <div className="flex gap-2 pt-3 border-t border-mint-teal/20">
          <div className="flex-1 h-9 bg-mint-teal/20 rounded-xl" />
          <div className="w-20 h-9 bg-teal/20 rounded-xl" />
        </div>
      </div>
    </div>
  );
}

/* ── clinic card ─────────────────────────────────────── */
type ClinicCardProps = {
  clinic: Clinic;
  language: 'en' | 'rw';
  t: typeof translations.en;
  index: number;
};

function ClinicCard({ clinic, language, t, index }: ClinicCardProps) {
  const name     = language === 'rw' ? clinic.nameRw    : clinic.name;
  const hours    = language === 'rw' ? clinic.hoursRw   : clinic.hours;
  const services = language === 'rw' ? clinic.servicesRw : clinic.services;
  const mapsUrl  = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(clinic.name + ' ' + clinic.address)}`;

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -6 }}
      transition={{ duration: 0.25, ease: 'easeOut', delay: index * 0.07 }}
      className="bg-white rounded-xl shadow-sm overflow-hidden flex"
    >
      {/* Mint-teal left accent */}
      <div className="w-1 bg-mint-teal flex-none" />

      <div className="p-4 flex-1 min-w-0">

        {/* Name + distance badge */}
        <div className="flex items-start justify-between gap-2">
          <h3 className="font-heading font-semibold text-base text-deep-navy leading-snug flex-1 min-w-0">
            {name}
          </h3>
          <span className="flex-none bg-teal text-white font-body text-xs font-semibold px-2.5 py-1 rounded-full">
            {clinic.distance}
          </span>
        </div>

        {/* Services pills — Mint Teal */}
        <div className="flex flex-wrap gap-1.5 mt-2.5">
          {services.slice(0, 3).map(s => (
            <span
              key={s}
              className="bg-mint-teal text-teal font-body text-[11px] font-medium px-2 py-0.5 rounded-full"
            >
              {s}
            </span>
          ))}
        </div>

        {/* Hours */}
        <div className="flex items-center gap-1.5 mt-2.5">
          <Clock size={13} className="text-slate-gray flex-none" strokeWidth={1.5} />
          <p className="font-body text-sm text-slate-gray">{hours}</p>
        </div>

        {/* Anonymity badge */}
        {clinic.anonymousVisits && (
          <div className="flex items-center gap-1.5 mt-1.5">
            <Lock size={13} className="text-health-green flex-none" strokeWidth={1.75} />
            <p className="font-body text-xs font-semibold text-health-green">
              {t.anonymousWelcome}
            </p>
          </div>
        )}

        {/* CTA row */}
        <div className="flex gap-2 mt-3 pt-3 border-t border-mint-teal/30">
          <a
            href={mapsUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="flex-1 flex items-center justify-center gap-1.5 px-3 py-2 border border-teal/35 rounded-xl font-heading font-semibold text-xs text-teal hover:bg-teal/5 active:bg-teal/10 transition-colors"
          >
            <MapPin size={13} strokeWidth={1.5} />
            {t.getDirections}
          </a>
          <a
            href={`tel:${clinic.phone}`}
            className="flex-none flex items-center justify-center gap-1.5 px-4 py-2 bg-teal text-white rounded-xl font-heading font-bold text-xs hover:bg-teal/90 active:opacity-90 transition-all"
          >
            <Phone size={13} strokeWidth={2} />
            {t.callClinic}
          </a>
        </div>

      </div>
    </motion.div>
  );
}

/* ── empty state ─────────────────────────────────────── */
function EmptyState({ message }: { message: string }) {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="flex flex-col items-center justify-center py-14 px-6 text-center"
    >
      <ShieldCheck size={48} className="text-teal mb-3" strokeWidth={1.25} />
      <p className="font-body text-sm text-slate-gray leading-relaxed max-w-64">
        {message}
      </p>
    </motion.div>
  );
}

/* ── idle prompt ─────────────────────────────────────── */
function IdlePrompt({ message }: { message: string }) {
  return (
    <div className="flex flex-col items-center justify-center py-12 px-6 text-center">
      <MapPin size={44} className="text-mint-teal mb-3" strokeWidth={1.25} />
      <p className="font-body text-sm text-slate-gray/70 leading-relaxed max-w-60">
        {message}
      </p>
    </div>
  );
}

/* ── page ────────────────────────────────────────────── */
export default function ClinicsPage() {
  const { language } = useApp();
  const t = translations[language];

  const [mode, setMode] = useState<Mode>('idle');
  const [selectedDistrict, setSelectedDistrict] = useState('');
  const [isLocating, setIsLocating] = useState(false);
  const [results, setResults] = useState<Clinic[]>([]);

  const nearestThree = [...clinics].sort((a, b) => a.distanceKm - b.distanceKm).slice(0, 3);

  const handleNearMe = useCallback(() => {
    if (mode === 'nearme') {
      setMode('idle');
      setResults([]);
      return;
    }
    setMode('nearme');
    setSelectedDistrict('');
    setIsLocating(true);
    setResults([]);

    // Request GPS permission for UX legitimacy (browser shows permission prompt)
    if (typeof navigator !== 'undefined' && navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        () => {},
        () => {},
        { timeout: 5000, maximumAge: 30000 },
      );
    }

    // Always resolve after 1.2 s — mock nearest clinics
    setTimeout(() => {
      setResults(nearestThree);
      setIsLocating(false);
    }, 1200);
  }, [mode, nearestThree]);

  const handleDistrictChange = (district: string) => {
    setSelectedDistrict(district);
    if (district) {
      setMode('district');
      setResults(clinics.filter(c => c.district === district));
    } else {
      setMode('idle');
      setResults([]);
    }
  };

  const districts = ['Nyarugenge', 'Gasabo', 'Kicukiro'];
  const showEmpty  = !isLocating && mode !== 'idle' && results.length === 0;
  const idleMsg    = language === 'en'
    ? 'Use "Near Me" or select a district to find youth-friendly clinics.'
    : 'Koresha "Hafi Yanjye" cyangwa hitamo akarere kugirango usange amavuriro.';

  return (
    <div className="min-h-screen bg-frost-white">

      <Header title={t.findClinic} subtitle={t.clinicsSubtitle} showBack />

      {/* ── search card ──────────────────────────────── */}
      <div className="px-4 py-4">
        <div className="bg-white rounded-2xl shadow-sm border border-mint-teal/30 p-4 flex flex-col gap-3">

          {/* Near Me button */}
          <button
            onClick={handleNearMe}
            className={`flex items-center justify-center gap-2 w-full py-3 rounded-xl font-heading font-semibold text-sm transition-all duration-150 ${
              mode === 'nearme'
                ? 'bg-teal text-white shadow-sm'
                : 'bg-mint-teal/40 text-teal border border-teal/20 hover:bg-mint-teal/60 active:bg-mint-teal/80'
            }`}
          >
            <Navigation size={16} strokeWidth={mode === 'nearme' ? 2 : 1.75} />
            {t.nearMe}
          </button>

          {/* "or" divider */}
          <div className="flex items-center gap-2">
            <div className="flex-1 h-px bg-mint-teal/40" />
            <span className="font-body text-xs text-slate-gray/55">
              {language === 'en' ? 'or' : 'cyangwa'}
            </span>
            <div className="flex-1 h-px bg-mint-teal/40" />
          </div>

          {/* District dropdown */}
          <div className="relative">
            <select
              value={selectedDistrict}
              onChange={e => handleDistrictChange(e.target.value)}
              className="w-full pl-3.5 pr-9 py-3 bg-frost-white border border-mint-teal/60 rounded-xl font-body text-sm text-deep-navy appearance-none focus:outline-none focus:ring-2 focus:ring-teal/30 focus:border-teal transition-all"
            >
              <option value="">{t.selectDistrict}</option>
              {districts.map(d => (
                <option key={d} value={d}>{d}</option>
              ))}
            </select>
            <ChevronDown
              size={16}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-gray pointer-events-none"
              strokeWidth={1.5}
            />
          </div>

        </div>
      </div>

      {/* ── results ──────────────────────────────────── */}
      <div className="px-4 pb-5 space-y-3">

        {/* Skeleton loading */}
        {isLocating && (
          <>
            <p className="font-body text-xs text-slate-gray/60 text-center pb-1">
              {t.locating}
            </p>
            {[0, 1, 2].map(i => <ClinicCardSkeleton key={i} />)}
          </>
        )}

        {/* Clinic cards */}
        {!isLocating && (
          <AnimatePresence mode="popLayout">
            {results.map((clinic, i) => (
              <ClinicCard
                key={clinic.id}
                clinic={clinic}
                language={language}
                t={t}
                index={i}
              />
            ))}
          </AnimatePresence>
        )}

        {/* Empty state */}
        {showEmpty && <EmptyState message={t.noResultsDistrict} />}

        {/* Idle state */}
        {mode === 'idle' && <IdlePrompt message={idleMsg} />}

      </div>

      {/* ── USSD fallback banner ──────────────────────── */}
      <div className="px-4 pb-8">
        <div className="bg-coral rounded-xl px-4 py-3 flex items-start gap-3">
          <Phone size={15} className="text-white flex-none mt-0.5" strokeWidth={2} />
          <p className="font-body text-xs text-white leading-relaxed">
            {t.ussdNote}
          </p>
        </div>
      </div>

    </div>
  );
}
