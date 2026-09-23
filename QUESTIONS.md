# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

## 1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

### Answer:

Yes, I would gradually refactor the database access layer toward a more consistent repository-based approach.

Currently, the code base uses different strategies for database access and manipulation. This can make the application harder to maintain because developers need to understand multiple patterns for performing similar operations.

I would keep the domain and business logic independent from the persistence technology and use repository interfaces as the boundary between the domain and infrastructure layers. The JPA/Panache-specific implementation would remain inside the infrastructure layer.

I would also make transaction boundaries explicit at the use-case or application-service level, so that a business operation is completed atomically.

However, I would not rewrite the entire application at once. I would refactor incrementally, starting with the areas that are most frequently changed or have the highest maintenance risk. This reduces regression risk while gradually making the architecture more consistent.

---

## 2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the Warehouse API from which we generate code, but for the other endpoints - Product and Store - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

### Answer:

The OpenAPI contract-first approach provides a clearly defined API contract before implementation. It helps keep the API specification, request/response models, and generated interfaces consistent. It is especially useful when APIs are consumed by multiple teams or external clients.

The main disadvantages are additional build and generation complexity, and developers need to understand how to customize or extend generated code correctly.

The manually implemented approach used by Product and Store is simpler for small internal APIs and gives developers more direct control over the implementation. However, it can lead to API documentation and implementation becoming inconsistent over time.

For externally consumed or important APIs, I would prefer the OpenAPI contract-first approach. I would keep the generated layer focused on API contracts and delegate business logic to application/use-case classes. For smaller internal APIs, manual implementation can still be reasonable.

For consistency, I would also consider standardizing the approach across the code base as the project grows.

---

## 3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

### Answer:

I would prioritize tests based on business risk and the importance of the functionality.

First, I would create unit tests for the core business and use-case logic because these tests are fast and can cover many business rules. For the Warehouse functionality, this includes validation of duplicate business unit codes, valid locations, warehouse limits, capacity rules, stock validation, archive behavior, and replacement rules.

Second, I would add integration tests for the REST endpoints and database interactions. These tests verify that the application works correctly across multiple layers and that persistence and API behavior are integrated correctly.

I would also test negative and error scenarios because invalid input and business-rule violations are important sources of production issues.

For external integrations, such as the legacy Store system, I would specifically test transaction behavior to ensure downstream calls happen only after a successful database commit and do not happen when the transaction rolls back.

To keep coverage effective over time, I would run the tests automatically in CI for every pull request and enforce a minimum code coverage threshold. However, I would not treat the coverage percentage as the only quality metric. The tests should also cover important business scenarios and failure cases.

I would regularly review coverage when new functionality is added and add regression tests whenever a bug is discovered.