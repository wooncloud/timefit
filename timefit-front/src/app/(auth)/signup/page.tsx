'use client';

import {cn} from '@/lib/utils';
import {Button} from '@/components/ui/button';
import {Input} from '@/components/ui/input';
import {Label} from '@/components/ui/label';
import {CalendarClock} from 'lucide-react';
import Link from 'next/link';
import {useState} from 'react';
import {useRouter} from 'next/navigation';
import {formatPhoneNumber, validateSignupForm} from './signup';
import {
    SignupFormData,
    SignupFormErrors,
    SignupHandlerResponse,
    SignupRequestBody
} from '@/types/auth/signup';

export default function SignUpPage() {
    const [formData, setFormData] = useState<SignupFormData>({
        email: '',
        password: '',
        confirmPassword: '',
        name: '',
        phoneNumber: ''
    });
    const [isLoading, setIsLoading] = useState(false);
    const [errors, setErrors] = useState<SignupFormErrors>({});
    const [message, setMessage] = useState('');
    const router = useRouter();

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        const fieldName = name as keyof SignupFormData;
        const nextValue = fieldName === 'phoneNumber' ? formatPhoneNumber(value) : value;
        setFormData(prev => ({
            ...prev,
            [fieldName]: nextValue
        }));
        // 입력시 해당 필드의 에러 메시지 제거
        if (errors[fieldName]) {
            setErrors(prev => ({
                ...prev,
                [fieldName]: undefined
            }));
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        const {isValid, errors: validationErrors} = validateSignupForm(formData);
        setErrors(validationErrors);

        if (!isValid) {
            return;
        }

        setIsLoading(true);
        setMessage('');

        try {
            const requestBody: SignupRequestBody = {
                email: formData.email,
                password: formData.password,
                name: formData.name,
                phoneNumber: formData.phoneNumber
            };
            const response = await fetch('/api/auth/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestBody),
            });

            const data = (await response.json()) as SignupHandlerResponse;

            if (data.success) {
                setMessage('회원가입이 완료되었습니다. 메인 페이지로 이동합니다.');
                // 성공시 메인 페이지로 리다이렉트
                setTimeout(() => {
                    router.push('/');
                }, 2000);
            } else {
                setMessage(data.message || '회원가입에 실패했습니다.');
            }
        } catch (error) {
            console.error('회원가입 오류:', error);
            setMessage('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="flex min-h-svh flex-col items-center justify-center gap-6 bg-background p-6 md:p-10">
            <div className="w-full max-w-sm">
                <form className={cn('grid gap-6')} onSubmit={handleSubmit}>
                    <div className="flex flex-col items-center gap-2">
                        <Link href="/" className="flex flex-col items-center gap-2 font-medium">
                            <div className="flex size-8 items-center justify-center rounded-md">
                                <CalendarClock className="size-6"/>
                            </div>
                            <span className="sr-only">Timefit</span>
                        </Link>
                        <h1 className="text-xl font-bold">
                            Join <Link href="/">Timefit</Link>
                        </h1>
                        <div className="text-center text-sm">
                            Already have an account?{' '}
                            <a href="/signin" className="underline underline-offset-4">
                                Sign in
                            </a>
                        </div>
                    </div>

                    {message && (
                        <div className={cn(
                            "text-center text-sm p-3 rounded-md",
                            message.includes('완료') ? "bg-green-50 text-green-700 border border-green-200" : "bg-red-50 text-red-700 border border-red-200"
                        )}>
                            {message}
                        </div>
                    )}

                    <div className="grid gap-1">
                        <Label htmlFor="email">Email</Label>
                        <Input
                            id="email"
                            name="email"
                            type="email"
                            placeholder="m@example.com"
                            value={formData.email}
                            onChange={handleInputChange}
                            className={errors.email ? "border-red-500" : ""}
                            required
                        />
                        {errors.email && <span className="text-sm text-red-500">{errors.email}</span>}
                    </div>
                    <div className="grid gap-1">
                        <Label htmlFor="password">비밀번호</Label>
                        <Input
                            id="password"
                            name="password"
                            type="password"
                            placeholder="••••••••"
                            value={formData.password}
                            onChange={handleInputChange}
                            className={errors.password ? "border-red-500" : ""}
                            required
                        />
                        {errors.password && <span className="text-sm text-red-500">{errors.password}</span>}
                    </div>
                    <div className="grid gap-1">
                        <Label htmlFor="confirmPassword">비밀번호 확인</Label>
                        <Input
                            id="confirmPassword"
                            name="confirmPassword"
                            type="password"
                            placeholder="••••••••"
                            value={formData.confirmPassword}
                            onChange={handleInputChange}
                            className={errors.confirmPassword ? "border-red-500" : ""}
                            required
                        />
                        {errors.confirmPassword && <span className="text-sm text-red-500">{errors.confirmPassword}</span>}
                    </div>
                    <div className="grid gap-1">
                        <Label htmlFor="name">이름</Label>
                        <Input
                            id="name"
                            name="name"
                            type="text"
                            placeholder="홍길동"
                            value={formData.name}
                            onChange={handleInputChange}
                            className={errors.name ? "border-red-500" : ""}
                            required
                        />
                        {errors.name && <span className="text-sm text-red-500">{errors.name}</span>}
                    </div>
                    <div className="grid gap-1">
                        <Label htmlFor="phoneNumber">전화번호</Label>
                        <Input
                            id="phoneNumber"
                            name="phoneNumber"
                            type="tel"
                            placeholder="010-1234-5678"
                            value={formData.phoneNumber}
                            onChange={handleInputChange}
                            className={errors.phoneNumber ? "border-red-500" : ""}
                            required
                        />
                        {errors.phoneNumber && <span className="text-sm text-red-500">{errors.phoneNumber}</span>}
                    </div>
                    <Button type="submit" className="w-full" disabled={isLoading}>
                        {isLoading ? '회원가입 중...' : '회원가입'}
                    </Button>

                    <div
                        className="relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t after:border-border">
        <span className="relative z-10 bg-background px-2 text-muted-foreground">
          Or
        </span>
                    </div>
                    <div className="grid gap-4 sm:grid-cols-2">
                        <Button variant="outline" type="button" className="w-full">
                            <img src="/icons/apple.svg" alt="Apple" className="size-5"/>
                            Apple로 회원가입
                        </Button>
                        <Button variant="outline" type="button" className="w-full">
                            <img src="/icons/google.svg" alt="Google" className="size-5"/>
                            Google로 회원가입
                        </Button>
                    </div>
                    <div
                        className="*:[a]:hover:text-primary *:[a]:underline *:[a]:underline-offset-4 text-balance text-center text-xs text-muted-foreground">
                        By clicking continue, you agree to our <a href="#">Terms of Service</a>{' '}
                        and <a href="/policy/privacy">Privacy Policy</a>.
                    </div>
                    <hr/>
                    <div
                        className="*:[a]:hover:text-primary *:[a]:underline *:[a]:underline-offset-4 text-balance text-center text-xs text-muted-foreground">
                        Go back to Homepage - <Link href="/">Timefit</Link>.
                    </div>
                </form>
            </div>
        </div>
    );
}
