import { Box, CircularProgress, Typography } from '@mui/material';

export default function Loader({ fullscreen = false, label = 'Loading…', size = 36 }) {
  if (fullscreen) {
    return (
      <Box
        sx={{
          position: 'fixed',
          inset: 0,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          gap: 2,
          bgcolor: 'background.default',
          zIndex: 1300,
        }}
      >
        <CircularProgress size={size} thickness={4} />
        <Typography variant="body2" color="text.secondary">
          {label}
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', py: 6, gap: 1.5 }}>
      <CircularProgress size={size} thickness={4} />
      <Typography variant="body2" color="text.secondary">
        {label}
      </Typography>
    </Box>
  );
}
