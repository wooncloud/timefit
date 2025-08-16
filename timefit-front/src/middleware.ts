import { NextRequest, NextResponse } from 'next/server';
import { updateSession } from '@/lib/supabase/middleware';

export async function middleware(request: NextRequest) {
  const userAgent = request.headers.get('user-agent') || '';
  const isMobile = /Mobile|Android|iPhone|iPad|Windows Phone/i.test(userAgent);
  
  const url = request.nextUrl.clone();
  const pathname = url.pathname;
  
  // Skip middleware for static files and API routes
  if (
    pathname.startsWith('/_next') ||
    pathname.startsWith('/api') ||
    pathname.includes('.')
  ) {
    return NextResponse.next();
  }
  
  // If on root path, redirect based on device
  if (pathname === '/') {
    if (isMobile) {
      url.pathname = '/mobile';
    } else {
      url.pathname = '/pc';
    }
    return NextResponse.redirect(url);
  }
  
  // If user is on wrong device-specific route, redirect
  if (isMobile && pathname.startsWith('/pc')) {
    url.pathname = pathname.replace('/pc', '/mobile');
    return NextResponse.redirect(url);
  }
  
  if (!isMobile && pathname.startsWith('/mobile')) {
    url.pathname = pathname.replace('/mobile', '/pc');
    return NextResponse.redirect(url);
  }
  
  // Update Supabase session
  return await updateSession(request);
}

export const config = {
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - api (API routes)
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico (favicon file)
     */
    '/((?!api|_next/static|_next/image|favicon.ico).*)',
  ],
};