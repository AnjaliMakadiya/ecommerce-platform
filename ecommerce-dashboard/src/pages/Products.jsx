import { useEffect, useMemo, useState } from 'react';
import {
  Box,
  Grid,
  Paper,
  Typography,
  TextField,
  InputAdornment,
  MenuItem,
  Pagination,
  Skeleton,
} from '@mui/material';
import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import StorefrontRoundedIcon from '@mui/icons-material/StorefrontRounded';
import { useSnackbar } from 'notistack';
import productService from '../services/productService';
import categoryService from '../services/categoryService';
import ProductCard from '../components/ProductCard';
import EmptyState from '../components/EmptyState';
import { useCart } from '../context/CartContext';

const PAGE_SIZE = 12;

export default function Products() {
  const { enqueueSnackbar } = useSnackbar();
  const { addItem } = useCart();

  const [products, setProducts] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  const [keyword, setKeyword] = useState('');
  const [debouncedKeyword, setDebouncedKeyword] = useState('');
  const [categoryId, setCategoryId] = useState('ALL');
  const [page, setPage] = useState(1); // MUI Pagination is 1-based; API is 0-based

  useEffect(() => {
    categoryService
      .getAll()
      .then(({ data }) => setCategories(data || []))
      .catch(() => enqueueSnackbar('Could not load categories.', { variant: 'error' }));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Debounce the search box so we don't hit the API on every keystroke.
  useEffect(() => {
    const timer = setTimeout(() => setDebouncedKeyword(keyword), 400);
    return () => clearTimeout(timer);
  }, [keyword]);

  useEffect(() => {
    setPage(1);
  }, [debouncedKeyword, categoryId]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    productService
      .browse({
        categoryId: categoryId === 'ALL' ? undefined : categoryId,
        keyword: debouncedKeyword || undefined,
        page: page - 1,
        size: PAGE_SIZE,
      })
      .then(({ data }) => {
        if (cancelled) return;
        setProducts(data.content || []);
        setTotalPages(data.totalPages ?? 0);
      })
      .catch(() => {
        if (!cancelled) enqueueSnackbar('Could not load products.', { variant: 'error' });
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [categoryId, debouncedKeyword, page]);

  const handleAddToCart = (product) => {
    addItem(product, 1);
    enqueueSnackbar(`Added "${product.name}" to cart.`, { variant: 'success' });
  };

  const skeletons = useMemo(() => Array.from({ length: PAGE_SIZE }), []);

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 0.5 }}>
        Shop products
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Browse the full catalog — filter by category or search by name.
      </Typography>

      <Paper sx={{ p: 2, mb: 3, display: 'flex', gap: 2, flexWrap: 'wrap' }}>
        <TextField
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          placeholder="Search products…"
          size="small"
          sx={{ flex: 1, minWidth: 220 }}
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
          label="Category"
          value={categoryId}
          onChange={(e) => setCategoryId(e.target.value)}
          sx={{ minWidth: 200 }}
        >
          <MenuItem value="ALL">All categories</MenuItem>
          {categories.map((c) => (
            <MenuItem key={c.id} value={c.id}>
              {c.name}
            </MenuItem>
          ))}
        </TextField>
      </Paper>

      {loading ? (
        <Grid container spacing={2}>
          {skeletons.map((_, i) => (
            <Grid item xs={12} sm={6} md={4} lg={3} key={i}>
              <Skeleton variant="rounded" height={300} />
            </Grid>
          ))}
        </Grid>
      ) : products.length === 0 ? (
        <EmptyState
          icon={StorefrontRoundedIcon}
          title="No products found"
          description="Try a different search term or category."
        />
      ) : (
        <>
          <Grid container spacing={2}>
            {products.map((product) => (
              <Grid item xs={12} sm={6} md={4} lg={3} key={product.id}>
                <ProductCard product={product} onAddToCart={handleAddToCart} />
              </Grid>
            ))}
          </Grid>
          {totalPages > 1 && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
              <Pagination
                count={totalPages}
                page={page}
                onChange={(_, value) => setPage(value)}
                color="primary"
              />
            </Box>
          )}
        </>
      )}
    </Box>
  );
}
