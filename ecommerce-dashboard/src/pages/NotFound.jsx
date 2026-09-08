import { Link as RouterLink } from 'react-router-dom';
import { Box, Typography, Button } from '@mui/material';

export default function NotFound() {
  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        textAlign: 'center',
        gap: 1.5,
        p: 3,
        bgcolor: 'background.default',
      }}
    >
      <Typography variant="overline" color="primary">
        Error 404
      </Typography>
      <Typography variant="h2" sx={{ fontSize: { xs: 48, sm: 64 } }}>
        Route not found
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ maxWidth: 420, mb: 2 }}>
        The page you&apos;re looking for doesn&apos;t exist or may have moved.
      </Typography>
      <Button component={RouterLink} to="/dashboard" variant="contained" size="large">
        Back to dashboard
      </Button>
    </Box>
  );
}
