# Preppin – CSC436 Mobile App

## How to Run

1. Clone the repository:
   git clone <repo-url>

2. Open Android Studio.

3. Click "Open" and select the `Preppin` folder.

4. Let Gradle sync.

5. Run the app on an emulator or device.

## Features

- Weekly meal planner
- Add and edit recipes
- Prep scheduling logic (auto-fills leftover meals)
- Dark mode toggle
- Bottom navigation (Home, Recipes, Prep)

## Architecture

- Jetpack Compose UI
- Navigation Compose
- State hoisted to MainApp
- ViewModel for meal planning logic
- Domain models separated in `/model`