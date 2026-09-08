import { useEffect, useMemo, useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import {
  Box,
  Grid,
  Paper,
  Typography,
  List,
  ListItem,
  ListItemText,
  Divider,
  Button,
  Skeleton,
} from '@mui/material';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';
import Inventory2RoundedIcon from '@mui/icons-material/Inventory2Rounded';
import HourglassBottomRoundedIcon from '@mui/icons-material/HourglassBottomRounded';
import LocalShippingRoundedIcon from '@mui/icons-material/LocalShippingRounded';
import NotificationsRoundedIcon from '@mui/icons-material/NotificationsRounded';
import { useSnackbar } from 'notistack';
import { useAuth } from '../context/AuthContext';
import orderService from '../services/orderService';
import notificationService from '../services/notificationService';
import StatusChip from '../components/StatusChip';
import EmptyState from '../components/EmptyState';
import { statusColors } from '../theme';
import { timeAgo } from '../utils/date';

function StatCard({ label, value, icon: Icon, accent, loading }) {
  return (
    <Paper sx={{ p: 2.5, height: '100%', position: 'relative', overflow: 'hidden' }}>
      <Box
        sx={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          height: 3,
          bgcolor: accent,
        }}
      />
      <Box sx={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
        <Box>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
            {label}
          </Typography>
          {loading ? (
            <Skeleton width={56} height={40} />
          ) : (
            <Typography variant="h3" sx={{ fontSize: 32 }}>
              {value}
            </Typography>
          )}
        </Box>
        <Box
          sx={{
            width: 40,
            height: 40,
            borderRadius: 2,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            bgcolor: `${accent}22`,
            color: accent,
          }}
        >
          <Icon fontSize="small" />
        </Box>
      </Box>
    </Paper>
  );
}

export default function Dashboard() {
  const { user, role } = useAuth();
  const { enqueueSnackbar } = useSnackbar();
  const [orders, setOrders] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    async function load() {
      setLoading(true);
      try {
        const [ordersRes, notifRes] = await Promise.all([
          orderService.getAll(),
          role === 'CUSTOMER' && user?.customerId
            ? notificationService.getByCustomer(user.customerId)
            : notificationService.getAll(),
        ]);
        if (cancelled) return;
        const allOrders = ordersRes.data || [];
        const scopedOrders =
          role === 'CUSTOMER' && user?.customerId
            ? allOrders.filter((o) => String(o.customerId) === String(user.customerId))
            : allOrders;
        setOrders(scopedOrders);
        setNotifications(notifRes.data || []);
      } catch (err) {
        enqueueSnackbar('Could not load dashboard data.', { variant: 'error' });
      } finally {
        if (!cancelled) setLoading(false);
      }
    }
    load();
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [role]);

  const stats = useMemo(() => {
    const total = orders.length;
    const pending = orders.filter((o) => ['CREATED', 'PROCESSING'].includes(o.status)).length;
    const delivered = orders.filter((o) => o.status === 'DELIVERED').length;
    return { total, pending, delivered, notifications: notifications.length };
  }, [orders, notifications]);

  const chartData = useMemo(() => {
    const counts = { CREATED: 0, PROCESSING: 0, SHIPPED: 0, DELIVERED: 0, CANCELLED: 0 };
    orders.forEach((o) => {
      if (counts[o.status] !== undefined) counts[o.status] += 1;
    });
    return Object.entries(counts).map(([status, count]) => ({
      status: statusColors[status].label,
      count,
      fill: statusColors[status].main,
    }));
  }, [orders]);

  const recentOrders = useMemo(
    () =>
      [...orders]
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 5),
    [orders]
  );

  const recentNotifications = useMemo(
    () =>
      [...notifications]
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 5),
    [notifications]
  );

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 0.5 }}>
        Welcome back{user?.username ? `, ${user.username}` : ''}
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        {role === 'CUSTOMER'
          ? 'Here is a summary of your orders and notifications.'
          : 'Here is what is happening across order operations today.'}
      </Typography>

      <Grid container spacing={2.5} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} lg={3}>
          <StatCard label={role === 'CUSTOMER' ? 'My Orders' : 'Total Orders'} value={stats.total} icon={Inventory2RoundedIcon} accent="#4C6FFF" loading={loading} />
        </Grid>
        <Grid item xs={12} sm={6} lg={3}>
          <StatCard label="Pending Orders" value={stats.pending} icon={HourglassBottomRoundedIcon} accent="#E8A33D" loading={loading} />
        </Grid>
        <Grid item xs={12} sm={6} lg={3}>
          <StatCard label="Delivered Orders" value={stats.delivered} icon={LocalShippingRoundedIcon} accent="#2BB673" loading={loading} />
        </Grid>
        <Grid item xs={12} sm={6} lg={3}>
          <StatCard label="Notifications" value={stats.notifications} icon={NotificationsRoundedIcon} accent="#E5484D" loading={loading} />
        </Grid>
      </Grid>

      <Grid container spacing={2.5}>
        <Grid item xs={12} lg={7}>
          <Paper sx={{ p: 2.5, height: '100%' }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              Orders by status
            </Typography>
            {loading ? (
              <Skeleton variant="rectangular" height={260} sx={{ borderRadius: 2 }} />
            ) : orders.length === 0 ? (
              <EmptyState icon={Inventory2RoundedIcon} title="No orders yet" description="Order status distribution will appear here once orders come in." />
            ) : (
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" opacity={0.15} vertical={false} />
                  <XAxis dataKey="status" tick={{ fontSize: 12 }} />
                  <YAxis allowDecimals={false} tick={{ fontSize: 12 }} />
                  <Tooltip
                    contentStyle={{ borderRadius: 8, fontSize: 13 }}
                    cursor={{ fill: 'rgba(128,128,128,0.08)' }}
                  />
                  <Bar dataKey="count" radius={[6, 6, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            )}
          </Paper>
        </Grid>

        <Grid item xs={12} lg={5}>
          <Paper sx={{ p: 2.5, height: '100%' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 1 }}>
              <Typography variant="h6">Recent orders</Typography>
              <Button component={RouterLink} to="/orders" size="small">
                View all
              </Button>
            </Box>
            {loading ? (
              <Skeleton variant="rectangular" height={220} sx={{ borderRadius: 2 }} />
            ) : recentOrders.length === 0 ? (
              <EmptyState icon={Inventory2RoundedIcon} title="No recent orders" description="New orders will show up here." />
            ) : (
              <List disablePadding>
                {recentOrders.map((o, i) => (
                  <Box key={o.orderId}>
                    <ListItem
                      component={RouterLink}
                      to={`/orders/${o.orderId}`}
                      sx={{ px: 0, textDecoration: 'none', color: 'inherit' }}
                      secondaryAction={<StatusChip status={o.status} />}
                    >
                      <ListItemText
                        primary={`Order #${o.orderId}`}
                        secondary={`Customer ${o.customerId} · ${timeAgo(o.createdAt)}`}
                      />
                    </ListItem>
                    {i < recentOrders.length - 1 && <Divider component="li" />}
                  </Box>
                ))}
              </List>
            )}
          </Paper>
        </Grid>

        <Grid item xs={12}>
          <Paper sx={{ p: 2.5 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 1 }}>
              <Typography variant="h6">Recent notifications</Typography>
              <Button component={RouterLink} to="/notifications" size="small">
                View all
              </Button>
            </Box>
            {loading ? (
              <Skeleton variant="rectangular" height={140} sx={{ borderRadius: 2 }} />
            ) : recentNotifications.length === 0 ? (
              <EmptyState icon={NotificationsRoundedIcon} title="No recent notifications" description="Order updates will be announced here." />
            ) : (
              <List disablePadding>
                {recentNotifications.map((n, i) => (
                  <Box key={n.notificationId}>
                    <ListItem sx={{ px: 0 }}>
                      <ListItemText primary={n.message} secondary={timeAgo(n.createdAt)} />
                    </ListItem>
                    {i < recentNotifications.length - 1 && <Divider component="li" />}
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
