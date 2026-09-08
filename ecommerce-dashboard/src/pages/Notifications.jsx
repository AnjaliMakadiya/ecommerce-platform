import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSnackbar } from 'notistack';
import {
  Box,
  Paper,
  Typography,
  TextField,
  InputAdornment,
  Stack,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  TableContainer,
  TablePagination,
  Skeleton,
} from '@mui/material';
import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import NotificationsRoundedIcon from '@mui/icons-material/NotificationsRounded';
import { useAuth } from '../context/AuthContext';
import notificationService from '../services/notificationService';
import EmptyState from '../components/EmptyState';
import { formatDateTime } from '../utils/date';

export default function Notifications() {
  const { role, user } = useAuth();
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();

  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [customerFilter, setCustomerFilter] = useState('');
  const [orderFilter, setOrderFilter] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const isCustomer = role === 'CUSTOMER';

  useEffect(() => {
    let cancelled = false;
    async function load() {
      setLoading(true);
      try {
        const { data } =
          isCustomer && user?.customerId
            ? await notificationService.getByCustomer(user.customerId)
            : await notificationService.getAll();
        if (!cancelled) setNotifications(data || []);
      } catch (err) {
        enqueueSnackbar('Could not load notifications.', { variant: 'error' });
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

  const filtered = useMemo(() => {
    return notifications.filter((n) => {
      const q = search.trim().toLowerCase();
      const matchesSearch = !q || (n.message || '').toLowerCase().includes(q);
      const matchesCustomer = !customerFilter || String(n.customerId) === customerFilter.trim();
      const matchesOrder = !orderFilter || String(n.orderId) === orderFilter.trim();
      return matchesSearch && matchesCustomer && matchesOrder;
    });
  }, [notifications, search, customerFilter, orderFilter]);

  const paginated = filtered.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 0.5 }}>
        {isCustomer ? 'My Notifications' : 'Notifications'}
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 2.5 }}>
        {filtered.length} of {notifications.length} notifications
      </Typography>

      <Paper sx={{ p: 2 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5} sx={{ mb: 2 }}>
          <TextField
            placeholder="Search message"
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
          {!isCustomer && (
            <TextField
              placeholder="Customer ID"
              size="small"
              sx={{ minWidth: { xs: '100%', sm: 160 } }}
              value={customerFilter}
              onChange={(e) => {
                setCustomerFilter(e.target.value);
                setPage(0);
              }}
            />
          )}
          <TextField
            placeholder="Order ID"
            size="small"
            sx={{ minWidth: { xs: '100%', sm: 160 } }}
            value={orderFilter}
            onChange={(e) => {
              setOrderFilter(e.target.value);
              setPage(0);
            }}
          />
        </Stack>

        {loading ? (
          <Stack spacing={1}>
            {Array.from({ length: 5 }).map((_, i) => (
              <Skeleton key={i} variant="rectangular" height={52} sx={{ borderRadius: 1 }} />
            ))}
          </Stack>
        ) : filtered.length === 0 ? (
          <EmptyState
            icon={NotificationsRoundedIcon}
            title="No notifications found"
            description={notifications.length === 0 ? 'Notifications will appear here as orders update.' : 'Try adjusting your search or filters.'}
          />
        ) : (
          <>
            <TableContainer>
              <Table size="medium">
                <TableHead>
                  <TableRow>
                    <TableCell>Message</TableCell>
                    {!isCustomer && <TableCell>Customer</TableCell>}
                    <TableCell>Order</TableCell>
                    <TableCell>Received</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {paginated.map((n) => (
                    <TableRow
                      key={n.notificationId}
                      hover
                      sx={{ cursor: n.orderId ? 'pointer' : 'default' }}
                      onClick={() => n.orderId && navigate(`/orders/${n.orderId}`)}
                    >
                      <TableCell>{n.message}</TableCell>
                      {!isCustomer && <TableCell>{n.customerId}</TableCell>}
                      <TableCell>{n.orderId ? `#${n.orderId}` : '—'}</TableCell>
                      <TableCell>{formatDateTime(n.createdAt)}</TableCell>
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
    </Box>
  );
}
