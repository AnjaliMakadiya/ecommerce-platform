export const ROLES = {
  ADMIN: 'ADMIN',
  OPERATOR: 'OPERATOR',
  CUSTOMER: 'CUSTOMER',
};

// Menu definitions per role. `icon` is a key resolved to an MUI icon in Sidebar.jsx.
export const NAV_ITEMS_BY_ROLE = {
  ADMIN: [
    { label: 'Dashboard', path: '/dashboard', icon: 'dashboard' },
    { label: 'Shop', path: '/products', icon: 'shop' },
    { label: 'All Products', path: '/admin/products', icon: 'products' },
    { label: 'Categories', path: '/admin/categories', icon: 'category' },
    { label: 'Users', path: '/users', icon: 'users' },
    { label: 'Orders', path: '/orders', icon: 'orders' },
    { label: 'Notifications', path: '/notifications', icon: 'notifications' },
  ],
  OPERATOR: [
    // { label: 'Dashboard', path: '/dashboard', icon: 'dashboard' },
    { label: 'Shop', path: '/products', icon: 'shop' },
    { label: 'My Products', path: '/my-products', icon: 'products' },
    { label: 'Orders', path: '/orders', icon: 'orders' },
    // { label: 'Notifications', path: '/notifications', icon: 'notifications' },
  ],
  CUSTOMER: [
    { label: 'Dashboard', path: '/dashboard', icon: 'dashboard' },
    { label: 'Shop', path: '/products', icon: 'shop' },
    { label: 'Cart', path: '/cart', icon: 'cart' },
    { label: 'My Orders', path: '/orders', icon: 'orders' },
    { label: 'My Notifications', path: '/notifications', icon: 'notifications' },
    { label: 'Profile', path: '/profile', icon: 'profile' },
  ],
};

export const getNavItems = (role) => NAV_ITEMS_BY_ROLE[role] || [];

// Which roles may create/edit/delete orders.
export const canManageOrders = (role) => role === 'ADMIN' || role === 'OPERATOR';
export const canDeleteOrders = (role) => role === 'ADMIN';
export const canViewUsers = (role) => role === 'ADMIN';

// Sellers manage their own product listings; admins moderate every listing.
export const canManageOwnProducts = (role) => role === 'OPERATOR';
export const canModerateProducts = (role) => role === 'ADMIN';
