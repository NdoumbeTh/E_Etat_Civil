export interface Stats {
  total: number;
  deposees: number;
  enTraitement: number;
  validees: number;
  rejetees: number;
  parType: Record<string, number>;
  delaiMoyenTraitementHeures: number | null;
}
