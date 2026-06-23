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

## Project Structure
Overview of the directory layout and what each package contains under the main application module (`app`).

### `app`
- **`core`**: Foundational, feature-agnostic application code.
  - `/di`: Hilt dependency injection modules providing singletons and repository bindings.
  - `/notification`: Alarm scheduling, receivers, and notifications.
  - `/utils`: Shared utilities, date/time formatters, and constants.
- **`data`**: Data layer components.
  - `/local`: Local Data Source (Room Database: entities, DAOs, type converters, models for Room relations, and migrations).
  - `/mapper`: Mappers between data models.
  - `/model`: Data state models and Enums (Theme options, Sort types).
  - `/repository`: Repository interfaces and their implementations (`/impl`).
- **`presentation`**: UI and Presentation layer components.
  - Feature packages (e.g., `adddailylog`, `settings`): Screens, ViewModels, State classes, Effect classes, Event classes, and specific mappers.
  - `/common`: Shared and reusable UI components, and presentation-specific UI models.
  - `/root`: Global application state and core navigation definitions.
  - `/theme`: Material 3 design system configuration (colors, shapes, typography, theme definition).
  - `/widgets`: App Widget configurations and receivers.

---

## Development Rules & Conventions
Mandatory architectural rules, coding guidelines, and conventions that must be strictly followed.

### Layer-Specific Rules
- **Data Layer**:
  - Any mapping logic must only be placed in the `Mappers.kt` file.
- **Presentation Layer**:
    - Follow a strict MVI (Model-View-Intent) architecture for all features.
    - One-time events (navigation, toasts, snackbars) must be handled via Effect, not State.
    - Mapping from local data layer entities to UI-specific states should be performed using dedicated mapper functions.
    - Presentation mappers must be placed under `presentation/mapper`.
    - Feature-specific presentation packages must contain:
      - Screen composable (`*Screen.kt`)
      - ViewModel (`*ViewModel.kt`)
      - State class (`*State.kt`)
      - Effect class (`*Effect.kt`)
      - Event class (`*Event.kt`)
    - Shared/reusable UI components must be placed under `presentation/common/components`.
    - If any components from different features require similar UI models, they should be placed in `presentation/common/model`.
    - ViewModels should use Hilt's `@HiltViewModel` and constructor injection for dependency injection.
    - UI should be driven entirely by state.
    - UI components must only consume UI models or UI states.
    - Use Material 3 Expressive components and design patterns.
    - Do not use `Card` components unless explicitly required by the design.
    - When implementing a new feature, match the structure and patterns used by existing features.


### Naming Conventions
- **Screens**: Use the suffix `Screen` (e.g., `HabitScreen`).
- **ViewModels**: Use the suffix `ViewModel` (e.g., `HabitViewModel`).
- **State Classes**: Use the suffix `State` (e.g., `HabitState`).
- **Effects**: Use the suffix `Effect` (e.g., `HabitEffect`).
- **Events / Intents**: Use the suffix `Event` (e.g., `HabitEvent`).
- **UI Models**: Use the suffix `UIModel` (e.g., `HabitItemUIModel`, `DailyLogItemUIModel`).
- **Mappers**: Use extension functions with explicit conversion names (e.g., `HabitWithDone.toHabitUIState(is24HourFormat: Boolean)`).
- **Repository**: Use the suffix `Repository` (e.g., `HabitRepository`).
- **Repository Implementations**: Use the suffix `RepositoryImpl` (e.g., `HabitRepositoryImpl`).
- **DAOs**: Use the suffix `Dao` (e.g., `HabitDao`).
- **Entities**: Use the suffix `Entity` (e.g., `HabitEntity`).


### General Guidelines
- Don't write unnecessary comments.
- Always keep consistent padding across the app.
- No hardcoded font sizes. Use Material Theme typography styles and adjust them if necessary.
- Avoid hardcoded strings; always define them in Android string resources (`R.string`) and access them using `stringResource()`.
- Prefer importing classes and functions directly; avoid using fully qualified package names within the code unless required to resolve naming conflicts.

- Keep business logic inside ViewModels and repositories.

