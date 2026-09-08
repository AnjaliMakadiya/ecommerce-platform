// import { StrictMode } from 'react';
// import { createRoot } from 'react-dom/client';

// import App from './App.jsx';
// import './index.css';

// import keycloak from './auth/keycloak';

// keycloak
//   .init({
//     onLoad: 'check-sso',
//     pkceMethod: 'S256',
//     checkLoginIframe: false,
//   })
//   .then(() => {
//     createRoot(document.getElementById('root')).render(
//       <StrictMode>
//         <App />
//       </StrictMode>
//     );
//   })
//   .catch((error) => {
//     console.error('Keycloak initialization failed:', error);

//     // Don't leave the user on a blank white screen if Keycloak is unreachable
//     createRoot(document.getElementById('root')).render(
//       <StrictMode>
//         <App />
//       </StrictMode>
//     );
//   });

import React from 'react';
import ReactDOM from 'react-dom/client';

import App from './App';

import { AuthProvider } from './context/AuthContext';
  
import keycloak from './auth/keycloak';

import './index.css';

async function startApplication() {

  try {

    const authenticated = await keycloak.init({
      onLoad: 'check-sso',
      pkceMethod: 'S256',
      checkLoginIframe: false,
    });

    console.log(
      'Keycloak initialized:',
      authenticated
    );

    if (authenticated) {

      console.log(
        'Keycloak user authenticated'
      );

      console.log(
        'Keycloak token:',
        keycloak.token
      );

      console.log(
        'Keycloak user:',
        keycloak.tokenParsed
      );

    } else {

      console.log(
        'No active Keycloak session'
      );

    }

    ReactDOM.createRoot(
      document.getElementById('root')
    ).render(

      <React.StrictMode>

        <AuthProvider>

          <App />

        </AuthProvider>

      </React.StrictMode>

    );

  } catch (error) {

    console.error(
      'Keycloak initialization failed:',
      error
    );

  }

}

startApplication();