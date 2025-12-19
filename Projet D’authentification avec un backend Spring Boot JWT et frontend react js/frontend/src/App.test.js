import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import App from './App';

// Mock child components
jest.mock('./Signup', () => {
  return function Signup() {
    return <div data-testid="signup-component">Signup Component</div>;
  };
});

jest.mock('./Login', () => {
  return function Login({ onLoginSuccess }) {
    return (
      <div data-testid="login-component">
        Login Component
        <button onClick={onLoginSuccess}>Mock Login</button>
      </div>
    );
  };
});

jest.mock('./Home', () => {
  return function Home() {
    return <div data-testid="home-component">Home Component</div>;
  };
});

describe('App Component', () => {
  beforeEach(() => {
    // Clear localStorage before each test
    localStorage.clear();
  });

  test('renders app with title', () => {
    render(<App />);
    
    expect(screen.getByText(/JWT Authentication System/i)).toBeInTheDocument();
  });

  test('renders Signup and Login components when not authenticated', () => {
    render(<App />);
    
    expect(screen.getByTestId('signup-component')).toBeInTheDocument();
    expect(screen.getByTestId('login-component')).toBeInTheDocument();
    expect(screen.queryByTestId('home-component')).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /logout/i })).not.toBeInTheDocument();
  });

  test('renders Home component and Logout button when authenticated', () => {
    // Set token in localStorage
    localStorage.setItem('token', 'mock-jwt-token');
    
    render(<App />);
    
    expect(screen.getByTestId('home-component')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /logout/i })).toBeInTheDocument();
    expect(screen.queryByTestId('signup-component')).not.toBeInTheDocument();
    expect(screen.queryByTestId('login-component')).not.toBeInTheDocument();
  });

  test('switches to authenticated view after successful login', async () => {
    render(<App />);
    
    // Initially not authenticated
    expect(screen.getByTestId('login-component')).toBeInTheDocument();
    expect(screen.queryByTestId('home-component')).not.toBeInTheDocument();
    
    // Simulate successful login
    const mockLoginButton = screen.getByText('Mock Login');
    fireEvent.click(mockLoginButton);
    
    // Wait for state update
    await waitFor(() => {
      expect(screen.getByTestId('home-component')).toBeInTheDocument();
    });
    
    expect(screen.queryByTestId('login-component')).not.toBeInTheDocument();
  });

  test('logout removes token and switches to unauthenticated view', async () => {
    // Set token in localStorage
    localStorage.setItem('token', 'mock-jwt-token');
    
    render(<App />);
    
    // Initially authenticated
    expect(screen.getByTestId('home-component')).toBeInTheDocument();
    expect(localStorage.getItem('token')).toBe('mock-jwt-token');
    
    // Click logout
    fireEvent.click(screen.getByRole('button', { name: /logout/i }));
    
    // Wait for state update
    await waitFor(() => {
      expect(screen.getByTestId('login-component')).toBeInTheDocument();
    });
    
    // Verify token is removed
    expect(localStorage.getItem('token')).toBeNull();
    expect(screen.queryByTestId('home-component')).not.toBeInTheDocument();
  });

  test('checks for token in localStorage on mount', () => {
    localStorage.setItem('token', 'existing-token');
    
    render(<App />);
    
    // Should render authenticated view
    expect(screen.getByTestId('home-component')).toBeInTheDocument();
  });
});
