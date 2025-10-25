'use client';

import { cn } from '@/lib/utils';
import Link from 'next/link';
import { CalendarClock } from 'lucide-react';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { ChangeEvent, FormEvent, useState } from 'react';
import { useRouter } from 'next/navigation';
import { initialSigninForm, validateSigninForm } from './signin';
import {
  SigninFormData,
  SigninFormErrors,
  SigninHandlerResponse,
  SigninRequestBody,
} from '@/types/auth/signin';

export default function BusinessSignInPage() {
  const [formData, setFormData] = useState<SigninFormData>(initialSigninForm);
  const [errors, setErrors] = useState<SigninFormErrors>({});
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [activeTab, setActiveTab] = useState<'user' | 'business'>('user');
  const router = useRouter();

  const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    const fieldName = name as keyof SigninFormData;

    setFormData(prev => ({
      ...prev,
      [fieldName]: value,
    }));

    if (errors[fieldName]) {
      setErrors(prev => ({
        ...prev,
        [fieldName]: undefined,
      }));
    }
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const { isValid, errors: validationErrors } = validateSigninForm(formData);
    setErrors(validationErrors);

    if (!isValid) {
      return;
    }

    setIsLoading(true);
    setMessage('');

    try {
      const requestBody: SigninRequestBody = {
        email: formData.email,
        password: formData.password,
      };

      const response = await fetch('/api/auth/signin', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(requestBody),
      });

      const data = (await response.json()) as SigninHandlerResponse;

      if (data.success) {
        setMessage('로그인에 성공했습니다. 메인 페이지로 이동합니다.');
        setTimeout(() => {
          router.replace('/');
        }, 1500);
      } else {
        setMessage(data.message || '로그인에 실패했습니다.');
      }
    } catch (error) {
      console.error('로그인 요청 오류:', error);
      setMessage('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-svh flex-col items-center justify-center gap-6 bg-background p-6 md:p-10">
      <div className="w-full max-w-sm">
        <form className={cn('grid gap-4')} onSubmit={handleSubmit}>
          <div className="flex flex-col items-center gap-2">
            <Link
              href="/"
              className="flex flex-col items-center gap-2 font-medium"
            >
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
              <Link href="/signup" className="underline underline-offset-4">
                Sign up
              </Link>
            </div>
          </div>
          {message && (
            <div
              className={cn(
                'rounded-md p-3 text-center text-sm',
                message.includes('성공')
                  ? 'border border-green-200 bg-green-50 text-green-700'
                  : 'border border-red-200 bg-red-50 text-red-700'
              )}
            >
              {message}
            </div>
          )}
          <div>
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              name="email"
              type="email"
              placeholder="m@example.com"
              value={formData.email}
              onChange={handleInputChange}
              className={errors.email ? 'border-red-500' : ''}
              required
            />
            {errors.email && (
              <span className="text-sm text-red-500">{errors.email}</span>
            )}
          </div>
          <div>
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              name="password"
              type="password"
              placeholder="••••••••"
              value={formData.password}
              onChange={handleInputChange}
              className={errors.password ? 'border-red-500' : ''}
              required
            />
            {errors.password && (
              <span className="text-sm text-red-500">{errors.password}</span>
            )}
          </div>
          <div>
            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? '로그인 중...' : '로그인'}
            </Button>
          </div>
          {/* <div className="relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t after:border-border">
                        <span className="relative z-10 bg-background px-2 text-muted-foreground">
                        Or
                        </span>
                    </div>
                    <div className="grid gap-4 sm:grid-cols-2">
                        <Button variant="outline" type="button" className="w-full">
                            <img src="/icons/apple.svg" alt="Apple" className="size-5"/>
                            Apple로 로그인
                        </Button>
                        <Button variant="outline" type="button" className="w-full">
                            <img src="/icons/google.svg" alt="Google" className="size-5"/>
                            Google로 로그인
                        </Button>
                    </div> */}
          <div className="*:[a]:hover:text-primary *:[a]:underline *:[a]:underline-offset-4 text-balance text-center text-xs text-muted-foreground">
            By clicking continue, you agree to our
            <Link href="/policy/service">Terms of Service</Link> and
            <Link href="/policy/privacy">Privacy Policy</Link>.
          </div>
          <hr />
          <div className="*:[a]:hover:text-primary *:[a]:underline *:[a]:underline-offset-4 text-balance text-center text-xs text-muted-foreground">
            Go back to Homepage - <Link href="/">Timefit</Link>.
          </div>
        </form>
      </div>
    </div>
  );
}
