import { useState } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { Box, Toolbar } from '@mui/material';
import Sidebar, { SIDEBAR_WIDTH } from '../components/Sidebar';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

const TITLES = {
  '/dashboard': 'Dashboard',
  '/orders': 'Orders',
  '/notifications': 'Notifications',
  '/profile': 'Profile',
  '/users': 'Users',
  '/products': 'Shop',
  '/cart': 'Cart',
  '/my-products': 'My Products',
  '/admin/products': 'All Products',
  '/admin/categories': 'Categories',
};

function titleFor(pathname) {
  if (TITLES[pathname]) return TITLES[pathname];
  if (pathname.startsWith('/orders/')) return 'Order Details';
  if (pathname.startsWith('/products/')) return 'Product Details';
  return 'E-Commerce Platform';
}

export default function DashboardLayout() {
  const [mobileOpen, setMobileOpen] = useState(false);
  const location = useLocation();

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <Sidebar mobileOpen={mobileOpen} onClose={() => setMobileOpen(false)} />
      <Box sx={{ flexGrow: 1, width: { md: `calc(100% - ${SIDEBAR_WIDTH}px)` }, display: 'flex', flexDirection: 'column' }}>
        <Navbar onMenuClick={() => setMobileOpen(true)} title={titleFor(location.pathname)} />
        <Toolbar />
        <Box sx={{ flex: 1, p: { xs: 2, md: 3 } }}>
          <Outlet />
        </Box>
        <Footer />
      </Box>
    </Box>
  );
}
