from flask import Flask, request, jsonify
from flask_cors import CORS
from rag_model import RAGModel
import os

app = Flask(__name__)
CORS(app)
# Initialize the RAG model with the data directory and Gemini API details
data_directory = os.path.join(os.path.dirname(__file__), '../data')
gemini_api_key = "AIzaSyBQG3DMkTHcI_kmgIE8hByXxwRLZZMGkT4"
gemini_api_url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="+gemini_api_key  # Replace with the actual API URL
rag_model = RAGModel(data_directory, gemini_api_url, gemini_api_key)
@app.route('/chat', methods=['POST'])
def chat():
    data = request.get_json()
    user_input = "You: " + data.get('message')
    if not user_input:
        return jsonify({'error': 'No message provided'}), 400

    response = rag_model.generate_response(user_input, False)
    return jsonify({'response': response})

if __name__ == '__main__':
    app.run(debug=True, port=5000)