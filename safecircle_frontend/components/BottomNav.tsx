'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { Home, MessageCircle, MapPin, BookOpen, ShieldCheck } from 'lucide-react';
import { useApp } from '@/app/providers';
import { translations } from '@/lib/translations';

const navItems = [
  { href: '/explore', icon: Home, labelKey: 'navHome' as const },
  { href: '/chat', icon: MessageCircle, labelKey: 'navChat' as const },
  { href: '/clinics', icon: MapPin, labelKey: 'navClinics' as const },
  { href: '/learn', icon: BookOpen, labelKey: 'navLearn' as const },
  { href: '/privacy', icon: ShieldCheck, labelKey: 'navShield' as const },
];

export default function BottomNav() {
  const pathname = usePathname();
  const { language } = useApp();
  const t = translations[language];

  return (
    <nav
      className="fixed bottom-0 left-0 right-0 z-50 bg-white border-t border-mint-teal/60"
      aria-label="Main navigation"
    >
      <div className="flex items-center justify-around max-w-2xl mx-auto pb-safe">
        {navItems.map(({ href, icon: Icon, labelKey }) => {
          const isActive = pathname === href || pathname.startsWith(href + '/');
          return (
            <Link
              key={href}
              href={href}
              className={`flex flex-col items-center gap-0.5 px-3 pt-2 pb-1.5 min-w-[56px] transition-colors ${
                isActive ? 'text-teal' : 'text-slate-gray'
              }`}
            >
              <div className={`p-1.5 rounded-xl transition-colors ${isActive ? 'bg-teal/10' : ''}`}>
                <Icon
                  size={20}
                  strokeWidth={isActive ? 2 : 1.5}
                />
              </div>
              <span className="font-body text-[10px] font-medium leading-none">
                {t[labelKey]}
              </span>
            </Link>
          );
        })}
      </div>
    </nav>
  );
}
