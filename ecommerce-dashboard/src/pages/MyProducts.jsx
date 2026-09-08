import { useEffect, useState } from 'react';
import {
  Box,
  Paper,
  Typography,
  Button,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  TableContainer,
  IconButton,
  Tooltip,
  Skeleton,
  Stack,
  Chip,
} from '@mui/material';
import AddRoundedIcon from '@mui/icons-material/AddRounded';
import EditRoundedIcon from '@mui/icons-material/EditRounded';
import DeleteRoundedIcon from '@mui/icons-material/DeleteRounded';
import Inventory2RoundedIcon from '@mui/icons-material/Inventory2Rounded';
import { useSnackbar } from 'notistack';
import productService from '../services/productService';
import categoryService from '../services/categoryService';
import ProductFormDialog from '../components/ProductFormDialog';
import ConfirmDialog from '../components/ConfirmDialog';
import EmptyState from '../components/EmptyState';

export default function MyProducts() {
  const { enqueueSnackbar } = useSnackbar();

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [deleting, setDeleting] = useState(null);
  const [saving, setSaving] = useState(false);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [{ data: productData }, { data: categoryData }] = await Promise.all([
        productService.getMyProducts(),
        categoryService.getAll(),
      ]);
      setProducts(productData || []);
      setCategories(categoryData || []);
    } catch {
      enqueueSnackbar('Could not load your products.', { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSubmit = async (values) => {
    setSaving(true);
    try {
      if (editing) {
        await productService.update(editing.id, values);
        enqueueSnackbar('Product updated.', { variant: 'success' });
      } else {
        await productService.create(values);
        enqueueSnackbar('Product added.', { variant: 'success' });
      }
      setFormOpen(false);
      setEditing(null);
      fetchData();
    } catch (err) {
      enqueueSnackbar(err?.response?.data?.error || 'Could not save product.', { variant: 'error' });
    } finally {
      setSaving(false);
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
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, alignItems: 'center', justifyContent: 'space-between', mb: 2.5 }}>
        <Box>
          <Typography variant="h4">My Products</Typography>
          <Typography variant="body2" color="text.secondary">
            {products.length} listing{products.length === 1 ? '' : 's'}
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddRoundedIcon />}
          onClick={() => {
            setEditing(null);
            setFormOpen(true);
          }}
        >
          Add Product
        </Button>
      </Box>

      <Paper sx={{ p: 2 }}>
        {loading ? (
          <Stack spacing={1}>
            {Array.from({ length: 4 }).map((_, i) => (
              <Skeleton key={i} variant="rectangular" height={52} sx={{ borderRadius: 1 }} />
            ))}
          </Stack>
        ) : products.length === 0 ? (
          <EmptyState
            icon={Inventory2RoundedIcon}
            title="No products yet"
            description="Add your first product to start selling."
            actionLabel="Add Product"
            onAction={() => setFormOpen(true)}
          />
        ) : (
          <TableContainer>
            <Table size="medium">
              <TableHead>
                <TableRow>
                  <TableCell>Product</TableCell>
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
                    <TableCell>{p.category?.name}</TableCell>
                    <TableCell align="right">₹{Number(p.price).toLocaleString('en-IN')}</TableCell>
                    <TableCell align="right">{p.stock}</TableCell>
                    <TableCell>
                      <Chip
                        label={p.status}
                        size="small"
                        color={p.status === 'ACTIVE' ? 'success' : 'default'}
                        variant="outlined"
                      />
                    </TableCell>
                    <TableCell align="right">
                      <Tooltip title="Edit">
                        <IconButton
                          size="small"
                          onClick={() => {
                            setEditing(p);
                            setFormOpen(true);
                          }}
                        >
                          <EditRoundedIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
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
        )}
      </Paper>

      <ProductFormDialog
        open={formOpen}
        onClose={() => {
          setFormOpen(false);
          setEditing(null);
        }}
        onSubmit={handleSubmit}
        saving={saving}
        categories={categories}
        initialValues={editing}
      />

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
