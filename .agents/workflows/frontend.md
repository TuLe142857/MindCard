---
description: Switch context to Frontend development and load frontend-specific rules.
---

# Frontend Context Workflow

You are now operating in the **Frontend Context**. Please strictly follow these instructions:

1. **Set Working Directory**: All operations, file reads, and command executions must be focused within the `./frontend/` directory.
2. **Load Local Rules**: Before starting the user's task, you MUST read the localized rule files located in `./frontend/.agents/rules/` to understand the specific tech stack and coding standards.
3. **Isolation**: Keep your coding focus strictly on the frontend. You MAY access `./backend/` files in **READ-ONLY** mode exclusively to read API documentation, inspect types, or understand backend logic. For API documentation, specifically refer to `./backend/docs/API_DESC.md` and `./backend/docs/openapi.json`. DO NOT modify any backend files.
4. **Execution**: Proceed with the user's request using the loaded frontend rules.