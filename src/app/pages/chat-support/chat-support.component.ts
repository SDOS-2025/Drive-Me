import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { AuthService } from '../../services/auth.service';
import { HttpClientModule } from '@angular/common/http';
import { ChatService, ChatMessage } from '../../services/chat.service';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import * as marked from 'marked';

interface CommonIssue {
  id: number;
  title: string;
  description: string;
}

@Component({
  selector: 'app-chat-support',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, DashboardNavbarComponent, HttpClientModule],
  providers: [AuthService, ChatService],
  templateUrl: './chat-support.component.html',
  styleUrl: './chat-support.component.css'
})
export class ChatSupportComponent implements OnInit {
  @ViewChild('chatContainer') private chatContainer!: ElementRef;

  userType: string = 'user';
  userName: string = '';
  userId: number = 0;

  messageText: string = '';
  messages: (ChatMessage & { renderedText?: SafeHtml })[] = []; // Extend ChatMessage to include renderedText
  isTyping: boolean = false;

  commonIssues: CommonIssue[] = [
    { id: 1, title: 'Payment Issues', description: 'Problems with payments, refunds, or charges.' },
    { id: 2, title: 'Trip Cancellation', description: 'Questions about cancellation policies or fees.' },
    { id: 3, title: 'Driver Feedback', description: 'Submit feedback about your driver experience.' },
    { id: 4, title: 'Account Issues', description: 'Problems with your account or login.' },
    { id: 5, title: 'Lost Items', description: 'Report items lost during a trip.' }
  ];

  sidebarMenuItems: any[] = [];

  constructor(
    private authService: AuthService,
    private chatService: ChatService,
    private sanitizer: DomSanitizer // Inject DomSanitizer
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.setupSidebarMenu();
    this.loadChatHistory();
  }

  loadUserData(): void {
    const userData = localStorage.getItem('currentUser');
    if (userData) {
      const user = JSON.parse(userData);
      this.userName = user.fullName || 'User';
      this.userId = user.id || 0;
      this.userType = user.role?.toLowerCase() || 'user';
    }
  }

  setupSidebarMenu(): void {
    if (this.userType === 'driver') {
      this.sidebarMenuItems = [
        { label: 'Dashboard', route: '/driver-dashboard' },
        { label: 'Available Trips', route: '/available-trips' },
        { label: 'All Trips', route: '/all-trips' },
        { label: 'Support', active: true, route: '/chat-support' },
        { label: 'Settings', route: '/settings' },
      ];
    } else {
      this.sidebarMenuItems = [
        { label: 'Dashboard', route: '/user-dashboard' },
        { label: 'My Bookings', route: '/my-bookings' },
        { label: 'Find Driver', route: '/find-driver' },
        { label: 'My Vehicles', route: '/my-vehicles' },
        { label: 'Support', active: true, route: '/chat-support' },
        { label: 'Settings', route: '/settings' },
      ];
    }
  }

  loadChatHistory(): void {
    if (this.userId) {
      this.chatService.getChatHistory(this.userId).subscribe({
        next: (history) => {
          if (history && history.length > 0) {
            this.messages = history.map(message => ({
              ...message,
              renderedText: this.parseMarkdown(message.text)
            }));
          } else {
            this.addWelcomeMessage();
          }
        },
        error: (err) => {
          console.error('Error loading chat history:', err);
          this.addWelcomeMessage();
        }
      });
    } else {
      this.addWelcomeMessage();
    }
  }

  addWelcomeMessage(): void {
    const welcomeMessage: ChatMessage & { renderedText?: SafeHtml } = {
      id: this.chatService.generateMessageId(),
      text: `Welcome to Drive-Me support, ${this.userName}! How can we help you today?`,
      sender: 'support',
      timestamp: new Date(),
      status: 'read',
      renderedText: this.parseMarkdown(`Welcome to Drive-Me support, ${this.userName}! How can we help you today?`)
    };
    this.messages = [welcomeMessage];
  }

  sendMessage(): void {
    if (!this.messageText.trim()) return;

    const userMessage: ChatMessage & { renderedText?: SafeHtml } = {
      id: this.chatService.generateMessageId(),
      text: this.messageText,
      sender: 'user',
      timestamp: new Date(),
      status: 'sent',
      renderedText: this.parseMarkdown(this.messageText)
    };

    this.messages.push(userMessage);
    const sentMessage = this.messageText;
    this.messageText = '';

    setTimeout(() => this.scrollToBottom(), 50);
    this.isTyping = true;

    this.chatService.sendMessage(sentMessage).subscribe({
      next: (response) => {
        this.addSupportResponse(response.response);
        this.isTyping = false;
      },
      error: (error) => {
        console.error('Error getting chat response:', error);
        this.isTyping = false;
        this.addSupportResponse(
          "I'm having trouble connecting to our support system. Please try again later or contact us at support@driveme.com."
        );
      }
    });
  }

  addSupportResponse(text: string): void {
    const supportMessage: ChatMessage & { renderedText?: SafeHtml } = {
      id: this.chatService.generateMessageId(),
      text: text,
      sender: 'support',
      timestamp: new Date(),
      status: 'delivered',
      renderedText: this.parseMarkdown(text)
    };
    this.messages.push(supportMessage);
    setTimeout(() => this.scrollToBottom(), 50);
  }

  selectCommonIssue(issue: CommonIssue): void {
    this.messageText = `I need help with: ${issue.title} - ${issue.description}`;
    this.sendMessage();
  }

  scrollToBottom(): void {
    try {
      this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
    } catch (err) {
      console.error(err);
    }
  }

  formatTime(date: Date): string {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  // Parse Markdown and sanitize the output
  private parseMarkdown(text: string): SafeHtml {
    const rawHtml = marked.parse(text) as string;
    return this.sanitizer.bypassSecurityTrustHtml(rawHtml);
  }
}