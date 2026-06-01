---
trigger: glob
globs: ./src/main/java/**/entity/**/*.java
---

# 1. Database Naming Conventions (PostgreSQL)
- **Table Names:** MUST be lowercase, snake_case, and **plural**. 
  - *Example:* `users`, `documents`, `document_categories`.
  - *Warning:* Always explicitly define the table name using `@Table(name = "...")` to avoid conflicts with PostgreSQL reserved keywords (e.g., never use a table named `user`).
- **Column Names:** MUST be lowercase and snake_case. 
  - *Example:* `user_id`, `created_at`, `file_size`.

# 2. ORM & Entity Class Rules
- **Class Naming:** Entity classes MUST use PascalCase (CamelCase) and be **singular**. 
  - *Example:* `User`, `Document`.
- **Mandatory Annotations:** Every Entity class MUST include the following Lombok annotations:
  - `@Getter` and `@Setter` (DO NOT use `@Data` on Entities to prevent StackOverflow errors in relational mappings).
  - `@NoArgsConstructor` and `@AllArgsConstructor`.
  - `@Builder`.

# 3. Field Definition Rules
- **Data Types:** ALWAYS use Java Wrapper Classes for numeric values to handle nullability in the database properly (e.g., use `Integer` instead of `int`, `Double` instead of `double`, `Boolean` instead of `boolean`).
- **Nullability:** Explicitly define whether a field can be null or not using `@Column(nullable = true/false)` for every column.
- **Default Values:** For fields that require a default value, you MUST define it using both Java and Database levels:
  - Use Lombok's `@Builder.Default` to assign the default value when instantiating via code.
  - Use Hibernate's `@ColumnDefault("value")` or `@Column(columnDefinition = "")` to set the schema default.
  - *Example:* 
```java
@Builder.Default
@ColumnDefault("0")
@Column(nullable = false)
private Integer viewCount = 0;
```
- **Timestamps:** - ALWAYS use `java.time.Instant` to store timestamps in UTC format.
  - Every entity should standardly include Audit fields: `createdAt` (annotated with `@CreationTimestamp`) and `updatedAt` (annotated with `@UpdateTimestamp`).