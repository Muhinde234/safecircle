'use client';

import { useState } from 'react';
import { motion } from 'framer-motion';
import { Clock, ShieldCheck, BookOpen, ChevronRight } from 'lucide-react';
import Header from '@/components/Header';
import TrustBadge from '@/components/TrustBadge';
import { useApp } from '@/app/providers';
import { translations, type Translations } from '@/lib/translations';
import { articles, type Article } from '@/lib/data';

type Category = 'All' | 'Sexual Health' | 'HIV & STIs' | 'Relationships' | 'Mental Health';

const categoryColors: Record<string, string> = {
  'Sexual Health': 'bg-health-green/10 text-health-green border-health-green/20',
  'HIV & STIs': 'bg-teal/10 text-teal border-teal/20',
  'Relationships': 'bg-coral/10 text-coral border-coral/20',
  'Mental Health': 'bg-deep-navy/10 text-deep-navy border-deep-navy/20',
};

const categoryGradients: Record<string, string> = {
  'Sexual Health': 'from-health-green/20 to-health-green/5',
  'HIV & STIs': 'from-teal/20 to-teal/5',
  'Relationships': 'from-coral/20 to-coral/5',
  'Mental Health': 'from-deep-navy/10 to-deep-navy/5',
};

function ArticleCard({ article, language, t }: {
  article: Article;
  language: string;
  t: Translations;
}) {
  const title = language === 'rw' ? article.titleRw : article.title;
  const excerpt = language === 'rw' ? article.excerptRw : article.excerpt;
  const colorClass = categoryColors[article.category] ?? 'bg-slate-gray/10 text-slate-gray border-slate-gray/20';
  const gradient = categoryGradients[article.category] ?? 'from-slate-gray/10 to-slate-gray/5';

  return (
    <div className="bg-white rounded-2xl overflow-hidden shadow-sm border border-mint-teal/20 hover:shadow-md transition-shadow">
      <div className={`bg-gradient-to-br ${gradient} px-4 py-4 flex items-center justify-between`}>
        <span className={`inline-flex items-center gap-1 border rounded-full font-body font-semibold text-[10px] px-2.5 py-0.5 ${colorClass}`}>
          {article.category}
        </span>
        {article.isFeatured && (
          <span className="font-body text-[10px] font-semibold text-teal bg-white/80 px-2 py-0.5 rounded-full border border-teal/20">
            {t.featured}
          </span>
        )}
      </div>

      <div className="px-4 py-3">
        <h3 className="font-heading font-semibold text-sm text-deep-navy leading-snug">{title}</h3>
        <p className="font-body text-xs text-slate-gray mt-1.5 leading-relaxed line-clamp-2">{excerpt}</p>

        <div className="flex items-center justify-between mt-3">
          <div className="flex items-center gap-2.5 flex-wrap">
            <span className="flex items-center gap-1 font-body text-[11px] text-slate-gray">
              <Clock size={11} strokeWidth={1.5} />
              {article.readTime} {t.minRead}
            </span>
            {article.isVerified && <TrustBadge variant="clinical" />}
          </div>
          <button className="flex items-center gap-0.5 font-body text-xs text-teal font-semibold hover:opacity-70 transition-opacity">
            {t.readMore} <ChevronRight size={12} />
          </button>
        </div>
      </div>
    </div>
  );
}

export default function LearnPage() {
  const { language } = useApp();
  const t = translations[language];
  const [activeCategory, setActiveCategory] = useState<Category>('All');

  const categories: Category[] = ['All', 'Sexual Health', 'HIV & STIs', 'Relationships', 'Mental Health'];

  const categoryLabels: Record<Category, string> = {
    'All': t.filterAll,
    'Sexual Health': t.filterSRH,
    'HIV & STIs': t.filterHIV,
    'Relationships': t.filterRelationships,
    'Mental Health': t.filterMentalHealth,
  };

  const filtered = activeCategory === 'All'
    ? articles
    : articles.filter(a => a.category === activeCategory);

  const featured = articles.find(a => a.isFeatured);

  return (
    <div className="bg-frost-white min-h-screen">
      <Header title={t.learnTitle} subtitle={t.learnSubtitle} />

      {/* Category filters */}
      <div className="bg-white border-b border-mint-teal/40 px-4 py-3">
        <div className="flex gap-2 overflow-x-auto pb-0.5">
          {categories.map(cat => (
            <button
              key={cat}
              onClick={() => setActiveCategory(cat)}
              className={`flex-none px-3 py-1.5 rounded-full font-body text-xs font-semibold transition-colors ${
                activeCategory === cat
                  ? 'bg-teal text-white'
                  : 'bg-mint-teal/40 text-teal hover:bg-mint-teal/70'
              }`}
            >
              {categoryLabels[cat]}
            </button>
          ))}
        </div>
      </div>

      <div className="px-4 py-4 space-y-4">
        {/* Featured banner — only on "All" */}
        {activeCategory === 'All' && featured && (
          <motion.div
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            className="bg-teal rounded-2xl p-4 relative overflow-hidden"
          >
            <div className="absolute -top-6 -right-6 w-24 h-24 rounded-full bg-white/10 pointer-events-none" />
            <div className="absolute bottom-2 right-8 w-12 h-12 rounded-full bg-white/5 pointer-events-none" />
            <div className="relative z-10">
              <div className="flex items-center gap-1.5 mb-2">
                <BookOpen size={14} className="text-mint-teal" strokeWidth={1.5} />
                <span className="font-body text-[11px] text-mint-teal font-semibold uppercase tracking-wide">
                  {t.featured}
                </span>
              </div>
              <h3 className="font-heading font-bold text-base text-white leading-snug max-w-[240px]">
                {language === 'rw' ? featured.titleRw : featured.title}
              </h3>
              <p className="font-body text-xs text-mint-teal mt-1.5 line-clamp-2 leading-relaxed">
                {language === 'rw' ? featured.excerptRw : featured.excerpt}
              </p>
              <div className="flex items-center gap-3 mt-3">
                <span className="flex items-center gap-1 font-body text-[11px] text-mint-teal/80">
                  <Clock size={10} /> {featured.readTime} {t.minRead}
                </span>
                <button className="px-3 py-1.5 bg-white text-teal font-heading font-bold text-xs rounded-xl">
                  {t.readMore}
                </button>
              </div>
            </div>
          </motion.div>
        )}

        {/* Article list */}
        {filtered.length === 0 && (
          <div className="text-center py-12">
            <BookOpen size={40} className="text-slate-gray/30 mx-auto mb-3" strokeWidth={1} />
            <p className="font-body text-sm text-slate-gray">
              {language === 'en' ? 'No articles in this category yet.' : 'Nta makuru muri iki kintu.'}
            </p>
          </div>
        )}

        {filtered.map((article, i) => (
          <motion.div
            key={article.id}
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.25, delay: i * 0.05 }}
          >
            <ArticleCard article={article} language={language} t={t} />
          </motion.div>
        ))}

        {/* Trust footer */}
        <div className="flex items-center justify-center gap-2 py-2">
          <ShieldCheck size={12} className="text-teal" strokeWidth={1.5} />
          <p className="font-body text-[11px] text-slate-gray/70">{t.learnSubtitle}</p>
        </div>
      </div>
    </div>
  );
}
