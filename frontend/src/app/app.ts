import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AssistantWidget } from './features/assistant/assistant-widget';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, AssistantWidget],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('frontend');
}
