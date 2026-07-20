const STORAGE_KEYS = {
  favorites: 'pantrychef:favorites',
  ratings: 'pantrychef:ratings',
  shoppingChecks: 'pantrychef:shoppingChecks'
};

const FALLBACK_INGREDIENTS = ['onion', 'tomato', 'eggs', 'spinach', 'cheese'];

const RECIPES = [
  { id: 'r1', name: 'Veggie Omelette', mealType: 'breakfast', diet: 'vegetarian', time: 15, difficulty: 'easy', ingredients: ['eggs', 'onion', 'tomato', 'spinach'], steps: ['Whisk eggs with salt.', 'Sauté onion and tomato.', 'Add spinach and eggs.', 'Fold and serve warm.'] },
  { id: 'r2', name: 'Chicken Rice Bowl', mealType: 'lunch', diet: 'high-protein', time: 30, difficulty: 'easy', ingredients: ['chicken breast', 'rice', 'bell pepper', 'onion'], steps: ['Cook rice.', 'Pan-sear chicken.', 'Stir-fry vegetables.', 'Assemble bowl and season.'] },
  { id: 'r3', name: 'Chickpea Salad Wrap', mealType: 'lunch', diet: 'vegetarian', time: 15, difficulty: 'easy', ingredients: ['chickpeas', 'tortilla', 'lettuce', 'yogurt'], steps: ['Mash chickpeas lightly.', 'Mix with yogurt and seasoning.', 'Fill tortilla with lettuce and mix.', 'Roll and slice.'] },
  { id: 'r4', name: 'Garlic Pasta', mealType: 'dinner', diet: 'normal', time: 30, difficulty: 'easy', ingredients: ['pasta', 'garlic', 'olive oil', 'parmesan'], steps: ['Boil pasta.', 'Cook garlic in olive oil.', 'Toss pasta in pan.', 'Top with parmesan.'] },
  { id: 'r5', name: 'Tuna Protein Toast', mealType: 'snack', diet: 'high-protein', time: 15, difficulty: 'easy', ingredients: ['tuna', 'bread', 'yogurt', 'lemon'], steps: ['Toast bread.', 'Mix tuna with yogurt and lemon.', 'Spread mixture over toast.', 'Serve immediately.'] },
  { id: 'r6', name: 'Lentil Soup', mealType: 'dinner', diet: 'vegetarian', time: 45, difficulty: 'medium', ingredients: ['lentils', 'carrot', 'onion', 'garlic'], steps: ['Sauté onion, garlic, and carrot.', 'Add lentils and water.', 'Simmer until soft.', 'Season and serve.'] },
  { id: 'r7', name: 'Turkey Stir-Fry', mealType: 'dinner', diet: 'high-protein', time: 30, difficulty: 'medium', ingredients: ['ground turkey', 'broccoli', 'soy sauce', 'garlic'], steps: ['Brown turkey in a pan.', 'Add broccoli and garlic.', 'Stir in soy sauce.', 'Cook until broccoli is tender.'] },
  { id: 'r8', name: 'Peanut Banana Oats', mealType: 'breakfast', diet: 'normal', time: 15, difficulty: 'easy', ingredients: ['oats', 'banana', 'milk', 'peanut butter'], steps: ['Cook oats with milk.', 'Stir in peanut butter.', 'Top with sliced banana.', 'Serve hot.'] },
  { id: 'r9', name: 'Caprese Snack Plate', mealType: 'snack', diet: 'vegetarian', time: 15, difficulty: 'easy', ingredients: ['tomato', 'mozzarella', 'basil', 'olive oil'], steps: ['Slice tomato and mozzarella.', 'Arrange on a plate.', 'Top with basil.', 'Drizzle olive oil and season.'] },
  { id: 'r10', name: 'Bean Burrito Bowl', mealType: 'lunch', diet: 'normal', time: 30, difficulty: 'easy', ingredients: ['black beans', 'rice', 'corn', 'tomato'], steps: ['Cook rice.', 'Warm beans and corn.', 'Top rice with vegetables.', 'Season and serve.'] }
];

const state = {
  ingredients: [],
  favorites: new Set(loadJSON(STORAGE_KEYS.favorites, [])),
  ratings: loadJSON(STORAGE_KEYS.ratings, {}),
  shoppingChecks: loadJSON(STORAGE_KEYS.shoppingChecks, {}),
  lastResults: []
};

const els = {
  imageInput: document.getElementById('imageInput'),
  manualInput: document.getElementById('manualInput'),
  addManualBtn: document.getElementById('addManualBtn'),
  tags: document.getElementById('ingredientTags'),
  mealFilter: document.getElementById('mealTypeFilter'),
  dietFilter: document.getElementById('dietFilter'),
  timeFilter: document.getElementById('timeFilter'),
  generateBtn: document.getElementById('generateBtn'),
  recipeResults: document.getElementById('recipeResults'),
  favoritesList: document.getElementById('favoritesList'),
  shoppingList: document.getElementById('shoppingList')
};

init();

function init() {
  bindEvents();
  renderTags();
  renderRecipes([]);
  renderFavorites();
  renderShoppingList();
}

function bindEvents() {
  els.addManualBtn.addEventListener('click', () => addIngredientsFromText(els.manualInput.value));
  els.manualInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      addIngredientsFromText(els.manualInput.value);
    }
  });

  els.imageInput.addEventListener('change', () => {
    const files = Array.from(els.imageInput.files || []);
    const detected = detectIngredientsFromFiles(files);
    addIngredients(detected);
    els.imageInput.value = '';
  });

  els.generateBtn.addEventListener('click', generateRecipes);
}

function normalizeIngredient(value) {
  return value.trim().toLowerCase();
}

function addIngredientsFromText(text) {
  addIngredients(text.split(',').map(normalizeIngredient).filter(Boolean));
  els.manualInput.value = '';
}

function addIngredients(items) {
  const merged = new Set(state.ingredients);
  items.map(normalizeIngredient).filter(Boolean).forEach((item) => merged.add(item));
  state.ingredients = Array.from(merged);
  renderTags();
  renderFavorites();
}

function removeIngredient(item) {
  state.ingredients = state.ingredients.filter((ing) => ing !== item);
  renderTags();
  renderFavorites();
}

function detectIngredientsFromFiles(files) {
  const keywordMap = {
    egg: 'eggs', tomato: 'tomato', onion: 'onion', chicken: 'chicken breast', rice: 'rice',
    milk: 'milk', spinach: 'spinach', cheese: 'cheese', tuna: 'tuna', banana: 'banana',
    bread: 'bread', pasta: 'pasta', beans: 'black beans', lentil: 'lentils', yogurt: 'yogurt'
  };

  const detected = new Set();
  files.forEach((file) => {
    const name = file.name.toLowerCase();
    Object.entries(keywordMap).forEach(([keyword, ingredient]) => {
      if (name.includes(keyword)) detected.add(ingredient);
    });
  });

  if (!detected.size && files.length) {
    FALLBACK_INGREDIENTS.slice(0, Math.min(files.length + 1, FALLBACK_INGREDIENTS.length))
      .forEach((ingredient) => detected.add(ingredient));
  }

  return Array.from(detected);
}

function renderTags() {
  if (!state.ingredients.length) {
    els.tags.innerHTML = '<p class="hint">No ingredients yet. Add from photos or text.</p>';
    return;
  }

  els.tags.innerHTML = state.ingredients
    .map((ingredient) => `
      <span class="tag">
        ${ingredient}
        <button type="button" aria-label="Remove ${ingredient}" data-remove="${ingredient}">✕</button>
      </span>
    `)
    .join('');

  els.tags.querySelectorAll('[data-remove]').forEach((button) => {
    button.addEventListener('click', () => removeIngredient(button.dataset.remove));
  });
}

function generateRecipes() {
  const meal = els.mealFilter.value;
  const diet = els.dietFilter.value;
  const maxTime = Number(els.timeFilter.value);
  const available = new Set(state.ingredients);

  const filtered = RECIPES.filter((recipe) => {
    const mealPass = meal === 'any' || recipe.mealType === meal;
    const dietPass = diet === 'any' || recipe.diet === diet;
    const timePass = recipe.time <= maxTime;
    return mealPass && dietPass && timePass;
  });

  const rankRecipes = (recipes) => recipes
    .map((recipe) => {
      const used = recipe.ingredients.filter((ing) => available.has(ing));
      const missing = recipe.ingredients.filter((ing) => !available.has(ing));
      return { ...recipe, used, missing, score: used.length };
    })
    .sort((a, b) => (b.score - a.score) || (a.missing.length - b.missing.length) || (a.time - b.time));

  const rankedFiltered = rankRecipes(filtered);
  const selected = [...rankedFiltered];

  if (selected.length < 3) {
    const selectedIds = new Set(selected.map((recipe) => recipe.id));
    const relaxedPool = RECIPES.filter((recipe) => recipe.time <= maxTime && !selectedIds.has(recipe.id));
    selected.push(...rankRecipes(relaxedPool));
  }

  state.lastResults = selected.slice(0, 5);
  renderRecipes(state.lastResults);
  renderShoppingList();
}

function toggleFavorite(recipeId) {
  if (state.favorites.has(recipeId)) {
    state.favorites.delete(recipeId);
  } else {
    state.favorites.add(recipeId);
  }
  localStorage.setItem(STORAGE_KEYS.favorites, JSON.stringify(Array.from(state.favorites)));
  renderRecipes(state.lastResults);
  renderFavorites();
}

function setRating(recipeId, value) {
  state.ratings[recipeId] = value;
  localStorage.setItem(STORAGE_KEYS.ratings, JSON.stringify(state.ratings));
  renderRecipes(state.lastResults);
  renderFavorites();
}

function renderStars(recipeId) {
  const current = Number(state.ratings[recipeId] || 0);
  return `<div class="stars">${[1, 2, 3, 4, 5].map((n) => `
    <button type="button" class="${n <= current ? 'active' : ''}" data-rate="${recipeId}:${n}" aria-label="Rate ${n} star">★</button>
  `).join('')}</div>`;
}

function recipeCard(recipe) {
  const favoriteLabel = state.favorites.has(recipe.id) ? 'Unsave' : 'Save';
  return `
    <article class="recipe-card">
      <div class="recipe-head">
        <div>
          <h3>${recipe.name}</h3>
          <p class="recipe-meta">${recipe.time} min · ${recipe.difficulty} · ${recipe.mealType} · ${recipe.diet}</p>
        </div>
        <button type="button" data-fav="${recipe.id}">${favoriteLabel}</button>
      </div>
      ${renderStars(recipe.id)}
      <p><strong>Ingredients used:</strong> <span class="ingredients-used">${recipe.used?.join(', ') || 'None yet'}</span></p>
      <p><strong>Missing ingredients:</strong> <span class="ingredients-missing">${recipe.missing?.join(', ') || 'None'}</span></p>
      <ol>${recipe.steps.map((step) => `<li>${step}</li>`).join('')}</ol>
    </article>
  `;
}

function wireRecipeCardEvents(container) {
  container.querySelectorAll('[data-fav]').forEach((button) => {
    button.addEventListener('click', () => toggleFavorite(button.dataset.fav));
  });
  container.querySelectorAll('[data-rate]').forEach((button) => {
    const [recipeId, score] = button.dataset.rate.split(':');
    button.addEventListener('click', () => setRating(recipeId, Number(score)));
  });
}

function renderRecipes(results) {
  if (!results.length) {
    els.recipeResults.innerHTML = '<p class="card hint">No recipes generated yet. Add ingredients and click Generate Recipes.</p>';
    return;
  }

  els.recipeResults.innerHTML = results.map(recipeCard).join('');
  wireRecipeCardEvents(els.recipeResults);
}

function renderFavorites() {
  const favorites = RECIPES
    .filter((recipe) => state.favorites.has(recipe.id))
    .map((recipe) => {
      const available = new Set(state.ingredients);
      const used = recipe.ingredients.filter((ing) => available.has(ing));
      const missing = recipe.ingredients.filter((ing) => !available.has(ing));
      return { ...recipe, used, missing };
    });

  if (!favorites.length) {
    els.favoritesList.innerHTML = '<p class="hint">No favorites saved yet.</p>';
    return;
  }

  els.favoritesList.innerHTML = favorites.map(recipeCard).join('');
  wireRecipeCardEvents(els.favoritesList);
}

function renderShoppingList() {
  const missingItems = Array.from(new Set(state.lastResults.flatMap((recipe) => recipe.missing || [])));

  if (!missingItems.length) {
    els.shoppingList.innerHTML = '<li class="hint">Generate recipes to build your shopping list.</li>';
    return;
  }

  els.shoppingList.innerHTML = missingItems.map((item) => {
    const checked = Boolean(state.shoppingChecks[item]);
    return `
      <li class="${checked ? 'done' : ''}">
        <input type="checkbox" data-shop-item="${item}" ${checked ? 'checked' : ''} />
        <span>${item}</span>
      </li>
    `;
  }).join('');

  els.shoppingList.querySelectorAll('[data-shop-item]').forEach((checkbox) => {
    checkbox.addEventListener('change', () => {
      state.shoppingChecks[checkbox.dataset.shopItem] = checkbox.checked;
      localStorage.setItem(STORAGE_KEYS.shoppingChecks, JSON.stringify(state.shoppingChecks));
      renderShoppingList();
    });
  });
}

function loadJSON(key, fallback) {
  try {
    const value = localStorage.getItem(key);
    return value ? JSON.parse(value) : fallback;
  } catch {
    return fallback;
  }
}
