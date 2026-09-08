import { Chip } from '@mui/material';
import { statusColors } from '../theme';

export default function StatusChip({ status, size = 'small' }) {
  const cfg = statusColors[status] || statusColors.CREATED;
  return (
    <Chip
      label={cfg.label}
      size={size}
      sx={{
        color: cfg.main,
        bgcolor: cfg.bg,
        border: `1px solid ${cfg.main}33`,
      }}
    />
  );
}
