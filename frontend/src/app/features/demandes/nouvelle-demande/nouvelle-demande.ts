import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { TypeActeService } from '../../../core/services/type-acte.service';
import { DemandeActeService } from '../../../core/services/demande-acte.service';
import { TypeActe } from '../../../core/models/demande-acte.model';

@Component({
  selector: 'app-nouvelle-demande',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './nouvelle-demande.html',
})
export class NouvelleDemande implements OnInit {
  typesActes = signal<TypeActe[]>([]);
  typeActeId: number | null = null;
  nom = '';
  dateNaissance = '';
  lieuNaissance = '';
  fichiers: File[] = [];
  enCours = signal(false);
  erreur = signal<string | null>(null);

  constructor(
    private typeActeService: TypeActeService,
    private demandeActeService: DemandeActeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.typeActeService.lister().subscribe((types) => {
      this.typesActes.set(types);
      if (types.length) this.typeActeId = types[0].id;
    });
  }

  get typeSelectionne(): TypeActe | undefined {
    return this.typesActes().find((t) => t.id === this.typeActeId);
  }

  onFichiersChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.fichiers = input.files ? Array.from(input.files) : [];
  }

  onSubmit(): void {
    if (!this.typeActeId) return;
    this.erreur.set(null);
    this.enCours.set(true);

    const infosDemandeur = JSON.stringify({
      nom: this.nom,
      dateNaissance: this.dateNaissance,
      lieuNaissance: this.lieuNaissance,
    });

    this.demandeActeService.soumettre(this.typeActeId, infosDemandeur, this.fichiers).subscribe({
      next: () => this.router.navigate(['/demandes']),
      error: () => {
        this.erreur.set("Erreur lors de l'envoi de la demande. Vérifie les champs et réessaie.");
        this.enCours.set(false);
      },
    });
  }
}
