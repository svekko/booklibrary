import { CommonModule } from "@angular/common";
import { Component, inject } from "@angular/core";
import { AlertService } from "./alert.service";

@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alert.component.html',
  styleUrl: './alert.component.scss'
})
export class AlertComponent {
  alertService = inject(AlertService);
}
