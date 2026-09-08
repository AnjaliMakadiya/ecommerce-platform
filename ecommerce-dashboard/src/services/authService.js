import api from '../api/axios';

// POST /api/auth/register
// body: { username, email, password, role }
const register = (payload) => api.post('/api/auth/register', payload);

// POST /api/auth/login
// body: { username, password } -> { token }
const login = (payload) => api.post('/api/auth/login', payload);

// NEW: exchange a Keycloak access token for our own app JWT
const socialLogin = (accessToken) =>
  api.post('/api/auth/social-login', { accessToken });

const authService = { register, login, socialLogin };

export default authService;
