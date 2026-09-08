import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { SnackbarProvider } from 'notistack';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ThemeModeProvider } from './context/ThemeModeContext';
import { CartProvider } from './context/CartContext';
import ProtectedRoute from './components/ProtectedRoute';
import DashboardLayout from './layouts/DashboardLayout';

import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Orders from './pages/Orders';
import OrderDetails from './pages/OrderDetails';
import Notifications from './pages/Notifications';
import Profile from './pages/Profile';
import Users from './pages/Users';
import Products from './pages/Products';
import ProductDetails from './pages/ProductDetails';
import Cart from './pages/Cart';
import MyProducts from './pages/MyProducts';
import AdminProducts from './pages/AdminProducts';
import Categories from './pages/Categories';
import NotFound from './pages/NotFound';

function RootRedirect() {
  const { isAuthenticated, initializing } = useAuth();

  // Keycloak found a session and we're still exchanging it for our own
  // app JWT - don't guess yet, or the user briefly flashes through /login.
  if (initializing) {
    return null; // or a spinner component, if you have one handy
  }

  return <Navigate to={isAuthenticated ? '/dashboard' : '/login'} replace />;
}

export default function App() {
  return (
    <ThemeModeProvider>
      <SnackbarProvider maxSnack={3} anchorOrigin={{ vertical: 'top', horizontal: 'right' }} autoHideDuration={3500}>
        <BrowserRouter>
          <AuthProvider>
            <CartProvider>
              <Routes>
                <Route path="/" element={<RootRedirect />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />

                <Route element={<ProtectedRoute />}>
                  <Route element={<DashboardLayout />}>
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/orders" element={<Orders />} />
                    <Route path="/orders/:id" element={<OrderDetails />} />
                    <Route path="/notifications" element={<Notifications />} />
                    <Route path="/profile" element={<Profile />} />

                    {/* Catalog browsing - open to every authenticated role */}
                    <Route path="/products" element={<Products />} />
                    <Route path="/products/:id" element={<ProductDetails />} />

                    <Route element={<ProtectedRoute allowedRoles={['CUSTOMER']} />}>
                      <Route path="/cart" element={<Cart />} />
                    </Route>

                    <Route element={<ProtectedRoute allowedRoles={['OPERATOR']} />}>
                      <Route path="/my-products" element={<MyProducts />} />
                    </Route>

                    <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
                      <Route path="/users" element={<Users />} />
                      <Route path="/admin/products" element={<AdminProducts />} />
                      <Route path="/admin/categories" element={<Categories />} />
                    </Route>
                  </Route>
                </Route>

                <Route path="*" element={<NotFound />} />
              </Routes>
            </CartProvider>
          </AuthProvider>
        </BrowserRouter>
      </SnackbarProvider>
    </ThemeModeProvider>
  );
}
