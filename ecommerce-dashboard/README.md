# E-Commerce Platform— Order Management System

A production-ready React 19 + Vite frontend for a Spring Boot microservices backend
(Authentication, Order, and Notification services, communicating over RabbitMQ).

## Stack

- React 19, Vite
- React Router DOM (routing + protected/role-based routes)
- Axios (centralized instance, JWT interceptor, 401 auto-logout)
- React Hook Form (validation on Login/Register/Create Order)
- Material UI v6 (components + theming, light/dark mode)
- Recharts (dashboard status chart)
- notistack (toast notifications)

## Getting started

```bash
npm install
cp .env.example .env      # point VITE_API_BASE_URL at your API gateway
npm run dev
```

The app runs at `http://localhost:5173` and expects the backend at the URL set in
`VITE_API_BASE_URL` (default `http://localhost:8080`).

```bash
npm run build     # production build to dist/
npm run preview   # preview the production build locally
```

## Connecting to your backend

All calls go through `src/api/axios.js`, which:

- Prefixes every request with `VITE_API_BASE_URL`
- Attaches `Authorization: Bearer <token>` from `localStorage`
- Logs the user out and redirects to `/login?sessionExpired=1` on any `401`

Endpoints used (must match your gateway routes):

| Service | Method & Path | Notes |
|---|---|---|
| Auth | `POST /api/auth/register` | `{ username, email, password, role }` |
| Auth | `POST /api/auth/login` | `{ username, password }` → `{ token }` |
| Orders | `GET /api/orders` | List (filtered client-side by role/search/status) |
| Orders | `GET /api/orders/{id}` | Order details |
| Orders | `POST /api/orders` | `{ customerId }` |
| Orders | `PUT /api/orders/{id}/status` | `{ status }` |
| Orders | `DELETE /api/orders/{id}` | Admin only in the UI |
| Notifications | `GET /api/notifications` | |
| Notifications | `GET /api/notifications/customer/{customerId}` | |
| Notifications | `GET /api/notifications/order/{orderId}` | |

### JWT claims

The client decodes the JWT payload (no verification — that's the backend's job) to
read `sub`/`username`, `role`, `email`, and `exp`. If your token uses different claim
names, adjust `buildUserFromToken` in `src/context/AuthContext.jsx`.

## Roles & navigation

Role-based menus and route guards live in `src/utils/roles.js` and
`src/components/ProtectedRoute.jsx`.

- **ADMIN** — Dashboard, Users, Orders (full manage + delete), Notifications
- **OPERATOR** — Dashboard, Create Order, Orders (create/update status), Notifications
- **CUSTOMER** — Dashboard, My Orders (read-only), My Notifications, Profile

The `/users` route is Admin-only. It currently builds a customer directory from order
activity since the spec doesn't define a user-listing endpoint yet — swap in a real
`GET /api/users` call in `src/pages/Users.jsx` once that exists.

## Project structure

```
src/
  api/axios.js              centralized Axios instance
  components/                Sidebar, Navbar, Footer, ProtectedRoute, Loader,
                              StatusChip, RouteRail, ConfirmDialog, EmptyState
  context/                  AuthContext, ThemeModeContext
  layouts/DashboardLayout.jsx
  pages/                    Login, Register, Dashboard, Orders, OrderDetails,
                              Notifications, Profile, Users, NotFound
  services/                 authService, orderService, notificationService
  utils/                    roles.js, jwt.js, date.js
  theme.js                  MUI theme + order-status color tokens
```

## Notes

- Search, filtering, sorting, and pagination on Orders/Notifications are done
  client-side against the full `GET` list responses — swap in server-side query
  params if your services support them at scale.
- Order status colors and the sequence used by the "route rail" on Order Details
  are defined once in `src/theme.js` (`statusColors`, `ORDER_STATUS_SEQUENCE`) and
  reused everywhere for consistency.
