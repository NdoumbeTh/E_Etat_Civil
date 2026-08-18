import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DemandeActeService } from '../../../core/services/demande-acte.service';
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

  constructor(private demandeActeService: DemandeActeService) {}

  ngOnInit(): void {
    this.demandeActeService.lister().subscribe({
      next: (demandes) => {
        this.demandes.set(demandes);
        this.chargement.set(false);
      },
      error: () => this.chargement.set(false),
    });
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
}
