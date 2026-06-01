---
trigger: always_on
---

# Monorepo Workspace Structure

This workspace is a Monorepo containing both the Frontend and Backend applications. 
**CRITICAL:** You MUST always be aware of the directory context you are operating in to apply the correct tech stack and localized rules. Never mix dependencies or architectural patterns between the two environments.

## 1. Backend Context
- **Root Directory:** `./backend/`
- **Rules Directory:** `./backend/.agents/rules/`
- **Behavior:** When reading, creating, or modifying files inside the `./backend/` directory, you MUST strictly follow the localized rules defined in its specific `.agents/rules/` folder. DO NOT apply frontend concepts here.

## 2. Frontend Context
- **Root Directory:** `./frontend/`
- **Rules Directory:** `./frontend/.agents/rules/`
- **Behavior:** When reading, creating, or modifying files inside the `./frontend/` directory, you MUST strictly follow the localized rules defined in its specific `.agents/rules/` folder. DO NOT apply backend concepts here.