import React, { useState, useEffect } from 'react';
import api from './api';

function Home({ onLogout }) {
  const [message, setMessage] = useState('');
  const [lastUpdated, setLastUpdated] = useState('');
  const [username, setUsername] = useState('');

  useEffect(() => {
    getSecureData();
  }, []);

  const getSecureData = async () => {
    try {
      const response = await api.get('/api/secure');
      setMessage(response.data);
      const now = new Date();
      setLastUpdated(now.toLocaleTimeString());
      
      // Extract username from message
      const match = response.data.match(/Welcome (\w+)!/);
      if (match) {
        setUsername(match[1]);
      }
    } catch (error) {
      if (error.response && error.response.status === 401) {
        setMessage('Error: Unauthorized - Please login again');
      } else if (error.response && error.response.data) {
        setMessage('Error: ' + error.response.data);
      } else {
        setMessage('Error: Failed to fetch secure data');
      }
    }
  };

  return (
    <div className="App">
      <div className="welcome-panel dashboard-left">
        <div className="dashboard-icon">
          <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7v10c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V7l-10-5zm0 10c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm0 4c-2.67 0-8 1.34-8 4v1h16v-1c0-2.66-5.33-4-8-4z"/>
          </svg>
        </div>
        <h1>Dashboard</h1>
        <p>Your secure space</p>
        {username && <h2 className="username-display">Hello, {username}!</h2>}
      </div>
      <div className="form-panel dashboard-right">
        <div className="dashboard-content">
          <h2>🎉 Welcome to Your Dashboard</h2>
          <div className="message-box">
            <p>{message || 'Loading...'}</p>
          </div>
          {lastUpdated && (
            <p className="last-updated">Last updated: {lastUpdated}</p>
          )}
          <div className="dashboard-actions">
            <button onClick={getSecureData} className="submit-button">
              Refresh Data
            </button>
            <button onClick={onLogout} className="logout-button">
              Logout
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Home;
