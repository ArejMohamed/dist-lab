# PantryChef

PantryChef is a mobile-responsive web app that helps users generate meal ideas from ingredients they already have.

## Quick run

Open `pantrychef-index.html` in a browser.

## Features

- Five app sections: Home, Generate Recipes, Favorites, Shopping List, Contact
- Ingredient input by:
  - image upload with friendly mock ingredient detection (filename inference + fallback samples)
  - manual comma-separated text
- Removable ingredient tags/chips
- 10 offline sample recipes across breakfast/lunch/dinner/snack and normal/vegetarian/high-protein diets
- Smart filters: meal type, diet, max cooking time (15/30/45 minutes)
- Recipe suggestions (up to 5) ranked by best ingredient match
- Recipe cards include time, difficulty, ingredients used, step-by-step instructions, and clearly marked missing ingredients
- Favorites and per-recipe 1–5 star ratings persisted in localStorage
- Deduplicated shopping list from missing ingredients with persisted checklist state
- Mobile-first card-based UI with food-themed styling
