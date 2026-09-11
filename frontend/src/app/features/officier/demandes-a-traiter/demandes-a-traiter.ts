import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import {
  DemandeActeService,
  PageResponse
} from '../../../core/services/demande-acte.service';

import { DemandeActe } from '../../../core/models/demande-acte.model';

@Component({
  selector: 'app-demandes-a-traiter',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './demandes-a-traiter.html',
})
export class DemandesATraiter implements OnInit {

  demandes = signal<DemandeActe[]>([]);
  chargement = signal(true);

  filtre = signal<'TOUTES' | 'A_TRAITER'>('A_TRAITER');

  // Pagination
  pageActuelle = signal(0);
  taillePage = signal(10);
  totalPages = signal(0);
  totalElements = signal(0);

  constructor(
    private demandeActeService: DemandeActeService
  ) {}

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);

    this.demandeActeService
      .lister(this.pageActuelle(), this.taillePage())
      .subscribe({
        next: (response: PageResponse<DemandeActe>) => {

          // Les demandes se trouvent maintenant dans response.content
          this.demandes.set(response.content);

          // Informations de pagination
          this.totalPages.set(response.totalPages);
          this.totalElements.set(response.totalElements);
          this.pageActuelle.set(response.number);

          this.chargement.set(false);
        },

        error: (error) => {
          console.error(
            'Erreur lors du chargement des demandes :',
            error
          );

          this.demandes.set([]);
          this.totalPages.set(0);
          this.totalElements.set(0);

          this.chargement.set(false);
        },
      });
  }

  get demandesFiltrees(): DemandeActe[] {
    if (this.filtre() === 'TOUTES') {
      return this.demandes();
    }

    return this.demandes().filter(
      (d) =>
        d.statut === 'DEPOSEE' ||
        d.statut === 'EN_TRAITEMENT'
    );
  }

  basculerFiltre(): void {
    this.filtre.set(
      this.filtre() === 'A_TRAITER'
        ? 'TOUTES'
        : 'A_TRAITER'
    );
  }

  // ============================
  // PAGINATION
  // ============================

  allerPage(page: number): void {
    if (page < 0 || page >= this.totalPages()) {
      return;
    }

    this.pageActuelle.set(page);
    this.charger();
  }

  pagePrecedente(): void {
    this.allerPage(this.pageActuelle() - 1);
  }

  pageSuivante(): void {
    this.allerPage(this.pageActuelle() + 1);
  }

  pages(): number[] {
    return Array.from(
      { length: this.totalPages() },
      (_, index) => index
    );
  }

  libelleStatut(statut: string): string {
    switch (statut) {
      case 'DEPOSEE':
        return 'Déposée';

      case 'EN_TRAITEMENT':
        return 'En traitement';

      case 'VALIDEE':
        return 'Validée';

      case 'REJETEE':
        return 'Rejetée';

      default:
        return statut;
    }
  }
}

