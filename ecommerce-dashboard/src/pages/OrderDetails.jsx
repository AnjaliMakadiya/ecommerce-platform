import { useEffect, useState } from 'react';
import { useNavigate, useParams, Link as RouterLink } from 'react-router-dom';
import { useSnackbar } from 'notistack';
import {
  Box,
  Paper,
  Typography,
  Grid,
  Button,
  IconButton,
  Divider,
  List,
  ListItem,
  ListItemText,
  MenuItem,
  TextField,
  Skeleton,
} from '@mui/material';
import ArrowBackRoundedIcon from '@mui/icons-material/ArrowBackRounded';
import NotificationsRoundedIcon from '@mui/icons-material/NotificationsRounded';
import RouteRail from '../components/RouteRail';
import StatusChip from '../components/StatusChip';
import EmptyState from '../components/EmptyState';
import Loader from '../components/Loader';
import { useAuth } from '../context/AuthContext';
import orderService from '../services/orderService';
import notificationService from '../services/notificationService';
import { canManageOrders } from '../utils/roles';
import { formatDateTime } from '../utils/date';
import { ORDER_STATUS_SEQUENCE } from '../theme';

const ALL_STATUSES = [...ORDER_STATUS_SEQUENCE, 'CANCELLED'];

export default function OrderDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { role } = useAuth();
  const { enqueueSnackbar } = useSnackbar();

  const [order, setOrder] = useState(null);
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [notifLoading, setNotifLoading] = useState(true);
  const [status, setStatus] = useState('');
  const [saving, setSaving] = useState(false);

  const canManage = canManageOrders(role);

  const loadOrder = async () => {
    setLoading(true);
    try {
      const { data } = await orderService.getById(id);
      setOrder(data);
      setStatus(data.status);
    } catch (err) {
      enqueueSnackbar('Could not load this order.', { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const loadNotifications = async () => {
    setNotifLoading(true);
    try {
      const { data } = await notificationService.getByOrder(id);
      setNotifications(data || []);
    } catch (err) {
      setNotifications([]);
    } finally {
      setNotifLoading(false);
    }
  };

  useEffect(() => {
    loadOrder();
    loadNotifications();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleStatusSave = async () => {
    setSaving(true);
    try {
      await orderService.updateStatus(order.orderId, status);
      enqueueSnackbar('Order status updated.', { variant: 'success' });
      loadOrder();
    } catch (err) {
      enqueueSnackbar('Could not update order status.', { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <Loader label="Loading order…" />;
  }

  if (!order) {
    return (
      <EmptyState
        title="Order not found"
        description="This order may have been removed."
        actionLabel="Back to Orders"
        onAction={() => navigate('/orders')}
      />
    );
  }

  return (
    <Box>
      <Button startIcon={<ArrowBackRoundedIcon />} onClick={() => navigate('/orders')} sx={{ mb: 2 }}>
        Back to orders
      </Button>

      <Grid container spacing={2.5}>
        <Grid item xs={12}>
          <Paper sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, alignItems: 'center', justifyContent: 'space-between', mb: 1 }}>
              <Box>
                <Typography variant="h4">Order #{order.orderId}</Typography>
                <Typography variant="body2" color="text.secondary">
                  Customer {order.customerId}
                </Typography>
              </Box>
              <StatusChip status={order.status} size="medium" />
            </Box>
            <Divider sx={{ my: 2 }} />
            <RouteRail status={order.status} />
          </Paper>
        </Grid>

        <Grid item xs={12}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              Items ({(order.items || []).length})
            </Typography>
            {(order.items || []).length === 0 ? (
              <Typography variant="body2" color="text.secondary">
                No line items recorded for this order.
              </Typography>
            ) : (
              <List disablePadding>
                {order.items.map((item, i) => (
                  <Box key={item.id ?? i}>
                    <ListItem sx={{ px: 0, py: 1.5 }}>
                      <Box
                        component="img"
                        src={item.productImageUrl || 'https://placehold.co/56x56?text=No+Image'}
                        alt={item.productName}
                        sx={{ width: 56, height: 56, borderRadius: 1.5, objectFit: 'cover', mr: 2 }}
                      />
                      <ListItemText
                        primary={item.productName}
                        secondary={`₹${Number(item.unitPrice).toLocaleString('en-IN')} × ${item.quantity}`}
                      />
                      <Typography variant="body1" fontWeight={700}>
                        ₹{Number(item.subtotal).toLocaleString('en-IN')}
                      </Typography>
                    </ListItem>
                    {i < order.items.length - 1 && <Divider component="li" />}
                  </Box>
                ))}
              </List>
            )}
            <Divider sx={{ my: 2 }} />
            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
              <Typography variant="h6">Total</Typography>
              <Typography variant="h6">₹{Number(order.totalAmount ?? 0).toLocaleString('en-IN')}</Typography>
            </Box>
          </Paper>
        </Grid>

        <Grid item xs={12} md={7}>
          <Paper sx={{ p: 3, height: '100%' }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              Order information
            </Typography>
            <Grid container spacing={2}>
              <InfoRow label="Order ID" value={`#${order.orderId}`} />
              <InfoRow label="Customer ID" value={order.customerId} />
              <InfoRow label="Status" value={<StatusChip status={order.status} />} />
              <InfoRow label="Created" value={formatDateTime(order.createdAt)} />
              <InfoRow label="Last updated" value={formatDateTime(order.updatedAt)} />
            </Grid>
          </Paper>
        </Grid>

        <Grid item xs={12} md={5}>
          <Paper sx={{ p: 3, height: '100%' }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              {canManage ? 'Update status' : 'Status'}
            </Typography>
            {canManage ? (
              <>
                <TextField select fullWidth label="Status" value={status} onChange={(e) => setStatus(e.target.value)} sx={{ mb: 2 }}>
                  {ALL_STATUSES.map((s) => (
                    <MenuItem key={s} value={s}>
                      {s.charAt(0) + s.slice(1).toLowerCase()}
                    </MenuItem>
                  ))}
                </TextField>
                <Button variant="contained" fullWidth disabled={saving || status === order.status} onClick={handleStatusSave}>
                  {saving ? 'Saving…' : 'Save changes'}
                </Button>
              </>
            ) : (
              <Typography variant="body2" color="text.secondary">
                Only operators and admins can change order status. Contact support if you need help with this order.
              </Typography>
            )}
          </Paper>
        </Grid>

        <Grid item xs={12}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ mb: 1 }}>
              Notifications for this order
            </Typography>
            {notifLoading ? (
              <Skeleton variant="rectangular" height={100} sx={{ borderRadius: 2, mt: 1 }} />
            ) : notifications.length === 0 ? (
              <EmptyState icon={NotificationsRoundedIcon} title="No notifications yet" description="Updates about this order will appear here." />
            ) : (
              <List disablePadding>
                {notifications.map((n, i) => (
                  <Box key={n.notificationId}>
                    <ListItem sx={{ px: 0 }}>
                      <ListItemText primary={n.message} secondary={formatDateTime(n.createdAt)} />
                    </ListItem>
                    {i < notifications.length - 1 && <Divider component="li" />}
                  </Box>
                ))}
              </List>
            )}
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
}

function InfoRow({ label, value }) {
  return (
    <Grid item xs={12} sm={6}>
      <Typography variant="caption" color="text.secondary">
        {label}
      </Typography>
      <Typography variant="body1" fontWeight={600}>
        {value}
      </Typography>
    </Grid>
  );
}
