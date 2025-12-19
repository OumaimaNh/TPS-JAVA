import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import Login from './Login';
import api from './api';

// Mock the api module
jest.mock('./api');

describe('Login Component', () => {
  let mockOnLoginSuccess;

  beforeEach(() => {
    mockOnLoginSuccess = jest.fn();
    // Clear localStorage before each test
    localStorage.clear();
    // Clear all mocks
    jest.clearAllMocks();
  });

  test('renders login form with username and password fields', () => {
    render(<Login onLoginSuccess={mockOnLoginSuccess} />);
    
    expect(screen.getByRole('heading', { name: /login/i })).toBeInTheDocument();
    expect(screen.getByText(/username:/i)).toBeInTheDocument();
    expect(screen.getByText(/password:/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
  });

  test('submits form with valid credentials and stores token', async () => {
    const mockToken = 'mock-jwt-token-12345';
    api.post.mockResolvedValue({
      data: { token: mockToken }
    });

    render(<Login onLoginSuccess={mockOnLoginSuccess} />);
    
    // Fill in the form
    const inputs = screen.getAllByRole('textbox');
    const passwordInputs = document.querySelectorAll('input[type="password"]');
    
    fireEvent.change(inputs[0], {
      target: { value: 'testuser' }
    });
    fireEvent.change(passwordInputs[0], {
      target: { value: 'testpassword' }
    });

    // Submit the form
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    // Wait for async operations
    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/auth/login', {
        username: 'testuser',
        password: 'testpassword'
      });
    });

    // Verify token is stored in localStorage
    expect(localStorage.getItem('token')).toBe(mockToken);
    
    // Verify callback is called
    expect(mockOnLoginSuccess).toHaveBeenCalledTimes(1);
  });

  test('displays error message on invalid credentials (401)', async () => {
    // Mock alert
    window.alert = jest.fn();
    
    api.post.mockRejectedValue({
      response: { status: 401 }
    });

    render(<Login onLoginSuccess={mockOnLoginSuccess} />);
    
    const inputs = screen.getAllByRole('textbox');
    const passwordInputs = document.querySelectorAll('input[type="password"]');
    
    fireEvent.change(inputs[0], {
      target: { value: 'wronguser' }
    });
    fireEvent.change(passwordInputs[0], {
      target: { value: 'wrongpassword' }
    });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith('Error: Invalid credentials');
    });

    // Verify token is NOT stored
    expect(localStorage.getItem('token')).toBeNull();
    
    // Verify callback is NOT called
    expect(mockOnLoginSuccess).not.toHaveBeenCalled();
  });

  test('displays error message on network failure', async () => {
    window.alert = jest.fn();
    
    api.post.mockRejectedValue(new Error('Network error'));

    render(<Login onLoginSuccess={mockOnLoginSuccess} />);
    
    const inputs = screen.getAllByRole('textbox');
    const passwordInputs = document.querySelectorAll('input[type="password"]');
    
    fireEvent.change(inputs[0], {
      target: { value: 'testuser' }
    });
    fireEvent.change(passwordInputs[0], {
      target: { value: 'testpassword' }
    });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith('Error: Login failed');
    });
  });
});
