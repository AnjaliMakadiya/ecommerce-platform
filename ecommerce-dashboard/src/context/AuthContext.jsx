// import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
// import authService from '../services/authService';
// import { registerUnauthorizedHandler } from '../api/axios';
// import { decodeJwt, isTokenExpired } from '../utils/jwt';

// import keycloak from '../auth/keycloak';

// const AuthContext = createContext(null);

// function buildUserFromToken(token) {
//   const claims = decodeJwt(token);
//   if (!claims) return null;
//   return {
//     username: claims.sub || claims.username || 'user',
//     role: claims.role || claims.authorities?.[0] || 'CUSTOMER',
//     email: claims.email,
//     customerId: claims.customerId ?? claims.userId ?? null,
//     exp: claims.exp,
//   };
// }

// export function AuthProvider({ children }) {
//   const [token, setToken] = useState(() => localStorage.getItem('token'));
//   const [user, setUser] = useState(() => {
//     const existing = localStorage.getItem('token');
//     return existing && !isTokenExpired(existing) ? buildUserFromToken(existing) : null;
//   });
//   const [loading, setLoading] = useState(false);

//   const logout = useCallback(() => {
//     localStorage.removeItem('token');
//     setToken(null);
//     setUser(null);
//   }, []);

//   // Wire the axios 401 interceptor to this context's logout so any expired /
//   // invalid token anywhere in the app redirects to Login automatically.
//   useEffect(() => {
//     registerUnauthorizedHandler(() => {
//       logout();
//       if (window.location.pathname !== '/login') {
//         window.location.assign('/login?sessionExpired=1');
//       }
//     });
//   }, [logout]);

//   // Persistent login: on mount, verify the stored token hasn't expired.
//   useEffect(() => {
//     if (token && isTokenExpired(token)) {
//       logout();
//     }
//   }, [token, logout]);

//   // Auto logout: schedule a timer for when the token actually expires.
//   useEffect(() => {
//     if (!user?.exp) return undefined;
//     const msRemaining = user.exp * 1000 - Date.now();
//     if (msRemaining <= 0) {
//       logout();
//       return undefined;
//     }
//     const timer = setTimeout(() => {
//       logout();
//       window.location.assign('/login?sessionExpired=1');
//     }, msRemaining);
//     return () => clearTimeout(timer);
//   }, [user, logout]);

// const login = useCallback(async (credentials) => {
//   setLoading(true);

//   try {
//     const response = await authService.login(credentials);

//     console.log("FULL RESPONSE");
//     console.log(response);

//     console.log("DATA");
//     console.log(response.data);

//     console.log("TOKEN");
//     console.log(response.data.token);

//     const jwt = response.data.token;

//     localStorage.setItem("token", jwt);

//     console.log("Stored Token:", localStorage.getItem("token"));

//     setToken(jwt);

//     const user = buildUserFromToken(jwt);

//     console.log("Decoded User:", user);

//     setUser(user);

//     return user;
//   } catch (e) {
//     console.error("LOGIN ERROR");
//     console.error(e);
//     throw e;
//   } finally {
//     setLoading(false);
//   }
// }, []);

//   const register = useCallback(async (payload) => {
//     setLoading(true);
//     try {
//       const { data } = await authService.register(payload);
//       return data;
//     } finally {
//       setLoading(false);
//     }
//   }, []);

//   const value = useMemo(
//     () => ({
//       token,
//       user,
//       role: user?.role || null,
//       isAuthenticated: Boolean(token && user),
//       loading,
//       login,
//       register,
//       logout,
//     }),
//     [token, user, loading, login, register, logout]
//   );

//   return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
// }

// export function useAuth() {
//   const ctx = useContext(AuthContext);
//   if (!ctx) throw new Error('useAuth must be used within AuthProvider');
//   return ctx;
// }


import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';

import authService from '../services/authService';
import { registerUnauthorizedHandler } from '../api/axios';
import { decodeJwt, isTokenExpired } from '../utils/jwt';
import keycloak from '../auth/keycloak';

const AuthContext = createContext(null);


// ======================================================
// BUILD USER FROM OLD JWT OR KEYCLOAK JWT
// ======================================================

// function buildUserFromToken(token) {

//   const claims =
//     decodeJwt(token);

//   if (!claims) {
//     return null;
//   }

//   const keycloakRoles =
//     claims.realm_access?.roles || [];

//   // Find one of our application roles

//   const applicationRole =
//     keycloakRoles.find(
//       (role) =>
//         [
//           'ADMIN',
//           'OPERATOR',
//           'CUSTOMER',
//         ].includes(
//           role.toUpperCase()
//         )
//     );

//   return {

//     username:
//       claims.preferred_username ||
//       claims.username ||
//       claims.sub ||
//       'user',

//     role:
//       claims.role ||
//       claims.authorities?.[0] ||
//       applicationRole ||

//       // Temporary frontend fallback.
//       // Later we will configure Keycloak
//       // to assign CUSTOMER automatically.

//       'CUSTOMER',


//     email:
//       claims.email,

//     customerId:
//       claims.customerId ??
//       claims.userId ??
//       claims.sub ??
//       null,

//     exp:
//       claims.exp,
//   };

// }

// ======================================================
// BUILD USER FROM OUR APP'S OWN JWT
// (both /api/auth/login and /api/auth/social-login return
//  this same token shape, so this stays a single code path)
// ======================================================

function buildUserFromToken(token) {
  const claims = decodeJwt(token);
  if (!claims) return null;

  return {
    username: claims.sub || claims.username || 'user',
    role: claims.role || claims.authorities?.[0] || 'CUSTOMER',
    email: claims.email,
    customerId: claims.customerId ?? claims.userId ?? null,
    exp: claims.exp,
  };
}


// ======================================================
// AUTH PROVIDER
// ======================================================

export function AuthProvider({ children }) {

  const [token, setToken] = useState(() => localStorage.getItem('token'));
const [user, setUser] = useState(() => {
    const existingToken = localStorage.getItem('token');
    return existingToken && !isTokenExpired(existingToken)
      ? buildUserFromToken(existingToken)
      : null;
  });

  const [loading, setLoading] = useState(false);


  // ====================================================
  // LOGOUT
  // ====================================================

  const logout = useCallback(() => {
     localStorage.removeItem('token');
     setToken(null);
     setUser(null);
 
     // Also end the Keycloak session, if there is one - otherwise a Google
     // user who "logs out" of our app stays silently signed into Keycloak,
     // and check-sso would just log them straight back in on next visit.
     if (keycloak.authenticated) {
       keycloak.logout({ redirectUri: window.location.origin + '/login' });
     }
   }, []);

  // ====================================================
  // AXIOS 401 HANDLER
  // ====================================================

 useEffect(() => {
     registerUnauthorizedHandler(() => {
       logout();
       if (window.location.pathname !== '/login') {
         window.location.assign('/login?sessionExpired=1');
       }
     });
   }, [logout]);

   // ====================================================
  // GOOGLE LOGIN: sync Keycloak session -> our own app JWT
  //
  // keycloak.init() already ran in main.jsx before this component even
  // mounted. If it found a session (either just came back from Google,
  // or a still-valid earlier session), we exchange that Keycloak token
  // for OUR OWN app JWT via /api/auth/social-login, and from that point
  // on the app behaves exactly like a normal username/password login -
  // same token shape, same expiry handling, same everything.
  // ====================================================

  useEffect(() => {
    if (!keycloak.authenticated || !keycloak.token) {
      return;
    }

    // Already exchanged and logged in during this session - don't repeat it
    // on every re-render.
    if (token) {
      return;
    }

    let cancelled = false;

    (async () => {
      setLoading(true);
      try {
        const response = await authService.socialLogin(keycloak.token);
        const jwt = response.data.token;

        if (cancelled) return;

        localStorage.setItem('token', jwt);
        setToken(jwt);
        setUser(buildUserFromToken(jwt));
      } catch (error) {
        console.error('Social login exchange failed:', error);
        if (!cancelled) {
          logout();
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [token, logout]);

  // ====================================================
  // CHECK TOKEN EXPIRATION (our app JWT, not Keycloak's)
  // ====================================================

  useEffect(() => {
    if (token && isTokenExpired(token)) {
      logout();
    }
  }, [token, logout]);

  // ====================================================
  // AUTO LOGOUT WHEN OUR APP JWT EXPIRES
  // ====================================================

  useEffect(() => {
    if (!user?.exp) return undefined;

    const msRemaining = user.exp * 1000 - Date.now();

    if (msRemaining <= 0) {
      logout();
      return undefined;
    }

    const timer = setTimeout(() => {
      logout();
      window.location.assign('/login?sessionExpired=1');
    }, msRemaining);

    return () => clearTimeout(timer);
  }, [user, logout]);

  // ====================================================
  // EXISTING USERNAME/PASSWORD LOGIN
  // ====================================================

  const login = useCallback(async (credentials) => {
    setLoading(true);
    try {
      const response = await authService.login(credentials);
      const jwt = response.data.token;

      localStorage.setItem('token', jwt);
      setToken(jwt);

      const loggedInUser = buildUserFromToken(jwt);
      setUser(loggedInUser);

      return loggedInUser;
    } catch (error) {
      console.error('LOGIN ERROR:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  }, []);

  // ====================================================
  // GOOGLE LOGIN THROUGH KEYCLOAK
  // (just kicks off the redirect - the exchange happens
  //  automatically in the effect above once Keycloak sends
  //  the user back with a session)
  // ====================================================

  const loginWithGoogle = useCallback(() => {
    keycloak.login({
      idpHint: 'google',
      redirectUri: window.location.origin,
    });
  }, []);

  // ====================================================
  // REGISTER
  // ====================================================

  const register = useCallback(async (payload) => {
    setLoading(true);
    try {
      const { data } = await authService.register(payload);
      return data;
    } finally {
      setLoading(false);
    }
  }, []);

  // ====================================================
  // CONTEXT VALUE
  // ====================================================

  const value = useMemo(
    () => ({
      token,
      user,
      role: user?.role || null,
      isAuthenticated: Boolean(token && user),
      loading,
      login,
      loginWithGoogle,
      register,
      logout,
    }),
    [token, user, loading, login, loginWithGoogle, register, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// ======================================================
// USE AUTH HOOK
// ======================================================

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
