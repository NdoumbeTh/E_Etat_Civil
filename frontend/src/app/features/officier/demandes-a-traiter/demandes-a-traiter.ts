import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DemandeActeService } from '../../../core/services/demande-acte.service';
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

  constructor(private demandeActeService: DemandeActeService) {}

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.demandeActeService.lister().subscribe({
      next: (demandes) => {
        demandes.sort((a, b) => new Date(a.dateDepot).getTime() - new Date(b.dateDepot).getTime());
        this.demandes.set(demandes);
        this.chargement.set(false);
      },
      error: () => this.chargement.set(false),
    });
  }

  get demandesFiltrees(): DemandeActe[] {
    if (this.filtre() === 'TOUTES') return this.demandes();
    return this.demandes().filter((d) => d.statut === 'DEPOSEE' || d.statut === 'EN_TRAITEMENT');
  }

  basculerFiltre(): void {
    this.filtre.set(this.filtre() === 'A_TRAITER' ? 'TOUTES' : 'A_TRAITER');
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
