# Algorithm Visualizer — Backend

The Spring Boot **algorithm engine**. For a given input array it runs the
requested sorting or searching algorithm in Java and returns the complete
step-by-step trace — every comparison, swap, pivot, and sorted marker — as JSON.
The React frontend is only a playback screen; **all algorithm logic lives here**.

> **Status:** Backend complete (Phases B1–B5). All seven algorithms are
> implemented as step-recording strategies, wired through a validating service,
> exposed over three REST endpoints, and covered by unit + integration tests.

## Tech stack

- Java 21
- Spring Boot 3.3.x (Web, Validation)
- Springdoc OpenAPI (Swagger UI)
- Lombok
- Maven · JUnit 5

## Run

```bash
mvn spring-boot:run
```

- App: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Run the tests:

```bash
mvn test
```

## How it works

```
   React (thin screen)                 Spring Boot (the engine)
  ┌────────────────────┐   POST /sort  ┌──────────────────────────┐
  │ user picks algo +   │ ───────────▶ │ runs algorithm in Java,  │
  │ array, hits "Run"   │              │ records EVERY step        │
  │ animates the steps  │ ◀─────────── │ returns full trace (JSON) │
  └────────────────────┘   steps[]    └──────────────────────────┘
```

Each algorithm implements a small `Algorithm` strategy interface and records its
work through a shared `StepRecorder` (which also tracks comparison/swap counts
and the accumulating "sorted" set). Spring collects every strategy into an
`AlgorithmRegistry` keyed by type, so the service selects one with no `switch`
statement — adding an algorithm is just dropping in a new `@Component`.

## API

### `POST /api/visualize/sort`

```bash
curl -X POST http://localhost:8080/api/visualize/sort \
  -H "Content-Type: application/json" \
  -d '{ "algorithm": "BUBBLE_SORT", "array": [12, 45, 3, 78, 21] }'
```

### `POST /api/visualize/search`

```bash
curl -X POST http://localhost:8080/api/visualize/search \
  -H "Content-Type: application/json" \
  -d '{ "algorithm": "BINARY_SEARCH", "array": [3, 12, 21, 45, 78], "target": 45 }'
```

### `GET /api/visualize/algorithms`

```bash
curl http://localhost:8080/api/visualize/algorithms
```

Returns every supported algorithm with its Big-O metadata (the frontend's
selector and complexity cards are driven entirely by this).

### Response shape (sort / search)

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

A step carries only the highlight fields relevant to it (unused ones are omitted
from the JSON): `comparing`, `swapping`, `sorted`, `pivot` for sorts;
`checking`, `found`, `low`/`mid`/`high` for searches. `found` is `null` for
sorts and a boolean for searches.

### Errors

All errors return a consistent body with HTTP 400 for bad input:

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

Validation rules: array must hold **2–30** integers (configurable via
`visualizer.min/max-array-size`), no null elements, the algorithm must match the
endpoint's category, `target` is required for search, and binary search requires
a sorted ascending array.

## Project structure

```
com.harsh.visualizer
├── controller/   VisualizationController (the 3 endpoints + OpenAPI docs)
├── dto/
│   ├── request/  SortRequest, SearchRequest
│   └── response/ VisualizationResponse, StepDto, AlgorithmInfo
├── algorithm/    Algorithm (strategy), StepRecorder, AlgorithmStep,
│   │             VisualizationResult, AlgorithmRegistry
│   ├── sorting/  Bubble, Selection, Insertion, Merge, Quick
│   └── searching/ Linear, Binary
├── enums/        AlgorithmType (+ Big-O data), AlgorithmCategory
├── service/      VisualizationService (validation + domain→DTO mapping)
├── exception/    GlobalExceptionHandler, ApiError, InvalidInputException
└── config/       CorsConfig, OpenApiConfig
```

## Supported algorithms

| Algorithm | Category | Best | Average | Worst | Space | Stable |
|---|---|---|---|---|---|---|
| Bubble Sort | Sorting | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Selection Sort | Sorting | O(n²) | O(n²) | O(n²) | O(1) | No |
| Insertion Sort | Sorting | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Merge Sort | Sorting | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes |
| Quick Sort | Sorting | O(n log n) | O(n log n) | O(n²) | O(log n) | No |
| Linear Search | Searching | O(1) | O(n) | O(n) | O(1) | — |
| Binary Search | Searching | O(1) | O(log n) | O(log n) | O(1) | — |

## Testing

- **Unit tests** — the step recorder, each sort and search strategy (against many
  array fixtures), the registry, and the service's validation + mapping.
- **Integration test** — `@SpringBootTest` + MockMvc exercising the real HTTP
  endpoints, JSON (de)serialisation, validation, and the error handler.

```bash
mvn test
```
