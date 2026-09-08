import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  CircularProgress,
} from '@mui/material';
import AddRoundedIcon from '@mui/icons-material/AddRounded';
import EditRoundedIcon from '@mui/icons-material/EditRounded';
import DeleteRoundedIcon from '@mui/icons-material/DeleteRounded';
import CategoryRoundedIcon from '@mui/icons-material/CategoryRounded';
import { useSnackbar } from 'notistack';
import categoryService from '../services/categoryService';
import ConfirmDialog from '../components/ConfirmDialog';
import EmptyState from '../components/EmptyState';

function CategoryFormDialog({ open, onClose, onSubmit, saving, initialValues }) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({ mode: 'onBlur', defaultValues: { name: '', description: '' } });

  useEffect(() => {
    if (open) reset(initialValues ? { name: initialValues.name, description: initialValues.description || '' } : { name: '', description: '' });
  }, [open, initialValues, reset]);

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>{initialValues ? 'Edit category' : 'Add category'}</DialogTitle>
      <Box component="form" id="category-form" onSubmit={handleSubmit(onSubmit)} noValidate>
        <DialogContent>
          <Stack spacing={2}>
            <TextField
              label="Name"
              fullWidth
              autoFocus
              {...register('name', { required: 'Name is required' })}
              error={Boolean(errors.name)}
              helperText={errors.name?.message}
            />
            <TextField label="Description" fullWidth multiline minRows={2} {...register('description')} />
          </Stack>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={onClose} disabled={saving}>
            Cancel
          </Button>
          <Button
            type="submit"
            form="category-form"
            variant="contained"
            disabled={saving}
            startIcon={saving ? <CircularProgress size={16} color="inherit" /> : null}
          >
            {initialValues ? 'Save changes' : 'Add category'}
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
  );
}

export default function Categories() {
  const { enqueueSnackbar } = useSnackbar();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [deleting, setDeleting] = useState(null);
  const [saving, setSaving] = useState(false);

  const fetchData = async () => {
    setLoading(true);
    try {
      const { data } = await categoryService.getAll();
      setCategories(data || []);
    } catch {
      enqueueSnackbar('Could not load categories.', { variant: 'error' });
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
        await categoryService.update(editing.id, values);
        enqueueSnackbar('Category updated.', { variant: 'success' });
      } else {
        await categoryService.create(values);
        enqueueSnackbar('Category added.', { variant: 'success' });
      }
      setFormOpen(false);
      setEditing(null);
      fetchData();
    } catch {
      enqueueSnackbar('Could not save category.', { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!deleting) return;
    setSaving(true);
    try {
      await categoryService.remove(deleting.id);
      enqueueSnackbar('Category deleted.', { variant: 'success' });
      setDeleting(null);
      fetchData();
    } catch {
      enqueueSnackbar('Could not delete category — it may still have products assigned.', { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, alignItems: 'center', justifyContent: 'space-between', mb: 2.5 }}>
        <Typography variant="h4">Categories</Typography>
        <Button
          variant="contained"
          startIcon={<AddRoundedIcon />}
          onClick={() => {
            setEditing(null);
            setFormOpen(true);
          }}
        >
          Add Category
        </Button>
      </Box>

      <Paper sx={{ p: 2 }}>
        {loading ? (
          <Stack spacing={1}>
            {Array.from({ length: 4 }).map((_, i) => (
              <Skeleton key={i} variant="rectangular" height={52} sx={{ borderRadius: 1 }} />
            ))}
          </Stack>
        ) : categories.length === 0 ? (
          <EmptyState
            icon={CategoryRoundedIcon}
            title="No categories yet"
            actionLabel="Add Category"
            onAction={() => setFormOpen(true)}
          />
        ) : (
          <TableContainer>
            <Table size="medium">
              <TableHead>
                <TableRow>
                  <TableCell>Name</TableCell>
                  <TableCell>Description</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {categories.map((c) => (
                  <TableRow key={c.id} hover>
                    <TableCell sx={{ fontWeight: 700 }}>{c.name}</TableCell>
                    <TableCell>{c.description}</TableCell>
                    <TableCell align="right">
                      <Tooltip title="Edit">
                        <IconButton
                          size="small"
                          onClick={() => {
                            setEditing(c);
                            setFormOpen(true);
                          }}
                        >
                          <EditRoundedIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Delete">
                        <IconButton size="small" color="error" onClick={() => setDeleting(c)}>
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

      <CategoryFormDialog
        open={formOpen}
        onClose={() => {
          setFormOpen(false);
          setEditing(null);
        }}
        onSubmit={handleSubmit}
        saving={saving}
        initialValues={editing}
      />

      <ConfirmDialog
        open={Boolean(deleting)}
        title="Delete category?"
        description={deleting ? `"${deleting.name}" will be permanently removed.` : ''}
        confirmLabel="Delete"
        loading={saving}
        onConfirm={handleDelete}
        onClose={() => setDeleting(null)}
      />
    </Box>
  );
}
