import os
import requests
from flask import Flask, request, jsonify
import re

app = Flask(__name__)

# API setup
API_URL = "https://router.huggingface.co/novita/v3/openai/chat/completions"
HF_API_TOKEN = os.getenv('HF_API_TOKEN', '')  # Use env var or default
HEADERS = {"Authorization": f"Bearer {HF_API_TOKEN}"}

MODEL = "deepseek/deepseek-v3-0324"
# MODEL = "meta-llama/Llama-2-7b-chat-hf"
# MODEL = "meta-llama/Meta-Llama-3-8B"

def fetchTaskFromLlama(student_topic):
    """Fetches task title + description from Hugging Face router API."""
    print("Fetching task from Hugging Face router API")
    payload = {
        "messages": [
            {
                "role": "user",
                "content": (
                    f"Create a small task title and a short description for a quiz related to the topic '{student_topic}'. "
                    f"Return it in this format:\n"
                    f"Title: [Your generated title]\n"
                    f"Description: [Your generated description]"
                )
            }
        ],
        "model": MODEL,
        "max_tokens": 150,
        "temperature": 0.5,
        "top_p": 0.9
    }

    response = requests.post(API_URL, headers=HEADERS, json=payload)
    if response.status_code == 200:
        result_text = response.json()["choices"][0]["message"]["content"]
        return result_text
    else:
        raise Exception(f"API request failed: {response.status_code} - {response.text}")


def fetchQuizFromLlama(student_topic):
    print("Fetching quiz from Hugging Face router API")
    payload = {
        "messages": [
            {
                "role": "user",
                "content": (
                    f"Generate a quiz with 3 questions to test students on the provided topic. "
                    f"For each question, generate 4 options where only one of the options is correct. "
                    f"Format your response as follows:\n"
                    f"**QUESTION 1:** [Your question here]?\n"
                    f"**OPTION A:** [First option]\n"
                    f"**OPTION B:** [Second option]\n"
                    f"**OPTION C:** [Third option]\n"
                    f"**OPTION D:** [Fourth option]\n"
                    f"**ANS:** [Correct answer letter]\n\n"
                    f"**QUESTION 2:** [Your question here]?\n"
                    f"**OPTION A:** [First option]\n"
                    f"**OPTION B:** [Second option]\n"
                    f"**OPTION C:** [Third option]\n"
                    f"**OPTION D:** [Fourth option]\n"
                    f"**ANS:** [Correct answer letter]\n\n"
                    f"**QUESTION 3:** [Your question here]?\n"
                    f"**OPTION A:** [First option]\n"
                    f"**OPTION B:** [Second option]\n"
                    f"**OPTION C:** [Third option]\n"
                    f"**OPTION D:** [Fourth option]\n"
                    f"**ANS:** [Correct answer letter]\n\n"
                    f"Ensure text is properly formatted. It needs to start with a question, then the options, and finally the correct answer. "
                    f"Follow this pattern for all questions. "
                    f"Here is the student topic:\n{student_topic}"
                )
            }
        ],
        "model": MODEL,
        "max_tokens": 500,
        "temperature": 0.7,
        "top_p": 0.9
    }

    response = requests.post(API_URL, headers=HEADERS, json=payload)
    if response.status_code == 200:
        result = response.json()["choices"][0]["message"]["content"]
        # print(result)
        return result
    else:
        raise Exception(f"API request failed: {response.status_code} - {response.text}")

def fetchAnswersFromLlama(questions_with_answers):
    print("Fetching AI-generated answers from Hugging Face router API")
    prompt_content = "Generate model responses based on the following student's quiz answers.\n\n"

    for idx, qa in enumerate(questions_with_answers, start=1):
        prompt_content += (
            f"{idx}. Question: {qa['question']}\n"
            f"Student Answer: {qa['user_answer']}\n\n"
        )

    prompt_content += (
        "For each Question and Student Answer above, generate a friendly AI response "
        "that explains or elaborates on their answer.\n"
        "Return each response separately in order, without numbering or extra text. Limit each answer within 25 words"
    )

    payload = {
        "messages": [
            {
                "role": "user",
                "content": prompt_content
            }
        ],
        "model": MODEL,
        "max_tokens": 800,
        "temperature": 0.7,
        "top_p": 0.9
    }

    response = requests.post(API_URL, headers=HEADERS, json=payload)
    if response.status_code == 200:
        result = response.json()["choices"][0]["message"]["content"]
        return result
    else:
        raise Exception(f"API request failed: {response.status_code} - {response.text}")

def process_task(task_text):
    """Processes the raw task LLM output into structured clean data."""
    task_info = {}

    lines = task_text.strip().split('\n')
    for line in lines:
        if line.startswith("Title:"):
            title = line.replace("Title:", "").strip()
            title = title.replace("**", "").replace('"', '').strip()  # remove ** and quotes
            task_info["task_title"] = title
        elif line.startswith("Description:"):
            description = line.replace("Description:", "").strip()
            description = description.replace("**", "").replace('"', '').strip()
            task_info["task_description"] = description

    return task_info


def process_quiz(quiz_text):
    questions = []
    # Updated regex to match bolded format with numbered questions
    pattern = re.compile(
        r'\*\*QUESTION \d+:\*\* (.+?)\n'
        r'\*\*OPTION A:\*\* (.+?)\n'
        r'\*\*OPTION B:\*\* (.+?)\n'
        r'\*\*OPTION C:\*\* (.+?)\n'
        r'\*\*OPTION D:\*\* (.+?)\n'
        r'\*\*ANS:\*\* (.+?)(?=\n|$)',
        re.DOTALL
    )
    matches = pattern.findall(quiz_text)

    for match in matches:
        question = match[0].strip()
        options = [match[1].strip(), match[2].strip(), match[3].strip(), match[4].strip()]
        correct_ans = match[5].strip()

        question_data = {
            "question": question,
            "options": options,
            "correct_answer": correct_ans
        }
        questions.append(question_data)

    return questions

def process_generated_answers(raw_content):
    """
    Process the raw AI-generated content and return a list of clean answer strings,
    removing unwanted formatting like *asterisks* and quotes.
    """
    # Try splitting by double newlines first
    answers = [ans.strip() for ans in raw_content.split("\n\n") if ans.strip()]

    # If fallback needed (meaning it didn't split well)
    if len(answers) <= 1:
        answers = [ans.strip() for ans in raw_content.split("\n") if ans.strip()]

    # Now clean each answer individually
    cleaned_answers = []
    for ans in answers:
        cleaned = ans.replace("**", "").replace("*", "").replace('"', '').strip()
        cleaned_answers.append(cleaned)

    return cleaned_answers

@app.route('/getTask', methods=['GET'])
def get_task():
    print("Task request received")
    student_topic = request.args.get('topic')
    if not student_topic:
        return jsonify({'error': 'Missing topic parameter'}), 400
    try:
        task_text = fetchTaskFromLlama(student_topic)
        print(task_text)
        processed_task = process_task(task_text)
        if not processed_task:
            return jsonify({'error': 'Failed to parse task data', 'raw_response': task_text}), 500
        return jsonify(processed_task), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/getQuiz', methods=['GET'])
def get_quiz():
    print("Request received")
    student_topic = request.args.get('topic')
    if not student_topic:
        return jsonify({'error': 'Missing topic parameter'}), 400
    try:
        quiz = fetchQuizFromLlama(student_topic)
        print(quiz)
        processed_quiz = process_quiz(quiz)
        if not processed_quiz:
            return jsonify({'error': 'Failed to parse quiz data', 'raw_response': quiz}), 500
        return jsonify({'quiz': processed_quiz}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
@app.route('/getAnswers', methods=['POST'])
def generate_answers():
    print("Request JSON body:", request.json)  # Debugging print

    if not request.is_json:
        return jsonify({'error': 'Invalid JSON'}), 400

    data = request.get_json()
    if not data:
        return jsonify({'error': 'Missing data'}), 400

    questions = data.get('questions')
    if not questions:
        return jsonify({'error': 'Missing questions'}), 400

    try:
        raw_answers = fetchAnswersFromLlama(questions)  # Fetch raw text
        print("Raw Answers from Llama:", raw_answers)

        processed_answers = process_generated_answers(raw_answers)  # Now split into list

        if not processed_answers:
            return jsonify({'error': 'Failed to parse answer data', 'raw_response': raw_answers}), 500

        return jsonify({'answers': processed_answers}), 200  # <-- RETURN LIST here
    except Exception as e:
        return jsonify({'error': str(e)}), 500
@app.route('/test', methods=['GET'])
def run_test():
    return jsonify({'quiz': "test"}), 200

if __name__ == '__main__':
    port_num = 5002
    print(f"App running on port {port_num}")
    app.run(port=port_num, host="0.0.0.0")
