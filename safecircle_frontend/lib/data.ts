export interface Clinic {
  id: number;
  name: string;
  nameRw: string;
  address: string;
  district: string;
  distance: string;
  distanceKm: number;
  phone: string;
  services: string[];
  servicesRw: string[];
  hours: string;
  hoursRw: string;
  isYouthFriendly: boolean;
  noJudgment: boolean;
  anonymousVisits: boolean;
}

export const clinics: Clinic[] = [
  {
    id: 1,
    name: 'CHUK Youth-Friendly Clinic',
    nameRw: "Kliniki y'Urubyiruko ya CHUK",
    address: 'KN 4 Ave, Kiyovu, Kigali',
    district: 'Nyarugenge',
    distance: '0.8 km',
    distanceKm: 0.8,
    phone: '+250 788 000 001',
    services: ['HIV Testing & PrEP', 'Family Planning', 'Counseling', 'STI Treatment'],
    servicesRw: ['Isuzuma rya HIV & PrEP', 'Kubara Urubyaro', 'Ubujyanama', 'Kuvura STI'],
    hours: 'Mon–Fri: 8am–5pm',
    hoursRw: 'Ku wa 1–5: 8am–5pm',
    isYouthFriendly: true,
    noJudgment: true,
    anonymousVisits: true,
  },
  {
    id: 2,
    name: 'Nyarugenge Youth Health Centre',
    nameRw: "Ikigo cy'Ubuzima cy'Urubyiruko cya Nyarugenge",
    address: 'KG 9 Ave, Nyarugenge',
    district: 'Nyarugenge',
    distance: '1.2 km',
    distanceKm: 1.2,
    phone: '+250 788 000 002',
    services: ['HIV Testing', 'PrEP', 'Mental Health', 'Counseling'],
    servicesRw: ['Isuzuma rya HIV', 'PrEP', 'Ubuzima bwo mu Mutwe', 'Ubujyanama'],
    hours: 'Mon–Sat: 8am–7pm',
    hoursRw: 'Ku wa 1–6: 8am–7pm',
    isYouthFriendly: true,
    noJudgment: true,
    anonymousVisits: true,
  },
  {
    id: 3,
    name: 'Remera Community Health Centre',
    nameRw: "Ikigo cy'Ubuzima Bw'Abaturage cya Remera",
    address: 'KG 7 Ave, Remera',
    district: 'Gasabo',
    distance: '2.4 km',
    distanceKm: 2.4,
    phone: '+250 788 000 003',
    services: ['HIV Testing', 'Family Planning', 'Maternal Health'],
    servicesRw: ['Isuzuma rya HIV', 'Kubara Urubyaro', "Ubuzima bw'Umubyeyi"],
    hours: 'Mon–Fri: 7:30am–4:30pm',
    hoursRw: 'Ku wa 1–5: 7:30am–4:30pm',
    isYouthFriendly: true,
    noJudgment: false,
    anonymousVisits: false,
  },
  {
    id: 4,
    name: 'Kicukiro Youth-Friendly Clinic',
    nameRw: "Kliniki y'Urubyiruko ya Kicukiro",
    address: 'KK 15 Rd, Kicukiro',
    district: 'Kicukiro',
    distance: '3.1 km',
    distanceKm: 3.1,
    phone: '+250 788 000 004',
    services: ['HIV Testing & ART', 'STI Treatment', 'Family Planning', 'Mental Health'],
    servicesRw: ['Isuzuma rya HIV & ART', 'Kuvura STI', 'Kubara Urubyaro', 'Ubuzima bwo mu Mutwe'],
    hours: 'Mon–Fri: 8am–6pm, Sat: 9am–1pm',
    hoursRw: 'Ku wa 1–5: 8am–6pm, ku wa 6: 9am–1pm',
    isYouthFriendly: true,
    noJudgment: true,
    anonymousVisits: true,
  },
  {
    id: 5,
    name: 'King Faisal Hospital SRH Unit',
    nameRw: "Inzu y'Isuzuma bwa SRH ya King Faisal",
    address: 'KG 544 St, Kiyovu',
    district: 'Nyarugenge',
    distance: '3.8 km',
    distanceKm: 3.8,
    phone: '+250 788 000 005',
    services: ['HIV Testing', 'ART', 'Specialist Care', 'Family Planning'],
    servicesRw: ['Isuzuma rya HIV', 'ART', "Inzobere z'Ubuzima", 'Kubara Urubyaro'],
    hours: 'Mon–Fri: 8am–5pm',
    hoursRw: 'Ku wa 1–5: 8am–5pm',
    isYouthFriendly: false,
    noJudgment: true,
    anonymousVisits: false,
  },
  {
    id: 6,
    name: 'Kacyiru District Hospital',
    nameRw: 'Ibitaro bya Kacyiru',
    address: 'KG 3 Ave, Kacyiru, Gasabo',
    district: 'Gasabo',
    distance: '1.0 km',
    distanceKm: 1.0,
    phone: '+250 788 000 006',
    services: ['HIV Testing & PrEP', 'STI Treatment', 'Youth SRH', 'Counseling'],
    servicesRw: ['Isuzuma rya HIV & PrEP', 'Kuvura STI', "SRH y'Urubyiruko", 'Ubujyanama'],
    hours: 'Mon–Fri: 7am–6pm, Sat: 8am–1pm',
    hoursRw: 'Ku wa 1–5: 7am–6pm, ku wa 6: 8am–1pm',
    isYouthFriendly: true,
    noJudgment: true,
    anonymousVisits: true,
  },
  {
    id: 7,
    name: 'Kimironko Health Centre',
    nameRw: 'Ikigo cy\'Ubuzima cya Kimironko',
    address: 'KG 11 Ave, Kimironko, Gasabo',
    district: 'Gasabo',
    distance: '1.1 km',
    distanceKm: 1.1,
    phone: '+250 788 000 007',
    services: ['HIV Testing', 'Family Planning', 'Youth Friendly SRH', 'PrEP'],
    servicesRw: ['Isuzuma rya HIV', 'Kubara Urubyaro', "SRH y'Urubyiruko", 'PrEP'],
    hours: 'Mon–Sat: 7:30am–5pm',
    hoursRw: 'Ku wa 1–6: 7:30am–5pm',
    isYouthFriendly: true,
    noJudgment: true,
    anonymousVisits: true,
  },
];

export interface Article {
  id: number;
  title: string;
  titleRw: string;
  category: 'Sexual Health' | 'HIV & STIs' | 'Relationships' | 'Mental Health';
  readTime: number;
  excerpt: string;
  excerptRw: string;
  isVerified: boolean;
  isFeatured: boolean;
  color: string;
}

export const articles: Article[] = [
  {
    id: 1,
    title: 'Understanding PrEP: Your HIV Prevention Option',
    titleRw: 'Gusobanukirwa na PrEP: Amahirwe yo Kwirinda HIV',
    category: 'HIV & STIs',
    readTime: 4,
    excerpt: "PrEP is a once-daily pill that can prevent HIV by up to 99%. Learn who it's for and how to access it in Kigali.",
    excerptRw: "PrEP ni ikinini kimwe ku munsi gishobora kwirinda HIV kugeza 99%. Menya uwo iyibereye n'uko uyihabwa i Kigali.",
    isVerified: true,
    isFeatured: true,
    color: 'bg-teal/10 text-teal',
  },
  {
    id: 2,
    title: 'Consent: What It Means and Why It Matters',
    titleRw: 'Kumvana: Icyo Bisobanura n\'Ubukungu Bwabyo',
    category: 'Relationships',
    readTime: 3,
    excerpt: 'Consent is enthusiastic, ongoing, and freely given. Understanding it protects everyone in a relationship.',
    excerptRw: 'Kumvana ni ubuntu, bukomeza, kandi butangwa ubushake. Gusobanukirwa nabwo barinda bose mu mibanire.',
    isVerified: true,
    isFeatured: false,
    color: 'bg-coral/10 text-coral',
  },
  {
    id: 3,
    title: 'Getting Tested for HIV: What to Expect',
    titleRw: 'Gupimwa HIV: Ibyo Ugomba Kumenya',
    category: 'HIV & STIs',
    readTime: 5,
    excerpt: 'Regular HIV testing is a normal, responsible part of caring for your health. Here\'s what happens during a test.',
    excerptRw: "Gupimwa HIV bihoraho ni inzira isanzwe y'kwita ku buzima bwawe. Dore ibiza gihe cyo gupimwa.",
    isVerified: true,
    isFeatured: false,
    color: 'bg-teal/10 text-teal',
  },
  {
    id: 4,
    title: 'Contraception Options Explained Simply',
    titleRw: "Amategeko y'Uburere bw'Urubyaro Yagaragajwe Neza",
    category: 'Sexual Health',
    readTime: 6,
    excerpt: 'From pills to condoms to long-term options — a clear, judgment-free guide to finding what works for you.',
    excerptRw: "Kuva ku nkingi kugera ku bigikisho kugeza ku mahitamo maremare — Intangiriro nziza kandi idashinja.",
    isVerified: true,
    isFeatured: true,
    color: 'bg-health-green/10 text-health-green',
  },
  {
    id: 5,
    title: 'STI Symptoms: When to See a Doctor',
    titleRw: 'Ibimenyetso bya STI: Igihe cyo Gusura Muganga',
    category: 'HIV & STIs',
    readTime: 4,
    excerpt: 'Many STIs have no obvious symptoms. Here\'s what to look out for and why regular check-ups matter.',
    excerptRw: "Indwara nyinshi za STI nta bimenyetso bisobanutse. Dore ibyo ugomba gutegeka n'impamvu isuzumwa bihoraho bikomerwa.",
    isVerified: true,
    isFeatured: false,
    color: 'bg-teal/10 text-teal',
  },
  {
    id: 6,
    title: 'Mental Health and Sexual Wellbeing',
    titleRw: 'Ubuzima bwo mu Mutwe n\'Ubuzima bwo Gusangira',
    category: 'Mental Health',
    readTime: 5,
    excerpt: 'Your mental and sexual health are deeply connected. Learn how to care for both and when to seek support.',
    excerptRw: "Ubuzima bwawe bwo mu mutwe n'ubwo gusangira bihuza cyane. Menya uko kwita kubyombi no gushaka ubufasha.",
    isVerified: true,
    isFeatured: false,
    color: 'bg-coral/10 text-coral',
  },
  {
    id: 7,
    title: 'Talking to Your Partner About Sexual Health',
    titleRw: 'Kuvugana n\'Inshuti yawe ku Buzima bwo Gusangira',
    category: 'Relationships',
    readTime: 3,
    excerpt: 'Starting the conversation about sexual health can feel awkward. Here are gentle ways to open the dialogue.',
    excerptRw: "Gutangira ikiganiro cy'ubuzima bwo gusangira bishobora kugaragara nk'ikibazo. Dore uburyo bworoshye bwo gutangiramo.",
    isVerified: false,
    isFeatured: false,
    color: 'bg-coral/10 text-coral',
  },
];
