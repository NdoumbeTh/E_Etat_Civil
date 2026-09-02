import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DemandeActeService } from '../../../core/services/demande-acte.service';
import { DemandeActe } from '../../../core/models/demande-acte.model';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-traiter-demande',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './traiter-demande.html',
})
export class TraiterDemande implements OnInit {
  demande = signal<DemandeActe | null>(null);
  chargement = signal(true);
  enCours = signal(false);
  erreur = signal<string | null>(null);
  motif = '';
  apiOrigin = environment.apiUrl.replace(/\/api$/, '');

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private demandeActeService: DemandeActeService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.demandeActeService.obtenir(id).subscribe({
      next: (d) => {
        this.demande.set(d);
        this.chargement.set(false);
      },
      error: () => this.chargement.set(false),
    });
  }

  /** Champs saisis par le citoyen, quel que soit le type d'acte (naissance/mariage/décès). */
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

  valider(): void {
    this.decider('VALIDEE');
  }

  rejeter(): void {
    if (!this.motif.trim()) {
      this.erreur.set('Le motif de rejet est obligatoire (RG-05).');
      return;
    }
    this.decider('REJETEE', this.motif);
  }

  private decider(decision: 'VALIDEE' | 'REJETEE', motif?: string): void {
    const d = this.demande();
    if (!d) return;
    this.erreur.set(null);
    this.enCours.set(true);

    this.demandeActeService.traiter(d.id, decision, motif).subscribe({
      next: () => this.router.navigate(['/officier/demandes']),
      error: (err) => {
        this.erreur.set(err.error ?? 'Erreur lors du traitement de la demande.');
        this.enCours.set(false);
      },
    });
  }
}
