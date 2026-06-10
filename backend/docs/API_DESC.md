> [!NOTE]  
> This document defines the standard API communication for the MindCard system.
> 
> **For Frontend (Devs & AI Agents):**
> - Carefully read **Section 1** to understand the 3 standard response formats (Success, Paginated, Error) and error codes.
> - See parameter/schema details in the OpenAPI file: [./openapi.json](./openapi.json)
> 
> **For Backend (Devs & AI Agents):** When modifying an API (input, output, logic) or error codes:
> 1. YOU MUST update the changes in the **API Summary (Section 2)** (and **Section 3** if it's complex).
> 2. AI Agents MUST request the User to run the server (in the dev environment), access Swagger UI, and download/overwrite 
> the latest [./openapi.json](./openapi.json) file to keep the contract synced.
> 3. When adding or modifying error codes in [ErrorCode.java](../src/main/java/vn/edu/ptithcm/mindcard/exception/ErrorCode.java), 
> YOU MUST also update the ErrorCode table in **Section 1.1** of this document.

# Table of Content
- 1 General Rules
- 2 API Summary
- 3 API Details

---

# 1 General Rules

## 1.1 API Response

**1. Success Response**
```json
{
  "success": true,
  "data": "any",
  "message": "string | null",
  "timestamp": "timestamp in miliseconds"
}
```

**2. Paginated Response**
```json
{
  "success": true,
  "data": "list of object",
  "meta": {
    "currentPage": "int, >= 1",
    "pageSize": "int, >= 1",
    "totalItems": "long, >= 0",
    "totalPages": "long, >= 0",
    "hasNext": "boolean",
    "hasPrev": "boolean"
  },
  "timestamp": "timestamp in miliseconds"
}
```

**3. Error Response**
*(Errors are automatically caught by the Global Exception Handler from AppException and formatted like this)*  

```json
{
  "success": false,
  "errorCode": "string: error code name",
  "errorDetails": "any | null. Often used for validation errors",
  "message": "string | null",
  "timestamp": "timestamp in miliseconds"
}
```

ErrorCodes are defined in the file: [ErrorCode.java](../src/main/java/vn/edu/ptithcm/mindcard/exception/ErrorCode.java)  
Reference table for common error codes (ErrorCode) and their meanings. When developers add a new error code to [ErrorCode.java](../src/main/java/vn/edu/ptithcm/mindcard/exception/ErrorCode.java),
please update it here if it's a global error.

| HTTP Status | ErrorCode                  | Meaning                                                         |
|:------------|:---------------------------|:----------------------------------------------------------------|
| 400         | `ACTION_ALREADY_PERFORMED` | The action has already been performed                           |
| 400         | `INVALID_OTP`              | Invalid OTP (not matching or expired)                           |
| 401         | `INVALID_JWT_TOKEN`        | The provided JWT token is invalid                               |
| 401         | `JWT_TOKEN_EXPIRED`        | The provided JWT token has expired                              |
| 401         | `JWT_TOKEN_REVOKED`        | The provided JWT token has been revoked                         |
| 401         | `LOGIN_FAILED`             | Identity or password mismatch                                   |
| 401         | `UNAUTHENTICATED`          | User is not authenticated                                       |
| 403         | `FORBIDDEN`                | No permission to perform this action                            |
| 404         | `NOT_FOUND`                | General not found error                                         |
| 404         | `RESOURCE_NOT_FOUND`       | Requested resource not found                                    |
| 404         | `USER_NOT_FOUND`           | The requested user was not found                                |
| 409         | `RESOURCE_ALREADY_EXIST`   | Resource already exists (e.g., duplicate email/username)        |
| 422         | `VALIDATION_ERROR`         | Input data validation error (e.g., missing field, wrong format) |
| 500         | `FILE_UPLOAD_FAILED`       | Failed to upload file (e.g., image or audio)                    |
| 500         | `SERVER_ERROR`             | Unknown server logic error                                      |

---

# 2. API Summary

| Function                        | Method   | Endpoint                                      | Description                                                                                                              |
|:--------------------------------|:---------|:----------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------|
| **Auth**                        |          |                                               |                                                                                                                          |
| Registration Step 1/2           | `POST`   | `/api/auth/register/request`                  | Request an OTP for registration via email                                                                                |
| Registration Step 2/2           | `POST`   | `/api/auth/register/complete`                 | Verify the OTP to complete account registration                                                                          |
| Login                           | `POST`   | `/api/auth/login`                             | Log into the system, write JWT Token to cookies, and return the response                                                 |
| Logout                          | `POST`   | `/api/auth/logout`                            | Log out and add the JWT Token to the blacklist                                                                           |
| Refresh token                   | `POST`   | `/api/auth/refresh`                           | Use the refresh token to extend/get a new access token                                                                   |
| Request password reset          | `POST`   | `/api/auth/forgot_password`                   | Request an OTP to reset the password when forgotten                                                                      |
| Reset password                  | `POST`   | `/api/auth/reset_password`                    | Verify the OTP to update with a new password                                                                             |
| **User**                        |          |                                               |                                                                                                                          |
| Get personal information        | `GET`    | `/api/users/me`                               | Get the private profile of the currently logged-in user (including email)                                                |
| Update avatar                   | `PATCH`  | `/api/users/me/avatar`                        | Update the user's avatar, return the new avatar URL if successful                                                        |
| Get personal decks              | `GET`    | `/api/users/me/decks`                         | Get a list of decks created by the current user (supports searching, sorting, filtering by topic/visibility, pagination) |
| Get saved decks                 | `GET`    | `/api/users/me/saved-decks`                   | Get a list of decks saved by the current user (supports searching, sorting, pagination)                                  |
| View public profile             | `GET`    | `/api/users/{username}`                       | Get the public profile information of another user via username                                                          |
| View user's public decks        | `GET`    | `/api/users/{username}/decks`                 | Get a list of public decks of another user via username (supports searching, filtering, pagination)                      |
| **Deck**                        |          |                                               |                                                                                                                          |
| Search public decks             | `GET`    | `/api/decks`                                  | Search for public decks in the system (supports filtering by keyword, topic, sorting, pagination)                        |
| Create a new Deck               | `POST`   | `/api/decks`                                  | Create a new deck                                                                                                        |
| View Deck details               | `GET`    | `/api/decks/{deckId}`                         | Get detailed information of a Deck by its ID                                                                             |
| Update Deck info                | `PATCH`  | `/api/decks/{deckId}`                         | Update Deck information (only the owner has permission)                                                                  |
| Delete Deck                     | `DELETE` | `/api/decks/{deckId}`                         | Soft delete a Deck (only the owner has permission)                                                                       |
| Save a public Deck              | `POST`   | `/api/decks/{deckId}/save`                    | Save a public deck to the user's saved decks list                                                                        |
| Rate Deck                       | `POST`   | `/api/decks/{deckId}/rating`                  | Rate a deck (from 1 to 5 stars)                                                                                          |
| Update visibility               | `PATCH`  | `/api/decks/{deckId}/visibility`              | Change the visibility status of the Deck (PUBLIC or PRIVATE)                                                             |
| Get Cards in Deck               | `GET`    | `/api/decks/{deckId}/cards`                   | Get a list of all cards in a Deck (only the owner has permission)                                                        |
| Batch add cards to Deck         | `POST`   | `/api/decks/{deckId}/card/batch`              | Add multiple cards to a deck simultaneously via a Batch Request (Form Data)                                              |
| **Saved Deck**                  |          |                                               |                                                                                                                          |
| View Saved Deck summary         | `GET`    | `/api/saved-decks/{savedDeckId}`              | Get a summary and study progress of a Saved Deck                                                                         |
| Update Saved Deck info          | `PATCH`  | `/api/saved-decks/{savedDeckId}`              | Update the custom name or description for a Saved Deck                                                                   |
| Check for original deck updates | `GET`    | `/api/saved-decks/{savedDeckId}/sync-summary` | Compare with the original deck to count new, updated, or deleted cards                                                   |
| View detailed updates           | `GET`    | `/api/saved-decks/{savedDeckId}/sync-details` | Get a detailed diff list of unsynced cards (paginated)                                                                   |
| Sync entire original deck       | `POST`   | `/api/saved-decks/{savedDeckId}/sync`         | Sync all cards in the Saved Deck to the latest version of the original deck                                              |
| Sync specific cards             | `POST`   | `/api/saved-decks/{savedDeckId}/sync/partial` | Only sync a specified list of cards                                                                                      |
| Get study/review queue          | `GET`    | `/api/saved-decks/{savedDeckId}/cards/batch`  | Get a list of cards to study/review today (Study Queue)                                                                  |
| **Card**                        |          |                                               |                                                                                                                          |
| Update Card info                | `PATCH`  | `/api/cards/{cardId}`                         | Update the question, answer, or other information fields of a Card                                                       |
| Delete Card                     | `DELETE` | `/api/cards/{cardId}`                         | Soft delete a Card from a deck (only the owner of the deck containing the card can do this)                              |
| Rate study quality              | `POST`   | `/api/cards/{cardId}/review`                  | Record study score (0-5) to calculate the next review schedule (SuperMemo-2)                                             |
| Update card's front image       | `POST`   | `/api/cards/{cardId}/front-image`             | Update the front image of a card (send an empty file to delete)                                                          |
| Update card's front audio       | `POST`   | `/api/cards/{cardId}/front-audio`             | Update the front audio of a card (send an empty file to delete)                                                          |
| Update card's back image        | `POST`   | `/api/cards/{cardId}/back-image`              | Update the back image of a card (send an empty file to delete)                                                           |
| Update card's back audio        | `POST`   | `/api/cards/{cardId}/back-audio`              | Update the back audio of a card (send an empty file to delete)                                                           |
| **Topic**                       |          |                                               |                                                                                                                          |
| Get all topics                  | `GET`    | `/api/topics`                                 | Get a list of all topics supported by the system                                                                         |
| **Health**                      |          |                                               |                                                                                                                          |
| System health check             | `GET`    | `/api/health`                                 | Check if the backend server is running normally                                                                          |

---

# 3. API Details

*(The template structure below is used to describe APIs with complex business logic in detail; 
for basic CRUD operations, you can skip this and simply check openapi.json/Swagger UI)*

## `[Domain] Endpoint Name`
- **Method:** `GET|POST|PUT|DELETE`
- **Endpoint:** `/api/domain/...`
- **Description:** Briefly explain the purpose and any special logic of the API (if any).
- **Permissions:** `Public` / `Authenticated` / `Admin`
- **Request Parameters / Body:**
  - `field_name` (Type, Required/Optional): Describe the meaning of the field.
- **Response Success:**
  - Returns the `XxxResponseDTO` object.
- **Possible Errors (AppException):**
  - `ERROR_CODE_1`: Describe the conditions under which this error occurs.
  - `ERROR_CODE_2`: Describe the conditions under which this error occurs.
