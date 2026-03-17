# Preppin – CSC436 Mobile App
## Overview
Preppin' is an Android meal planning and meal-prep tracking application built with Jetpack Compose. The app allows users to organize their weekly meals (Breakfast, Lunch, Dinner) across the week and plan cooking sessions that generate prepped meals for later days. Users can also maintain a recipe collection, attach photos of dishes using the device's camera, and visually track which meals are currently being cooked or already prepped. The goal of the app is to simplify the weekly meal prep load and help users manage leftovers.

## Figma
[Figma Wireframe Link](https://www.figma.com/design/6XUBQ9xms5mGrzbr9o8xai/CSC436-Wireframe-Map?node-id=2002-446&m=dev&t=FeX5pmwSyXBAfRDa-1)

## Key Android / Jetpack Compose Features
The app relies on the following Android and Jetpack Compose features:
### Jetpack Compose
- Composable UI architecture
- Material3 components
- Navigation Compose for screen routing
- Adaptive UI layouts for portrait/landscape orientations
- LazyColumn and LazyVerticalGrid for dynamic list/grid layouts

### Jetpack Libraries
- Room Database
    - Local persistent storage for recipes and meal slots
    - Flow-based reactive data updates
- DataStore (preferences)
    - Persists UI preferences such as Dark Mode
- ViewModel + StateFlow
    - Maintains UI state across configuration changes
    - Handles business logic for meal planning
 
### Camera & Media
- CameraX
    - Capture photos of recipes/dishes directly within the app
- Coil
    - Efficient loading of images into Compose UI
 
### Layout & UI Behavior
- Responsive UI that adapts to orientation
- Bottom navigation bar in portrait
- Navigation rail on the left side in landscape
- Dynamic calendar layout that swaps axes in landscape mode

## Third-Party Libraries
- CameraX
- Coil
- Room
- DataStore
- Naviagtion Compose

## Device Requirements
### Android Version
- Minimum SDK: 24
- Target SDK: 36

### Required Device Features
- Camera (for recipe photos)
- File storage access for saved images
  
      


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
