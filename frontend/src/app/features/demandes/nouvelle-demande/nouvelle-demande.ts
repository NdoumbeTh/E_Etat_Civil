import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { TypeActeService } from '../../../core/services/type-acte.service';
import { DemandeActeService } from '../../../core/services/demande-acte.service';
import { TypeActe } from '../../../core/models/demande-acte.model';

interface ChampFormulaire {
  key: string;
  label: string;
  type: 'text' | 'date' | 'time' | 'select';
  options?: { valeur: string; libelle: string }[];
}

const CHAMPS_PAR_TYPE: Record<string, ChampFormulaire[]> = {
  NAISSANCE: [
    { key: 'nomEnfant', label: "Nom complet de l'enfant", type: 'text' },
    { key: 'dateNaissance', label: 'Date de naissance', type: 'date' },
    { key: 'heureNaissance', label: 'Heure de naissance', type: 'time' },
    {
      key: 'sexe',
      label: "Sexe de l'enfant",
      type: 'select',
      options: [
        { valeur: 'M', libelle: 'Masculin' },
        { valeur: 'F', libelle: 'Féminin' },
      ],
    },
    { key: 'lieuNaissance', label: 'Lieu de naissance', type: 'text' },
    { key: 'nomPere', label: 'Nom du père', type: 'text' },
    { key: 'nomMere', label: 'Nom de la mère', type: 'text' },
  ],
  MARIAGE: [
    { key: 'nomEpoux', label: "Nom complet de l'époux", type: 'text' },
    { key: 'nomEpouse', label: "Nom complet de l'épouse", type: 'text' },
    { key: 'dateMariage', label: 'Date du mariage', type: 'date' },
    { key: 'lieuMariage', label: 'Lieu du mariage', type: 'text' },
    {
      key: 'regimeMatrimonial',
      label: 'Régime matrimonial',
      type: 'select',
      options: [
        { valeur: 'séparation des biens', libelle: 'Séparation des biens' },
        { valeur: 'communauté des biens', libelle: 'Communauté des biens' },
      ],
    },
  ],
  DECES: [
    { key: 'nomDefunt', label: 'Nom complet du défunt', type: 'text' },
    { key: 'dateNaissanceDefunt', label: 'Date de naissance du défunt', type: 'date' },
    { key: 'dateDeces', label: 'Date du décès', type: 'date' },
    { key: 'lieuDeces', label: 'Lieu du décès (commune)', type: 'text' },
    { key: 'nomDeclarant', label: 'Nom du déclarant', type: 'text' },
  ],
};

const CHAMPS_PAR_DEFAUT: ChampFormulaire[] = [
  { key: 'nom', label: 'Nom complet', type: 'text' },
];

@Component({
  selector: 'app-nouvelle-demande',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './nouvelle-demande.html',
})
export class NouvelleDemande implements OnInit {
  typesActes = signal<TypeActe[]>([]);
  typeActeId: number | null = null;
  formData: Record<string, string> = {};
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
      if (types.length) {
        this.typeActeId = types[0].id;
        this.onTypeChange();
      }
    });
  }

  get typeSelectionne(): TypeActe | undefined {
    return this.typesActes().find((t) => t.id === this.typeActeId);
  }

  get champsActuels(): ChampFormulaire[] {
    const libelle = this.typeSelectionne?.libelle ?? '';
    return CHAMPS_PAR_TYPE[libelle] ?? CHAMPS_PAR_DEFAUT;
  }

  onTypeChange(): void {
    const nouveauFormData: Record<string, string> = {};
    for (const champ of this.champsActuels) {
      nouveauFormData[champ.key] = this.formData[champ.key] ?? '';
    }
    this.formData = nouveauFormData;
  }

  onFichiersChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.fichiers = input.files ? Array.from(input.files) : [];
  }

  onSubmit(): void {
    if (!this.typeActeId) return;
    this.erreur.set(null);
    this.enCours.set(true);

    const infosDemandeur = JSON.stringify(this.formData);

    this.demandeActeService.soumettre(this.typeActeId, infosDemandeur, this.fichiers).subscribe({
      next: () => this.router.navigate(['/demandes']),
      error: () => {
        this.erreur.set("Erreur lors de l'envoi de la demande. Vérifie les champs et réessaie.");
        this.enCours.set(false);
      },
    });
  }
}
