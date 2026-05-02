import BottomNav from '@/components/BottomNav';
import PrivateSessionBanner from '@/components/PrivateSessionBanner';

export default function MainLayout({ children }: { children: React.ReactNode }) {
  return (
    <>
      <PrivateSessionBanner />
      <main className="pb-nav">
        {children}
      </main>
      <BottomNav />
    </>
  );
}
