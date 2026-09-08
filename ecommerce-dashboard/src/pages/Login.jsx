import { useForm } from 'react-hook-form';
import {
  Link as RouterLink,
  useNavigate,
  useSearchParams,
} from 'react-router-dom';

import { useSnackbar } from 'notistack';

import {
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  InputAdornment,
  IconButton,
  Link,
  CircularProgress,
} from '@mui/material';

import LocalShippingRoundedIcon
  from '@mui/icons-material/LocalShippingRounded';

import Visibility
  from '@mui/icons-material/Visibility';

import VisibilityOff
  from '@mui/icons-material/VisibilityOff';

import GoogleIcon
  from '@mui/icons-material/Google';

import {
  useState,
  useEffect,
} from 'react';

import { useAuth }
  from '../context/AuthContext';


export default function Login() {

  const {
    login,
    loginWithGoogle,
    isAuthenticated,
    loading,
    user,
  } = useAuth();

  const navigate = useNavigate();

  const {
    enqueueSnackbar,
  } = useSnackbar();

  const [
    showPassword,
    setShowPassword,
  ] = useState(false);

  const [
    searchParams,
  ] = useSearchParams();


  // Redirect users according to their role
  const redirectByRole = (currentUser) => {

    const role =
      currentUser?.role?.toUpperCase();

    switch (role) {

      case 'ADMIN':
        navigate(
          '/dashboard',
          {
            replace: true,
          }
        );
        break;

      case 'OPERATOR':
        navigate(
          '/my-products',
          {
            replace: true,
          }
        );
        break;

      case 'CUSTOMER':
        navigate(
          '/products',
          {
            replace: true,
          }
        );
        break;

      default:
        console.error(
          'Unknown user role:',
          currentUser?.role
        );

        navigate(
          '/login',
          {
            replace: true,
          }
        );
    }
  };


  // If an authenticated user opens /login,
  // redirect based on their role
  useEffect(() => {

    if (
      isAuthenticated &&
      user
    ) {
      redirectByRole(user);
    }

  }, [
    isAuthenticated,
    user,
  ]);


  // Show session-expired message
  useEffect(() => {

    if (
      searchParams.get(
        'sessionExpired'
      )
    ) {

      enqueueSnackbar(
        'Your session expired. Please sign in again.',
        {
          variant: 'warning',
        }
      );
    }

  }, [
    searchParams,
    enqueueSnackbar,
  ]);


  const {
    register,
    handleSubmit,

    formState: {
      errors,
    },

  } = useForm({
    mode: 'onBlur',
  });


  const onSubmit = async (
    values
  ) => {

    try {

      const loggedInUser =
        await login(values);

      console.log(
        'LOGIN SUCCESS'
      );

      console.log(
        'Logged-in user:',
        loggedInUser
      );

      enqueueSnackbar(
        'Welcome back!',
        {
          variant: 'success',
        }
      );

      redirectByRole(
        loggedInUser
      );

    } catch (err) {

      console.error(
        'LOGIN ERROR:',
        err
      );

      const message =
        err?.response
          ?.data
          ?.message
        ||
        'Invalid username or password.';

      enqueueSnackbar(
        message,
        {
          variant: 'error',
        }
      );
    }
  };


  // Google login now delegates entirely to AuthContext's loginWithGoogle,
  // which already knows the correct redirectUri (window.location.origin)
  // and keeps all Keycloak-related logic in one place.
  const handleGoogleLogin = () => {
    loginWithGoogle();
  };


  return (

    <Box
      sx={{

        minHeight:
          '100vh',

        display:
          'flex',

        alignItems:
          'center',

        justifyContent:
          'center',

        background:
          'radial-gradient(circle at 20% 20%, rgba(76,111,255,0.16), transparent 45%), radial-gradient(circle at 80% 80%, rgba(43,182,115,0.12), transparent 45%)',

        bgcolor:
          'background.default',

        p:
          2,

      }}
    >

      <Paper
        elevation={0}

        sx={{

          width:
            '100%',

          maxWidth:
            420,

          p:
            4,

          border:
            '1px solid',

          borderColor:
            'divider',

        }}
      >

        <Box
          sx={{

            display:
              'flex',

            alignItems:
              'center',

            gap:
              1.5,

            mb:
              3,

          }}
        >

          <Box
            sx={{

              width:
                42,

              height:
                42,

              borderRadius:
                2.5,

              display:
                'flex',

              alignItems:
                'center',

              justifyContent:
                'center',

              background:
                'linear-gradient(135deg, #4C6FFF, #7C93FF)',

              color:
                '#fff',

            }}
          >

            <LocalShippingRoundedIcon />

          </Box>

          <Box>

            <Typography
              variant="h5"
            >
              E-Commerce Platform
            </Typography>

            <Typography
              variant="caption"
              color="text.secondary"
            >
              Order Management System
            </Typography>

          </Box>

        </Box>


        <Typography
          variant="h4"
          sx={{
            mb:
              0.5,
          }}
        >
          Sign in
        </Typography>


        <Typography
          variant="body2"
          color="text.secondary"

          sx={{
            mb:
              3,
          }}
        >
          Enter your credentials to access your workspace.
        </Typography>


        <Box
          component="form"

          onSubmit={
            handleSubmit(
              onSubmit
            )
          }

          noValidate
        >

          <TextField

            label="Username"

            fullWidth

            margin="normal"

            autoFocus

            {...register(
              'username',
              {
                required:
                  'Username is required',
              }
            )}

            error={
              Boolean(
                errors.username
              )
            }

            helperText={
              errors
                .username
                ?.message
            }

          />

          <TextField

            label="Password"

            type={
              showPassword
                ? 'text'
                : 'password'
            }

            fullWidth

            margin="normal"

            {...register(
              'password',
              {

                required:
                  'Password is required',

                minLength:
                  {

                    value:
                      6,

                    message:
                      'Password must be at least 6 characters',

                  },
              }
            )}

            error={
              Boolean(
                errors.password
              )
            }

            helperText={
              errors
                .password
                ?.message
            }

            InputProps={{

              endAdornment:

                <InputAdornment
                  position="end"
                >

                  <IconButton

                    onClick={() =>
                      setShowPassword(
                        (current) =>
                          !current
                      )
                    }
                    edge="end"
                  >
                    {
                      showPassword
                        ? <VisibilityOff />
                        : <Visibility />
                    }

                  </IconButton>

                </InputAdornment>,

            }}

          />

          <Button

            type="submit"
            fullWidth
            variant="contained"
            size="large"
            disabled={
              loading
            }

            sx={{

              mt:
                2,
              mb:
                2,
              py:
                1.2,

            }}

            startIcon={
              loading
                ? (
                  <CircularProgress
                    size={18}
                    color="inherit"
                  />
                )

                : null

            }

          >

            {
              loading
                ? 'Signing in…'
                : 'Sign in'
            }

          </Button>

        </Box>

        <Button

          fullWidth
          variant="outlined"
          size="large"

          onClick={
            handleGoogleLogin
          }

          startIcon={
            <GoogleIcon />
          }

          sx={{

            mb: 2,
            py: 1.2,

          }}

        >
          Continue with Google
        </Button>

        <Typography

          variant="body2"
          align="center"
          color="text.secondary"

        >

          Don&apos;t have an account?{' '}

          <Link

            component={
              RouterLink
            }

            to="/register"
            underline="hover"

          >

            Create one

          </Link>

        </Typography>

      </Paper>

    </Box>
  );
}
