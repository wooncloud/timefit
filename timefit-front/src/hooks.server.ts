import { redirect, type Handle } from '@sveltejs/kit';

const PC_VERSION = 'v1';
const MOBILE_VERSION = 'v1';

/** @type {import('@sveltejs/kit').Handle} */
export const handle: Handle = async ({ event, resolve }) => {
	const { url, request } = event;
	const userAgent = request.headers.get('user-agent')?.toLowerCase() || '';
	const isMobile = /mobile|iphone|ipad|android|windows phone/g.test(userAgent);

	// Chrome DevTools나 기타 .well-known 요청 처리
	if (url.pathname.startsWith('/.well-known/')) {
		return new Response('{}', {
			status: 200,
			headers: {
				'Content-Type': 'application/json'
			}
		});
	}

	if (
		url.pathname.startsWith('/pc') ||
		url.pathname.startsWith('/m') ||
		url.pathname.startsWith('/api') ||
		url.pathname.includes('.') ||
		url.pathname.startsWith('/_app')
	) {
		return resolve(event);
	}

	const basePath = isMobile ? `/m/${MOBILE_VERSION}` : `/pc/${PC_VERSION}`;
	const redirectPath = `${basePath}${url.pathname === '/' ? '' : url.pathname}${url.search}`;
	throw redirect(307, redirectPath);
}
