---
description: Switch context to Backend development and load backend-specific rules.
---
# Backend Context Workflow
You are now operating in the **Backend Context**. Please strictly follow these instructions:
1. **Set Working Directory**: All operations, file reads, and command executions must be focused within the `./backend/` directory.
2. **Load Local Rules**: Before starting the user's task, you MUST read the localized rule files located in `./backend/.agents/rules/` to understand the specific tech stack and coding standards.
3. **Isolation**: DO NOT apply frontend concepts or access `./frontend/` files. Keep the focus strictly on the backend.
4. **Execution**: Proceed with the user's request using the loaded backend rules.