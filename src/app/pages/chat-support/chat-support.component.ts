import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { DashboardNavbarComponent } from "../../components/dashboard-navbar/dashboard-navbar.component";
import { AuthService } from '../../services/auth.service';
import { HttpClient } from '@angular/common/http';

interface ChatMessage {
  id: string;
  text: string;
  sender: 'user' | 'support';
  timestamp: Date;
  status?: 'sent' | 'delivered' | 'read' | 'pending';
}

interface CommonIssue {
  id: number;
  title: string;
  description: string;
}

@Component({
  selector: 'app-chat-support',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, DashboardNavbarComponent],
  providers: [AuthService],
  templateUrl: './chat-support.component.html',
  styleUrl: './chat-support.component.css'
})
export class ChatSupportComponent implements OnInit {
  @ViewChild('chatContainer') private chatContainer!: ElementRef;
  
  userType: string = 'user'; // 'user' or 'driver'
  userName: string = '';
  userId: number = 0;
  
  messageText: string = '';
  messages: ChatMessage[] = [];
  isTyping: boolean = false;
  
  commonIssues: CommonIssue[] = [
    { 
      id: 1, 
      title: 'Payment Issues', 
      description: 'Problems with payments, refunds, or charges.'
    },
    { 
      id: 2, 
      title: 'Trip Cancellation', 
      description: 'Questions about cancellation policies or fees.'
    },
    { 
      id: 3, 
      title: 'Driver Feedback', 
      description: 'Submit feedback about your driver experience.'
    },
    { 
      id: 4, 
      title: 'Account Issues', 
      description: 'Problems with your account or login.'
    },
    { 
      id: 5, 
      title: 'Lost Items', 
      description: 'Report items lost during a trip.'
    }
  ];
  
  // Sidebar menu items - will be set based on user type
  sidebarMenuItems: any[] = [];

  constructor(
    private authService: AuthService,
    private http: HttpClient
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
      
      // Determine user type
      if (user.role) {
        this.userType = user.role.toLowerCase();
      }
    }
  }

  setupSidebarMenu(): void {
    if (this.userType === 'driver') {
      this.sidebarMenuItems = [
        { label: 'Dashboard', route: '/driver-dashboard' },
        { label: 'Available Trips', route: '/available-trips' },
        { label: 'All Trips', route: '/all-trips' },
        { label: 'Chat Support', active: true, route: '/chat-support' },
        { label: 'Settings', route: '/settings' },
      ];
    } else {
      this.sidebarMenuItems = [
        { label: 'Dashboard', route: '/user-dashboard' },
        { label: 'Book a Ride', route: '/find-driver' },
        { label: 'My Bookings', route: '/my-bookings' },
        { label: 'My Vehicles', route: '/my-vehicles' },
        { label: 'Chat Support', active: true, route: '/chat-support' },
        { label: 'Settings', route: '/settings' },
      ];
    }
  }

  loadChatHistory(): void {
    // In a real app, you would fetch the chat history from your backend
    // For now, we'll add a welcome message
    const welcomeMessage: ChatMessage = {
      id: this.generateMessageId(),
      text: `Welcome to Drive-Me support, ${this.userName}! How can we help you today?`,
      sender: 'support',
      timestamp: new Date(),
      status: 'read'
    };
    
    this.messages = [welcomeMessage];
  }

  sendMessage(): void {
    if (!this.messageText.trim()) return;
    
    // Create and add user message
    const userMessage: ChatMessage = {
      id: this.generateMessageId(),
      text: this.messageText,
      sender: 'user',
      timestamp: new Date(),
      status: 'sent'
    };
    
    this.messages.push(userMessage);
    const sentMessage = this.messageText;
    this.messageText = '';
    
    // Scroll to bottom
    setTimeout(() => this.scrollToBottom(), 50);
    
    // Show "typing" indicator
    this.isTyping = true;
    
    // Make API call to get response
    this.getSupportResponse(sentMessage);
  }

  getSupportResponse(userMessage: string): void {
    // In a real app, you would call your backend API here
    // For now, we'll simulate a response after a delay
    
    // Example API call:
    // this.http.post<any>('/api/support/chat', { message: userMessage })
    //   .subscribe(response => {
    //     this.addSupportResponse(response.message);
    //     this.isTyping = false;
    //   });
    
    // Simulate API call with timeout
    setTimeout(() => {
      let response = this.generateAutoResponse(userMessage);
      this.addSupportResponse(response);
      this.isTyping = false;
    }, 1500);
  }

  generateAutoResponse(userMessage: string): string {
    const lowercaseMessage = userMessage.toLowerCase();
    
    if (lowercaseMessage.includes('payment') || lowercaseMessage.includes('refund') || lowercaseMessage.includes('charge')) {
      return 'I understand you have a question about payments. For payment issues, please provide your trip ID and I can look into the details for you. Alternatively, you can visit your trip history to see all payment information.';
    } 
    else if (lowercaseMessage.includes('cancel') || lowercaseMessage.includes('cancellation')) {
      return 'If you need to cancel a trip, you can do so through the app. Please note that cancellation fees may apply depending on how close to the scheduled pickup time you cancel. Would you like me to explain our cancellation policy?';
    }
    else if (lowercaseMessage.includes('hello') || lowercaseMessage.includes('hi')) {
      return `Hello ${this.userName}! Welcome to Drive-Me support. How can I assist you today?`;
    }
    else if (lowercaseMessage.includes('lost') || lowercaseMessage.includes('item') || lowercaseMessage.includes('found')) {
      return "If you've lost an item during a trip, please provide your trip details and a description of the item. We'll contact the driver and try to locate it for you.";
    }
    else if (lowercaseMessage.includes('driver') || lowercaseMessage.includes('rude') || lowercaseMessage.includes('feedback')) {
      return "Im sorry to hear you had an issue. Could you provide the trip details and explain what happened? We take driver conduct very seriously and will investigate this matter promptly.";
    }
    else if (lowercaseMessage.includes('account') || lowercaseMessage.includes('login') || lowercaseMessage.includes('password')) {
      return "For account-related issues, please try resetting your password first. If you continue experiencing problems, please provide more details about the specific issue you're facing so I can better assist you.";
    }
    else {
      return 'Thank you for your message. A support representative will review your query and get back to you shortly. Is there anything specific about your issue that you can share to help us resolve it faster?';
    }
  }

  addSupportResponse(text: string): void {
    const supportMessage: ChatMessage = {
      id: this.generateMessageId(),
      text: text,
      sender: 'support',
      timestamp: new Date(),
      status: 'delivered'
    };
    
    this.messages.push(supportMessage);
    
    // Scroll to bottom
    setTimeout(() => this.scrollToBottom(), 50);
  }

  // Add the missing selectCommonIssue method
  selectCommonIssue(issue: CommonIssue): void {
    this.messageText = `I need help with: ${issue.title} - ${issue.description}`;
    this.sendMessage();
  }

  generateMessageId(): string {
    return `msg_${Date.now()}_${Math.floor(Math.random() * 1000)}`;
  }

  scrollToBottom(): void {
    try {
      this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
    } catch(err) { 
      console.error(err);
    }
  }

  formatTime(date: Date): string {
    return date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
  }
}