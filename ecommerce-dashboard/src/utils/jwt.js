// Minimal, dependency-free JWT payload decoder (no signature verification —
// verification always happens server-side; this is purely for reading claims
// like role/username/exp on the client to drive UI state).
export function decodeJwt(token) {
  try {
    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
    const json = decodeURIComponent(
      atob(padded)
        .split('')
        .map((c) => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
        .join('')
    );
    return JSON.parse(json);
  } catch (err) {
    return null;
  }
}

export function isTokenExpired(token) {
  const claims = decodeJwt(token);
  if (!claims || !claims.exp) return false; // if no exp claim, treat as non-expiring
  const nowInSeconds = Date.now() / 1000;
  return claims.exp < nowInSeconds;
}
