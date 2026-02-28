# Helpdesk Frontend Generation Contract

Build a production-style frontend app for this Helpdesk backend API.

## Tech Constraints
- Framework: React + TypeScript
- Build: Vite
- Routing: React Router
- HTTP: Axios
- State: React Query (TanStack Query) + local auth store (Context or Zustand)
- UI: clean responsive layout (mobile + desktop)
- No mock data; always call real API
- Use env var for base URL: `VITE_API_BASE_URL` (default `http://localhost:8080`)

## Auth Model
- JWT Bearer auth
- Public endpoints:
  - `POST /api/auth/register`
  - `POST /api/auth/login`
- All other `/api/**` endpoints require `Authorization: Bearer <token>`
- Persist token in localStorage and restore on refresh
- Auto-attach token with Axios interceptor
- On `401/403`: clear auth and redirect to login

## API Contracts

### 1) Register
- `POST /api/auth/register`
- Request:
```json
{
  "username": "agent1",
  "email": "agent1@example.com",
  "password": "Password123!"
}
```
- Response:
```json
{
  "accessToken": "jwt-token",
  "tokenType": "Bearer"
}
```

### 2) Login
- `POST /api/auth/login`
- Request:
```json
{
  "username": "agent1",
  "password": "Password123!"
}
```
- Response: same as register

### 3) Users
- `POST /api/users`
```json
{
  "name": "John Doe",
  "email": "john@example.com"
}
```
- `GET /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
```json
{
  "name": "John Updated",
  "email": "john.updated@example.com"
}
```
- `DELETE /api/users/{id}`
- User response:
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com"
}
```

### 4) Tickets
- `POST /api/tickets`
```json
{
  "title": "Unable to login",
  "description": "Details...",
  "userId": 1,
  "status": "OPEN"
}
```
- `GET /api/tickets` (optional query: `?userId=1`)
- `GET /api/tickets/{id}`
- `PUT /api/tickets/{id}` (full payload required, including `userId`)
- `DELETE /api/tickets/{id}`

Ticket response:
```json
{
  "id": 10,
  "title": "Unable to login",
  "description": "Details...",
  "status": "OPEN",
  "userId": 1,
  "userName": "John Doe",
  "comments": [
    {
      "id": 101,
      "commentText": "Investigating",
      "createdBy": "support.agent",
      "createdAt": "2026-02-27T14:00:00Z"
    }
  ]
}
```

### 5) Ticket Comments
- `POST /api/tickets/{id}/comments`
```json
{
  "commentText": "Investigating logs",
  "createdBy": "support.agent"
}
```
- `DELETE /api/tickets/{id}/comments/{commentId}`

### 6) Audit Logs by Ticket
- `GET /api/tickets/{ticketId}/audit-logs`
- Response item:
```json
{
  "id": 25,
  "entityName": "Ticket",
  "entityId": "8",
  "parentEntityName": null,
  "parentEntityId": null,
  "operation": "UPDATE",
  "changedAt": "2026-02-27T14:53:26Z",
  "actor": "agent1",
  "data": {
    "user": {
      "old": { "id": 1, "name": "Alice" },
      "new": { "id": 2, "name": "Bob" }
    }
  }
}
```

## Required Pages
1. Login page
2. Register page
3. Dashboard shell with navbar + logout
4. Users page (list + create + edit + delete)
5. Tickets page (list + create + edit + delete + filter by user)
6. Ticket details page:
   - show ticket fields
   - list/add/delete comments
   - show audit timeline (`/audit-logs`)

## UX Requirements
- Form validation + inline errors
- Loading, empty, and error states on every page
- Toast/snackbar for success/failure
- Confirm before delete
- Accessibility basics (labels, keyboard focus, ARIA where needed)

## Code Quality Requirements
- Strong TypeScript types for all API contracts
- Centralized API client module
- Reusable components (table, modal/form, status badge)
- Modular feature-based folders
- Include README with run instructions:
  - `npm install`
  - `npm run dev`
  - required `.env`

---

## Amendment v2: Ticket Activity Feed + Comments in Ticket Details

Implement 2 missing features:

1. Ticket-specific Audit Activity Feed (side panel on Ticket Details)
2. Add Comment flow inside Ticket Details

### Backend Endpoints (already available)
- `GET /api/tickets/{ticketId}`
- `POST /api/tickets/{ticketId}/comments`
- `DELETE /api/tickets/{ticketId}/comments/{commentId}`
- `GET /api/tickets/{ticketId}/audit-logs`

All endpoints above are protected and require:
`Authorization: Bearer <accessToken>`

### Required UI Changes

#### A) Ticket Details layout
- Desktop: two-column layout
  - Left/main: ticket details + comments section
  - Right/side: activity feed (audit logs)
- Mobile: stacked layout (details/comments first, feed after)

#### B) Comments section in Ticket Details
- Render existing comments from ticket response (`comments[]`)
- Add comment form:
  - `commentText` required
  - `createdBy` optional (default to logged-in username if available)
- Submit: `POST /api/tickets/{id}/comments`
- Delete comment with confirmation: `DELETE /api/tickets/{id}/comments/{commentId}`
- After add/delete:
  - refresh ticket details
  - refresh audit feed
  - show success/error toast

#### C) Activity Feed (ticket-specific)
- Source: `GET /api/tickets/{ticketId}/audit-logs`
- Render as timeline cards:
  - `changedAt`
  - `actor`
  - `operation`
  - `entityName`
  - readable `data` diff/snapshot
- UI should sort newest-first
- Empty state: `No activity yet for this ticket.`

### Updated FE Types

```ts
type TicketComment = {
  id: number;
  commentText: string;
  createdBy: string;
  createdAt: string;
};

type Ticket = {
  id: number;
  title: string;
  description: string;
  status: "OPEN" | "IN_PROGRESS" | "RESOLVED" | "CLOSED";
  userId: number;
  userName: string;
  comments: TicketComment[];
};

type CreateTicketCommentRequest = {
  commentText: string;
  createdBy?: string;
};

type AuditLogItem = {
  id: number;
  entityName: string;
  entityId: string;
  parentEntityName: string | null;
  parentEntityId: string | null;
  operation: "CREATE" | "UPDATE" | "DELETE";
  changedAt: string;
  actor: string;
  data: Record<string, unknown> | null;
};
```

### API Client Additions
- `getTicketById(ticketId: number): Promise<Ticket>`
- `addTicketComment(ticketId: number, payload: CreateTicketCommentRequest): Promise<Ticket>`
- `deleteTicketComment(ticketId: number, commentId: number): Promise<Ticket>`
- `getTicketAuditLogs(ticketId: number): Promise<AuditLogItem[]>`

### React Query Behavior
- `ticketDetail` key: `['ticket', ticketId]`
- `ticketAuditLogs` key: `['ticket-audit-logs', ticketId]`
- After add/delete comment success: invalidate both keys

### UX Requirements
- Loading skeletons for detail + feed
- Inline validation for comment form
- Disable submit while request in flight
- Confirm before delete
- Independent error states for comments and feed

### Definition of Done
- Ticket details page shows comments and supports add/delete
- Right-side activity feed displays audit logs for selected ticket
- Feed refreshes after ticket/comment mutations
- No auth/CORS regressions
- New code remains strongly typed (no `any`)

---

## Amendment v3: Admin Section + Priority/Impact Configuration

Implement backend-aligned amendments for Priority/Impact and introduce a dedicated Admin section.

### Goal
Add a new Admin area where Priority and Impact are configured, and update Ticket flows to support these fields.

### Scope
1. New Admin section in app navigation
2. Priority management UI (CRUD)
3. Impact management UI (CRUD)
4. Ticket create/edit/details/list should include Priority + Impact
5. Keep existing auth/JWT behavior unchanged

### API Contract Updates

#### Priority
- `POST /api/priorities`
```json
{
  "name": "P1",
  "description": "Critical priority",
  "active": true
}
```
- `GET /api/priorities`
- `GET /api/priorities/{id}`
- `PUT /api/priorities/{id}`
- `DELETE /api/priorities/{id}`

Response:
```json
{
  "id": 1,
  "name": "P1",
  "description": "Critical priority",
  "active": true
}
```

#### Impact
- `POST /api/impacts`
```json
{
  "name": "High",
  "description": "High business impact",
  "active": true
}
```
- `GET /api/impacts`
- `GET /api/impacts/{id}`
- `PUT /api/impacts/{id}`
- `DELETE /api/impacts/{id}`

Response:
```json
{
  "id": 1,
  "name": "High",
  "description": "High business impact",
  "active": true
}
```

#### Ticket request update
`POST/PUT /api/tickets...` now supports:
```json
{
  "title": "Issue",
  "description": "Details",
  "userId": 1,
  "priorityId": 1,
  "impactId": 1,
  "status": "OPEN"
}
```
`priorityId` and `impactId` are optional (nullable).

#### Ticket response update
```json
{
  "id": 10,
  "title": "Issue",
  "description": "Details",
  "status": "OPEN",
  "userId": 1,
  "userName": "John Doe",
  "priorityId": 1,
  "priorityName": "P1",
  "impactId": 1,
  "impactName": "High",
  "comments": []
}
```

### Required FE Changes

#### 1) Navigation / IA
Add an `Admin` section in primary nav.
Under `Admin`, add:
- `Priorities`
- `Impacts`

Routing example:
- `/admin/priorities`
- `/admin/impacts`

#### 2) Admin - Priorities page
Build full CRUD page:
- Table columns: `name`, `description`, `active`, actions
- Create modal/form
- Edit modal/form
- Delete with confirm
- Toggle/display active state

#### 3) Admin - Impacts page
Mirror Priority page behavior:
- Table + create/edit/delete
- fields: `name`, `description`, `active`

#### 4) Ticket Forms
Update both Create Ticket and Edit Ticket forms:
- Add dropdown/select for `Priority`
- Add dropdown/select for `Impact`
- Load options from:
  - `GET /api/priorities`
  - `GET /api/impacts`
- Include `priorityId` and `impactId` in payload
- Allow “None” option (send null / omit)

#### 5) Ticket List + Details
Display priority/impact in:
- Ticket list row/cards
- Ticket details header/body

Fallback display:
- If null: show `-` or `None`

#### 6) Data Layer Additions
Define types:
```ts
type Priority = {
  id: number;
  name: string;
  description?: string | null;
  active: boolean;
};

type Impact = {
  id: number;
  name: string;
  description?: string | null;
  active: boolean;
};

type Ticket = {
  id: number;
  title: string;
  description: string;
  status: "OPEN" | "IN_PROGRESS" | "RESOLVED" | "CLOSED";
  userId: number;
  userName: string;
  priorityId?: number | null;
  priorityName?: string | null;
  impactId?: number | null;
  impactName?: string | null;
  comments: TicketComment[];
};
```

API client methods:
- `getPriorities()`
- `createPriority(payload)`
- `updatePriority(id, payload)`
- `deletePriority(id)`
- `getImpacts()`
- `createImpact(payload)`
- `updateImpact(id, payload)`
- `deleteImpact(id)`

#### 7) Query Keys
Suggested React Query keys:
- `['priorities']`
- `['impacts']`
- `['tickets']`
- `['ticket', ticketId]`

Invalidate on mutations:
- Priority/Impact mutations -> invalidate their lists
- Ticket create/update -> invalidate tickets + ticket detail

#### 8) Validation & UX
- `name` required for Priority/Impact
- Show loading/empty/error states on Admin pages
- Show toast/snackbar for create/update/delete
- Keep forms keyboard-accessible

#### 9) Security/Headers
All new admin and ticket calls remain protected:
- send `Authorization: Bearer {{accessToken}}`

### Definition of Done
- Admin section appears with Priority and Impact pages
- Priority/Impact CRUD works end-to-end
- Ticket create/edit supports selecting priority/impact
- Ticket list/details show priority/impact values
- No regressions in auth, comments, or audit feed
- Type-safe implementation (avoid `any`)

---

## Amendment v4: Admin Audit Logs Screen

Implement a dedicated Admin Audit Logs screen for admin-config entities (`Priority`, `Impact`).

### Objective
Add a new Admin Audit Logs page that reads from the backend admin-audit endpoint and supports entity filtering.

### New Backend API
- `GET /api/admin/audit-logs`
  - returns audit logs for all admin-tagged entities.
- `GET /api/admin/audit-logs?entityName=Priority`
- `GET /api/admin/audit-logs?entityName=Impact`

Auth requirement:
- Protected endpoint, must send `Authorization: Bearer <accessToken>`.

Invalid `entityName` returns `400`.

### Frontend Changes Required

#### 1) Navigation and routing
Add new route under Admin:
- `/admin/audit-logs`

Admin nav should include:
- Priorities
- Impacts
- Audit Logs

#### 2) Admin Audit Logs page
Build a page with:
- entity filter dropdown: `All`, `Priority`, `Impact`
- optional refresh action
- audit feed/table area

Filter behavior:
- `All` -> `GET /api/admin/audit-logs`
- `Priority` -> `GET /api/admin/audit-logs?entityName=Priority`
- `Impact` -> `GET /api/admin/audit-logs?entityName=Impact`

#### 3) Data rendering
For each row/card, display:
- `changedAt`
- `actor`
- `entityName`
- `entityId`
- `operation` (`CREATE`, `UPDATE`, `DELETE`)
- `data` payload in readable format

Rendering guidance for `data`:
- for `UPDATE`, show field-level `old -> new` values
- for nested objects (e.g., `{id,name}`), prefer `name (id)` display
- fallback: expandable raw JSON

Sort display: newest first.

#### 4) Types
```ts
type AdminAuditEntityName = "Priority" | "Impact";

type AuditLogItem = {
  id: number;
  entityName: string;
  entityId: string;
  parentEntityName: string | null;
  parentEntityId: string | null;
  operation: "CREATE" | "UPDATE" | "DELETE";
  changedAt: string;
  actor: string;
  data: Record<string, unknown> | null;
};
```

#### 5) API client additions
```ts
getAdminAuditLogs(entityName?: "Priority" | "Impact"): Promise<AuditLogItem[]>
```

Implementation:
- without param -> no query string
- with param -> append `entityName`

#### 6) React Query behavior
Suggested query key:
- `['admin-audit-logs', entityName ?? 'ALL']`

Invalidate this query after successful Priority/Impact create/update/delete.

#### 7) UX requirements
- loading state
- empty state: `No admin audit activity yet.`
- error state with retry
- preserve selected filter in local state (or URL query if app uses that pattern)
- responsive layout consistent with existing Admin pages

#### 8) Security and errors
- ensure JWT interceptor applies
- keep global 401/403 handling
- handle 400 gracefully (defensive)

### Definition of Done
- `/admin/audit-logs` route exists and is linked from Admin nav
- entity filter works and calls correct backend endpoint
- priority/impact audit entries render correctly
- CRUD changes in Priority/Impact are visible after refresh/invalidation
- no regressions in existing Admin, Ticket, Comments, or Ticket Audit features

---

## Amendment v5: Ticket Edit with Priority/Impact + Admin Update APIs

Use existing backend update APIs and include Priority/Impact on Ticket Edit screen.

### Existing Backend APIs

#### Priority
- `GET /api/priorities`
- `PUT /api/priorities/{id}`

#### Impact
- `GET /api/impacts`
- `PUT /api/impacts/{id}`

#### Ticket
- `GET /api/tickets/{id}`
- `PUT /api/tickets/{id}`

Ticket update payload supports:
```json
{
  "title": "Issue title",
  "description": "Issue details",
  "userId": 1,
  "priorityId": 1,
  "impactId": 1,
  "status": "IN_PROGRESS"
}
```
`priorityId` and `impactId` can be null/omitted for “None”.

### FE Work Required

#### 1) Ticket Edit screen
Update Ticket Edit form to include:
- Priority dropdown
- Impact dropdown

Populate options from:
- `GET /api/priorities`
- `GET /api/impacts`

Behavior:
- preselect current values from ticket (`priorityId`, `impactId`)
- include “None” option
- on submit call `PUT /api/tickets/{id}` with full payload:
  - `title`, `description`, `userId`, `status`, `priorityId`, `impactId`

#### 2) Validation
Keep required validation for:
- `title`
- `description`
- `userId`

`priorityId` and `impactId` remain optional.
Disable submit while request is in-flight.

#### 3) Admin edit screens
Ensure Admin pages use update APIs:
- Priority edit -> `PUT /api/priorities/{id}`
- Impact edit -> `PUT /api/impacts/{id}`

#### 4) Data model updates
Ensure Ticket type includes:
```ts
type Ticket = {
  id: number;
  title: string;
  description: string;
  status: "OPEN" | "IN_PROGRESS" | "RESOLVED" | "CLOSED";
  userId: number;
  userName: string;
  priorityId?: number | null;
  priorityName?: string | null;
  impactId?: number | null;
  impactName?: string | null;
  comments: TicketComment[];
};
```

#### 5) Query invalidation
After successful ticket update:
- invalidate `['ticket', ticketId]`
- invalidate `['tickets']`
- optional: invalidate ticket audit query if shown nearby

After successful priority/impact update:
- invalidate `['priorities']` / `['impacts']`
- invalidate ticket list/detail if showing priority/impact names

#### 6) UX expectations
- loading states while fetching ticket/options
- inline form errors
- success/error toast on update
- responsive and keyboard-accessible form controls

### Definition of Done
- Ticket Edit includes priority + impact and persists values correctly.
- Priority/Impact Admin edit screens use PUT endpoints correctly.
- Updated values appear in ticket list/details without manual hard refresh.

---

## Amendment v6: SLA Rules Admin + Ticket SLA Display

Implement SLA rules as a new admin configuration and reflect assigned SLA on tickets.

### Backend support now available

#### SLA Rule APIs
- `POST /api/sla-rules`
- `GET /api/sla-rules`
- `GET /api/sla-rules/{id}`
- `PUT /api/sla-rules/{id}`
- `DELETE /api/sla-rules/{id}`

`Create/Update SLA Rule` payload:
```json
{
  "name": "High Priority SLA",
  "description": "SLA for high priority tickets",
  "priorityId": 1,
  "responseTimeMinutes": 30,
  "resolutionTimeMinutes": 240,
  "sortOrder": 10,
  "active": true
}
```

`SLA Rule` response:
```json
{
  "id": 1,
  "name": "High Priority SLA",
  "description": "SLA for high priority tickets",
  "priorityId": 1,
  "priorityName": "High",
  "responseTimeMinutes": 30,
  "resolutionTimeMinutes": 240,
  "sortOrder": 10,
  "active": true
}
```

### Ticket contract update
Ticket response now includes assigned SLA:
```json
{
  "slaRuleId": 1,
  "slaRuleName": "High Priority SLA"
}
```

Assignment behavior (backend):
- SLA auto-assigned on ticket create/update based on ticket priority.
- Match strategy: first active rule by `sortOrder` then `id`.
- If no matching active SLA rule for the ticket priority, ticket SLA is null.

### FE changes required

#### 1) Admin nav and route
Add a new admin route:
- `/admin/sla-rules`

Admin nav now should include:
- Priorities
- Impacts
- SLA Rules
- Audit Logs

#### 2) SLA Rules admin page
Build full CRUD page for SLA rules:
- Table columns:
  - `name`
  - `priorityName`
  - `responseTimeMinutes`
  - `resolutionTimeMinutes`
  - `sortOrder`
  - `active`
  - actions
- Create/Edit form fields:
  - name (required)
  - description
  - priority select (required; source: priorities list)
  - responseTimeMinutes (required, positive)
  - resolutionTimeMinutes (required, positive)
  - sortOrder (optional, default 100)
  - active
- Delete with confirmation

#### 3) Ticket UI updates
Display SLA on:
- Ticket list (column/tag)
- Ticket details panel
- Ticket edit/read view summaries

Display fallback when null:
- `No SLA`

#### 4) Types
```ts
type SlaRule = {
  id: number;
  name: string;
  description?: string | null;
  priorityId: number;
  priorityName: string;
  responseTimeMinutes: number;
  resolutionTimeMinutes: number;
  sortOrder: number;
  active: boolean;
};

type Ticket = {
  id: number;
  title: string;
  description: string;
  status: "OPEN" | "IN_PROGRESS" | "RESOLVED" | "CLOSED";
  userId: number;
  userName: string;
  priorityId?: number | null;
  priorityName?: string | null;
  impactId?: number | null;
  impactName?: string | null;
  slaRuleId?: number | null;
  slaRuleName?: string | null;
  comments: TicketComment[];
};
```

#### 5) API client additions
Add methods:
- `getSlaRules(active?: boolean)`
- `createSlaRule(payload)`
- `updateSlaRule(id, payload)`
- `deleteSlaRule(id)`

Use JWT interceptor for all calls.

#### 6) Query keys and invalidation
Suggested keys:
- `['sla-rules', active ?? 'ALL']`
- `['tickets']`
- `['ticket', ticketId]`

Invalidate after SLA rule mutations:
- `['sla-rules', ...]`
- `['tickets']`
- relevant ticket detail queries (SLA assignment can change after ticket updates)

#### 7) Validation and UX
- enforce positive ints for response/resolution minutes
- show loading/empty/error states
- success/error toasts
- responsive layout, keyboard accessible forms

### Definition of Done
- Admin SLA Rules page works with full CRUD.
- SLA name appears on ticket list/details.
- FE understands nullable SLA for unmatched tickets.
- No regression to existing Priority/Impact, ticket audit, or comments features.
