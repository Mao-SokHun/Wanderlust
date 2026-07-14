# Wanderlust — Navigation map

**App purpose:** Free trip **place suggestions** in Cambodia — explore, save, plan. **No booking, no payment.**

## Auth flow

| ចុច / Action | ទៅ Screen |
|-------------|-----------|
| Splash (auto ~2s) | Welcome |
| Get Started | Main → **Home** tab |
| Log In (welcome) | Login |
| Register (welcome) | Register |
| Sign In (login success) | Main → **Home** tab |
| Sign Up (login) | Register |
| Sign In (register) | Login |
| Create Account (success) | Main → **Home** tab |
| Back (login/register) | Welcome |
| Logout (profile) | Welcome |

## Main tabs (bottom nav)

| Tab | Screen |
|-----|--------|
| Home | HomeScreen |
| Explore | ExploreScreen |
| Saved | SavedScreen |
| Profile | ProfileScreen |

## From Home

| ចុច | ទៅ |
|-----|-----|
| Search bar | Explore tab |
| View all | All Destinations (list) |
| Category chip | Explore tab + filter |
| Destination card | Place Detail |
| Bottom nav | Switch tab |

## From Explore / Saved

| ចុច | ទៅ |
|-----|-----|
| Place card | Place Detail |

## Place Detail

| ចុច | ទៅ |
|-----|-----|
| ← Back / system back | Previous screen (All Destinations, or Main tab) |
| Nearby place | Another Place Detail (back returns to previous) |
| Save to my list | Main → Saved tab |

## Profile

| ចុច | ទៅ |
|-----|-----|
| Saved Plans | Saved plans screen → Saved tab |
| Settings | SettingsScreen |
| Help Center | HelpCenterScreen |
| Admin Dashboard (admin only) | Admin |
| Logout | Welcome |

## Saved Plans

| ចុច | ទៅ |
|-----|-----|
| ← Back / system back | Previous screen (Profile or Admin) |
| Saved place row | Main → Saved tab |

## Admin

| ចុច | ទៅ |
|-----|-----|
| Back | Main → Profile |
| Export Data | ExportDataScreen |
| View All (Recent Saves) | Saved plans |
| Add Tour | AddTourScreen |
| Edit Tour | EditTourScreen |
| Manage Users | ManageUsersScreen |
| Analytics | AnalyticsScreen |
