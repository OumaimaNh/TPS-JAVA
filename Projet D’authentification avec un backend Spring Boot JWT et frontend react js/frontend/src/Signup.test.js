import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import Signup from './Signup';
import api from './api';

// Mock the api module
jest.mock('./api');

describe('Signup Component', () => {
  beforeEach(() => {
    // Clear all mocks
    jest.clearAllMocks();
  });

  test('renders signup form with username, email, and password fields', () => {
    render(<Signup />);
    
    expect(screen.getByText('Signup')).toBeInTheDocument();
    expect(screen.getByText(/username:/i)).toBeInTheDocument();
    expect(screen.getByText(/email:/i)).toBeInTheDocument();
    expect(screen.getByText(/password:/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /sign up/i })).toBeInTheDocument();
  });

  test('submits form with valid data and displays success message', async () => {
    window.alert = jest.fn();
    
    api.post.mockResolvedValue({
      data: 'User created successfully'
    });

    render(<Signup />);
    
    // Fill in the form
    const inputs = screen.getAllByRole('textbox');
    const usernameInput = inputs[0];
    const emailInput = inputs[1];
    const passwordInputs = document.querySelectorAll('input[type="password"]');
    const passwordInput = passwordInputs[0];
    
    fireEvent.change(usernameInput, { target: { value: 'newuser' } });
    fireEvent.change(emailInput, { target: { value: 'newuser@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });

    // Submit the form
    fireEvent.click(screen.getByRole('button', { name: /sign up/i }));

    // Wait for async operations
    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/auth/signup', {
        username: 'newuser',
        email: 'newuser@example.com',
        password: 'password123'
      });
    });

    // Verify success alert and form is cleared
    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith('Success: User created successfully');
    });
    
    // Verify form is cleared by checking the inputs again
    await waitFor(() => {
      const clearedInputs = screen.getAllByRole('textbox');
      const clearedPasswordInputs = document.querySelectorAll('input[type="password"]');
      expect(clearedInputs[0].value).toBe('');
      expect(clearedInputs[1].value).toBe('');
      expect(clearedPasswordInputs[0].value).toBe('');
    });
  });

  test('displays error message when username already exists', async () => {
    window.alert = jest.fn();
    
    api.post.mockRejectedValue({
      response: { data: 'Username already taken' }
    });

    render(<Signup />);
    
    const inputs = screen.getAllByRole('textbox');
    const passwordInputs = document.querySelectorAll('input[type="password"]');
    
    fireEvent.change(inputs[0], {
      target: { value: 'existinguser' }
    });
    fireEvent.change(inputs[1], {
      target: { value: 'existing@example.com' }
    });
    fireEvent.change(passwordInputs[0], {
      target: { value: 'password123' }
    });
    fireEvent.click(screen.getByRole('button', { name: /sign up/i }));

    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith('Error: Username already taken');
    });
  });

  test('displays generic error message on network failure', async () => {
    window.alert = jest.fn();
    
    api.post.mockRejectedValue(new Error('Network error'));

    render(<Signup />);
    
    const inputs = screen.getAllByRole('textbox');
    const passwordInputs = document.querySelectorAll('input[type="password"]');
    
    fireEvent.change(inputs[0], {
      target: { value: 'testuser' }
    });
    fireEvent.change(inputs[1], {
      target: { value: 'test@example.com' }
    });
    fireEvent.change(passwordInputs[0], {
      target: { value: 'password123' }
    });
    fireEvent.click(screen.getByRole('button', { name: /sign up/i }));

    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith('Error: Signup failed');
    });
  });
});
