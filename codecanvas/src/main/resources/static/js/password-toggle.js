/*
 * TraceTables: adds a "show / hide password" eye button to every password field on the page.
 */
(function () {
    'use strict';
    var EYE = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" ' +
        'stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z"/>' +
        '<circle cx="12" cy="12" r="3"/></svg>';
    var EYE_OFF = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" ' +
        'stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 3l18 18"/>' +
        '<path d="M10.6 5.1A10.8 10.8 0 0 1 12 5c6.5 0 10 7 10 7a17 17 0 0 1-3.2 4.2M6.6 6.6C3.9 8.4 2 12 2 12s3.5 7 10 7a10 10 0 0 0 5.4-1.6"/>' +
        '<path d="M9.9 9.9a3 3 0 0 0 4.2 4.2"/></svg>';

    function enhance(input) {
        var wrap = document.createElement('div');
        wrap.className = 'pw-wrap';
        wrap.style.marginBottom = getComputedStyle(input).marginBottom;
        input.parentNode.insertBefore(wrap, input);
        wrap.appendChild(input);
        input.style.marginBottom = '0';
        input.style.paddingRight = '46px';

        var btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'pw-toggle';
        btn.setAttribute('aria-label', 'Show password');
        btn.setAttribute('aria-pressed', 'false');
        btn.innerHTML = EYE;
        btn.addEventListener('click', function () {
            var show = input.type === 'password';
            input.type = show ? 'text' : 'password';
            btn.innerHTML = show ? EYE_OFF : EYE;
            btn.setAttribute('aria-label', show ? 'Hide password' : 'Show password');
            btn.setAttribute('aria-pressed', String(show));
            input.focus();
        });
        wrap.appendChild(btn);
    }

    var css = '.pw-wrap{position:relative}' +
        '.pw-wrap .pw-toggle{position:absolute;top:50%;right:8px;transform:translateY(-50%);width:34px;height:34px;' +
        'margin:0;padding:0;display:flex;align-items:center;justify-content:center;border:none;border-radius:8px;' +
        'background:transparent;color:#94a3b8;box-shadow:none;cursor:pointer;filter:none}' +
        '.pw-wrap .pw-toggle:hover{color:#c7d2fe;background:rgba(255,255,255,0.06);transform:translateY(-50%);box-shadow:none}' +
        '.pw-wrap .pw-toggle:focus-visible{outline:2px solid #818cf8;outline-offset:1px}';
    var style = document.createElement('style');
    style.textContent = css;
    document.head.appendChild(style);

    document.querySelectorAll('input[type="password"]').forEach(enhance);
})();
