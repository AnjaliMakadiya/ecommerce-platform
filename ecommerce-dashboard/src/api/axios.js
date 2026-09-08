import axios from 'axios';

// Base URL for the API gateway in front of the Spring Boot microservices.
// Override via .env -> VITE_API_BASE_URL, e.g. http://localhost:8080
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Attach JWT automatically on every request.
api.interceptors.request.use(
  (config) => {
    
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// A listener the AuthContext registers so this module (which cannot use hooks)
// can trigger a logout + redirect when the backend reports 401.
let onUnauthorized = () => {};
export const registerUnauthorizedHandler = (handler) => {
  onUnauthorized = handler;
};

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      onUnauthorized();
    }
    return Promise.reject(error);
  }
);

export default api;
