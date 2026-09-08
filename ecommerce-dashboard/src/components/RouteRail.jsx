import { Box, Typography } from '@mui/material';
import CheckIcon from '@mui/icons-material/Check';
import CloseIcon from '@mui/icons-material/Close';
import { ORDER_STATUS_SEQUENCE, statusColors } from '../theme';

// Signature visual for the Order Details page: a horizontal "route rail"
// echoing a shipment tracking strip, rather than a generic MUI Stepper.
// Cancelled orders render as a severed rail rather than forcing a fake step.
export default function RouteRail({ status }) {
  if (status === 'CANCELLED') {
    return (
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, py: 2 }}>
        <Box
          sx={{
            width: 34,
            height: 34,
            borderRadius: '50%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            bgcolor: statusColors.CANCELLED.bg,
            color: statusColors.CANCELLED.main,
            border: `1px solid ${statusColors.CANCELLED.main}55`,
          }}
        >
          <CloseIcon fontSize="small" />
        </Box>
        <Typography color={statusColors.CANCELLED.main} fontWeight={600}>
          Order cancelled
        </Typography>
      </Box>
    );
  }

  const currentIndex = ORDER_STATUS_SEQUENCE.indexOf(status);

  return (
    <Box sx={{ display: 'flex', alignItems: 'center', width: '100%', py: 2 }}>
      {ORDER_STATUS_SEQUENCE.map((step, i) => {
        const cfg = statusColors[step];
        const done = i <= currentIndex;
        const isLast = i === ORDER_STATUS_SEQUENCE.length - 1;
        return (
          <Box key={step} sx={{ display: 'flex', alignItems: 'center', flex: isLast ? '0 0 auto' : 1 }}>
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 0.75 }}>
              <Box
                sx={{
                  width: 34,
                  height: 34,
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  bgcolor: done ? cfg.main : 'transparent',
                  color: done ? '#fff' : 'text.secondary',
                  border: `2px solid ${done ? cfg.main : 'rgba(128,128,128,0.35)'}`,
                  transition: 'all .25s ease',
                }}
              >
                {done ? <CheckIcon fontSize="small" /> : null}
              </Box>
              <Typography
                variant="caption"
                sx={{ color: done ? cfg.main : 'text.secondary', fontWeight: 600, whiteSpace: 'nowrap' }}
              >
                {cfg.label}
              </Typography>
            </Box>
            {!isLast && (
              <Box
                sx={{
                  flex: 1,
                  height: 3,
                  mx: 1,
                  mb: 2.5,
                  borderRadius: 2,
                  bgcolor: i < currentIndex ? statusColors[ORDER_STATUS_SEQUENCE[i + 1]].main : 'rgba(128,128,128,0.2)',
                  transition: 'all .25s ease',
                }}
              />
            )}
          </Box>
        );
      })}
    </Box>
  );
}
