# SkyCast — Requirements Specification

As of October 7, 2026

## Overview and objectives

SkyCast is an Android weather app that shows current conditions, a 7-day forecast and charts for any city, using the free Open-Meteo API. It is an individual class project due Sunday, October 11, 2026, and must include at least two activities, a fragment and data visualization.

Objectives:

1. Show accurate current weather and a 7-day forecast for any city the user searches for.
2. Present weather data visually with three charts: 24-hour temperature, 7-day high/low, and chance of rain.
3. Let users save favorite cities and switch between them in one tap.
4. Keep working without internet by showing the last saved forecast.
5. Follow the SDLC model and document each phase in the final report.

## SDLC model

SkyCast follows the Waterfall model with feedback loops. The requirements are fixed by the assignment, the deadline is five days away, and the report's sections already match Waterfall's phases. A defect found in testing loops back to development before the next phase starts.

- [x] 1. Analysis and requirements — Wed, Oct 7
- [x] 2. Alternative solutions and design — Wed, Oct 7
- [ ] 3. Development — Thu, Oct 8 to Fri, Oct 9
- [ ] 4. Integration and testing — Sat, Oct 10
- [ ] 5. Implementation on a real device, fixing issues found — Sat, Oct 10
- [ ] 6. Evaluation, PowerPoint report and submission — Sun, Oct 11

## Users and stakeholders

| Who | Role | What they need |
| --- | --- | --- |
| App user | Primary actor | Quick, readable weather for their city and saved cities |
| Open-Meteo API | External system | Receives forecast and city-search requests over HTTPS |
| Instructor | Grader | Working app that meets the required components, plus the report |
| Developer (student) | Builds and tests the app | Clear requirements to build and test against |

## Functional requirements

Thirteen requirements make up the core app; FR-14 and FR-15 are stretch goals if time allows.

| ID | The app shall… | Priority | Screen |
| --- | --- | --- | --- |
| FR-01 | Show current weather for the selected city: temperature, feels-like, description, icon, humidity, wind, sunrise and sunset | Must | Today |
| FR-02 | Show a line chart of temperature for the next 24 hours | Must | Today |
| FR-03 | Show a line chart of daily high and low temperatures for 7 days | Must | Forecast |
| FR-04 | Show a bar chart of daily chance of rain (0–100%) for 7 days | Must | Forecast |
| FR-05 | List the 7 days with day name, icon, description and high/low | Must | Forecast |
| FR-06 | Let the user search cities by name and pick one from the results | Must | Search |
| FR-07 | Let the user save or unsave the current city as a favorite | Should | Today |
| FR-08 | List favorite cities; tapping one loads it, a delete button removes it | Should | Favorites |
| FR-09 | Let the user choose °F + mph or °C + km/h, and remember the choice | Should | Settings |
| FR-10 | Reopen on the last city viewed (New York on first launch) | Should | All |
| FR-11 | Refresh the weather on demand (pull down or Refresh) | Should | Today |
| FR-12 | When offline, show the last saved forecast with its "last updated" time | Should | Today, Forecast |
| FR-13 | Show clear messages for no internet, empty search and no cities found | Must | All |
| FR-14 | Use the device's location to find the nearest city | Could | Today |
| FR-15 | Send a daily weather notification | Could | Background |

## Non-functional requirements

| ID | Category | Requirement | How it's measured |
| --- | --- | --- | --- |
| NFR-01 | Performance | Weather appears within 3 seconds of opening the app or picking a city | Timed on Wi-Fi and mobile data |
| NFR-02 | Responsiveness | Network and database work never runs on the UI thread | No freezes or "App not responding" dialogs during testing |
| NFR-03 | Reliability | No crash when offline, when the API fails, or when the phone rotates | Each case tested on a device |
| NFR-04 | Usability | Every feature is reachable within 2 taps of the Today tab | Walk-through of each feature |
| NFR-05 | Compatibility | Runs on Android 6.0 (API 23) and newer, in portrait and landscape | Tested on the emulator and a real phone |
| NFR-06 | Security and privacy | HTTPS only, no API key, no account; only the Internet and network-state permissions | Manifest review |
| NFR-07 | Maintainability | Code split into data, ui, view and util packages, each class commented | Code review |
| NFR-08 | Accessibility | Icons have content descriptions, body text is at least 14sp, and charts have readable labels | Checked with TalkBack and large font size |
| NFR-09 | Storage | At most one cached forecast per city, replaced on each successful refresh | Database inspection |

## Use cases

Seven use cases cover all the core requirements; the use-case diagram is in `usecase.puml`. The App user starts every use case; the Open-Meteo API supplies the data for UC-01 to UC-03.

| ID | Use case | Main flow | Requirements |
| --- | --- | --- | --- |
| UC-01 | View current weather | Open the app → Today tab loads the last city's weather and 24-hour chart | FR-01, FR-02, FR-10 |
| UC-02 | View forecast | Open the Forecast tab → see the high/low chart, rain chart and daily list | FR-03, FR-04, FR-05 |
| UC-03 | Search for a city | Tap Search → type a name → tap a result → Today shows that city | FR-06, FR-13 |
| UC-04 | Manage favorites | Tap Save on Today; on Favorites, tap a city to load it or delete it | FR-07, FR-08 |
| UC-05 | Change units | Open Settings → pick °F/mph or °C/km/h → back → weather reloads | FR-09 |
| UC-06 | Refresh weather | Pull down on Today → fresh data replaces the old | FR-11 |
| UC-07 | View offline data | Network fails → the last saved forecast shows with an offline banner | FR-12, FR-13 |

Diagrams: `usecase.puml` (use cases), `screen_flow.puml` (navigation), `architecture.puml` (system diagram).

## Constraints and assumptions

The build setup is copied from the working RecipeApp project, so no new libraries can be added and every feature uses what Android and the old support libraries already provide.

| Constraint | Effect on the design |
| --- | --- |
| Java only, build files identical to RecipeApp (Android Gradle Plugin 3.1.0, compileSdk 23, support libraries 23.0.1) | No Kotlin, AndroidX or Material Components |
| No new dependencies | HttpURLConnection + org.json for the API, SQLiteOpenHelper for storage |
| No chart library | A custom View draws the line and bar charts with Canvas |
| Design library 23.0.1 has no bottom navigation bar | TabLayout + ViewPager switch between the three fragments |
| APIs limited to Android 6.0 (API 23) | No java.time; dates parsed with SimpleDateFormat |
| Due Sunday, Oct 11 | Stretch goals FR-14 and FR-15 only if the core is done by Saturday |

Assumptions:

- The Open-Meteo free API stays available and needs no key.
- The phone has internet the first time each city is loaded; after that, cached data covers offline use.
- The version numbers above come from the original Deitel project; RecipeApp's actual files take precedence if they differ.

## Assignment coverage and acceptance

SkyCast exceeds each required component: three activities, three fragments and three charts.

| Assignment requirement | How SkyCast meets it |
| --- | --- |
| At least two activities | MainActivity, SearchActivity, SettingsActivity |
| A fragment | TodayFragment, ForecastFragment, FavoritesFragment |
| Data visualization | 24-hour temperature line chart, 7-day high/low line chart, rain-chance bar chart |
| Follow the SDLC model | Waterfall phases above, each documented in the PowerPoint report |
| More functionality = more points | City search, favorites, unit settings, offline cache, refresh, error handling |

The app is accepted when every Must requirement passes its test case on a real device, and no NFR-03 crash case occurs. Each requirement gets one row in the test report (ID, steps, expected result, actual result, pass/fail).
