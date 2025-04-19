def load_text_data(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        return file.read()

def load_csv_data(file_path):
    import pandas as pd
    return pd.read_csv(file_path)

def load_json_data(file_path):
    import json
    with open(file_path, 'r', encoding='utf-8') as file:
        return json.load(file)

def load_data(file_path):
    if file_path.endswith('.txt'):
        return load_text_data(file_path)
    elif file_path.endswith('.csv'):
        return load_csv_data(file_path)
    elif file_path.endswith('.json'):
        return load_json_data(file_path)
    else:
        raise ValueError("Unsupported file format: {}".format(file_path))