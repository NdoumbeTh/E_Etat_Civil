import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DemandeActe } from '../models/demande-acte.model';

@Injectable({ providedIn: 'root' })
export class DemandeActeService {
  constructor(private http: HttpClient) {}

  soumettre(typeActeId: number, infosDemandeur: string, pieces: File[]): Observable<DemandeActe> {
    const form = new FormData();
    form.append('typeActeId', String(typeActeId));
    form.append('infosDemandeur', infosDemandeur);
    pieces.forEach((f) => form.append('pieces', f));

    return this.http.post<DemandeActe>(`${environment.apiUrl}/demandes-actes`, form);
  }

  lister(): Observable<DemandeActe[]> {
    return this.http.get<DemandeActe[]>(`${environment.apiUrl}/demandes-actes`);
  }
  traiter(id: number, decision: 'VALIDEE' | 'REJETEE', motif?: string): Observable<DemandeActe> {
  return this.http.patch<DemandeActe>(`${environment.apiUrl}/demandes-actes/${id}/traitement`, {
    decision,
    motif,
  });
}

  obtenir(id: number): Observable<DemandeActe> {
    return this.http.get<DemandeActe>(`${environment.apiUrl}/demandes-actes/${id}`);
  }
}
