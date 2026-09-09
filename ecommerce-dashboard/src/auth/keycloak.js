import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  // url: 'http://localhost:8181',
  url: 'https://anjali-ecommerce.duckdns.org/auth',
  realm: 'ecommerce-realm',
  clientId: 'ecommerce-frontend',
});

export default keycloak;