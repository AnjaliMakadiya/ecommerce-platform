import api from '../api/axios';

// GET /api/categories -> Category[] (public)
const getAll = () => api.get('/api/categories');

// GET /api/categories/{id} -> Category (public)
const getById = (id) => api.get(`/api/categories/${id}`);

// POST /api/categories  body: { name, description } (ADMIN only)
const create = (payload) => api.post('/api/categories', payload);

// PUT /api/categories/{id}  body: { name, description } (ADMIN only)
const update = (id, payload) => api.put(`/api/categories/${id}`, payload);

// DELETE /api/categories/{id} (ADMIN only)
const remove = (id) => api.delete(`/api/categories/${id}`);

const categoryService = { getAll, getById, create, update, remove };

export default categoryService;
