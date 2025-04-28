import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ChatMessage {
  id: string;
  text: string;
  sender: 'user' | 'support';
  timestamp: Date;
  status?: 'sent' | 'delivered' | 'read' | 'pending';
  isFormatted?: boolean; // Indicates if the message is formatted
}

export interface ChatResponse {
  response: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:5000'; // Python Flask server URL

  constructor(private http: HttpClient) {}

  // Send message to chatbot and get response
  sendMessage(message: string): Observable<ChatResponse> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<ChatResponse>(`${this.apiUrl}/chat`, { message }, { headers });
  }

  // Get chat history for a user
  getChatHistory(userId: number): Observable<ChatMessage[]> {
    // In a real implementation, you would store and retrieve chat history from your backend
    // For now, we'll return an empty array
    return new Observable(observer => {
      observer.next([]);
      observer.complete();
    });
  }

  // Generate a unique message ID
  generateMessageId(): string {
    return `msg_${Date.now()}_${Math.floor(Math.random() * 1000)}`;
  }
}