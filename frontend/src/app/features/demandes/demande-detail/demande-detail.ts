import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DemandeActeService } from '../../../core/services/demande-acte.service';
import { DemandeActe } from '../../../core/models/demande-acte.model';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-demande-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './demande-detail.html',
  styleUrls: ['./demande-detail.css'],
})
export class DemandeDetail implements OnInit {
  demande = signal<DemandeActe | null>(null);
  chargement = signal(true);
  erreur = signal<string | null>(null);
  apiOrigin = environment.apiUrl.replace(/\/api$/, '');

  constructor(private route: ActivatedRoute, private demandeActeService: DemandeActeService) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.demandeActeService.obtenir(id).subscribe({
      next: (d) => {
        this.demande.set(d);
        this.chargement.set(false);
      },
      error: (err) => {
        this.erreur.set(err.status === 403 ? "Tu n'as pas accès à cette demande." : 'Demande introuvable.');
        this.chargement.set(false);
      },
    });
  }

  get infosEntries(): [string, string][] {
    const d = this.demande();
    if (!d) return [];
    try {
      const infos = JSON.parse(d.infosDemandeur) as Record<string, string>;
      return Object.entries(infos).map(([cle, valeur]) => [this.humaniser(cle), valeur]);
    } catch {
      return [];
    }
  }

  private humaniser(cle: string): string {
    const avecEspaces = cle.replace(/([A-Z])/g, ' $1').toLowerCase();
    return avecEspaces.charAt(0).toUpperCase() + avecEspaces.slice(1);
  }

  libelleStatut(statut: string): string {
    switch (statut) {
      case 'DEPOSEE': return 'Déposée — en attente de traitement';
      case 'EN_TRAITEMENT': return 'En cours de traitement';
      case 'VALIDEE': return 'Validée';
      case 'REJETEE': return 'Rejetée';
      default: return statut;
    }
  }
}
