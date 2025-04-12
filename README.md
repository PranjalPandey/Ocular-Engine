# Ocular-Engine

An Optical Character Recognition (OCR) Engine written in Java.

## Features
- Extract text from images and scanned documents.
- Support for multiple image formats (JPEG, PNG, etc.).
- Easy integration into Java-based applications.
- Customizable OCR configurations.

## Installation
1. Clone this repository:
   ```bash
   git clone https://github.com/PranjalPandey/Ocular-Engine.git
   ```

2. Navigate to the project directory:
  ```bash
  cd Ocular-Engine
```
3. Build the project using your preferred Java build tool (e.g., Maven or Gradle).

## Usage
1. Import the compiled .jar file into your Java project.
2. Use the provided API to load images and extract text:
```Java
OcularEngine engine = new OcularEngine();
String extractedText = engine.processImage("path/to/image.jpg");
System.out.println(extractedText);
```
