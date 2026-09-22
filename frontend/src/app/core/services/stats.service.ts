import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Stats } from '../models/stats.model';

@Injectable({ providedIn: 'root' })
export class StatsService {
  constructor(private http: HttpClient) {}

  obtenir(): Observable<Stats> {
    return this.http.get<Stats>(`${environment.apiUrl}/stats`);
  }
}
