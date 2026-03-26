# INFO6255 Selenium Test Automation — Group 2

Northeastern University | Spring 2026  
Course: INFO6255 – Software Quality Control

---

## Project Overview

Automated end-to-end test suite for NEU web applications built with **Selenium WebDriver**, **TestNG**, and **Maven**. The framework follows the Page Object Model pattern with data-driven testing via Excel and HTML reporting via ExtentReports.

---

## Tech Stack

| Tool | Version | Purpose |
|---|---|---|
| Java | 11 | Programming language |
| Selenium WebDriver | 4.15.0 | Browser automation |
| TestNG | 7.8.0 | Test framework |
| Maven | 3.x | Build & dependency management |
| Apache POI | 5.2.3 | Excel data reading |
| ExtentReports | 5.1.1 | HTML test reporting |

---

## Project Structure

```
Group2-selenium-automation-project/
├── src/
│   └── test/
│       ├── java/
│       │   ├── base/
│       │   │   └── BaseTest.java          # WebDriver setup/teardown
│       │   ├── pages/
│       │   │   └── LoginPage.java         # Page Object Model
│       │   ├── tests/
│       │   │   ├── Scenario1Test.java     # Scenario 1
│       │   │   ├── Scenario2Test.java     # Scenario 2 — Canvas calendar events
│       │   │   ├── Scenario3Test.java     # Scenario 3 — Snell Library booking
│       │   │   ├── Scenario4Test.java     # Scenario 4 — Dataset download (negative)
│       │   │   └── Scenario5Test.java     # Scenario 5 — Academic calendar
│       │   └── utils/
│       │       ├── ExcelReader.java       # Excel data reader
│       │       ├── ReportManager.java     # ExtentReports singleton
│       │       ├── ScreenshotHelper.java  # Before/after screenshots
│       │       └── TestUtils.java         # Utility helpers
│       └── resources/
│           ├── test_data.xlsx             # All test data (NOT committed to Git)
│           └── testng.xml                 # Test suite configuration (must be at project root)
├── reports/
│   └── TestReport.html                   # Generated HTML report
├── screenshots/                          # Auto-generated screenshots per scenario
├── pom.xml
├── testng.xml                            # ← Must be at project ROOT
└── README.md
```

---

## Test Scenarios

| # | Scenario | Type | Status |
|---|---|---|---|
| 1 | Scenario 1 | Positive | ✅ |
| 2 | Add two Event tasks on Canvas Calendar | Positive | ✅ |
| 3 | Reserve Snell Library Study Room | Positive | ✅ |
| 4 | Download a Dataset | **Negative** (must FAIL) | ✅ |
| 5 | Update Academic Calendar | Positive | ✅ |

> Scenario 4 is the intentional negative test — it is expected to fail as per requirements.

---

## Excel Test Data (`test_data.xlsx`)

The file lives at `src/test/resources/test_data.xlsx` and is **not committed to Git** (contains credentials).

### Sheet: `Login`
| username | password |
|---|---|
| your@northeastern.edu | yourpassword |

### Sheet: `CanvasEvents` (Scenario 2)
| title | date | time | end_time | location | calendar |
|---|---|---|---|---|---|
| Event Title 1 | 30 March 2026 | 14:00 | 15:00 | Snell Library Room 2 | Rugved Ajayrao Gundawar |
| Event Title 2 | 31 March 2026 | 16:00 | 17:00 | Online | Rugved Ajayrao Gundawar |

> ⚠️ All date/time cells must be formatted as **Text** in Excel to prevent auto-conversion.

### Sheet: `AcademicCalendar` (Scenario 5)
| expected_button |
|---|
| Add to My Calendar |

### Sheet: `Dataset` (Scenario 4)
| base_url | expected_wrong_text |
|---|---|
| https://onesearch.library.northeastern.edu | some_wrong_text |

### Sheet: `Login` (Scenario 3 — also uses `libraryURL`)
| username | password | libraryURL |
|---|---|---|
| your@northeastern.edu | yourpassword | https://library.northeastern.edu |

---

## Setup

### Prerequisites
- Java 11+
- Maven 3.x
- Google Chrome (latest)
- IntelliJ IDEA (recommended)

### Installation

```bash
# Clone the repository
git clone <repo-url>
cd Group2-selenium-automation-project

# Install dependencies
mvn clean install -DskipTests
```

### Add test data
Create `src/test/resources/test_data.xlsx` with the sheets described above.  
**Never commit this file** — it contains login credentials.

---

## Running Tests

### Run all scenarios
```bash
mvn clean test
```

### Run a single scenario from IntelliJ
Open the test file → click the green play button next to the `@Test` method.

### Run a specific scenario from terminal
```bash
mvn clean test -Dtest=Scenario2Test
```

---

## Two-Factor Authentication (Duo)

NEU uses Microsoft SSO with Duo 2FA. The ONE permitted manual step per scenario is **approving the Duo push on your phone**. The test will print:

```
=====================================================
  ⚠️  DUO 2FA: Please approve the push on your phone.
      Waiting up to 60 s for browser to redirect...
=====================================================
```

Approve the push and the test continues automatically.

---

## Reports & Screenshots

After each run:
- **HTML Report**: `reports/TestReport.html` — open in any browser
- **Screenshots**: `screenshots/<scenario_name>/` — before and after every step

---

## Team

Group 2 — INFO6255 Spring 2026, Northeastern University
