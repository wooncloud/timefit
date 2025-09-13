import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { CalendarClock } from 'lucide-react';
import Link from 'next/link';

export function LoginForm({
  className,
  ...props
}: React.ComponentProps<'form'>) {
  return (
    <form className={cn('grid gap-6', className)} {...props}>
      <div className="flex flex-col items-center gap-2">
        <Link href="/" className="flex flex-col items-center gap-2 font-medium">
          <div className="flex size-8 items-center justify-center rounded-md">
            <CalendarClock className="size-6" />
          </div>
          <span className="sr-only">Timefit</span>
        </Link>
        <h1 className="text-xl font-bold">
          Welcome to <Link href="/">Timefit</Link>
        </h1>
        <div className="text-center text-sm">
          Don&apos;t have an account?{' '}
          <a href="/signup" className="underline underline-offset-4">
            Sign up
          </a>
        </div>
      </div>
      <Tabs defaultValue="user">
        <div className="flex flex-col items-center gap-2">
          <TabsList>
            <TabsTrigger value="user">개인 로그인</TabsTrigger>
            <TabsTrigger value="business">사업자 로그인</TabsTrigger>
          </TabsList>
        </div>
        <div className="py-2">
          <Label htmlFor="email">Email</Label>
          <Input id="email" type="email" placeholder="m@example.com" required />
        </div>
        <div className="py-2">
          <Label htmlFor="password">Password</Label>
          <Input
            id="password"
            type="password"
            placeholder="••••••••"
            required
          />
        </div>
        {/* <Button type="submit" className="w-full">
        Login
      </Button> */}
        <TabsContent value="user">
          <Link href="/business">
            {' '}
            {/* 임시 */}
            <Button className="w-full">개인 로그인</Button>
          </Link>
        </TabsContent>
        <TabsContent value="business">
          <Link href="/business">
            {' '}
            {/* 임시 */}
            <Button className="w-full">사업자 로그인</Button>
          </Link>
        </TabsContent>
      </Tabs>

      <div className="relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t after:border-border">
        <span className="relative z-10 bg-background px-2 text-muted-foreground">
          Or
        </span>
      </div>
      <div className="grid gap-4 sm:grid-cols-2">
        <Button variant="outline" type="button" className="w-full">
          <img src="/icons/apple.svg" alt="Apple" className="size-5" />
          Continue with Apple
        </Button>
        <Button variant="outline" type="button" className="w-full">
          <img src="/icons/google.svg" alt="Google" className="size-5" />
          Continue with Google
        </Button>
      </div>
      <div className="*:[a]:hover:text-primary *:[a]:underline *:[a]:underline-offset-4 text-balance text-center text-xs text-muted-foreground">
        By clicking continue, you agree to our <a href="#">Terms of Service</a>{' '}
        and <a href="#">Privacy Policy</a>.
      </div>
      <hr />
      <div className="*:[a]:hover:text-primary *:[a]:underline *:[a]:underline-offset-4 text-balance text-center text-xs text-muted-foreground">
        Go back to Homepage - <Link href="/">Timefit</Link>.
      </div>
    </form>
  );
}
