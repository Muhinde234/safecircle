'use client';

import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Clock, ShieldCheck, BookOpen, ChevronRight, Bookmark, Volume2 } from 'lucide-react';
import Header from '@/components/Header';
import TrustBadge from '@/components/TrustBadge';
import { useApp } from '@/app/providers';
import { translations, type Translations } from '@/lib/translations';
import { api } from '@/lib/api';

type Category = 'All' | 'Sexual Health' | 'HIV & STIs' | 'Relationships' | 'Mental Health';

interface Article {
  id: string | number;
  title: string;
  titleRw: string;
  category: Category;
  readTime: number;
  excerpt: string;
  excerptRw: string;
  isVerified: boolean;
  isFeatured: boolean;
  color: string;
  audioUrl?: string;
  language?: string;
}

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

function ArticleCard({
  article,
  language,
  t,
  localT,
}: {
  article: Article;
  language: string;
  t: Translations;
  localT: Record<string, string>;
}) {
  const { bookmarks, addBookmark, removeBookmark } = useApp();
  const isBookmarked = bookmarks.some(b => b.bookmarkType === 'ARTICLE' && b.targetId === String(article.id));

  const toggleBookmark = async (e: React.MouseEvent) => {
    e.stopPropagation();
    if (isBookmarked) {
      await removeBookmark('ARTICLE', String(article.id));
    } else {
      await addBookmark('ARTICLE', String(article.id));
    }
  };

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
        <div className="flex items-center gap-2">
          {article.isFeatured && (
            <span className="font-body text-[10px] font-semibold text-teal bg-white/80 px-2 py-0.5 rounded-full border border-teal/20">
              {t.featured}
            </span>
          )}
          <button
            onClick={toggleBookmark}
            aria-label={isBookmarked ? localT.removeBookmark : localT.bookmarkArticle}
            className="text-teal p-1 hover:bg-teal/10 rounded-full transition-colors"
          >
            <Bookmark size={15} fill={isBookmarked ? 'currentColor' : 'none'} />
          </button>
        </div>
      </div>

      <div className="px-4 py-3">
        <h3 className="font-heading font-semibold text-sm text-deep-navy leading-snug">{title}</h3>
        <p className="font-body text-xs text-slate-gray mt-1.5 leading-relaxed line-clamp-2">{excerpt}</p>

        <div className="flex items-center justify-between mt-3">
          <div className="flex items-center gap-2.5 flex-wrap">
            <span className="flex items-center gap-1 font-body text-[11px] text-slate-gray">
              <Clock size={11} strokeWidth={1.5} />
              {article.readTime} {localT.readTime}
            </span>
            {article.isVerified && <TrustBadge variant="clinical" />}
          </div>
          <div className="flex items-center gap-3">
            {article.audioUrl && (
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  const audio = new Audio(article.audioUrl);
                  audio.play().catch(err => console.error('Audio play error:', err));
                }}
                className="flex items-center gap-1 text-xs text-teal font-semibold hover:opacity-75 transition-opacity"
                title={localT.audioVoiceover}
              >
                <Volume2 size={13} />
                <span className="text-[11px]">{localT.audioVoiceover}</span>
              </button>
            )}
            <button className="flex items-center gap-0.5 font-body text-xs text-teal font-semibold hover:opacity-70 transition-opacity">
              {t.readMore} <ChevronRight size={12} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default function LearnPage() {
  const { language } = useApp();
  const t = translations[language];
  const [activeCategory, setActiveCategory] = useState<Category>('All');
  const [articlesList, setArticlesList] = useState<Article[]>([]);
  const [isLowBandwidth, setIsLowBandwidth] = useState(false);
  const [loading, setLoading] = useState(true);

  const localT = {
    en: {
      lowBandwidth: 'Data Saver (Low Bandwidth)',
      audioVoiceover: 'Listen',
      readTime: 'min read',
      noAudio: 'No audio available',
      bookmarkArticle: 'Bookmark article',
      removeBookmark: 'Remove bookmark',
    },
    rw: {
      lowBandwidth: 'Koresha Data Nke',
      audioVoiceover: 'Umva',
      readTime: 'iminota yo gusoma',
      noAudio: 'Nta jwi rihari',
      bookmarkArticle: 'Bika amakuru',
      removeBookmark: 'Siba mubitswe',
    },
  }[language];

  useEffect(() => {
    const fetchArticles = async () => {
      setLoading(true);
      try {
        const res = isLowBandwidth
          ? await api.getLowBandwidthContent()
          : await api.getContent();

        const rawItems = res.items || [];
        const mapped: Article[] = rawItems.map((item, index) => {
          let mappedCategory: Category = 'Sexual Health';
          if (item.category === 'HIV' || item.category === 'STI') {
            mappedCategory = 'HIV & STIs';
          } else if (item.category === 'MYTHS') {
            mappedCategory = 'Mental Health';
          } else if (item.category === 'PREVENTION') {
            mappedCategory = 'Sexual Health';
          } else {
            mappedCategory = 'Sexual Health';
          }

          const readTime = Math.max(1, Math.ceil((item.excerpt || item.body || '').split(' ').length / 80));

          return {
            id: item.id,
            title: item.title,
            titleRw: item.title,
            category: mappedCategory,
            readTime: readTime,
            excerpt: item.excerpt || item.body || '',
            excerptRw: item.excerpt || item.body || '',
            isVerified: true,
            isFeatured: index === 0,
            color: categoryColors[mappedCategory] ?? 'bg-slate-gray/10 text-slate-gray',
            audioUrl: item.audioUrl,
            language: item.language,
          };
        });

        // Filter by language
        let displayItems = mapped.filter(item => {
          if (!item.language) return true;
          return item.language.toLowerCase() === language.toLowerCase();
        });

        if (displayItems.length === 0) {
          displayItems = mapped; // fallback to show all if language specific not found
        }

        setArticlesList(displayItems);
      } catch (err) {
        console.error('Failed to load articles:', err);
        setArticlesList([]);
      } finally {
        setLoading(false);
      }
    };

    fetchArticles();
  }, [isLowBandwidth, language]);

  const categories: Category[] = ['All', 'Sexual Health', 'HIV & STIs', 'Relationships', 'Mental Health'];

  const categoryLabels: Record<Category, string> = {
    'All': t.filterAll,
    'Sexual Health': t.filterSRH,
    'HIV & STIs': t.filterHIV,
    'Relationships': t.filterRelationships,
    'Mental Health': t.filterMentalHealth,
  };

  const filtered = activeCategory === 'All'
    ? articlesList
    : articlesList.filter(a => a.category === activeCategory);

  const featured = filtered.find(a => a.isFeatured);

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

      {/* Low-Bandwidth Toggle */}
      <div className="bg-white px-5 py-2.5 flex items-center justify-between border-b border-mint-teal/20">
        <span className="font-body text-xs text-slate-gray font-medium">
          {localT.lowBandwidth}
        </span>
        <button
          onClick={() => setIsLowBandwidth(!isLowBandwidth)}
          className={`relative w-9 h-5 rounded-full transition-colors duration-200 ${
            isLowBandwidth ? 'bg-teal' : 'bg-slate-gray/25'
          }`}
        >
          <span
            className={`absolute top-0.5 left-0.5 w-4 h-4 bg-white rounded-full transition-transform duration-200 ${
              isLowBandwidth ? 'transform translate-x-4' : ''
            }`}
          />
        </button>
      </div>

      <div className="px-4 py-4 space-y-4">
        {/* Loading state */}
        {loading && (
          <div className="flex flex-col items-center justify-center py-16 gap-3">
            <div className="w-8 h-8 border-4 border-teal border-t-transparent rounded-full animate-spin" />
            <p className="font-body text-xs text-slate-gray/70">
              {language === 'en' ? 'Loading educational articles...' : 'Gushaka amakuru y\'ubuzima...'}
            </p>
          </div>
        )}

        {/* Featured banner — only on "All" */}
        {!loading && activeCategory === 'All' && featured && (
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
                  <Clock size={10} /> {featured.readTime} {localT.readTime}
                </span>
                <button className="px-3 py-1.5 bg-white text-teal font-heading font-bold text-xs rounded-xl">
                  {t.readMore}
                </button>
              </div>
            </div>
          </motion.div>
        )}

        {/* Article list */}
        {!loading && filtered.length === 0 && (
          <div className="text-center py-12">
            <BookOpen size={40} className="text-slate-gray/30 mx-auto mb-3" strokeWidth={1} />
            <p className="font-body text-sm text-slate-gray">
              {language === 'en' ? 'No articles in this category yet.' : 'Nta makuru muri iki kintu.'}
            </p>
          </div>
        )}

        {!loading && filtered.map((article, i) => (
          <motion.div
            key={article.id}
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.25, delay: i * 0.05 }}
          >
            <ArticleCard article={article} language={language} t={t} localT={localT} />
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
