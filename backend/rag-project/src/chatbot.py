import os
from rag_model import RAGModel

def main():
    # Initialize the RAG model with the data directory and Gemini API details
    data_directory = os.path.join(os.path.dirname(__file__), '../data')
    gemini_api_key = "AIzaSyBQG3DMkTHcI_kmgIE8hByXxwRLZZMGkT4"
    gemini_api_url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="+gemini_api_key  # Replace with the actual API URL
      # Replace with your actual API key

    rag_model = RAGModel(data_directory, gemini_api_url, gemini_api_key)

    print("Welcome to the Chatbot! Type 'exit' to quit.")
    
    while True:
        user_input = input("You: ")
        if user_input.lower() == 'exit':
            break
        
        response = rag_model.generate_response(user_input, False)
        print(f"Chatbot: {response}")

if __name__ == "__main__":
    main()