import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSnackbar } from 'notistack';
import {
  Box,
  Paper,
  Typography,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  TableContainer,
  Alert,
  Skeleton,
  Stack,
} from '@mui/material';
import GroupRoundedIcon from '@mui/icons-material/GroupRounded';
import orderService from '../services/orderService';
import EmptyState from '../components/EmptyState';
import { formatDateTime } from '../utils/date';

// The API spec provided does not include a dedicated user-listing endpoint
// (only /api/auth/register and /api/auth/login). Until the Authentication
// Service exposes something like GET /api/users, this page builds a
// read-only customer directory from order activity so Admins still have a
// useful view. Swap the block below for a real userService.getAll() call
// once that endpoint exists — the rest of the page (table, loading, empty
// state) will keep working unchanged.
export default function Users() {
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    async function load() {
      setLoading(true);
      try {
        const { data } = await orderService.getAll();
        if (!cancelled) setOrders(data || []);
      } catch (err) {
        enqueueSnackbar('Could not load customer activity.', { variant: 'error' });
      } finally {
        if (!cancelled) setLoading(false);
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [enqueueSnackbar]);

  const customers = useMemo(() => {
    const map = new Map();
    orders.forEach((o) => {
      const key = o.customerId;
      if (!map.has(key)) {
        map.set(key, { customerId: key, orderCount: 0, lastActivity: o.updatedAt || o.createdAt });
      }
      const entry = map.get(key);
      entry.orderCount += 1;
      const latest = new Date(o.updatedAt || o.createdAt);
      if (latest > new Date(entry.lastActivity)) entry.lastActivity = o.updatedAt || o.createdAt;
    });
    return Array.from(map.values()).sort((a, b) => b.orderCount - a.orderCount);
  }, [orders]);

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 0.5 }}>
        Users
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
        Customer directory derived from order activity
      </Typography>

      <Alert severity="info" sx={{ mb: 2.5 }}>
        The Authentication Service doesn&apos;t yet expose a user-listing endpoint. This view is built from order
        data as a stand-in — connect <code>GET /api/users</code> here once it&apos;s available for full account details.
      </Alert>

      <Paper sx={{ p: 2 }}>
        {loading ? (
          <Stack spacing={1}>
            {Array.from({ length: 5 }).map((_, i) => (
              <Skeleton key={i} variant="rectangular" height={52} sx={{ borderRadius: 1 }} />
            ))}
          </Stack>
        ) : customers.length === 0 ? (
          <EmptyState icon={GroupRoundedIcon} title="No customer activity yet" description="Customers will appear here once orders are placed." />
        ) : (
          <TableContainer>
            <Table size="medium">
              <TableHead>
                <TableRow>
                  <TableCell>Customer ID</TableCell>
                  <TableCell>Orders placed</TableCell>
                  <TableCell>Last activity</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {customers.map((c) => (
                  <TableRow key={c.customerId} hover sx={{ cursor: 'pointer' }} onClick={() => navigate(`/orders?customerId=${c.customerId}`)}>
                    <TableCell sx={{ fontWeight: 700 }}>{c.customerId}</TableCell>
                    <TableCell>{c.orderCount}</TableCell>
                    <TableCell>{formatDateTime(c.lastActivity)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Paper>
    </Box>
  );
}
