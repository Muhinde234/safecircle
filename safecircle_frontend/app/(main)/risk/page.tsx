'use client';

import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ChevronLeft, ShieldCheck, Phone, ArrowRight, RefreshCw, MapPin } from 'lucide-react';
import { useRouter } from 'next/navigation';
import Header from '@/components/Header';
import { useApp } from '@/app/providers';
import { translations } from '@/lib/translations';
import { api } from '@/lib/api';
import type { QuestionnaireDto, RiskAssessmentResponse, QuestionnaireOption } from '@/lib/api';

const slideIn = {
  hidden: { opacity: 0, x: 20 },
  show: { opacity: 1, x: 0, transition: { duration: 0.25, ease: 'easeOut' } },
  exit: { opacity: 0, x: -20, transition: { duration: 0.2, ease: 'easeIn' } },
} as const;

const localTranslations: Record<string, Record<string, string>> = {
  en: {
    title: 'Risk Assessment',
    subtitle: 'Confidential decision-tree assessment',
    loading: 'Loading assessment questionnaire...',
    restart: 'Restart Assessment',
    riskLevelLabel: 'Risk Level',
    urgencyWindowLabel: 'Urgency Window',
    recommendedActionLabel: 'Recommended Action',
    findClinicsNearby: 'Find Clinics Nearby',
    emergencyCall: 'Confidential Emergency Line',
    backToExplore: 'Back to Explore',
    highRisk: 'High Risk',
    mediumRisk: 'Medium Risk',
    lowRisk: 'Low Risk',
  },
  rw: {
    title: 'Sura Uko Uhagaze (Risk)',
    subtitle: 'Isuzumwa ry\'ibanga ry\'akaga',
    loading: 'Gushaka ibibazo by\'isuzuma...',
    restart: 'Subiramo Isuzuma',
    riskLevelLabel: 'Ikigero cy\'Akaga',
    urgencyWindowLabel: 'Igihe cyo Gukora',
    recommendedActionLabel: 'Inama Uhabwa',
    findClinicsNearby: 'Shaka Amavuriro Hafi Yawe',
    emergencyCall: 'Umurongo w\'Ubutabazi w\'Ibanga',
    backToExplore: 'Subira Ahabanza',
    highRisk: 'Akaga Gakomeye',
    mediumRisk: 'Akaga Kiringaniye',
    lowRisk: 'Akaga Gake',
  },
};

const valueTranslations: Record<string, string> = {
  // Questions
  'What event occurred?': 'Ni ikihe gikorwa cyabaye?',
  'Did this event happen within the last 72 hours (3 days)?': 'Ese ibi byabaye mu masaha 72 ashize (iminsi 3)?',
  'Are you experiencing any physical symptoms (e.g. pain, burning, sores, unusual discharge)?': 'Ese ufite ibimenyetso (nk\'ububabare, gushya, ibisebe, cyangwa ibyiyumviro bidasanzwe)?',
  'Are you experiencing any physical symptoms?': 'Ese ufite ibimenyetso by\'ububabare?',
  'Are you experiencing physical symptoms?': 'Ese ufite ibimenyetso?',

  // Options
  'Unprotected intercourse (vaginal/anal)': 'Imibonano mpuzabitsina idakingiye (mu gitsina/mu kibuno)',
  'Condom broke/slipped during intercourse': 'Agakingirizo kacitse/kanyeshyushe mu gihe cy\'imibonano',
  'Shared needles or injection equipment': 'Gusangira inshinge cyangwa ibikoresho by\'ubuvuzi',
  'Oral sex without barrier': 'Imibonano yo mu kanwa idakingiye',
  'Yes, within the last 72 hours': 'Yego, mu masaha 72 ashize',
  'No, more than 72 hours ago': 'Oya, birenze amasaha 72',
  'Yes, symptoms are present': 'Yego, ibimenyetso bihari',
  'No, no symptoms': 'Oya, nta bimenyetso bihari',

  // Actions & Urgency Window
  'Please visit an emergency clinic for PEP (Post-Exposure Prophylaxis).': 'Nyamuneka gana ivuriro rya hafi ry\'ubutabazi kugira ngo uhabwe PEP.',
  'Consult a healthcare provider to discuss preventative measures.': 'Baza muganga w\'ubuzima kugira ngo muganire ku buryo bwo kwirinda.',
  'Schedule a routine screening at your local clinic.': 'Genda kwipimisha bisanzwe ku ivuriro rya hafi yawe.',
  'Within 24 hours': 'Mu masaha 24',
  'Within 48-72 hours': 'Mu masaha 48-72',
  'Next available appointment': 'Kuri gahunda ikurikira ihari',
};

export default function RiskAssessmentPage() {
  const { language, sessionId } = useApp();
  const t = translations[language];
  const localT = localTranslations[language];
  const router = useRouter();

  const [questionnaire, setQuestionnaire] = useState<QuestionnaireDto | null>(null);
  const [currentNodeId, setCurrentNodeId] = useState<string>('');
  const [loading, setLoading] = useState(true);
  const [history, setHistory] = useState<string[]>([]);
  const [assessmentResult, setAssessmentResult] = useState<RiskAssessmentResponse | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const fetchQuestionnaire = async () => {
      try {
        const data = await api.getQuestionnaire();
        setQuestionnaire(data);
        if (data.startQuestionId) {
          setCurrentNodeId(data.startQuestionId);
        }
      } catch (err) {
        console.error('Failed to fetch questionnaire:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchQuestionnaire();
  }, []);

  const translateText = (text: string): string => {
    if (language === 'en') return text;
    return valueTranslations[text] || text;
  };

  const handleOptionSelect = async (option: QuestionnaireOption) => {
    if (option.nextQuestionId) {
      setHistory(prev => [...prev, currentNodeId]);
      setCurrentNodeId(option.nextQuestionId);
    } else if (option.riskAssessmentShortcut) {
      setSubmitting(true);
      try {
        const shortcut = option.riskAssessmentShortcut;
        const result = await api.assessRisk(
          sessionId || '',
          shortcut.eventType,
          shortcut.hoursSinceEvent,
          shortcut.symptomsPresent
        );
        setAssessmentResult(result);
      } catch (err) {
        console.error('Risk assessment failed:', err);
      } finally {
        setSubmitting(false);
      }
    }
  };

  const handleBack = () => {
    if (history.length > 0) {
      const prev = history[history.length - 1];
      setHistory(prevHistory => prevHistory.slice(0, -1));
      setCurrentNodeId(prev);
    } else {
      router.back();
    }
  };

  const handleRestart = () => {
    setHistory([]);
    setAssessmentResult(null);
    if (questionnaire?.startQuestionId) {
      setCurrentNodeId(questionnaire.startQuestionId);
    }
  };

  const currentNode = questionnaire?.questions?.[currentNodeId];

  return (
    <div className="min-h-screen bg-frost-white flex flex-col">
      <Header title={localT.title} subtitle={localT.subtitle} showBack onBack={handleBack} />

      <div className="flex-1 max-w-md mx-auto w-full px-4 py-6 flex flex-col">
        {loading && (
          <div className="flex-1 flex flex-col items-center justify-center gap-3">
            <div className="w-8 h-8 border-4 border-teal border-t-transparent rounded-full animate-spin" />
            <p className="font-body text-xs text-slate-gray/70">{localT.loading}</p>
          </div>
        )}

        {!loading && questionnaire && !assessmentResult && (
          <div className="flex-1 flex flex-col justify-center gap-6">
            <AnimatePresence mode="wait">
              {currentNode && (
                <motion.div
                  key={currentNode.id}
                  variants={slideIn}
                  initial="hidden"
                  animate="show"
                  exit="exit"
                  className="bg-white rounded-2xl border border-mint-teal/30 p-5 shadow-sm space-y-4"
                >
                  {/* Step indicator */}
                  <div className="flex items-center gap-1.5">
                    <div className="w-1.5 h-1.5 rounded-full bg-teal" />
                    <span className="font-body text-[10px] text-slate-gray font-semibold uppercase tracking-wider">
                      Question
                    </span>
                  </div>

                  {/* Question text */}
                  <h2 className="font-heading font-bold text-lg text-deep-navy leading-snug">
                    {translateText(currentNode.text)}
                  </h2>

                  {/* Options */}
                  <div className="flex flex-col gap-2 pt-2">
                    {currentNode.options.map((opt, i) => (
                      <button
                        key={i}
                        onClick={() => handleOptionSelect(opt)}
                        disabled={submitting}
                        className="flex items-center justify-between w-full p-4 rounded-xl border border-mint-teal/40 hover:bg-mint-teal/10 hover:border-teal/30 active:bg-mint-teal/20 transition-all font-body text-sm text-left text-deep-navy leading-relaxed"
                      >
                        <span className="flex-1 pr-3">{translateText(opt.text)}</span>
                        <ArrowRight size={14} className="text-teal/70 flex-none" />
                      </button>
                    ))}
                  </div>
                </motion.div>
              )}
            </AnimatePresence>

            {history.length > 0 && (
              <button
                onClick={handleBack}
                className="flex items-center justify-center gap-1.5 py-2 font-heading font-semibold text-xs text-slate-gray/80 hover:text-teal transition-colors"
              >
                <ChevronLeft size={14} /> Back
              </button>
            )}
          </div>
        )}

        {!loading && assessmentResult && (
          <motion.div
            initial={{ opacity: 0, y: 15 }}
            animate={{ opacity: 1, y: 0 }}
            className="flex-1 flex flex-col justify-center gap-5"
          >
            {/* Result Card */}
            <div className="bg-white rounded-2xl border border-mint-teal/30 p-6 shadow-sm space-y-5">
              <div className="flex items-center justify-center mx-auto w-12 h-12 rounded-full bg-teal/10">
                <ShieldCheck size={26} className="text-teal" />
              </div>

              <h2 className="font-heading font-bold text-xl text-deep-navy text-center">
                Assessment Complete
              </h2>

              <div className="space-y-4 pt-2">
                {/* Risk Level */}
                <div className="p-3 bg-frost-white rounded-xl border border-mint-teal/20">
                  <span className="font-body text-[10px] text-slate-gray font-semibold uppercase tracking-wider">
                    {localT.riskLevelLabel}
                  </span>
                  <div className="flex items-center gap-2 mt-1">
                    <span
                      className={`w-2 h-2 rounded-full ${
                        assessmentResult.riskLevel === 'HIGH'
                          ? 'bg-coral'
                          : assessmentResult.riskLevel === 'MEDIUM'
                          ? 'bg-amber-500'
                          : 'bg-health-green'
                      }`}
                    />
                    <span className="font-heading font-bold text-sm text-deep-navy">
                      {assessmentResult.riskLevel === 'HIGH'
                        ? localT.highRisk
                        : assessmentResult.riskLevel === 'MEDIUM'
                        ? localT.mediumRisk
                        : localT.lowRisk}
                    </span>
                  </div>
                </div>

                {/* Urgency Window */}
                <div className="p-3 bg-frost-white rounded-xl border border-mint-teal/20">
                  <span className="font-body text-[10px] text-slate-gray font-semibold uppercase tracking-wider">
                    {localT.urgencyWindowLabel}
                  </span>
                  <p className="font-heading font-bold text-sm text-deep-navy mt-1">
                    {translateText(assessmentResult.urgencyWindow)}
                  </p>
                </div>

                {/* Recommended Action */}
                <div className="p-3 bg-frost-white rounded-xl border border-mint-teal/20">
                  <span className="font-body text-[10px] text-slate-gray font-semibold uppercase tracking-wider">
                    {localT.recommendedActionLabel}
                  </span>
                  <p className="font-body text-sm text-deep-navy mt-1 leading-relaxed">
                    {translateText(assessmentResult.recommendedAction)}
                  </p>
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="flex flex-col gap-2.5">
              <button
                onClick={() => router.push('/clinics')}
                className="flex items-center justify-center gap-2 w-full h-13 bg-teal text-white font-heading font-bold text-sm rounded-xl shadow-sm active:opacity-90 transition-opacity"
              >
                <MapPin size={16} />
                {localT.findClinicsNearby}
              </button>

              <button
                onClick={handleRestart}
                className="flex items-center justify-center gap-2 w-full h-11 border border-teal/40 text-teal font-heading font-semibold text-xs rounded-xl active:bg-teal/5 transition-colors"
              >
                <RefreshCw size={14} />
                {localT.restart}
              </button>

              <button
                onClick={() => router.push('/explore')}
                className="flex items-center justify-center gap-1.5 py-2 font-heading font-semibold text-xs text-slate-gray/80 hover:text-teal transition-colors"
              >
                {localT.backToExplore}
              </button>
            </div>
          </motion.div>
        )}
      </div>

      {/* Emergency Call Banner */}
      <div className="px-4 pb-8">
        <div className="bg-coral rounded-xl px-4 py-3 flex items-start gap-3">
          <Phone size={15} className="text-white flex-none mt-0.5" strokeWidth={2} />
          <div>
            <p className="font-body text-xs font-bold text-white">
              {localT.emergencyCall}
            </p>
            <p className="font-body text-[11px] text-white/90 mt-0.5">
              {t.callLine} · {t.smsLine}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
