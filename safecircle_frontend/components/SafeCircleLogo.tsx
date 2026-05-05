interface SafeCircleLogoProps {
  size?: number;
  className?: string;
}

export default function SafeCircleLogo({ size = 64, className = '' }: SafeCircleLogoProps) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 64 64"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={className}
      aria-label="Safe Circle"
    >
      {/* Dashed outer ring */}
      <circle
        cx="32"
        cy="32"
        r="30"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeDasharray="3 2.5"
        fill="none"
        strokeOpacity="0.55"
      />
      {/* Shield */}
      <path
        d="M32 6L10 14V29C10 42 19.5 54.4 32 58C44.5 54.4 54 42 54 29V14L32 6Z"
        fill="currentColor"
        fillOpacity="0.15"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinejoin="round"
      />
      {/* Inner ring */}
      <circle cx="32" cy="32" r="11" stroke="currentColor" strokeWidth="2" fill="none" />
      {/* Center dot */}
      <circle cx="32" cy="32" r="3.5" fill="currentColor" />
    </svg>
  );
}
