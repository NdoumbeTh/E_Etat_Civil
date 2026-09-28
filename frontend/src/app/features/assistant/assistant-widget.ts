import { CommonModule } from '@angular/common';
import { Component, ElementRef, ViewChild, effect, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { AssistantService } from '../../core/services/assistant.service';
import { MessageChat } from '../../core/models/assistant.model';

const MESSAGE_ACCUEIL: MessageChat = {
  role: 'assistant',
  texte:
    "Bonjour ! Je peux vous aider sur les pièces à fournir, le déroulement du traitement et les démarches d'état civil. Que souhaitez-vous savoir ?",
};

@Component({
  selector: 'app-assistant-widget',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    @if (auth.role() === 'CITOYEN') {

      @if (ouvert()) {
        <section class="chat-fenetre" role="dialog" aria-label="Assistant virtuel">
          <header class="chat-entete">
            <div class="chat-titre">
              <span class="chat-pastille"></span>
              <div>
                <strong>Assistant e-ÉtatCivil</strong>
                <small>Démarches d'état civil</small>
              </div>
            </div>
            <button type="button" class="chat-fermer" (click)="basculer()" aria-label="Fermer l'assistant">✕</button>
          </header>

          <div class="chat-messages" #zoneMessages>
            @for (message of messages(); track $index) {
              <div class="chat-bulle" [class.bulle-utilisateur]="message.role === 'utilisateur'"
                   [class.bulle-assistant]="message.role === 'assistant'">
                {{ message.texte }}
              </div>
            }
            @if (enCours()) {
              <div class="chat-bulle bulle-assistant bulle-attente">L'assistant réfléchit…</div>
            }
          </div>

          <form class="chat-saisie" (ngSubmit)="envoyer()">
            <input
              type="text"
              name="message"
              [(ngModel)]="saisie"
              placeholder="Posez votre question…"
              autocomplete="off"
              [disabled]="enCours()"
            />
            <button type="submit" [disabled]="enCours() || !saisie.trim()" aria-label="Envoyer">➤</button>
          </form>
        </section>
      }

      <button type="button" class="chat-bouton" (click)="basculer()" [attr.aria-label]="ouvert() ? 'Fermer la fenêtre de l’assistant' : 'Ouvrir l’assistant'">
        @if (ouvert()) {
          <span class="chat-bouton-croix">✕</span>
        } @else {
          <svg viewBox="0 0 24 24" width="28" height="28" fill="currentColor" aria-hidden="true">
            <path d="M4 3h16a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H9l-5 4v-4H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2z"/>
          </svg>
        }
      </button>

    }
  `,
  styles: [`
    :host { --chat-couleur: var(--primary, #0f5132); --chat-couleur-claire: #e7f3ec; }

    .chat-bouton {
      position: fixed; right: 24px; bottom: 24px; z-index: 1000;
      width: 60px; height: 60px; border: none; border-radius: 50%;
      background: var(--chat-couleur); color: #fff; cursor: pointer;
      display: flex; align-items: center; justify-content: center;
      box-shadow: 0 6px 18px rgba(0, 0, 0, 0.28);
      transition: transform 0.15s ease, box-shadow 0.15s ease;
    }
    .chat-bouton:hover { transform: scale(1.06); box-shadow: 0 8px 22px rgba(0, 0, 0, 0.32); }
    .chat-bouton-croix { font-size: 22px; line-height: 1; }

    .chat-fenetre {
      position: fixed; right: 24px; bottom: 96px; z-index: 1000;
      width: 360px; max-width: calc(100vw - 32px);
      height: 480px; max-height: calc(100vh - 130px);
      display: flex; flex-direction: column; overflow: hidden;
      background: #fff; border-radius: 14px;
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.28);
      animation: chat-apparition 0.18s ease-out;
    }
    @keyframes chat-apparition {
      from { opacity: 0; transform: translateY(12px) scale(0.98); }
      to { opacity: 1; transform: translateY(0) scale(1); }
    }

    .chat-entete {
      display: flex; align-items: center; justify-content: space-between;
      padding: 12px 14px; background: var(--chat-couleur); color: #fff;
    }
    .chat-titre { display: flex; align-items: center; gap: 10px; }
    .chat-titre strong { display: block; font-size: 14px; }
    .chat-titre small { display: block; font-size: 11px; opacity: 0.85; }
    .chat-pastille { width: 9px; height: 9px; border-radius: 50%; background: #5ee08f; }
    .chat-fermer { background: none; border: none; color: #fff; font-size: 16px; cursor: pointer; padding: 4px 6px; }

    .chat-messages {
      flex: 1; overflow-y: auto; padding: 14px;
      display: flex; flex-direction: column; gap: 8px; background: #f6f7f8;
    }
    .chat-bulle {
      max-width: 82%; padding: 9px 12px; border-radius: 12px;
      font-size: 13.5px; line-height: 1.4; white-space: pre-wrap; word-break: break-word;
    }
    .bulle-assistant { align-self: flex-start; background: #fff; color: #222; border: 1px solid #e3e5e8; border-bottom-left-radius: 3px; }
    .bulle-utilisateur { align-self: flex-end; background: var(--chat-couleur); color: #fff; border-bottom-right-radius: 3px; }
    .bulle-attente { font-style: italic; color: #666; }

    .chat-saisie { display: flex; gap: 8px; padding: 10px; border-top: 1px solid #e3e5e8; background: #fff; }
    .chat-saisie input {
      flex: 1; padding: 9px 12px; border: 1px solid #cfd3d8; border-radius: 20px;
      font-size: 13.5px; outline: none;
    }
    .chat-saisie input:focus { border-color: var(--chat-couleur); }
    .chat-saisie button {
      width: 38px; height: 38px; border: none; border-radius: 50%;
      background: var(--chat-couleur); color: #fff; cursor: pointer; font-size: 14px;
    }
    .chat-saisie button:disabled { opacity: 0.5; cursor: not-allowed; }
  `],
})
export class AssistantWidget {
  ouvert = signal(false);
  messages = signal<MessageChat[]>([MESSAGE_ACCUEIL]);
  enCours = signal(false);
  saisie = '';

  @ViewChild('zoneMessages') zoneMessages?: ElementRef<HTMLDivElement>;

  constructor(
    public auth: AuthService,
    private assistantService: AssistantService
  ) {
    // À la déconnexion, on ferme la fenêtre et on efface la conversation
    // pour qu'un autre citoyen ne voie pas l'historique du précédent.
    effect(() => {
      if (this.auth.role() !== 'CITOYEN') {
        this.ouvert.set(false);
        this.messages.set([MESSAGE_ACCUEIL]);
        this.saisie = '';
        this.enCours.set(false);
      }
    });
  }

  basculer(): void {
    this.ouvert.update((v) => !v);
    if (this.ouvert()) this.defilerVersLeBas();
  }

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
          { role: 'assistant', texte: "Désolé, je n'ai pas pu répondre. Réessayez dans un instant." },
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
