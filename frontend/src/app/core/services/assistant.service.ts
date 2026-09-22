import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AssistantService {
  constructor(private http: HttpClient) {}

  poserQuestion(message: string): Observable<{ reponse: string }> {
    return this.http.post<{ reponse: string }>(`${environment.apiUrl}/assistant`, { message });
  }
}
