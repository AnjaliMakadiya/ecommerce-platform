import { NavLink, useLocation } from 'react-router-dom';
import { Box, Drawer, List, ListItemButton, ListItemIcon, ListItemText, Typography, Divider } from '@mui/material';
import DashboardRoundedIcon from '@mui/icons-material/DashboardRounded';
import Inventory2RoundedIcon from '@mui/icons-material/Inventory2Rounded';
import AddCircleRoundedIcon from '@mui/icons-material/AddCircleRounded';
import NotificationsRoundedIcon from '@mui/icons-material/NotificationsRounded';
import GroupRoundedIcon from '@mui/icons-material/GroupRounded';
import PersonRoundedIcon from '@mui/icons-material/PersonRounded';
import LocalShippingRoundedIcon from '@mui/icons-material/LocalShippingRounded';
import StorefrontRoundedIcon from '@mui/icons-material/StorefrontRounded';
import ShoppingCartRoundedIcon from '@mui/icons-material/ShoppingCartRounded';
import CategoryRoundedIcon from '@mui/icons-material/CategoryRounded';
import { useAuth } from '../context/AuthContext';
import { getNavItems } from '../utils/roles';

export const SIDEBAR_WIDTH = 248;

const ICONS = {
  dashboard: DashboardRoundedIcon,
  orders: Inventory2RoundedIcon,
  create: AddCircleRoundedIcon,
  notifications: NotificationsRoundedIcon,
  users: GroupRoundedIcon,
  profile: PersonRoundedIcon,
  shop: StorefrontRoundedIcon,
  products: Inventory2RoundedIcon,
  cart: ShoppingCartRoundedIcon,
  category: CategoryRoundedIcon,
};

function SidebarContent({ onNavigate }) {
  const { role } = useAuth();
  const items = getNavItems(role);
  const location = useLocation();

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.25, px: 2.5, py: 2.75 }}>
        <Box
          sx={{
            width: 34,
            height: 34,
            borderRadius: 2,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            background: 'linear-gradient(135deg, #4C6FFF, #7C93FF)',
            color: '#fff',
          }}
        >
          <LocalShippingRoundedIcon fontSize="small" />
        </Box>
        <Box>
          <Typography variant="h6" sx={{ lineHeight: 1.1 }}>
            E-Commerce Platform
          </Typography>
          <Typography variant="caption" color="text.secondary">
            Order Operations
          </Typography>
        </Box>
      </Box>
      <Divider />
      <List sx={{ px: 1.5, py: 2, flex: 1 }}>
        {items.map((item) => {
          const Icon = ICONS[item.icon] || DashboardRoundedIcon;
          const basePath = item.path.split('?')[0];
          const active = location.pathname === basePath;
          return (
            <ListItemButton
              key={item.path}
              component={NavLink}
              to={item.path}
              onClick={onNavigate}
              selected={active}
              sx={{
                borderRadius: 2,
                mb: 0.5,
                '&.Mui-selected': {
                  bgcolor: 'primary.main',
                  color: '#fff',
                  '& .MuiListItemIcon-root': { color: '#fff' },
                  '&:hover': { bgcolor: 'primary.dark' },
                },
              }}
            >
              <ListItemIcon sx={{ minWidth: 38 }}>
                <Icon fontSize="small" />
              </ListItemIcon>
              <ListItemText primaryTypographyProps={{ fontSize: 14, fontWeight: 600 }}>
                {item.label}
              </ListItemText>
            </ListItemButton>
          );
        })}
      </List>
      <Divider />
      <Box sx={{ px: 2.5, py: 2 }}>
        <Typography variant="caption" color="text.secondary">
          Signed in as
        </Typography>
        <Typography variant="body2" fontWeight={700} noWrap>
          {role}
        </Typography>
      </Box>
    </Box>
  );
}

export default function Sidebar({ mobileOpen, onClose }) {
  return (
    <>
      <Drawer
        variant="permanent"
        sx={{
          display: { xs: 'none', md: 'block' },
          width: SIDEBAR_WIDTH,
          flexShrink: 0,
          '& .MuiDrawer-paper': { width: SIDEBAR_WIDTH, boxSizing: 'border-box', borderRight: '1px solid', borderColor: 'divider' },
        }}
        open
      >
        <SidebarContent />
      </Drawer>
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={onClose}
        ModalProps={{ keepMounted: true }}
        sx={{
          display: { xs: 'block', md: 'none' },
          '& .MuiDrawer-paper': { width: SIDEBAR_WIDTH, boxSizing: 'border-box' },
        }}
      >
        <SidebarContent onNavigate={onClose} />
      </Drawer>
    </>
  );
}
