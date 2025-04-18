import os
import json
import requests
import pandas as pd
from PyPDF2 import PdfReader
from utils.embedding_utils import EmbeddingUtils


class RAGModel:
    def __init__(self, data_folder, gemini_api_url, gemini_api_key):
        self.data_folder = data_folder
        self.gemini_api_url = gemini_api_url
        self.gemini_api_key = gemini_api_key
        self.embedding_utils = EmbeddingUtils()
        self.knowledge_base, self.knowledge_embeddings = self.load_knowledge_base()

    def load_knowledge_base(self):
        """Load knowledge base from .txt, .csv, and .pdf files and generate embeddings."""
        data_files = [f for f in os.listdir(self.data_folder) if f.endswith(('.txt', '.csv', '.pdf'))]
        knowledge_base = []

        for file in data_files:
            file_path = os.path.join(self.data_folder, file)

            if file.endswith('.txt'):
                # Load text files
                with open(file_path, 'r', encoding='utf-8') as f:
                    knowledge_base.extend(f.readlines())

            elif file.endswith('.csv'):
                # Load CSV files
                df = pd.read_csv(file_path)
                for col in df.columns:
                    knowledge_base.extend(df[col].dropna().astype(str).tolist())

            elif file.endswith('.pdf'):
                # Load PDF files
                reader = PdfReader(file_path)
                for page in reader.pages:
                    knowledge_base.append(page.extract_text())

        # Generate embeddings for the knowledge base
        knowledge_embeddings = self.embedding_utils.generate_embeddings(knowledge_base)
        return knowledge_base, knowledge_embeddings

    def process_query(self, query, use_full_pdf=False):
        """
        Process a query by either using the entire PDF content as context
        or retrieving the most relevant pieces of knowledge.
        """
        if use_full_pdf:
            # Use the entire PDF content as context
            pdf_files = [f for f in os.listdir(self.data_folder) if f.endswith('.pdf')]
            pdf_context = ""

            for file in pdf_files:
                file_path = os.path.join(self.data_folder, file)
                reader = PdfReader(file_path)
                for page in reader.pages:
                    pdf_context += page.extract_text() + "\n"

            relevant_context = pdf_context.strip()
        else:
            # Use embeddings to find the most relevant pieces of knowledge
            query_embedding = self.embedding_utils.generate_embeddings([query])
            top_indices, _ = self.embedding_utils.find_most_similar(query_embedding, self.knowledge_embeddings)
            relevant_context = "\n".join([self.knowledge_base[i] for i in top_indices])
            
        # Construct the payload with a system role for instructions
        payload = {
            "contents": [
                {"role": "model", "parts": [{"text": "You are a helpful assistant. Respond with exact directions to the user's query. Use the context provided to answer the question."}]},
                {"role": "user", "parts": [{"text": query}]},
                {"role": "user", "parts": [{"text": relevant_context}]}
            ]
        }
        headers = {
            "Content-Type": "application/json"
        }

        # Log the payload for debugging
        print("Payload:", json.dumps(payload, indent=2))

        # Make the API call to the Gemini LLM
        response = requests.post(self.gemini_api_url, headers=headers, json=payload)

        # Handle the response
        if response.status_code == 200:
            candidates = response.json().get("candidates", [])
            if candidates:
                return candidates[0].get("content", {}).get("parts", [{}])[0].get("text", "No response from Gemini.")
            return "No response from Gemini."
        else:
            return f"Error: {response.status_code} - {response.text}"

    def generate_response(self, user_input, use_full_pdf=False):
        """Generate a response for the user input."""
        return self.process_query(user_input, use_full_pdf=use_full_pdf)