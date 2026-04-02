# Local AI Auto-Commenter

## About the Project
This is a local AI tool built with Spring Boot that automatically writes Javadocs for your Java projects. Instead of writing comments manually, this tool reads your code, understands the logic, and writes clean, standard comments back into your files. 
Because it uses a local model, your code never leaves your machine. 

## Tech Stack
* **Framework:** Spring Boot 
* **AI Orchestration:** Spring AI
* **Local LLM:** Ollama
* **Code Parsing:** JavaParser

## How It Works
1. The tool scans your project folder for `.java` files.
2. It uses JavaParser to extract methods that do not have comments.
3. It sends the raw code to the local Ollama model.
4. The AI generates a standard Javadoc explaining what the code does.
5. The tool safely writes the comment back into your source file without breaking the layout.

## UI
The project includes a simple, clean web interface to specify a directory path and see the live progress of the auto-commenting process.

## Demo

https://github.com/user-attachments/assets/911da3f3-65c0-47e6-ae92-3c313967125b


