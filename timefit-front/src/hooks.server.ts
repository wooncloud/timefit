import { redirect, type Handle } from '@sveltejs/kit';

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
        url.pathname.startsWith('/m') ||
        url.pathname.startsWith('/api') ||
        url.pathname.includes('.') ||
        url.pathname.startsWith('/_app')
    ) {
        return resolve(event);
    }

    // 루트 경로 처리
    if (url.pathname === '/') {
        if (isMobile) {
            throw redirect(307, `/m${url.search}`);
        }
        // PC의 경우 그대로 진행 (/(pc) 그룹이 처리)
        return resolve(event);
    }

    // 다른 경로들의 경우 모바일/PC에 따라 리다이렉트
    if (isMobile && !url.pathname.startsWith('/m')) {
        throw redirect(307, `/m${url.pathname}${url.search}`);
    }

    return resolve(event);
};
