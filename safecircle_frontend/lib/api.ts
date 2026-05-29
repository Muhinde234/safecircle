const API_BASE_URL = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8089/api/v1';

export interface SessionResponse {
  sessionId: string;
  nickname: string;
  createdAt: string;
}

export interface BookmarkResponse {
  bookmarkType: string;
  targetId: string;
  createdAt?: string;
}

export interface ClinicResponse {
  id: string;
  name: string;
  district: string;
  address: string;
  youthFriendly: boolean;
  contactInfo: string;
  services: string[];
  whatToExpect: string;
}

export interface ContentItemResponse {
  id: string;
  title: string;
  category: string;
  excerpt: string;
  body: string;
  language: string;
  isPublished: boolean;
  isFeatured: boolean;
  audioUrl?: string;
  createdAt?: string;
}

export interface QuestionnaireOption {
  text: string;
  textRw: string;
  nextNodeId?: string;
  recommendation?: string;
}

export interface QuestionnaireNode {
  id: string;
  text: string;
  textRw: string;
  options: QuestionnaireOption[];
}

export interface QuestionnaireDto {
  startQuestionId: string;
  questions: Record<string, QuestionnaireNode>;
}

export interface RiskAssessmentResponse {
  riskLevel: string;
  recommendation: string;
  details: string;
}

export interface ChatMessageResponse {
  reply: string;
  source: string;
  timestamp: string;
}

export interface ChatHistoryResponse {
  id: string;
  role: string;
  message: string;
  source?: string;
  language?: string;
  createdAt?: string;
}

export interface ModeratedMessageResponse {
  id: string;
  sessionId: string;
  nickname: string;
  messageText: string;
  createdAt: string;
  isFlagged: boolean;
  moderationNotes?: string;
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const url = `${API_BASE_URL}${path}`;
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options?.headers || {}),
    },
  });

  if (!response.ok) {
    let errorMessage = `HTTP error! Status: ${response.status}`;
    try {
      const errorData = await response.json();
      if (errorData.message) errorMessage = errorData.message;
    } catch {}
    throw new Error(errorMessage);
  }

  // Handle 201/204 No Content or empty responses
  if (response.status === 204 || response.headers.get('content-length') === '0') {
    return {} as T;
  }

  try {
    return await response.json() as T;
  } catch {
    return {} as T;
  }
}

export const api = {
  // Session
  createAnonymousSession: (nickname?: string): Promise<SessionResponse> => {
    return request<SessionResponse>('/sessions/anonymous', {
      method: 'POST',
      body: JSON.stringify(nickname ? { nickname } : {}),
    });
  },

  // Clinics
  getClinics: (filters?: { district?: string; youthFriendly?: boolean; service?: string }): Promise<ClinicResponse[]> => {
    const params = new URLSearchParams();
    if (filters?.district) params.append('district', filters.district);
    if (filters?.youthFriendly !== undefined) params.append('youthFriendly', String(filters.youthFriendly));
    if (filters?.service) params.append('service', filters.service);

    const query = params.toString() ? `?${params.toString()}` : '';
    return request<ClinicResponse[]>(`/clinics${query}`);
  },

  // Content
  getContent: (filters?: { category?: string; limit?: number }): Promise<{ items: ContentItemResponse[] }> => {
    const params = new URLSearchParams();
    if (filters?.category) params.append('category', filters.category);
    if (filters?.limit !== undefined) params.append('limit', String(filters.limit));

    const query = params.toString() ? `?${params.toString()}` : '';
    return request<{ items: ContentItemResponse[] }>(`/content${query}`);
  },

  getLowBandwidthContent: (filters?: { category?: string; limit?: number }): Promise<{ items: ContentItemResponse[] }> => {
    const params = new URLSearchParams();
    if (filters?.category) params.append('category', filters.category);
    if (filters?.limit !== undefined) params.append('limit', String(filters.limit));

    const query = params.toString() ? `?${params.toString()}` : '';
    return request<{ items: ContentItemResponse[] }>(`/content/low-bandwidth${query}`);
  },

  // Risk Assessment
  getQuestionnaire: (): Promise<QuestionnaireDto> => {
    return request<QuestionnaireDto>('/risk/questionnaire');
  },

  assessRisk: (sessionId: string, eventType: string, hoursSinceEvent: number, symptomsPresent: boolean): Promise<RiskAssessmentResponse> => {
    return request<RiskAssessmentResponse>('/risk/assess', {
      method: 'POST',
      body: JSON.stringify({ sessionId, eventType, hoursSinceEvent, symptomsPresent }),
    });
  },

  // Bookmarks
  getBookmarks: (sessionId: string): Promise<BookmarkResponse[]> => {
    return request<BookmarkResponse[]>(`/sessions/${sessionId}/bookmarks`);
  },

  addBookmark: (sessionId: string, type: string, targetId: string): Promise<void> => {
    return request<void>(`/sessions/${sessionId}/bookmarks?type=${type}&targetId=${targetId}`, {
      method: 'POST',
    });
  },

  removeBookmark: (sessionId: string, type: string, targetId: string): Promise<void> => {
    return request<void>(`/sessions/${sessionId}/bookmarks?type=${type}&targetId=${targetId}`, {
      method: 'DELETE',
    });
  },

  // Chat
  sendChatMessage: (sessionId: string, message: string, language?: string): Promise<ChatMessageResponse> => {
    return request<ChatMessageResponse>('/chat/messages', {
      method: 'POST',
      body: JSON.stringify({ sessionId, message, language }),
    });
  },

  getChatHistory: (sessionId: string): Promise<ChatHistoryResponse[]> => {
    return request<ChatHistoryResponse[]>(`/chat/history?sessionId=${sessionId}`);
  },

  // Moderation
  flagChatMessage: (messageId: string, notes?: string): Promise<ModeratedMessageResponse> => {
    const params = new URLSearchParams();
    if (notes) params.append('notes', notes);
    const query = params.toString() ? `?${params.toString()}` : '';
    return request<ModeratedMessageResponse>(`/moderation/chat-messages/${messageId}/flag${query}`, {
      method: 'PUT',
    });
  },

  getFlaggedChatMessages: (): Promise<ModeratedMessageResponse[]> => {
    return request<ModeratedMessageResponse[]>('/moderation/chat-messages/flagged');
  },
};
