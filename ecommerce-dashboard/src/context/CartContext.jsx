import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { useAuth } from './AuthContext';

// There is no cart microservice yet (see Phase 3/4 of the roadmap: cart-service +
// order-service accepting line items). Until then, the cart lives in localStorage,
// scoped PER LOGGED-IN USER (keyed by username) so switching accounts on the same
// browser doesn't leak one person's cart into another's. Logging out clears the
// active cart view (falls back to the anonymous 'guest' bucket, which stays empty
// unless someone adds to cart while logged out).
const CartContext = createContext(null);
const STORAGE_PREFIX = 'cart_items:';

function keyFor(username) {
  return `${STORAGE_PREFIX}${username || 'guest'}`;
}

function readStoredCart(username) {
  try {
    const raw = localStorage.getItem(keyFor(username));
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export function CartProvider({ children }) {
  const { user } = useAuth();
  const username = user?.username ?? null;

  const [items, setItems] = useState(() => readStoredCart(username));

  // Whenever the logged-in user changes (login, logout, switching accounts in the
  // same browser), reload the cart that belongs to THAT user instead of reusing
  // whatever was in state from the previous session.
  useEffect(() => {
    setItems(readStoredCart(username));
  }, [username]);

  useEffect(() => {
    localStorage.setItem(keyFor(username), JSON.stringify(items));
  }, [items, username]);

  const addItem = useCallback((product, qty = 1) => {
    setItems((prev) => {
      const existing = prev.find((i) => i.productId === product.id);
      if (existing) {
        return prev.map((i) =>
          i.productId === product.id ? { ...i, qty: Math.min(i.qty + qty, product.stock ?? 999) } : i
        );
      }
      return [
        ...prev,
        {
          productId: product.id,
          name: product.name,
          price: product.price,
          imageUrl: product.imageUrl,
          stock: product.stock,
          qty,
        },
      ];
    });
  }, []);

  const updateQty = useCallback((productId, qty) => {
    setItems((prev) =>
      prev
        .map((i) => (i.productId === productId ? { ...i, qty: Math.max(1, qty) } : i))
        .filter((i) => i.qty > 0)
    );
  }, []);

  const removeItem = useCallback((productId) => {
    setItems((prev) => prev.filter((i) => i.productId !== productId));
  }, []);

  const clearCart = useCallback(() => setItems([]), []);

  const totalItems = useMemo(() => items.reduce((sum, i) => sum + i.qty, 0), [items]);
  const totalPrice = useMemo(() => items.reduce((sum, i) => sum + i.qty * Number(i.price), 0), [items]);

  const value = useMemo(
    () => ({ items, addItem, updateQty, removeItem, clearCart, totalItems, totalPrice }),
    [items, addItem, updateQty, removeItem, clearCart, totalItems, totalPrice]
  );

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error('useCart must be used within CartProvider');
  return ctx;
}
