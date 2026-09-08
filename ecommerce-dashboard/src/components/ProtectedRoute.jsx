import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Box, Typography, Button } from '@mui/material';

// Wrap route groups that need authentication. Optionally pass `allowedRoles`
// to restrict access further (e.g. Users page is ADMIN-only).
export default function ProtectedRoute({ allowedRoles }) {
  const { isAuthenticated, role } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (allowedRoles && !allowedRoles.includes(role)) {
    return (
      <Box
        sx={{
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          gap: 2,
          p: 4,
          textAlign: 'center',
        }}
      >
        <Typography variant="h4">Access restricted</Typography>
        <Typography color="text.secondary">
          Your role ({role}) doesn&apos;t have permission to view this page.
        </Typography>
        <Button variant="contained" href="/dashboard">
          Back to dashboard
        </Button>
      </Box>
    );
  }

  return <Outlet />;
}
