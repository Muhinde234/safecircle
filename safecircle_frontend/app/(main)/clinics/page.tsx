'use client';

import { useState } from 'react';
import { motion } from 'framer-motion';
import { Search, MapPin, Phone, Clock, ShieldCheck, CheckCircle } from 'lucide-react';
import Header from '@/components/Header';
import { useApp } from '@/app/providers';
import { translations } from '@/lib/translations';
import { clinics } from '@/lib/data';

type Filter = 'all' | 'hiv' | 'family' | 'counseling';

const filterMap: Record<Filter, string> = {
  all: 'All Services',
  hiv: 'HIV Services',
  family: 'Family Planning',
  counseling: 'Counseling',
};

export default function ClinicsPage() {
  const { language } = useApp();
  const t = translations[language];
  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState<Filter>('all');

  const filterKeys: Filter[] = ['all', 'hiv', 'family', 'counseling'];
  const filterLabels: Record<Filter, string> = {
    all: t.allServices,
    hiv: t.hivServices,
    family: t.familyPlanning,
    counseling: t.counseling,
  };

  const filterServiceMap: Record<Filter, string> = {
    all: '',
    hiv: 'HIV',
    family: 'Family',
    counseling: 'Counseling',
  };

  const filtered = clinics.filter(c => {
    const name = language === 'rw' ? c.nameRw : c.name;
    const matchSearch = search === '' || name.toLowerCase().includes(search.toLowerCase()) || c.district.toLowerCase().includes(search.toLowerCase());
    const keyword = filterServiceMap[filter];
    const matchFilter = filter === 'all' || c.services.some(s => s.includes(keyword));
    return matchSearch && matchFilter;
  });

  return (
    <div className="bg-frost-white min-h-screen">
      <Header title={t.clinicsTitle} subtitle={t.clinicsSubtitle} />

      {/* Search bar */}
      <div className="px-4 pt-4 pb-2 bg-white border-b border-mint-teal/40">
        <div className="relative">
          <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-gray" strokeWidth={1.5} />
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder={t.searchPlaceholder}
            className="w-full pl-9 pr-4 py-2.5 bg-frost-white border border-mint-teal/60 rounded-xl font-body text-sm text-deep-navy placeholder:text-slate-gray/50 focus:outline-none focus:ring-2 focus:ring-teal/30 focus:border-teal/50 transition-all"
          />
        </div>

        {/* Filter chips */}
        <div className="flex gap-2 overflow-x-auto pb-1 pt-3">
          {filterKeys.map(key => (
            <button
              key={key}
              onClick={() => setFilter(key)}
              className={`flex-none px-3 py-1.5 rounded-full font-body text-xs font-semibold transition-colors ${
                filter === key
                  ? 'bg-teal text-white'
                  : 'bg-mint-teal/40 text-teal hover:bg-mint-teal/70'
              }`}
            >
              {filterLabels[key]}
            </button>
          ))}
        </div>
      </div>

      {/* Clinic list */}
      <div className="px-4 py-4 space-y-3">
        {filtered.length === 0 && (
          <div className="text-center py-12">
            <MapPin size={40} className="text-slate-gray/30 mx-auto mb-3" strokeWidth={1} />
            <p className="font-body text-sm text-slate-gray">
              {language === 'en' ? 'No clinics found. Try a different search.' : 'Nta vuriro ryabonetse. Gerageza gushaka ibindi.'}
            </p>
          </div>
        )}

        {filtered.map((clinic, i) => {
          const name = language === 'rw' ? clinic.nameRw : clinic.name;
          const hours = language === 'rw' ? clinic.hoursRw : clinic.hours;
          const services = language === 'rw' ? clinic.servicesRw : clinic.services;

          return (
            <motion.div
              key={clinic.id}
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.25, delay: i * 0.05 }}
              className="bg-white rounded-2xl p-4 shadow-sm border border-mint-teal/20"
            >
              {/* Top row */}
              <div className="flex items-start justify-between gap-3">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 flex-wrap">
                    <h3 className="font-heading font-semibold text-sm text-deep-navy leading-snug">{name}</h3>
                    {clinic.isYouthFriendly && (
                      <span className="flex items-center gap-0.5 bg-teal/10 text-teal text-[10px] font-body font-semibold px-2 py-0.5 rounded-full border border-teal/20">
                        <ShieldCheck size={9} strokeWidth={2} /> {t.youthFriendly}
                      </span>
                    )}
                    {clinic.noJudgment && (
                      <span className="flex items-center gap-0.5 bg-health-green/10 text-health-green text-[10px] font-body font-semibold px-2 py-0.5 rounded-full border border-health-green/20">
                        <CheckCircle size={9} strokeWidth={2} /> {t.noJudgment}
                      </span>
                    )}
                  </div>

                  <div className="flex items-center gap-1 mt-1.5">
                    <MapPin size={12} className="text-slate-gray flex-none" strokeWidth={1.5} />
                    <p className="font-body text-xs text-slate-gray leading-tight">{clinic.address}</p>
                    <span className="ml-auto flex-none bg-mint-teal/50 text-teal text-[10px] font-body font-semibold px-2 py-0.5 rounded-full">
                      {clinic.distance}
                    </span>
                  </div>
                </div>
              </div>

              {/* Services */}
              <div className="mt-3">
                <p className="font-body text-[10px] text-slate-gray uppercase tracking-wide mb-1.5">{t.services}</p>
                <div className="flex flex-wrap gap-1.5">
                  {services.map(s => (
                    <span key={s} className="px-2 py-0.5 bg-frost-white border border-mint-teal/40 rounded-full font-body text-[11px] text-deep-navy">
                      {s}
                    </span>
                  ))}
                </div>
              </div>

              {/* Hours + actions */}
              <div className="mt-3 flex items-center justify-between gap-3">
                <div className="flex items-center gap-1.5">
                  <Clock size={12} className="text-slate-gray" strokeWidth={1.5} />
                  <p className="font-body text-[11px] text-slate-gray">{hours}</p>
                </div>
                <a
                  href={`tel:${clinic.phone}`}
                  className="flex items-center gap-1.5 px-3 py-2 bg-coral text-white font-heading font-bold text-xs rounded-xl shadow-sm hover:bg-coral/90 transition-colors"
                >
                  <Phone size={13} strokeWidth={2} />
                  {t.callClinic}
                </a>
              </div>
            </motion.div>
          );
        })}
      </div>

      {/* Trust note */}
      <div className="px-4 pb-2 flex items-center gap-1.5 justify-center">
        <ShieldCheck size={12} className="text-teal" strokeWidth={1.5} />
        <p className="font-body text-[11px] text-slate-gray/70">{t.mohApproved}</p>
      </div>
    </div>
  );
}
