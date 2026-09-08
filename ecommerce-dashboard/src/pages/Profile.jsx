import { useNavigate } from 'react-router-dom';
import { Box, Paper, Typography, Avatar, Grid, Button, Divider, Chip } from '@mui/material';
import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded';
import { useAuth } from '../context/AuthContext';

export default function Profile() {
  const { user, role, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const initials = (user?.username || '?').slice(0, 2).toUpperCase();

  return (
    <Box sx={{ maxWidth: 640 }}>
      <Typography variant="h4" sx={{ mb: 2.5 }}>
        Profile
      </Typography>

      <Paper sx={{ p: 3.5 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2.5, mb: 3 }}>
          <Avatar sx={{ width: 64, height: 64, fontSize: 22, bgcolor: 'primary.main' }}>{initials}</Avatar>
          <Box>
            <Typography variant="h5">{user?.username}</Typography>
            <Chip label={role} size="small" color="primary" variant="outlined" sx={{ mt: 0.5 }} />
          </Box>
        </Box>

        <Divider sx={{ mb: 3 }} />

        <Grid container spacing={2.5}>
          <Grid item xs={12} sm={6}>
            <Typography variant="caption" color="text.secondary">
              Username
            </Typography>
            <Typography variant="body1" fontWeight={600}>
              {user?.username || '—'}
            </Typography>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Typography variant="caption" color="text.secondary">
              Email
            </Typography>
            <Typography variant="body1" fontWeight={600}>
              {user?.email || '—'}
            </Typography>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Typography variant="caption" color="text.secondary">
              Role
            </Typography>
            <Typography variant="body1" fontWeight={600}>
              {role}
            </Typography>
          </Grid>
          {user?.customerId != null && (
            <Grid item xs={12} sm={6}>
              <Typography variant="caption" color="text.secondary">
                Customer ID
              </Typography>
              <Typography variant="body1" fontWeight={600}>
                {user.customerId}
              </Typography>
            </Grid>
          )}
        </Grid>

        <Divider sx={{ my: 3 }} />

        <Button variant="outlined" color="error" startIcon={<LogoutRoundedIcon />} onClick={handleLogout}>
          Logout
        </Button>
      </Paper>
    </Box>
  );
}
