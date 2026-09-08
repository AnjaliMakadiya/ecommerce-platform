import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link as RouterLink } from 'react-router-dom';
import {
  Box,
  Grid,
  Paper,
  Typography,
  Button,
  Chip,
  IconButton,
  TextField,
  Breadcrumbs,
  Link,
} from '@mui/material';
import AddShoppingCartRoundedIcon from '@mui/icons-material/AddShoppingCartRounded';
import RemoveRoundedIcon from '@mui/icons-material/RemoveRounded';
import AddRoundedIcon from '@mui/icons-material/AddRounded';
import { useSnackbar } from 'notistack';
import productService from '../services/productService';
import Loader from '../components/Loader';
import EmptyState from '../components/EmptyState';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';

export default function ProductDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { enqueueSnackbar } = useSnackbar();
  const { addItem } = useCart();
  const { role } = useAuth();
  const canAddToCart = role === 'CUSTOMER';

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const [qty, setQty] = useState(1);

  useEffect(() => {
    setLoading(true);
    productService
      .getById(id)
      .then(({ data }) => setProduct(data))
      .catch(() => setNotFound(true))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <Loader label="Loading product…" />;
  if (notFound || !product) {
    return (
      <EmptyState
        title="Product not found"
        description="This product may have been removed or disabled."
        actionLabel="Back to shop"
        onAction={() => navigate('/products')}
      />
    );
  }

  const outOfStock = (product.stock ?? 0) <= 0;

  const handleAddToCart = () => {
    addItem(product, qty);
    enqueueSnackbar(`Added ${qty} × "${product.name}" to cart.`, { variant: 'success' });
  };

  return (
    <Box>
      <Breadcrumbs sx={{ mb: 2 }}>
        <Link component={RouterLink} to="/products" underline="hover">
          Shop
        </Link>
        <Typography color="text.primary">{product.name}</Typography>
      </Breadcrumbs>

      <Grid container spacing={4}>
        <Grid item xs={12} md={5}>
          <Paper sx={{ p: 1, border: '1px solid', borderColor: 'divider' }}>
            <Box
              component="img"
              src={product.imageUrl || 'https://placehold.co/500x500?text=No+Image'}
              alt={product.name}
              sx={{ width: '100%', borderRadius: 2, objectFit: 'cover', bgcolor: 'action.hover' }}
            />
          </Paper>
        </Grid>
        <Grid item xs={12} md={7}>
          {product.category?.name && (
            <Chip label={product.category.name} size="small" variant="outlined" sx={{ mb: 1.5 }} />
          )}
          <Typography variant="h4" sx={{ mb: 1 }}>
            {product.name}
          </Typography>
          <Typography variant="h3" sx={{ fontSize: 30, mb: 2 }}>
            ₹{Number(product.price).toLocaleString('en-IN')}
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mb: 3, whiteSpace: 'pre-line' }}>
            {product.description || 'No description provided.'}
          </Typography>

          {outOfStock ? (
            <Chip label="Out of stock" color="error" sx={{ mb: 2 }} />
          ) : (
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              {product.stock} available
            </Typography>
          )}

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
            {canAddToCart ? (
              <>
                <Box sx={{ display: 'flex', alignItems: 'center', border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
                  <IconButton size="small" onClick={() => setQty((q) => Math.max(1, q - 1))} disabled={outOfStock}>
                    <RemoveRoundedIcon fontSize="small" />
                  </IconButton>
                  <TextField
                    value={qty}
                    size="small"
                    variant="standard"
                    InputProps={{ disableUnderline: true, sx: { width: 36, textAlign: 'center' } }}
                    inputProps={{ style: { textAlign: 'center' }, readOnly: true }}
                  />
                  <IconButton
                    size="small"
                    onClick={() => setQty((q) => Math.min(product.stock ?? 999, q + 1))}
                    disabled={outOfStock}
                  >
                    <AddRoundedIcon fontSize="small" />
                  </IconButton>
                </Box>
                <Button
                  variant="contained"
                  size="large"
                  startIcon={<AddShoppingCartRoundedIcon />}
                  disabled={outOfStock}
                  onClick={handleAddToCart}
                >
                  Add to cart
                </Button>
              </>
            ) : (
              <Typography variant="body2" color="text.secondary">
                Only customer accounts can add products to cart.
              </Typography>
            )}
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
}
