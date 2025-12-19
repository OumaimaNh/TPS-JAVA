import React, { useState, useEffect } from 'react';
import './App.css';
import Signup from './Signup';
import Login from './Login';
import Home from './Home';

function App() {
  const [isLogged, setIsLogged] = useState(false);
  const [activeTab, setActiveTab] = useState('login');

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      setIsLogged(true);
    }
  }, []);

  const handleLoginSuccess = () => {
    setIsLogged(true);
  };

  const logout = () => {
    localStorage.removeItem('token');
    setIsLogged(false);
  };

  if (isLogged) {
    return <Home onLogout={logout} />;
  }

  return (
    <div className="App">
      <div className="welcome-panel">
        <h1>Welcome</h1>
        <p>Enter your details and sign up here</p>
        <div className="tab-buttons">
          <button
            className={`tab-button ${activeTab === 'signup' ? 'active' : 'inactive'}`}
            onClick={() => setActiveTab('signup')}
          >
            Sign Up
          </button>
          <button
            className={`tab-button ${activeTab === 'login' ? 'active' : 'inactive'}`}
            onClick={() => setActiveTab('login')}
          >
            Login
          </button>
        </div>
      </div>
      <div className="form-panel">
        {activeTab === 'login' ? (
          <Login onLoginSuccess={handleLoginSuccess} />
        ) : (
          <Signup onSignupSuccess={() => setActiveTab('login')} />
        )}
      </div>
    </div>
  );
}

export default App;
