import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import Home from './Home';
import api from './api';

// Mock the api module
jest.mock('./api');

describe('Home Component', () => {
  beforeEach(() => {
    // Clear all mocks
    jest.clearAllMocks();
  });

  test('renders home component with title', () => {
    api.get.mockResolvedValue({ data: 'Secure data' });
    
    render(<Home />);
    
    expect(screen.getByText(/Home - Protected Content/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /refresh data/i })).toBeInTheDocument();
  });

  test('fetches and displays secure data on mount with token', async () => {
    const mockMessage = 'Welcome to secure area!';
    api.get.mockResolvedValue({ data: mockMessage });

    render(<Home />);

    // Initially shows loading
    expect(screen.getByText(/loading/i)).toBeInTheDocument();

    // Wait for API call
    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith('/api/secure');
    });

    // Verify message is displayed
    expect(screen.getByText(mockMessage)).toBeInTheDocument();
  });

  test('displays error message when API call fails with 401', async () => {
    api.get.mockRejectedValue({
      response: { status: 401 }
    });

    render(<Home />);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith('/api/secure');
    });

    // Verify error message is displayed
    expect(screen.getByText(/Error: Unauthorized - Please login again/i)).toBeInTheDocument();
  });

  test('displays error message on network failure', async () => {
    api.get.mockRejectedValue(new Error('Network error'));

    render(<Home />);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith('/api/secure');
    });

    expect(screen.getByText(/Error: Failed to fetch secure data/i)).toBeInTheDocument();
  });

  test('refresh button fetches data again', async () => {
    const mockMessage = 'Refreshed secure data';
    api.get.mockResolvedValue({ data: mockMessage });

    render(<Home />);

    // Wait for initial load
    await waitFor(() => {
      expect(api.get).toHaveBeenCalledTimes(1);
    });

    // Click refresh button
    fireEvent.click(screen.getByRole('button', { name: /refresh data/i }));

    // Wait for second API call
    await waitFor(() => {
      expect(api.get).toHaveBeenCalledTimes(2);
    });

    expect(screen.getByText(mockMessage)).toBeInTheDocument();
  });
});
