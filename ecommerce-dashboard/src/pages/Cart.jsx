import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
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
  IconButton,
  Button,
  TextField,
  Divider,
} from '@mui/material';
import DeleteRoundedIcon from '@mui/icons-material/DeleteRounded';
import RemoveShoppingCartRoundedIcon from '@mui/icons-material/RemoveShoppingCartRounded';
import { useSnackbar } from 'notistack';
import { useCart } from '../context/CartContext';
import orderService from '../services/orderService';
import EmptyState from '../components/EmptyState';

export default function Cart() {
  const { items, updateQty, removeItem, clearCart, totalItems, totalPrice } = useCart();
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();
  const [placingOrder, setPlacingOrder] = useState(false);

  const handleCheckout = async () => {
    setPlacingOrder(true);
    try {
      const payload = {
        items: items.map((i) => ({ productId: i.productId, quantity: i.qty })),
      };
      await orderService.create(payload);
      enqueueSnackbar('Order placed! Track it from the Orders page.', { variant: 'success' });
      clearCart();
      navigate('/orders');
    } catch (err) {
      const message = err?.response?.data || 'Could not place order.';
      enqueueSnackbar(typeof message === 'string' ? message : 'Could not place order.', { variant: 'error' });
    } finally {
      setPlacingOrder(false);
    }
  };

  if (items.length === 0) {
    return (
      <EmptyState
        icon={RemoveShoppingCartRoundedIcon}
        title="Your cart is empty"
        description="Browse the shop and add products to get started."
        actionLabel="Go to shop"
        onAction={() => navigate('/products')}
      />
    );
  }

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3 }}>
        Your cart ({totalItems})
      </Typography>

      <TableContainer component={Paper} sx={{ mb: 3 }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Product</TableCell>
              <TableCell align="right">Price</TableCell>
              <TableCell align="center">Qty</TableCell>
              <TableCell align="right">Subtotal</TableCell>
              <TableCell align="right" />
            </TableRow>
          </TableHead>
          <TableBody>
            {items.map((item) => (
              <TableRow key={item.productId}>
                <TableCell>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                    <Box
                      component="img"
                      src={item.imageUrl || 'https://placehold.co/60x60?text=No+Image'}
                      alt={item.name}
                      sx={{ width: 48, height: 48, borderRadius: 1.5, objectFit: 'cover' }}
                    />
                    <Typography variant="body2" fontWeight={600}>
                      {item.name}
                    </Typography>
                  </Box>
                </TableCell>
                <TableCell align="right">₹{Number(item.price).toLocaleString('en-IN')}</TableCell>
                <TableCell align="center">
                  <TextField
                    type="number"
                    size="small"
                    value={item.qty}
                    onChange={(e) => updateQty(item.productId, Number(e.target.value))}
                    inputProps={{ min: 1, max: item.stock ?? 999, style: { textAlign: 'center', width: 48 } }}
                  />
                </TableCell>
                <TableCell align="right">
                  ₹{(item.qty * Number(item.price)).toLocaleString('en-IN')}
                </TableCell>
                <TableCell align="right">
                  <IconButton size="small" color="error" onClick={() => removeItem(item.productId)}>
                    <DeleteRoundedIcon fontSize="small" />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Paper sx={{ p: 3, maxWidth: 380, ml: 'auto' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
          <Typography color="text.secondary">Items</Typography>
          <Typography>{totalItems}</Typography>
        </Box>
        <Divider sx={{ my: 1.5 }} />
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
          <Typography variant="h6">Total</Typography>
          <Typography variant="h6">₹{totalPrice.toLocaleString('en-IN')}</Typography>
        </Box>
        <Button fullWidth variant="contained" size="large" disabled={placingOrder} onClick={handleCheckout}>
          {placingOrder ? 'Placing order…' : 'Checkout'}
        </Button>
      </Paper>
    </Box>
  );
}
