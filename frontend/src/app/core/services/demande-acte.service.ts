import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DemandeActe } from '../models/demande-acte.model';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

@Injectable({ providedIn: 'root' })
export class DemandeActeService {

  constructor(private http: HttpClient) {}

  soumettre(
    typeActeId: number,
    infosDemandeur: string,
    pieces: File[]
  ): Observable<DemandeActe> {

    const form = new FormData();

    form.append('typeActeId', String(typeActeId));
    form.append('infosDemandeur', infosDemandeur);

    pieces.forEach((f) => {
      form.append('pieces', f);
    });

    return this.http.post<DemandeActe>(
      `${environment.apiUrl}/demandes-actes`,
      form
    );
  }


  // ==============================
  // LISTE PAGINEE
  // ==============================

  lister(
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<DemandeActe>> {

    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<PageResponse<DemandeActe>>(
      `${environment.apiUrl}/demandes-actes`,
      { params }
    );
  }


  // ==============================
  // TRAITER
  // ==============================

  traiter(
    id: number,
    decision: 'VALIDEE' | 'REJETEE',
    motif?: string
  ): Observable<DemandeActe> {

    return this.http.patch<DemandeActe>(
      `${environment.apiUrl}/demandes-actes/${id}/traitement`,
      {
        decision,
        motif,
      }
    );
  }


  // ==============================
  // DETAIL
  // ==============================

  obtenir(id: number): Observable<DemandeActe> {

    return this.http.get<DemandeActe>(
      `${environment.apiUrl}/demandes-actes/${id}`
    );
  }
}