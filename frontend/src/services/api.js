import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
});

// Add a request interceptor to include the JWT token in headers
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

export const authService = {
    login: async (email, password) => {
        const response = await api.post('/auth/login', { email, password });
        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('user', JSON.stringify(response.data));
        }
        return response.data;
    },
    signup: async (name, email, password) => {
        const response = await api.post('/auth/signup', { name, email, password });
        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('user', JSON.stringify(response.data));
        }
        return response.data;
    },
    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },
    getCurrentUser: () => {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    }
};

export const issueService = {
    getAllIssues: () => api.get('/issues'),
    getMyIssues: () => api.get('/issues/my'),
    getStats: () => api.get('/issues/stats'),
    createIssue: (formData) => api.post('/issues', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    }),
    updateStatus: (id, status, proofFile) => {
        const formData = new FormData();
        formData.append('status', status);
        if (proofFile) formData.append('proof', proofFile);
        return api.put(`/issues/${id}/status`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },
    searchByLocation: (location) => api.get(`/issues/search?location=${location}`)
};

export const userService = {
    getProfile: () => api.get('/user/profile')
};

export default api;
