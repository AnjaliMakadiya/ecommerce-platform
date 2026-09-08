import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import {
  Box,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  MenuItem,
  CircularProgress,
  Stack,
} from '@mui/material';

export default function ProductFormDialog({ open, onClose, onSubmit, saving, categories, initialValues }) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({ mode: 'onBlur', defaultValues: { name: '', description: '', price: '', stock: '', imageUrl: '', categoryId: '' } });

  useEffect(() => {
    if (open) {
      reset(
        initialValues
          ? {
              name: initialValues.name,
              description: initialValues.description || '',
              price: initialValues.price,
              stock: initialValues.stock,
              imageUrl: initialValues.imageUrl || '',
              categoryId: initialValues.category?.id ?? '',
            }
          : { name: '', description: '', price: '', stock: '', imageUrl: '', categoryId: categories[0]?.id ?? '' }
      );
    }
  }, [open, initialValues, categories, reset]);

  const submit = (values) => {
    onSubmit({
      ...values,
      price: Number(values.price),
      stock: Number(values.stock),
      categoryId: Number(values.categoryId),
    });
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{initialValues ? 'Edit product' : 'Add product'}</DialogTitle>
      <Box component="form" id="product-form" onSubmit={handleSubmit(submit)} noValidate>
        <DialogContent>
          <Stack spacing={2}>
            <TextField
              label="Product name"
              fullWidth
              {...register('name', { required: 'Name is required' })}
              error={Boolean(errors.name)}
              helperText={errors.name?.message}
            />
            <TextField
              label="Description"
              fullWidth
              multiline
              minRows={3}
              {...register('description')}
            />
            <Stack direction="row" spacing={2}>
              <TextField
                label="Price (₹)"
                type="number"
                fullWidth
                {...register('price', { required: 'Price is required', min: { value: 0, message: 'Must be 0 or more' } })}
                error={Boolean(errors.price)}
                helperText={errors.price?.message}
              />
              <TextField
                label="Stock"
                type="number"
                fullWidth
                {...register('stock', { required: 'Stock is required', min: { value: 0, message: 'Must be 0 or more' } })}
                error={Boolean(errors.stock)}
                helperText={errors.stock?.message}
              />
            </Stack>
            <TextField
              label="Image URL"
              fullWidth
              placeholder="https://…"
              {...register('imageUrl')}
            />
            <TextField
              select
              label="Category"
              fullWidth
              {...register('categoryId', { required: 'Category is required' })}
              error={Boolean(errors.categoryId)}
              helperText={errors.categoryId?.message}
            >
              {categories.map((c) => (
                <MenuItem key={c.id} value={c.id}>
                  {c.name}
                </MenuItem>
              ))}
            </TextField>
          </Stack>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={onClose} disabled={saving}>
            Cancel
          </Button>
          <Button
            type="submit"
            form="product-form"
            variant="contained"
            disabled={saving}
            startIcon={saving ? <CircularProgress size={16} color="inherit" /> : null}
          >
            {initialValues ? 'Save changes' : 'Add product'}
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
  );
}
