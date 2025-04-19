from sentence_transformers import SentenceTransformer
import numpy as np

class EmbeddingUtils:
    def __init__(self, model_name="all-MiniLM-L6-v2"):
        self.model = SentenceTransformer(model_name)

    def generate_embeddings(self, texts):
        """Generate embeddings for a list of texts."""
        return self.model.encode(texts, convert_to_tensor=True)

    def find_most_similar(self, query_embedding, knowledge_embeddings, top_k=7):
        """Find the most similar pieces of text in the knowledge base."""
        from torch.nn.functional import cosine_similarity

        similarities = cosine_similarity(query_embedding, knowledge_embeddings)
        top_indices = similarities.argsort(descending=True)[:top_k]
        return top_indices, similarities[top_indices]