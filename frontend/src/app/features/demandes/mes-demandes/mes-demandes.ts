import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import {
  DemandeActeService,
  PageResponse
} from '../../../core/services/demande-acte.service';

import { DemandeActe } from '../../../core/models/demande-acte.model';

@Component({
  selector: 'app-mes-demandes',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mes-demandes.html',
})
export class MesDemandes implements OnInit {

  demandes = signal<DemandeActe[]>([]);

  chargement = signal(true);

  // ==============================
  // PAGINATION
  // ==============================

  pageActuelle = signal(0);

  taillePage = signal(10);

  totalPages = signal(0);

  totalElements = signal(0);


  constructor(
    private demandeActeService: DemandeActeService
  ) {}


  ngOnInit(): void {
    this.chargerDemandes();
  }


  // ==============================
  // CHARGER LES DEMANDES
  // ==============================

  chargerDemandes(): void {

    this.chargement.set(true);

    this.demandeActeService
      .lister(
        this.pageActuelle(),
        this.taillePage()
      )
      .subscribe({

        next: (response: PageResponse<DemandeActe>) => {

          this.demandes.set(response.content);

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

          this.chargement.set(false);
        }

      });
  }


  // ==============================
  // CHANGER DE PAGE
  // ==============================

  allerPage(page: number): void {

    if (
      page < 0 ||
      page >= this.totalPages()
    ) {
      return;
    }

    this.pageActuelle.set(page);

    this.chargerDemandes();
  }


  // ==============================
  // PAGE PRECEDENTE
  // ==============================

  pagePrecedente(): void {

    this.allerPage(
      this.pageActuelle() - 1
    );
  }


  // ==============================
  // PAGE SUIVANTE
  // ==============================

  pageSuivante(): void {

    this.allerPage(
      this.pageActuelle() + 1
    );
  }


  // ==============================
  // NUMEROS DES PAGES
  // ==============================

  pages(): number[] {

    return Array.from(
      { length: this.totalPages() },
      (_, index) => index
    );
  }


  // ==============================
  // LIBELLE STATUT
  // ==============================

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