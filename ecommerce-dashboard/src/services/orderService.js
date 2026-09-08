import api from '../api/axios';

// GET /api/orders -> Order[]  (ADMIN/OPERATOR only - all orders across every customer)
const getAll = () => api.get('/api/orders');

// GET /api/orders/my -> Order[]  (CUSTOMER only - their own order history)
const getMy = () => api.get('/api/orders/my');

// GET /api/orders/{id} -> Order
const getById = (id) => api.get(`/api/orders/${id}`);

// POST /api/orders  body: { items: [{ productId, quantity }] }
// customerId is NOT sent - the backend derives it from the logged-in user's JWT.
const create = (payload) => api.post('/api/orders', payload);

// PUT /api/orders/{id}/status  body: { status }
const updateStatus = (id, status) => api.put(`/api/orders/${id}/status`, { status });

// DELETE /api/orders/{id}
const remove = (id) => api.delete(`/api/orders/${id}`);

const orderService = { getAll, getMy, getById, create, updateStatus, remove };

export default orderService;
