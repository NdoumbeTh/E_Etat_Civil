import { CommonModule } from '@angular/common';
import { Component, ElementRef, signal, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AssistantService } from '../../core/services/assistant.service';
import { MessageChat } from '../../core/models/assistant.model';

@Component({
  selector: 'app-assistant',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './assistant.html',
})
export class Assistant {
  messages = signal<MessageChat[]>([
    {
      role: 'assistant',
      texte:
        "Bonjour ! Je peux t'aider sur les pièces à fournir, le déroulement du traitement et les démarches d'état civil. Que veux-tu savoir ?",
    },
  ]);
  saisie = '';
  enCours = signal(false);

  @ViewChild('zoneMessages') zoneMessages?: ElementRef<HTMLDivElement>;

  constructor(private assistantService: AssistantService) {}

  envoyer(): void {
    const texte = this.saisie.trim();
    if (!texte || this.enCours()) return;

    this.messages.update((m) => [...m, { role: 'utilisateur', texte }]);
    this.saisie = '';
    this.enCours.set(true);
    this.defilerVersLeBas();

    this.assistantService.poserQuestion(texte).subscribe({
      next: (res) => {
        this.messages.update((m) => [...m, { role: 'assistant', texte: res.reponse }]);
        this.enCours.set(false);
        this.defilerVersLeBas();
      },
      error: () => {
        this.messages.update((m) => [
          ...m,
          { role: 'assistant', texte: "Désolé, je n'ai pas pu répondre. Réessaie dans un instant." },
        ]);
        this.enCours.set(false);
        this.defilerVersLeBas();
      },
    });
  }

  private defilerVersLeBas(): void {
    setTimeout(() => {
      const el = this.zoneMessages?.nativeElement;
      if (el) el.scrollTop = el.scrollHeight;
    }, 0);
  }
}
