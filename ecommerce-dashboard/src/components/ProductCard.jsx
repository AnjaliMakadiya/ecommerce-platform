import { Card, CardActionArea, CardContent, CardMedia, Typography, Box, Button, Chip } from '@mui/material';
import AddShoppingCartRoundedIcon from '@mui/icons-material/AddShoppingCartRounded';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProductCard({ product, onAddToCart }) {
  const navigate = useNavigate();
  const { role } = useAuth();
  const outOfStock = (product.stock ?? 0) <= 0;
  const canAddToCart = role === 'CUSTOMER';

  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', border: '1px solid', borderColor: 'divider' }}>
      <CardActionArea onClick={() => navigate(`/products/${product.id}`)} sx={{ flex: '0 0 auto' }}>
        <CardMedia
          component="img"
          image={product.imageUrl || 'https://placehold.co/400x400?text=No+Image'}
          alt={product.name}
          sx={{ height: 160, objectFit: 'cover', bgcolor: 'action.hover' }}
        />
      </CardActionArea>
      <CardContent sx={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 0.75 }}>
        <Typography
          variant="subtitle2"
          fontWeight={700}
          sx={{
            cursor: 'pointer',
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden',
          }}
          onClick={() => navigate(`/products/${product.id}`)}
        >
          {product.name}
        </Typography>
        {product.category?.name && (
          <Chip label={product.category.name} size="small" variant="outlined" sx={{ alignSelf: 'flex-start' }} />
        )}
        <Box sx={{ flex: 1 }} />
        <Typography variant="h6" sx={{ fontSize: 18 }}>
          ₹{Number(product.price).toLocaleString('en-IN')}
        </Typography>
        {outOfStock ? (
          <Chip label="Out of stock" color="error" size="small" variant="outlined" sx={{ alignSelf: 'flex-start' }} />
        ) : (
          <Typography variant="caption" color="text.secondary">
            {product.stock} in stock
          </Typography>
        )}
        {canAddToCart && (
          <Button
            fullWidth
            size="small"
            variant="contained"
            startIcon={<AddShoppingCartRoundedIcon />}
            disabled={outOfStock}
            onClick={(e) => {
              e.stopPropagation();
              onAddToCart(product);
            }}
          >
            Add to cart
          </Button>
        )}
      </CardContent>
    </Card>
  );
}
