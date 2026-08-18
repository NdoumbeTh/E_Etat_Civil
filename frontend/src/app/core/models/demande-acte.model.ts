export interface TypeActe {
  id: number;
  libelle: string;
  piecesRequises: string;
}

export interface PieceJustificative {
  id: number;
  nomFichier: string;
  url: string;
}

export interface DemandeActe {
  id: number;
  typeActeLibelle: string;
  infosDemandeur: string;
  statut: 'DEPOSEE' | 'EN_TRAITEMENT' | 'VALIDEE' | 'REJETEE';
  motifRejet: string | null;
  dateDepot: string;
  pieces: PieceJustificative[];
}
