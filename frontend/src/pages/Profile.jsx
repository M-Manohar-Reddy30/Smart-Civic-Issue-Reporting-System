import React, { useState, useEffect } from 'react';
import { userService } from '../services/api';
import { User, Mail, Shield, Award, FileText, Calendar, AlertCircle } from 'lucide-react';

const Profile = () => {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        try {
            const response = await userService.getProfile();
            setProfile(response.data);
        } catch (err) {
            setError('Failed to load profile data');
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div className="dashboard-container">Loading profile...</div>;

    return (
        <div className="dashboard-container">
            <header style={{ marginBottom: '2.5rem' }}>
                <h1 style={{ fontSize: '2rem', fontWeight: 'bold' }}>My Profile</h1>
                <p style={{ color: 'var(--text-secondary)' }}>Manage your account and track your civic impact.</p>
            </header>

            {error && (
                <div style={{ 
                    backgroundColor: 'rgba(239, 68, 68, 0.1)', 
                    color: '#ef4444', 
                    padding: '1rem', 
                    borderRadius: '0.5rem', 
                    marginBottom: '1.5rem' 
                }}>
                    {error}
                </div>
            )}

            {profile && (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '2rem' }}>
                    {/* User Info Card */}
                    <div className="card">
                        <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', marginBottom: '2rem' }}>
                            <div style={{ 
                                width: '80px', 
                                height: '80px', 
                                backgroundColor: 'var(--accent-blue)', 
                                borderRadius: '50%', 
                                display: 'flex', 
                                alignItems: 'center', 
                                justifyContent: 'center',
                                color: 'white',
                                fontSize: '2rem',
                                fontWeight: 'bold'
                            }}>
                                {profile.name.charAt(0).toUpperCase()}
                            </div>
                            <div>
                                <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>{profile.name}</h2>
                                <span style={{ 
                                    backgroundColor: profile.role === 'ADMIN' ? 'rgba(59, 130, 246, 0.1)' : 'rgba(16, 185, 129, 0.1)',
                                    color: profile.role === 'ADMIN' ? '#3b82f6' : '#10b981',
                                    padding: '0.25rem 0.75rem',
                                    borderRadius: '1rem',
                                    fontSize: '0.75rem',
                                    fontWeight: '600',
                                    marginTop: '0.5rem',
                                    display: 'inline-block'
                                }}>
                                    {profile.role}
                                </span>
                            </div>
                        </div>

                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                                <Mail size={18} color="var(--text-secondary)" />
                                <div>
                                    <p style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Email Address</p>
                                    <p style={{ fontWeight: '500' }}>{profile.email}</p>
                                </div>
                            </div>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                                <Shield size={18} color="var(--text-secondary)" />
                                <div>
                                    <p style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Account Status</p>
                                    <p style={{ fontWeight: '500' }}>Active</p>
                                </div>
                            </div>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                                <Calendar size={18} color="var(--text-secondary)" />
                                <div>
                                    <p style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Member Since</p>
                                    <p style={{ fontWeight: '500' }}>{new Date(profile.createdAt).toLocaleDateString()}</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Impact Stats Card */}
                    <div className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
                        <h3 style={{ fontSize: '1.25rem', fontWeight: 'bold', marginBottom: '2rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <Award color="#fbbf24" /> Your Community Impact
                        </h3>
                        
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
                            <div style={{ 
                                backgroundColor: 'rgba(59, 130, 246, 0.05)', 
                                padding: '1.5rem', 
                                borderRadius: '1rem',
                                textAlign: 'center'
                            }}>
                                <Award size={32} color="#3b82f6" style={{ marginBottom: '0.5rem' }} />
                                <p style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#3b82f6' }}>{profile.points}</p>
                                <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Total Points</p>
                            </div>
                            <div style={{ 
                                backgroundColor: 'rgba(16, 185, 129, 0.05)', 
                                padding: '1.5rem', 
                                borderRadius: '1rem',
                                textAlign: 'center'
                            }}>
                                <FileText size={32} color="#10b981" style={{ marginBottom: '0.5rem' }} />
                                <p style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#10b981' }}>{profile.issuesCount}</p>
                                <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Issues Reported</p>
                            </div>
                        </div>

                        <div style={{ 
                            marginTop: '2rem', 
                            padding: '1rem', 
                            backgroundColor: 'var(--bg-secondary)', 
                            borderRadius: '0.5rem',
                            fontSize: '0.875rem',
                            display: 'flex',
                            gap: '0.75rem'
                        }}>
                            <AlertCircle size={20} color="var(--accent-blue)" />
                            <p>You earn <strong>10 points</strong> for every report and <strong>20 points</strong> when it gets resolved!</p>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Profile;
