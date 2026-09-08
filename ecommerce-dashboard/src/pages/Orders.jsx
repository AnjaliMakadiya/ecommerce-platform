import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSnackbar } from 'notistack';
import {
  Box,
  Paper,
  Typography,
  TextField,
  InputAdornment,
  MenuItem,
  Button,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  TableContainer,
  TablePagination,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Skeleton,
  Tooltip,
  Stack,
} from '@mui/material';
import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import StorefrontRoundedIcon from '@mui/icons-material/StorefrontRounded';
import VisibilityRoundedIcon from '@mui/icons-material/VisibilityRounded';
import EditRoundedIcon from '@mui/icons-material/EditRounded';
import DeleteRoundedIcon from '@mui/icons-material/DeleteRounded';
import Inventory2RoundedIcon from '@mui/icons-material/Inventory2Rounded';
import { useAuth } from '../context/AuthContext';
import orderService from '../services/orderService';
import StatusChip from '../components/StatusChip';
import EmptyState from '../components/EmptyState';
import ConfirmDialog from '../components/ConfirmDialog';
import { canDeleteOrders } from '../utils/roles';
import { formatDateTime } from '../utils/date';
import { ORDER_STATUS_SEQUENCE } from '../theme';

const ALL_STATUSES = [...ORDER_STATUS_SEQUENCE, 'CANCELLED'];

export default function Orders() {
  const { role } = useAuth();
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();

  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const [editOrder, setEditOrder] = useState(null);
  const [deleteOrder, setDeleteOrder] = useState(null);
  const [saving, setSaving] = useState(false);

  // Only operators/admins can change an order's fulfillment status. Order CREATION is
  // customer-only and happens via Cart checkout, not from this page - see Cart.jsx.
  const canUpdateStatus = role === 'ADMIN' || role === 'OPERATOR';
  const canDelete = canDeleteOrders(role);
  const isCustomer = role === 'CUSTOMER';

  const fetchOrders = async () => {
    setLoading(true);
    try {
      // Customers see only their own orders (/my); admins/operators see every order.
      const { data } = isCustomer ? await orderService.getMy() : await orderService.getAll();
      setOrders(data || []);
    } catch (err) {
      enqueueSnackbar('Could not load orders.', { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [role]);

  const filtered = useMemo(() => {
    return orders.filter((o) => {
      const matchesStatus = statusFilter === 'ALL' || o.status === statusFilter;
      const q = search.trim().toLowerCase();
      const matchesSearch =
        !q ||
        String(o.orderId).includes(q) ||
        String(o.customerId).includes(q) ||
        (o.status || '').toLowerCase().includes(q);
      return matchesStatus && matchesSearch;
    });
  }, [orders, search, statusFilter]);

  const paginated = filtered.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);

  const handleDelete = async () => {
    if (!deleteOrder) return;
    setSaving(true);
    try {
      await orderService.remove(deleteOrder.orderId);
      enqueueSnackbar(`Order #${deleteOrder.orderId} deleted.`, { variant: 'success' });
      setDeleteOrder(null);
      fetchOrders();
    } catch (err) {
      enqueueSnackbar('Could not delete order.', { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, alignItems: 'center', justifyContent: 'space-between', mb: 2.5 }}>
        <Box>
          <Typography variant="h4">{isCustomer ? 'My Orders' : 'Orders'}</Typography>
          <Typography variant="body2" color="text.secondary">
            {filtered.length} of {orders.length} orders
          </Typography>
        </Box>
        {isCustomer && (
          <Button variant="contained" startIcon={<StorefrontRoundedIcon />} onClick={() => navigate('/products')}>
            Shop products
          </Button>
        )}
      </Box>

      <Paper sx={{ p: 2 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5} sx={{ mb: 2 }}>
          <TextField
            placeholder="Search by order ID, customer ID, or status"
            size="small"
            fullWidth
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPage(0);
            }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchRoundedIcon fontSize="small" />
                </InputAdornment>
              ),
            }}
          />
          <TextField
            select
            size="small"
            label="Status"
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value);
              setPage(0);
            }}
            sx={{ minWidth: { xs: '100%', sm: 200 } }}
          >
            <MenuItem value="ALL">All statuses</MenuItem>
            {ALL_STATUSES.map((s) => (
              <MenuItem key={s} value={s}>
                {s.charAt(0) + s.slice(1).toLowerCase()}
              </MenuItem>
            ))}
          </TextField>
        </Stack>

        {loading ? (
          <Stack spacing={1}>
            {Array.from({ length: 5 }).map((_, i) => (
              <Skeleton key={i} variant="rectangular" height={52} sx={{ borderRadius: 1 }} />
            ))}
          </Stack>
        ) : filtered.length === 0 ? (
          <EmptyState
            icon={Inventory2RoundedIcon}
            title="No orders found"
            description={
              orders.length === 0
                ? isCustomer
                  ? "You haven't placed any orders yet."
                  : 'No orders have been placed yet.'
                : 'Try adjusting your search or filter.'
            }
            actionLabel={isCustomer && orders.length === 0 ? 'Shop products' : undefined}
            onAction={isCustomer && orders.length === 0 ? () => navigate('/products') : undefined}
          />
        ) : (
          <>
            <TableContainer>
              <Table size="medium">
                <TableHead>
                  <TableRow>
                    <TableCell>Order ID</TableCell>
                    <TableCell>Customer</TableCell>
                    <TableCell>Items</TableCell>
                    <TableCell align="right">Total</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Created</TableCell>
                    <TableCell align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {paginated.map((o) => (
                    <TableRow key={o.orderId} hover sx={{ cursor: 'pointer' }} onClick={() => navigate(`/orders/${o.orderId}`)}>
                      <TableCell sx={{ fontWeight: 700 }}>#{o.orderId}</TableCell>
                      <TableCell>{o.customerId}</TableCell>
                      <TableCell>{(o.items || []).length} item{(o.items || []).length === 1 ? '' : 's'}</TableCell>
                      <TableCell align="right">₹{Number(o.totalAmount ?? 0).toLocaleString('en-IN')}</TableCell>
                      <TableCell>
                        <StatusChip status={o.status} />
                      </TableCell>
                      <TableCell>{formatDateTime(o.createdAt)}</TableCell>
                      <TableCell align="right" onClick={(e) => e.stopPropagation()}>
                        <Tooltip title="View details">
                          <IconButton size="small" onClick={() => navigate(`/orders/${o.orderId}`)}>
                            <VisibilityRoundedIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        {canUpdateStatus && (
                          <Tooltip title="Update status">
                            <IconButton size="small" onClick={() => setEditOrder(o)}>
                              <EditRoundedIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        )}
                        {canDelete && (
                          <Tooltip title="Delete order">
                            <IconButton size="small" color="error" onClick={() => setDeleteOrder(o)}>
                              <DeleteRoundedIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
            <TablePagination
              component="div"
              count={filtered.length}
              page={page}
              onPageChange={(_, p) => setPage(p)}
              rowsPerPage={rowsPerPage}
              onRowsPerPageChange={(e) => {
                setRowsPerPage(parseInt(e.target.value, 10));
                setPage(0);
              }}
              rowsPerPageOptions={[5, 10, 25, 50]}
            />
          </>
        )}
      </Paper>

      {canUpdateStatus && (
        <EditStatusDialog order={editOrder} onClose={() => setEditOrder(null)} onSaved={() => { setEditOrder(null); fetchOrders(); }} />
      )}

      <ConfirmDialog
        open={Boolean(deleteOrder)}
        title="Delete this order?"
        description={deleteOrder ? `Order #${deleteOrder.orderId} will be permanently removed. This cannot be undone.` : ''}
        confirmLabel="Delete"
        loading={saving}
        onConfirm={handleDelete}
        onClose={() => setDeleteOrder(null)}
      />
    </Box>
  );
}

function EditStatusDialog({ order, onClose, onSaved }) {
  const { enqueueSnackbar } = useSnackbar();
  const [status, setStatus] = useState(order?.status || 'CREATED');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (order) setStatus(order.status);
  }, [order]);

  const handleSave = async () => {
    if (!order) return;
    setSubmitting(true);
    try {
      await orderService.updateStatus(order.orderId, status);
      enqueueSnackbar(`Order #${order.orderId} updated to ${status}.`, { variant: 'success' });
      onSaved();
    } catch (err) {
      enqueueSnackbar('Could not update order status.', { variant: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={Boolean(order)} onClose={onClose} fullWidth maxWidth="xs">
      <DialogTitle>Update order status</DialogTitle>
      <DialogContent>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Order #{order?.orderId} · Customer {order?.customerId}
        </Typography>
        <TextField select label="Status" fullWidth value={status} onChange={(e) => setStatus(e.target.value)}>
          {ALL_STATUSES.map((s) => (
            <MenuItem key={s} value={s}>
              {s.charAt(0) + s.slice(1).toLowerCase()}
            </MenuItem>
          ))}
        </TextField>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2.5 }}>
        <Button onClick={onClose} disabled={submitting}>
          Cancel
        </Button>
        <Button variant="contained" onClick={handleSave} disabled={submitting}>
          {submitting ? 'Saving…' : 'Save changes'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
