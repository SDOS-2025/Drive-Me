import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule,NgFor],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  @Input() menuItems: { icon: string, label: string, active?: boolean }[] = [];
  @Input() isDarkTheme: boolean = false;
}
