/*
 * CodeCanvas client helpers.
 *  - CodeCanvasApi.send(): fetch() with the Spring Security CSRF token (from <meta name="_csrf">).
 *  - CodeCanvasApi.syncProgress(): loads the signed-in student's progress from the server and writes it
 *    into the localStorage keys the existing pages already read, then fires "codecanvas:progress".
 *    The server is the source of truth; localStorage is only a display cache.
 */
(function () {
    const KEYS = {
        name: 'codecanvas_student_name',
        topics: 'codecanvas_completed_topics',
        quizzes: 'codecanvas_quizzes_completed',
        mock: 'codecanvas_mock_exam',
        icseMock: 'codecanvas_icse_mock_exam'
    };

    function meta(name) {
        const el = document.querySelector('meta[name="' + name + '"]');
        return el ? el.getAttribute('content') : null;
    }

    function handleUnauthorized(response) {
        if (response.status === 401) {
            window.location.href = '/';
            throw new Error('Session expired');
        }
        return response;
    }

    async function send(url, method, body) {
        const headers = { 'Accept': 'application/json' };
        const token = meta('_csrf');
        const header = meta('_csrf_header');
        if (token && header) headers[header] = token;
        const options = { method: method || 'POST', headers: headers, credentials: 'same-origin' };
        if (body !== undefined) {
            headers['Content-Type'] = 'application/json';
            options.body = JSON.stringify(body);
        }
        return handleUnauthorized(await fetch(url, options));
    }

    function store(key, value) {
        try {
            if (value === null || value === undefined) localStorage.removeItem(key);
            else localStorage.setItem(key, typeof value === 'string' ? value : JSON.stringify(value));
        } catch (e) { /* storage unavailable: pages still render from the event detail */ }
    }

    async function syncProgress() {
        try {
            const response = handleUnauthorized(await fetch('/api/progress', {
                headers: { 'Accept': 'application/json' }, credentials: 'same-origin'
            }));
            if (!response.ok) return null;
            const progress = await response.json();
            store(KEYS.name, progress.displayName || null);
            store(KEYS.topics, progress.completedTopics || {});
            store(KEYS.quizzes, String(progress.quizzesCompleted || 0));
            store(KEYS.mock, progress.mockExam);
            store(KEYS.icseMock, progress.icseMockExam);
            document.dispatchEvent(new CustomEvent('codecanvas:progress', { detail: progress }));
            return progress;
        } catch (e) {
            console.error('Could not load progress from server:', e);
            return null;
        }
    }

    window.CodeCanvasApi = { send: send, syncProgress: syncProgress, KEYS: KEYS };
})();
