import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TypeActe } from '../models/demande-acte.model';

@Injectable({ providedIn: 'root' })
export class TypeActeService {
  constructor(private http: HttpClient) {}

  lister(): Observable<TypeActe[]> {
    return this.http.get<TypeActe[]>(`${environment.apiUrl}/types-actes`);
  }
}
