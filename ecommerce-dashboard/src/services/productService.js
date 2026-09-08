import api from '../api/axios';

// GET /api/products?categoryId=&keyword=&page=&size= -> Page<Product> (public, no auth needed)
const browse = ({ categoryId, keyword, page = 0, size = 12 } = {}) =>
  api.get('/api/products', { params: { categoryId, keyword, page, size } });

// GET /api/products/{id} -> Product (public)
const getById = (id) => api.get(`/api/products/${id}`);

// GET /api/products/my -> Product[] (OPERATOR only, their own listings)
const getMyProducts = () => api.get('/api/products/my');

// GET /api/products/admin/all?page=&size= -> Page<Product> (ADMIN only, all statuses)
const getAllForAdmin = ({ page = 0, size = 20 } = {}) =>
  api.get('/api/products/admin/all', { params: { page, size } });

// POST /api/products  body: { name, description, price, stock, imageUrl, categoryId } (OPERATOR)
const create = (payload) => api.post('/api/products', payload);

// PUT /api/products/{id}  body: partial product fields (OPERATOR owner / ADMIN)
const update = (id, payload) => api.put(`/api/products/${id}`, payload);

// DELETE /api/products/{id} (OPERATOR owner / ADMIN)
const remove = (id) => api.delete(`/api/products/${id}`);

// PATCH /api/products/{id}/status  body: { status: 'ACTIVE' | 'DISABLED' } (ADMIN only)
const updateStatus = (id, status) => api.patch(`/api/products/${id}/status`, { status });

const productService = { browse, getById, getMyProducts, getAllForAdmin, create, update, remove, updateStatus };

export default productService;
