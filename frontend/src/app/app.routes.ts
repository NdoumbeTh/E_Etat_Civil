import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login').then((m) => m.Login),
  },
  {
  path: 'register',
  loadComponent: () => import('./features/register/register').then((m) => m.Register),
},
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard').then((m) => m.Dashboard),
    canActivate: [authGuard],
  },
  {
  path: 'demandes',
  loadComponent: () => import('./features/demandes/mes-demandes/mes-demandes').then((m) => m.MesDemandes),
  canActivate: [authGuard],
},
{
  path: 'demandes/nouvelle',
  loadComponent: () => import('./features/demandes/nouvelle-demande/nouvelle-demande').then((m) => m.NouvelleDemande),
  canActivate: [authGuard],
},
{
  path: 'officier/demandes',
  loadComponent: () => import('./features/officier/demandes-a-traiter/demandes-a-traiter').then((m) => m.DemandesATraiter),
  canActivate: [authGuard],
},
{
  path: 'officier/demandes/:id',
  loadComponent: () => import('./features/officier/traiter-demande/traiter-demande').then((m) => m.TraiterDemande),
  canActivate: [authGuard],
},
{
  path: 'demandes/:id',
  loadComponent: () => import('./features/demandes/demande-detail/demande-detail').then((m) => m.DemandeDetail),
  canActivate: [authGuard],
},
  { path: '**', redirectTo: 'login' },
];
