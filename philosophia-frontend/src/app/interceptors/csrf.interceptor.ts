import { HttpInterceptorFn } from '@angular/common/http';

function getCookie(name: string): string | null {
  const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
  return match ? decodeURIComponent(match[2]) : null;
}

export const csrfInterceptor: HttpInterceptorFn = (req, next) => {
  const xsrfToken = getCookie('XSRF-TOKEN');

  let clonedReq = req.clone({ withCredentials: true });

  if (xsrfToken) {
    clonedReq = clonedReq.clone({
      setHeaders: { 'X-XSRF-TOKEN': xsrfToken },
    });
  }

  return next(clonedReq);
};
