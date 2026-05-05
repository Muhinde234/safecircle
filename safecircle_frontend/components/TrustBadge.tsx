import { ShieldCheck } from 'lucide-react';

interface TrustBadgeProps {
  variant: 'moh' | 'clinical';
  size?: 'sm' | 'md';
  label?: string;
}

export default function TrustBadge({ variant, size = 'sm', label }: TrustBadgeProps) {
  const isMoh = variant === 'moh';

  const defaultLabel = isMoh ? 'Rwanda MoH Approved' : 'Clinically Verified';
  const display = label ?? defaultLabel;

  const colorClass = isMoh
    ? 'bg-teal/10 text-teal border-teal/20'
    : 'bg-health-green/10 text-health-green border-health-green/20';

  const iconSize = size === 'sm' ? 10 : 12;
  const textClass = size === 'sm' ? 'text-[10px] px-2 py-0.5' : 'text-xs px-2.5 py-1';

  return (
    <span
      className={`inline-flex items-center gap-1 border rounded-full font-body font-medium whitespace-nowrap ${colorClass} ${textClass}`}
    >
      <ShieldCheck size={iconSize} strokeWidth={2} />
      {display}
    </span>
  );
}
