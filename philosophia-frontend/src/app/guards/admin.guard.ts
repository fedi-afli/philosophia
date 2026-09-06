// guards/admin.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService} from "../services/authentification.service";

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.hasRole('ADMIN')) {
    return true;
  }

  router.navigate(['/']);
  return false;
};
