import { Logo } from '@/components/ui/logo';

export function Footer() {
  return (
    <footer className="mt-auto border-t border-border py-12 text-center text-sm text-muted-foreground">
      <div className="container mx-auto px-6">
        <div className="mb-2 flex items-center justify-center gap-2 font-semibold text-foreground/80">
          <Logo size={20} className="text-foreground/80" />
          <p>TimeFit</p>
        </div>
        <p>© 2026 TimeFit. All rights reserved.</p>
      </div>
    </footer>
  );
}
