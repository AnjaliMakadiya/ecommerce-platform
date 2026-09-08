import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { useSnackbar } from 'notistack';
import {
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  Link,
  MenuItem,
  CircularProgress,
} from '@mui/material';
import LocalShippingRoundedIcon from '@mui/icons-material/LocalShippingRounded';
import { useAuth } from '../context/AuthContext';

const ROLE_OPTIONS = [
  { value: 'CUSTOMER', label: 'Customer' },
  { value: 'OPERATOR', label: 'Operator' },
  { value: 'ADMIN', label: 'Admin' },
];

export default function Register() {
  const { register: registerUser, isAuthenticated, loading } = useAuth();
  const navigate = useNavigate();
  const { enqueueSnackbar } = useSnackbar();

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({ mode: 'onBlur', defaultValues: { role: 'CUSTOMER' } });

  useEffect(() => {
    if (isAuthenticated) navigate('/dashboard', { replace: true });
  }, [isAuthenticated, navigate]);

  const password = watch('password');

  const onSubmit = async ({ confirmPassword, ...payload }) => {
    try {
      await registerUser(payload);
      enqueueSnackbar('Account created. Please sign in.', { variant: 'success' });
      navigate('/login', { replace: true });
    } catch (err) {
      const message = err?.response?.data?.message || 'Registration failed. Please try again.';
      enqueueSnackbar(message, { variant: 'error' });
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background:
          'radial-gradient(circle at 20% 20%, rgba(76,111,255,0.16), transparent 45%), radial-gradient(circle at 80% 80%, rgba(43,182,115,0.12), transparent 45%)',
        bgcolor: 'background.default',
        p: 2,
      }}
    >
      <Paper elevation={0} sx={{ width: '100%', maxWidth: 460, p: 4, border: '1px solid', borderColor: 'divider' }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 3 }}>
          <Box
            sx={{
              width: 42,
              height: 42,
              borderRadius: 2.5,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              background: 'linear-gradient(135deg, #4C6FFF, #7C93FF)',
              color: '#fff',
            }}
          >
            <LocalShippingRoundedIcon />
          </Box>
          <Box>
            <Typography variant="h5">E-Commerce Platform</Typography>
            <Typography variant="caption" color="text.secondary">
              Order Management System
            </Typography>
          </Box>
        </Box>

        <Typography variant="h4" sx={{ mb: 0.5 }}>
          Create account
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Set up access to the Order Management System.
        </Typography>

        <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
          <TextField
              label="name"
              fullWidth
              margin="normal"
              autoFocus
              {...register('name', { required: 'name is required', minLength: { value: 3, message: 'At least 3 characters' } })}
              error={Boolean(errors.name)}
              helperText={errors.name?.message}
          />
          <TextField
            label="Username"
            fullWidth
            margin="normal"
            autoFocus
            {...register('username', { required: 'Username is required', minLength: { value: 3, message: 'At least 3 characters' } })}
            error={Boolean(errors.username)}
            helperText={errors.username?.message}
          />
          <TextField
            label="Email"
            type="email"
            fullWidth
            margin="normal"
            {...register('email', {
              required: 'Email is required',
              pattern: { value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: 'Enter a valid email address' },
            })}
            error={Boolean(errors.email)}
            helperText={errors.email?.message}
          />
          <TextField
            label="Password"
            type="password"
            fullWidth
            margin="normal"
            {...register('password', {
              required: 'Password is required',
              minLength: { value: 6, message: 'Password must be at least 6 characters' },
            })}
            error={Boolean(errors.password)}
            helperText={errors.password?.message}
          />
          <TextField
            label="Confirm password"
            type="password"
            fullWidth
            margin="normal"
            {...register('confirmPassword', {
              required: 'Please confirm your password',
              validate: (value) => value === password || 'Passwords do not match',
            })}
            error={Boolean(errors.confirmPassword)}
            helperText={errors.confirmPassword?.message}
          />
          {/*<TextField*/}
          {/*  select*/}
          {/*  label="Role"*/}
          {/*  fullWidth*/}
          {/*  margin="normal"*/}
          {/*  defaultValue="CUSTOMER"*/}
          {/*  {...register('role', { required: true })}*/}
          {/*>*/}
          {/*  {ROLE_OPTIONS.map((opt) => (*/}
          {/*    <MenuItem key={opt.value} value={opt.value}>*/}
          {/*      {opt.label}*/}
          {/*    </MenuItem>*/}
          {/*  ))}*/}
          {/*</TextField>*/}

          <Button
            type="submit"
            fullWidth
            variant="contained"
            size="large"
            disabled={loading}
            sx={{ mt: 2, mb: 2, py: 1.2 }}
            startIcon={loading ? <CircularProgress size={18} color="inherit" /> : null}
          >
            {loading ? 'Creating account…' : 'Create account'}
          </Button>
        </Box>

        <Typography variant="body2" align="center" color="text.secondary">
          Already have an account?{' '}
          <Link component={RouterLink} to="/login" underline="hover">
            Sign in
          </Link>
        </Typography>
      </Paper>
    </Box>
  );
}
