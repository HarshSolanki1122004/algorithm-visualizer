# 🧮 Algorithm Visualizer

> A full-stack application that turns sorting and searching algorithms into a step-by-step animation — where **a Java / Spring Boot backend is the engine** that computes every step, and a thin React frontend just plays it back.

<p>
  <a href="https://github.com/HarshSolanki1122004/algorithm-visualizer/actions/workflows/ci.yml"><img alt="CI" src="https://github.com/HarshSolanki1122004/algorithm-visualizer/actions/workflows/ci.yml/badge.svg"></a>
  <img alt="Java" src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?logo=springboot&logoColor=white">
  <img alt="React" src="https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=black">
  <img alt="Vite" src="https://img.shields.io/badge/Vite-5-646CFF?logo=vite&logoColor=white">
  <img alt="Tailwind CSS" src="https://img.shields.io/badge/Tailwind-3-38BDF8?logo=tailwindcss&logoColor=white">
  <img alt="Tests" src="https://img.shields.io/badge/tests-80%2B%20passing-brightgreen">
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-blue"></a>
</p>

---

## 📖 What is this?

Most algorithm visualizers do the algorithm work **in the browser** in JavaScript. This project flips that on purpose.

The **Spring Boot backend is the star**: it implements all seven algorithms in Java and, for any input array, computes the **complete list of steps** — every comparison, swap, pivot selection, and "now sorted" marker — and returns that whole trace as JSON over a REST API.

The **React frontend is intentionally thin**: it sends an array, receives the steps, and plays them back like a flipbook (coloring numbered boxes). **It contains no algorithm logic at all.**

> **Why build it this way?** It puts the real engineering — clean object-oriented design, the strategy pattern, an extensible step-capture model, DTOs, REST API design, input validation, and a full test suite — in **Java**, which is exactly what this project is meant to demonstrate.

```
   React (thin playback screen)              Spring Boot (the engine)
  ┌──────────────────────────┐  POST /sort  ┌────────────────────────────┐
  │ pick algorithm + array   │ ───────────▶ │ runs the algorithm in Java, │
  │ press "Run"              │              │ records EVERY step          │
  │ animate the steps        │ ◀─────────── │ returns the full trace JSON │
  └──────────────────────────┘   steps[]    └────────────────────────────┘
```

---

## 📑 Table of Contents

- [Features](#-features)
- [What this project demonstrates](#-what-this-project-demonstrates)
- [Tech stack](#-tech-stack)
- [Architecture & design](#-architecture--design)
- [API reference](#-api-reference)
- [Algorithms & complexity](#-algorithms--complexity)
- [Getting started](#-getting-started)
- [Testing](#-testing)
- [Project structure](#-project-structure)
- [Key engineering decisions](#-key-engineering-decisions)
- [Roadmap](#-roadmap)
- [Author](#-author)

---

## ✨ Features

- **7 algorithms** — Bubble, Selection, Insertion, Merge, and Quick sort; Linear and Binary search.
- **True step-by-step traces** — the backend records *every* comparison, swap, pivot, and sorted marker, with a human-readable description per step.
- **Full playback controls** — play / pause, step forward / back, reset, and a 0.25×–4× speed slider.
- **Color-coded states** — comparing, swapping, pivot, sorted, and found are each a distinct color, driven entirely by the backend's per-step data.
- **Live complexity card** — best / average / worst / space complexity and stability, served by the API (the UI never hardcodes Big-O data).
- **Streaming step log** — a numbered, auto-scrolling log plus a run summary (comparisons / swaps / found).
- **Manual or random input** — type your own array or generate one (auto-sorted for binary search).
- **Robust validation & errors** — friendly, consistent HTTP 400 responses for bad input.
- **Light / dark theme** with no flash on load.
- **Interactive API docs** — Swagger UI out of the box.

> 💡 **Tip for reviewers:** add a short screen-recording GIF here (e.g. `docs/demo.gif`) — a 5-second clip of a quick sort animating is the single most effective thing on this page.
>
> ```markdown
> ![Demo](docs/demo.gif)
> ```

---

## 🎯 What this project demonstrates

| Area | Shown by |
|---|---|
| **Object-oriented design** | A `Algorithm` strategy interface implemented by seven classes; an immutable step model |
| **Design patterns** | **Strategy** (per-algorithm) + a Spring-wired **registry** — no `switch` statements anywhere |
| **Clean architecture** | Clear `controller → service → algorithm` layering with a strict domain ↔ DTO boundary |
| **REST API design** | Resource-oriented endpoints, DTOs, a documented contract, OpenAPI/Swagger |
| **Validation & error handling** | Bean Validation + semantic checks, centralized `@RestControllerAdvice` |
| **Testing** | 69 unit tests (algorithms, recorder, registry, service) + an 11-case MockMvc integration suite |
| **Frontend engineering** | React hooks + Context state management, Framer Motion animation, a design-token theme system |
| **Documentation** | Javadoc throughout, per-module READMEs, curl examples, this overview |

---

## 🛠 Tech stack

**Backend**
- Java 21
- Spring Boot 3.3 (Web, Validation)
- springdoc OpenAPI (Swagger UI)
- Lombok
- Maven
- JUnit 5 + Spring MockMvc

**Frontend**
- React 18
- Vite 5
- Tailwind CSS 3 (CSS-variable design tokens, light/dark)
- Framer Motion (animation)
- lucide-react (icons)

---

## 🏗 Architecture & design

The backend is layered so each piece has one job:

```
HTTP request
   │
   ▼
VisualizationController     ← REST endpoints, OpenAPI docs, @Valid
   │
   ▼
VisualizationService       ← validation, strategy selection, domain → DTO mapping
   │
   ▼
AlgorithmRegistry          ← Map<AlgorithmType, Algorithm>, built by Spring
   │
   ▼
Algorithm (strategy)       ← BubbleSort, QuickSort, BinarySearch, …
   │   uses
   ▼
StepRecorder → AlgorithmStep (immutable)   ← the step-capture core
```

**The step-capture core.** Every algorithm runs over a copy of the input and reports its work through a shared `StepRecorder`:

```java
recorder.recordComparison(arr, j, j + 1, "Compare arr[j] and arr[j+1]");
recorder.recordSwap(arr, j, j + 1, "Swap them");   // mutates + snapshots
recorder.markSorted(n - 1 - i);                     // accumulates sorted indices
```

The recorder produces an ordered list of **immutable `AlgorithmStep` records** and tracks the running comparison/swap counts — so the algorithms themselves stay tiny and readable.

**The strategy registry.** Each algorithm declares its own type, and Spring collects them all into one map:

```java
public AlgorithmRegistry(List<Algorithm> algorithms) {
    // Spring injects every @Component Algorithm; index them by type.
}
```

The result: **adding a new algorithm is just dropping in one `@Component`** — no controller, service, or `switch` changes required.

---

## 🔌 API reference

Base URL: `http://localhost:8080/api/visualize` · Swagger UI: `/swagger-ui.html`

### `POST /sort`

```bash
curl -X POST http://localhost:8080/api/visualize/sort \
  -H "Content-Type: application/json" \
  -d '{ "algorithm": "BUBBLE_SORT", "array": [12, 45, 3, 78, 21] }'
```

### `POST /search`

```bash
curl -X POST http://localhost:8080/api/visualize/search \
  -H "Content-Type: application/json" \
  -d '{ "algorithm": "BINARY_SEARCH", "array": [3, 12, 21, 45, 78], "target": 45 }'
```

### `GET /algorithms`

```bash
curl http://localhost:8080/api/visualize/algorithms
```

### Response shape

```json
{
  "algorithm": "BUBBLE_SORT",
  "initialArray": [12, 45, 3, 78, 21],
  "totalSteps": 47,
  "comparisons": 38,
  "swaps": 12,
  "found": null,
  "steps": [
    {
      "array": [12, 45, 3, 78, 21],
      "comparing": [0, 1],
      "description": "Compare arr[0]=12 and arr[1]=45"
    }
  ]
}
```

Each step carries only the highlight fields relevant to it — `comparing`, `swapping`, `sorted`, `pivot` for sorts; `checking`, `found`, `low`/`mid`/`high` for searches.

### Errors

Bad input returns a consistent body with HTTP 400:

```json
{
  "timestamp": "2026-06-21T10:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "binary search requires a sorted (ascending) array",
  "path": "/api/visualize/search",
  "fieldErrors": {}
}
```

---

## 📊 Algorithms & complexity

| Algorithm | Category | Best | Average | Worst | Space | Stable |
|---|---|---|---|---|---|---|
| Bubble Sort | Sorting | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Selection Sort | Sorting | O(n²) | O(n²) | O(n²) | O(1) | No |
| Insertion Sort | Sorting | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Merge Sort | Sorting | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes |
| Quick Sort | Sorting | O(n log n) | O(n log n) | O(n²) | O(log n) | No |
| Linear Search | Searching | O(1) | O(n) | O(n) | O(1) | — |
| Binary Search | Searching | O(1) | O(log n) | O(log n) | O(1) | — |

*This table is served live by `GET /algorithms` — the frontend never hardcodes it.*

---

## 🚀 Getting started

You need **JDK 21**, **Maven**, and **Node 18+**. Open two terminals.

### 1. Backend (port 8080)

```bash
cd backend
mvn spring-boot:run
```

- API: `http://localhost:8080/api/visualize`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### 2. Frontend (port 5173)

```bash
cd frontend
npm install      # first time only
npm run dev
```

Open **`http://localhost:5173`**, pick an algorithm in the sidebar, and press **Run**.

> The backend's CORS config already allows the Vite dev origin. To point the UI at a different backend, set `VITE_API_BASE`, e.g.
> `VITE_API_BASE=http://localhost:9000/api/visualize npm run dev`.

---

## 🧪 Testing

```bash
cd backend
mvn test
```

- **Unit tests** — the `StepRecorder`, every sort and search (run against many array fixtures: empty, single, reversed, duplicates, negatives), the registry, and the service's validation + mapping.
- **Integration test** — a `@SpringBootTest` + MockMvc suite that drives the real HTTP endpoints, JSON (de)serialization, validation, and the global error handler.

```bash
cd frontend
npm run build    # type-checks and bundles the frontend
```

---

## 📁 Project structure

```
AlgorithmProject/
├── backend/                                 ← Spring Boot engine (Java 21)
│   └── src/main/java/com/harsh/visualizer/
│       ├── controller/   VisualizationController (3 endpoints + OpenAPI)
│       ├── service/      VisualizationService (validation + mapping)
│       ├── algorithm/
│       │   ├── Algorithm.java          (strategy interface)
│       │   ├── StepRecorder.java       (step-capture core)
│       │   ├── AlgorithmStep.java      (immutable step model)
│       │   ├── AlgorithmRegistry.java  (strategy map)
│       │   ├── sorting/   Bubble, Selection, Insertion, Merge, Quick
│       │   └── searching/ Linear, Binary
│       ├── dto/          request/ + response/ records
│       ├── enums/        AlgorithmType (+ Big-O data), AlgorithmCategory
│       ├── exception/    GlobalExceptionHandler, ApiError
│       └── config/       CorsConfig, OpenApiConfig
│
└── frontend/                                ← React playback screen (Vite)
    └── src/
        ├── api/         visualizerApi.js     (fetch client)
        ├── context/     VisualizerContext    (shared state)
        ├── hooks/       useVisualizer        (trace + playback state machine)
        ├── components/  ArrayBox, ArrayStage, PlaybackControls, BigOCard, …
        └── lib/         stepState.js         (step → color logic)
```

---

## 💡 Key engineering decisions

- **The frontend has zero algorithm logic.** Colors, complexity data, and step descriptions all come from the backend. `stepState.js` only maps a backend field to a CSS token.
- **Strategy + registry over conditionals.** The service never asks *which* algorithm — it looks one up. New algorithms self-register via Spring.
- **Immutable domain model.** `AlgorithmStep` and `VisualizationResult` are records with defensively-copied collections, so a recorded trace can never be mutated.
- **Domain ↔ DTO separation.** Algorithms know nothing about JSON; the service maps the domain step model to the API DTOs at the boundary (and drops empty highlight arrays so the payload stays lean).
- **Validation in two layers.** Bean Validation guards the request shape; the service adds semantic rules (array size, sortedness for binary search, endpoint/category match) with messages a UI can show directly.
- **Shared frontend state via Context.** One `useVisualizer` hook powers the sidebar, canvas, and step log with no prop-drilling.

---

## 🧭 Roadmap

Ideas for extending the project:

- More algorithms (Heap sort, Shell sort, Jump/Interpolation search)
- Adjustable array size and a "compare two algorithms side by side" mode
- Persist and share a run via a permalink
- Containerize with Docker Compose for one-command startup
- Deploy (backend on Render/Fly, frontend on Vercel/Netlify)

---

## 👤 Author

**Harsh Solanki** — Java Developer

- LinkedIn: [harsh-solanki-427799261](https://www.linkedin.com/in/harsh-solanki-427799261/)
- LeetCode: [BinaryHarshh](https://leetcode.com/u/BinaryHarshh/)

## 📄 License

Released under the [MIT License](LICENSE).