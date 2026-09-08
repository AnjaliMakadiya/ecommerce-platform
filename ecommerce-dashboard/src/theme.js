import { createTheme } from '@mui/material/styles';

// Fleetdesk design tokens — a "control tower" ops console for order fulfillment.
// Palette is built around a deep ink base with a signal-indigo accent, and a
// dedicated status spectrum used consistently for order state everywhere
// (cards, chips, the route rail, charts).
export const statusColors = {
  CREATED: { main: '#8B93A7', bg: 'rgba(139,147,167,0.14)', label: 'Created' },
  PROCESSING: { main: '#E8A33D', bg: 'rgba(232,163,61,0.14)', label: 'Processing' },
  SHIPPED: { main: '#4C6FFF', bg: 'rgba(76,111,255,0.14)', label: 'Shipped' },
  DELIVERED: { main: '#2BB673', bg: 'rgba(43,182,115,0.14)', label: 'Delivered' },
  CANCELLED: { main: '#E5484D', bg: 'rgba(229,72,77,0.14)', label: 'Cancelled' },
};

export const ORDER_STATUS_SEQUENCE = ['CREATED', 'PROCESSING', 'SHIPPED', 'DELIVERED'];

const buildTheme = (mode) =>
  createTheme({
    palette: {
      mode,
      primary: { main: '#4C6FFF', dark: '#3453D9', light: '#7C93FF', contrastText: '#fff' },
      secondary: { main: '#E8A33D' },
      success: { main: '#2BB673' },
      warning: { main: '#E8A33D' },
      error: { main: '#E5484D' },
      info: { main: '#4C6FFF' },
      background:
        mode === 'dark'
          ? { default: '#0E1220', paper: '#151A2C' }
          : { default: '#F4F6FB', paper: '#FFFFFF' },
      divider: mode === 'dark' ? 'rgba(255,255,255,0.08)' : 'rgba(16,24,40,0.08)',
      text:
        mode === 'dark'
          ? { primary: '#EDEFF7', secondary: '#9AA2BD' }
          : { primary: '#111633', secondary: '#5B6180' },
    },
    shape: { borderRadius: 14 },
    typography: {
      fontFamily: '"Inter", "Segoe UI", sans-serif',
      h1: { fontFamily: '"Space Grotesk", sans-serif', fontWeight: 700 },
      h2: { fontFamily: '"Space Grotesk", sans-serif', fontWeight: 700 },
      h3: { fontFamily: '"Space Grotesk", sans-serif', fontWeight: 600 },
      h4: { fontFamily: '"Space Grotesk", sans-serif', fontWeight: 600 },
      h5: { fontFamily: '"Space Grotesk", sans-serif', fontWeight: 600 },
      h6: { fontFamily: '"Space Grotesk", sans-serif', fontWeight: 600 },
      button: { textTransform: 'none', fontWeight: 600 },
      overline: { fontFamily: '"JetBrains Mono", monospace', letterSpacing: 1.2 },
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: { borderRadius: 10, paddingInline: 16 },
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: { backgroundImage: 'none' },
        },
      },
      MuiCard: {
        styleOverrides: {
          root: {
            border: mode === 'dark' ? '1px solid rgba(255,255,255,0.06)' : '1px solid rgba(16,24,40,0.06)',
            boxShadow: 'none',
          },
        },
      },
      MuiChip: {
        styleOverrides: {
          root: { fontWeight: 600 },
        },
      },
      MuiTableCell: {
        styleOverrides: {
          head: { fontWeight: 700, fontSize: 12, textTransform: 'uppercase', letterSpacing: 0.6 },
        },
      },
    },
  });

export default buildTheme;
