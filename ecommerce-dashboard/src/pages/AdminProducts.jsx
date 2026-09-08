import { useEffect, useState } from 'react';
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
  TablePagination,
  IconButton,
  Tooltip,
  Skeleton,
  Stack,
  Chip,
  Switch,
} from '@mui/material';
import DeleteRoundedIcon from '@mui/icons-material/DeleteRounded';
import Inventory2RoundedIcon from '@mui/icons-material/Inventory2Rounded';
import { useSnackbar } from 'notistack';
import productService from '../services/productService';
import ConfirmDialog from '../components/ConfirmDialog';
import EmptyState from '../components/EmptyState';

export default function AdminProducts() {
  const { enqueueSnackbar } = useSnackbar();

  const [products, setProducts] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [deleting, setDeleting] = useState(null);
  const [saving, setSaving] = useState(false);

  const fetchData = async () => {
    setLoading(true);
    try {
      const { data } = await productService.getAllForAdmin({ page, size: rowsPerPage });
      setProducts(data.content || []);
      setTotalElements(data.totalElements ?? 0);
    } catch {
      enqueueSnackbar('Could not load products.', { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, rowsPerPage]);

  const handleToggleStatus = async (product) => {
    const nextStatus = product.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE';
    try {
      await productService.updateStatus(product.id, nextStatus);
      enqueueSnackbar(`Product ${nextStatus === 'ACTIVE' ? 'enabled' : 'disabled'}.`, { variant: 'success' });
      fetchData();
    } catch {
      enqueueSnackbar('Could not update product status.', { variant: 'error' });
    }
  };

  const handleDelete = async () => {
    if (!deleting) return;
    setSaving(true);
    try {
      await productService.remove(deleting.id);
      enqueueSnackbar('Product deleted.', { variant: 'success' });
      setDeleting(null);
      fetchData();
    } catch {
      enqueueSnackbar('Could not delete product.', { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  return (
    <Box>
      <Box sx={{ mb: 2.5 }}>
        <Typography variant="h4">All Products</Typography>
        <Typography variant="body2" color="text.secondary">
          Moderate listings from every seller — {totalElements} total
        </Typography>
      </Box>

      <Paper sx={{ p: 2 }}>
        {loading ? (
          <Stack spacing={1}>
            {Array.from({ length: 6 }).map((_, i) => (
              <Skeleton key={i} variant="rectangular" height={52} sx={{ borderRadius: 1 }} />
            ))}
          </Stack>
        ) : products.length === 0 ? (
          <EmptyState icon={Inventory2RoundedIcon} title="No products found" />
        ) : (
          <>
            <TableContainer>
              <Table size="medium">
                <TableHead>
                  <TableRow>
                    <TableCell>Product</TableCell>
                    <TableCell>Seller ID</TableCell>
                    <TableCell>Category</TableCell>
                    <TableCell align="right">Price</TableCell>
                    <TableCell align="right">Stock</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {products.map((p) => (
                    <TableRow key={p.id} hover>
                      <TableCell sx={{ fontWeight: 700 }}>{p.name}</TableCell>
                      <TableCell>{p.sellerId}</TableCell>
                      <TableCell>{p.category?.name}</TableCell>
                      <TableCell align="right">₹{Number(p.price).toLocaleString('en-IN')}</TableCell>
                      <TableCell align="right">{p.stock}</TableCell>
                      <TableCell>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                          <Switch
                            size="small"
                            checked={p.status === 'ACTIVE'}
                            onChange={() => handleToggleStatus(p)}
                          />
                          <Chip
                            label={p.status}
                            size="small"
                            color={p.status === 'ACTIVE' ? 'success' : 'default'}
                            variant="outlined"
                          />
                        </Box>
                      </TableCell>
                      <TableCell align="right">
                        <Tooltip title="Delete">
                          <IconButton size="small" color="error" onClick={() => setDeleting(p)}>
                            <DeleteRoundedIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
            <TablePagination
              component="div"
              count={totalElements}
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

      <ConfirmDialog
        open={Boolean(deleting)}
        title="Delete product?"
        description={deleting ? `"${deleting.name}" will be permanently removed.` : ''}
        confirmLabel="Delete"
        loading={saving}
        onConfirm={handleDelete}
        onClose={() => setDeleting(null)}
      />
    </Box>
  );
}
