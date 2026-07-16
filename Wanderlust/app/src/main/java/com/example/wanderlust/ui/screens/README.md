# Android source layout

```
com/example/wanderlust/
├── MainActivity.kt          # App entry / navigation host
├── WanderlustApp.kt
├── data/                    # Models, API, Room, repositories
├── navigation/              # AppScreen, AppNavigator
├── viewmodel/
├── locale/
├── util/
└── ui/
    ├── components/          # Shared UI pieces
    ├── theme/
    └── screens/             # One screen (or form) ≈ one file
        ├── auth/            # Login, Register, passwords
        ├── home/            # Splash, Welcome, Home, Tips, MainShell
        ├── tours/           # Marketplace, detail, bookings
        ├── saved/           # Saved list, add place
        ├── profile/         # Profile, edit, settings, export
        ├── info/            # About, Help, Privacy/Terms
        ├── business/        # Studio, add/edit tour
        │   └── forms/       # Trip / Rental / Tour package post forms
        └── admin/           # Admin dashboard screens
```

**Rule:** put new screens under the matching `ui/screens/...` folder. Do not dump new screens at the package root.
