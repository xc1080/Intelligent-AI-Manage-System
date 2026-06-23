# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

IIMS (Intelligent Information Management System) is an AI-powered integrated management platform. It includes an Electronic Educational Administration System (EAS) and a Document Management System (DMS), plus AI chat with RAG (knowledge base Q&A over documents).

## Tech Stack

| Layer | Tech |
|-------|------|
| Frontend | Vue 3 + TypeScript + Vite 7 + Pinia/Vuex + Element Plus + TailwindCSS 4 |
| Backend | Spring Boot 3.5 + Java 17 + Maven multi-module |
| ORM | MyBatis Plus + PageHelper |
| Auth | Sa-Token (Redis-backed) + JWT + RSA password encryption |
| AI | Spring AI 1.1.2 (Ollama, OpenAI, DeepSeek Chat clients) |
| Vector Store | Milvus |
| File Storage | MinIO |
| DB / Cache | MySQL 8 + Redis 7 |
| Realtime | SSE (server-sent events) for streaming AI responses |

## Dev Commands

```powershell
# Frontend (from iims-client/)
npm install        # Install dependencies
npm run dev        # Start dev server on http://localhost:8089
npm run build      # Type-check + production build (runs vue-tsc then vite build)
npm run build-only # Vite build only (skip type-check)
npm run preview    # Preview production build

# Backend (from iims-server/)
mvn -pl iims-starter -am -DskipTests package  # Package starter module as JAR
java -jar .\iims-starter\target\iims-starter-1.0.0.jar  # Run backend (port 8090)

# Infrastructure (from deploy-bundle/)
docker compose up -d                    # Start MySQL 8 + Redis 7 + MinIO
docker compose -f docker-compose-ai.yml up -d  # Start Milvus + Ollama
```

## Architecture

### Frontend (`iims-client/`)

```
src/
├── api/           # API client modules (grouped by domain: ai/, archive/, dms/, monitor/, settings/)
├── components/    # Shared components (breadcrumb, markdown editor, file upload, toc, etc.)
├── composables/   # Vue composables (utils.ts)
├── icons/         # SVG icon registration
├── layout/        # App shell (sidebar, navbar, app-main, wiki/article detail layouts)
├── router/        # Route definitions (constant + async routes)
├── store/         # Vuex stores (app, user, settings, permission)
├── styles/        # Global CSS (index.css, animate.css)
├── types/         # TypeScript ambient declarations
├── utils/         # Axios instance with interceptors, SSE client, auth helpers, validation
└── views/         # Page components by feature (ai/, system/, settings/, permission/, knowledge/, etc.)
```

Key patterns:
- **Dynamic routing**: Routes come from the backend. `store/modules/permission.ts` fetches user menus, builds route trees, and calls `router.addRoute()`. `router-guard.ts` orchestrates the flow.
- **API layer**: `utils/request.ts` creates an Axios instance with base URL from `.env`, auto-attaches the token header, and handles 401/errors globally. `utils/request-sse.ts` uses `@microsoft/fetch-event-source` for SSE streaming.
- **Auth**: RSA-encrypted login (JSEncrypt with public key from `/user/login/key`), token stored in localStorage via `utils/auth.ts`.

### Backend (`iims-server/`)

Maven multi-module project. Module dependencies flow: `starter` → `auth` + `integral` + `common` → `ai` | `archive` | `subscriber` | `search`

| Module | Responsibility |
|--------|---------------|
| `iims-starter` | Entry point (`IimsStarterApplication.java`), application config |
| `iims-module-common` | Shared entities, base classes, enums, exceptions, utils, MinIO service, Markdown helpers |
| `iims-module-integral` | Core business logic — users, roles, menus, orgs, articles, wiki, comments, dict, stats, model settings |
| `iims-module-auth` | JWT filter, Sa-Token config, auto-fill aspect, CORS, SnowFlake ID gen |
| `iims-module-ai` | AI chat (multi-model, streaming via SSE), custom ReAct agent, RAG pipeline (Milvus vector store), knowledge graph, AI tools |
| `iims-module-archive` | Archive metadata management (fond tree, multi-type archive forms, file upload/preview) |
| `iims-module-subscriber` | SSE notification system for real-time events |
| `iims-module-search` | Placeholder module (no source yet) |

Architecture patterns:
- **Controller → Service (interface + impl) → Mapper (MyBatis Plus)**: Standard layered architecture.
- **DTO/VO pattern**: Request DTOs in `model/dto/`, response VOs in `model/vo/` per module.
- **Event-driven**: `iims-module-integral` uses Spring `ApplicationEvent` for async tasks (document embedding, article read tracking, wiki doc writing) using `@Async` with a configurable thread pool.
- **AI chat pipeline**: `ChatServiceImpl` → agent selection → prompt construction → RAG context injection (Milvus similarity search) → model call → streaming SSE response. Supports OpenAI-compatible APIs, Ollama, and DeepSeek Chat.
- **RAG document pipeline**: Factory pattern (`DocumentServiceFactory`) dispatches by file type → Tika extraction → chunking → embedding → Milvus upsert.

### Infrastructure

```
deploy-bundle/
├── docker-compose.yml       # MySQL 8 + Redis 7 + MinIO
├── docker-compose-ai.yml    # Milvus + Ollama
├── sql/                     # SQL init scripts
├── nginx/                   # Nginx config for production
└── app/                     # Pre-built JAR
```

## AI Model Configuration

Model records live in `iims_ai_chat_models` table. Supports:
- **Endpoint types**: OpenAI-compatible, DeepSeek Chat, Ollama
- **Model types**: Chat, Embedding, Image
- **Config per model**: API key (AES-encrypted), base URL, model name, options (temperature, max tokens, top_p, etc.)
- **Agent system**: Custom prompt templates + model binding in `iims_ai_agent` table

Spring AI BOM version: `1.1.2`. All model starters managed in parent POM.

## Troubleshooting

- **Backend fails to start with Druid decrypt error**: Override `spring.datasource.druid.filter.config.enabled=false` in `application-dev.yml` (already configured).
- **Backend "Unable to find a suitable main class"**: Don't use `mvn spring-boot:run` at the parent POM level. Use `mvn -pl iims-starter -am -DskipTests package` then `java -jar`.
- **Routes not loading**: Check browser console for `[loadView]` logs — the view path convention is `domain/feature/Index.vue`. The permission store logs matched routes.
- **SSE streaming not working**: Ensure the `sse-emitter` endpoint is reachable. The SSE client uses `@microsoft/fetch-event-source` with auto-reconnect.
