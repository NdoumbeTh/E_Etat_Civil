import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';
import {
  DemandeActeService,
  PageResponse
} from '../../core/services/demande-acte.service';
import { StatsService } from '../../core/services/stats.service';

import { DemandeActe } from '../../core/models/demande-acte.model';
import { Stats } from '../../core/models/stats.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {

  demandes = signal<DemandeActe[]>([]);
  chargement = signal(true);

  totalDemandes = signal(0);
  demandesEnAttente = signal(0);
  demandesValidees = signal(0);
  demandesRejetees = signal(0);

  // Chef de service
  statsChef = signal<Stats | null>(null);

  constructor(
    public auth: AuthService,
    private router: Router,
    private demandeActeService: DemandeActeService,
    private statsService: StatsService
  ) {}

  ngOnInit(): void {
    this.chargerDashboard();
  }

  chargerDashboard(): void {

    const role = this.auth.role();

    if (role === 'CHEF_SERVICE') {
      this.chargerStatsChef();
      return;
    }

    if (role !== 'CITOYEN' && role !== 'OFFICIER') {
      this.chargement.set(false);
      return;
    }

    this.demandeActeService.lister(0, 10).subscribe({
      next: (response: PageResponse<DemandeActe>) => {
        this.demandes.set(response.content);
        this.totalDemandes.set(response.totalElements);

        this.demandesEnAttente.set(
          response.content.filter(
            d => d.statut === 'DEPOSEE' || d.statut === 'EN_TRAITEMENT'
          ).length
        );

        this.demandesValidees.set(
          response.content.filter(d => d.statut === 'VALIDEE').length
        );

        this.demandesRejetees.set(
          response.content.filter(d => d.statut === 'REJETEE').length
        );

        this.chargement.set(false);
      },
      error: (error) => {
        console.error('Erreur lors du chargement du dashboard :', error);
        this.demandes.set([]);
        this.chargement.set(false);
      }
    });
  }

  chargerStatsChef(): void {
    this.chargement.set(true);
    this.statsService.obtenir().subscribe({
      next: (stats) => {
        this.statsChef.set(stats);
        this.chargement.set(false);
      },
      error: (error) => {
        console.error('Erreur lors du chargement des statistiques :', error);
        this.statsChef.set(null);
        this.chargement.set(false);
      },
    });
  }

  get parTypeEntries(): [string, number][] {
    const s = this.statsChef();
    return s ? Object.entries(s.parType) : [];
  }

  formatDelai(heures: number | null | undefined): string {
    if (heures === null || heures === undefined) return 'N/A';
    if (heures < 24) return `${heures.toFixed(1)} h`;
    return `${(heures / 24).toFixed(1)} j`;
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  libelleStatut(statut: string): string {
    switch (statut) {
      case 'DEPOSEE': return 'Déposée';
      case 'EN_TRAITEMENT': return 'En traitement';
      case 'VALIDEE': return 'Validée';
      case 'REJETEE': return 'Rejetée';
      default: return statut;
    }
  }

  classeStatut(statut: string): string {
    return 'statut-' + statut.toLowerCase();
  }
}
