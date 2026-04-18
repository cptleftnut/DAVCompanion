#!/bin/bash

# Initialiser git
git init

# Tilføj alle filer
git add .

# Opret commit
git commit -m "Initial commit: DAVCompanion med Gemini, ChromaDB og GitHub Actions"

# Sæt branch til main
git branch -M main

# Forbind til dit repo (fjerner først eventuel eksisterende origin for at undgå fejl)
git remote remove origin 2>/dev/null
git remote add origin https://github.com/cptleftnut/DAVCompanion.git

# Push koden
git push -u origin main

echo "Upload til GitHub er fuldført! GitHub Actions burde nu bygge din APK automatisk."
