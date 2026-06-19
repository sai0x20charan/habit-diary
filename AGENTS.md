# Habit Diary

Habit Diary is an Android mobile application used to track habits while capturing daily life through photos, videos, and notes, all in one place.

## Technologies
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Database**: Room Database
- **Dependency Injection**: Hilt (Dagger Hilt)
- **Architecture**: MVI (Model-View-Intent)
- **Navigation**: Navigation 3
- **Serialization**: kotlinx.serialization
- **Design**: Material Design 3 Expressive

---

## Project Structure and Architecture
These conventions are mandatory and should be followed consistently across all features and modules.

### `app`
The main application module containing all code, resources, and configurations.

#### `data`
**Contents**:
- `/local`: Local Data Source (Room Database: entities, DAOs, type converters, and migrations)
- `/model`: Enums (Theme options, Sort types)
- `/repository`: Repository interfaces and implementations
- `/mapper`: Mappers between models

**Rules**:
- Any mapping logic must only be placed in the `Mappers.kt` file.

#### `presentation`
**Contents**:
- Screens
- ViewModels
- State classes
- Effect classes
- Event classes (representing intents)
- Mappers
- Reusable UI components

**Rules**:
- UI components must only consume UI models or UI states.
- Mapping from local data layer entities to UI-specific states should be performed using dedicated mapper functions.
- Feature-specific presentation packages must contain:
  - Screen composable (`*Screen.kt`)
  - ViewModel (`*ViewModel.kt`)
  - State class (`*State.kt`)
  - Effect class (`*Effect.kt`)
  - Event class (`*Event.kt`)
- Shared/reusable UI components must be placed under `presentation/common/components`.
- If any components from different features require similar UI models, they should be placed in `presentation/common/model`.
- Presentation mappers must be placed under `presentation/mapper`.

**Naming Conventions**:

- **Screens**
  - Use the suffix `Screen`.
  - Example:
    - `HabitScreen`
- **ViewModels**
  - Use the suffix `ViewModel`.
  - Example:
    - `HabitViewModel`
- **State Classes**
  - Use the suffix `State`.
  - Example:
    - `HabitState`
- **Effects**
  - Use the suffix `Effect`.
  - Example:
    - `HabitEffect`
- **Events / Intents**
  - Use the suffix `Event`.
  - Example:
    - `HabitEvent`
- **UI Models**
  - Use the suffix `UIModel` (e.g., `HabitItemUIModel`, `DailyLogItemUIModel`).
- **Mappers**
  - Use extension functions with explicit conversion names.
  - Example:
    - `HabitWithDone.toHabitUIState(is24HourFormat: Boolean)`

#### `di`
Contains all Hilt dependency injection modules (e.g., `AppModule.kt`) providing singletons and repository bindings.

#### `notification`
Manages scheduling of habit reminders using `AlarmManager`, broadcast receivers, and system notifications.

#### `ui/theme`
Contains the Material 3 design system configurations, including custom colors, shapes, typography, and theme definitions.

#### `utils`
Contains shared infrastructure utilities, date formatters (using `kotlinx.datetime`), and system helpers (e.g., `PermissionManager`, `DateUtil`).

#### `widgets`
Contains App Widget configurations and receivers (e.g., `AddDailyLogWidget` and `HabitDiaryWidgetManager`).

---

## AI Guidelines

- Follow a strict MVI (Model-View-Intent) architecture for all features.
- Each feature must contain its own:
  - Screen (`*Screen.kt`)
  - ViewModel (`*ViewModel.kt`)
  - State (`*State.kt`)
  - Effect (`*Effect.kt`)
  - Event / Intent (`*Event.kt`)
- Keep business logic inside ViewModels and repositories.
- UI should be driven entirely by state.
- When implementing a new feature, match the structure and patterns used by existing features.
- Prefer importing classes and functions directly; avoid using fully qualified package names within the code unless required to resolve naming conflicts.
- ViewModels should use Hilt's `@HiltViewModel` and constructor injection for dependency injection.
- Don't write unnecessary comments.
- Always keep consistent padding across the app.
- No hardcoded font sizes. Use Material Theme typography styles and adjust them if necessary.
- Avoid hardcoded strings; always define them in Android string resources (`R.string`) and access them using `stringResource()`.

### UI Guidelines
- Use Material 3 Expressive components and design patterns.
- Do not use standard `Card` components unless explicitly required by the design or matching existing checklist/stats item designs.
