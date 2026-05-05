import React, { useState, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route, Link, Navigate, useNavigate, useLocation } from 'react-router-dom'
import UserDashboard from './pages/UserDashboard'
import AdminDashboard from './pages/AdminDashboard'
import Login from './pages/Login'
import Signup from './pages/Signup'
import Profile from './pages/Profile'
import ProtectedRoute from './components/ProtectedRoute'
import { authService } from './services/api'
import { LayoutDashboard, User, LogOut, LogIn, UserPlus, Globe } from 'lucide-react'

function AppContent() {
  const [user, setUser] = useState(authService.getCurrentUser());
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    setUser(authService.getCurrentUser());
  }, [location]);

  const handleLogout = () => {
    authService.logout();
    setUser(null);
    navigate('/login');
  };

  return (
    <div className="app-container">
      <header>
        <div className="logo">
          <Link to="/" style={{ textDecoration: 'none', color: 'inherit' }}>
            <h1>CivicReport Pro</h1>
            <p style={{margin: 0, fontSize: '0.8rem', color: 'var(--text-secondary)'}}>Smart Civic Issue Tracking</p>
          </Link>
        </div>
        <nav>
          {/* Public Link */}
          <Link to="/" className={location.pathname === '/' ? 'active' : ''}>
            <Globe size={18} /> Community Feed
          </Link>

          {user ? (
            <>
              {/* Common Auth Links */}
              <Link to="/dashboard" className={location.pathname === '/dashboard' ? 'active' : ''}>
                <LayoutDashboard size={18} /> Dashboard
              </Link>
              
              {/* Admin specific */}
              {user.role === 'ADMIN' && (
                <Link to="/admin" className={location.pathname === '/admin' ? 'active' : ''}>
                  Admin Panel
                </Link>
              )}

              <Link to="/profile" className={location.pathname === '/profile' ? 'active' : ''}>
                <User size={18} /> Profile
              </Link>
              
              <button onClick={handleLogout} className="btn-logout" style={{ 
                background: 'none', 
                border: 'none', 
                color: '#ef4444', 
                cursor: 'pointer', 
                display: 'flex', 
                alignItems: 'center', 
                gap: '0.5rem',
                fontWeight: '500'
              }}>
                <LogOut size={18} /> Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className={location.pathname === '/login' ? 'active' : ''}>
                <LogIn size={18} /> Login
              </Link>
              <Link to="/signup" className={location.pathname === '/signup' ? 'active' : ''}>
                <UserPlus size={18} /> Signup
              </Link>
            </>
          )}
        </nav>
      </header>

      <main>
        <Routes>
          {/* Public Home Page - Shows all issues */}
          <Route path="/" element={<UserDashboard publicView={true} />} />
          
          {/* Auth Routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          
          {/* Protected Routes */}
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <UserDashboard />
            </ProtectedRoute>
          } />
          
          <Route path="/profile" element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          } />
          
          <Route path="/admin" element={
            <ProtectedRoute adminOnly={true}>
              <AdminDashboard />
            </ProtectedRoute>
          } />

          {/* Catch all */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      
      <footer style={{marginTop: '4rem', padding: '2rem 0', borderTop: '1px solid var(--border-color)', textAlign: 'center', color: 'var(--text-secondary)', fontSize: '0.875rem'}}>
        &copy; 2026 Smart Civic Issue Reporting System. Built for society.
      </footer>
    </div>
  );
}

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App
