import { useState, useEffect } from 'react'
import { issueService } from '../services/api'
import { MapPin, Tag, FileText, Upload, Trophy, Info, Search, AlertCircle } from 'lucide-react'

const getImageUrl = (path) => `http://localhost:8080/uploads/${path}`;

function UserDashboard({ publicView = false }) {
  const [issues, setIssues] = useState([])
  const [stats, setStats] = useState({ total: 0, resolved: 0, citizenScore: 0 })
  const [loading, setLoading] = useState(false)
  const [fetchLoading, setFetchLoading] = useState(true)
  const [notification, setNotification] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [formData, setFormData] = useState({
    category: 'Pothole',
    description: '',
    location: '',
    image: null
  })

  useEffect(() => {
    fetchIssues()
    fetchStats()
  }, [publicView])

  const showNotification = (message, type = 'success') => {
    setNotification({ message, type })
    setTimeout(() => setNotification(null), 4000)
  }

  const fetchIssues = async () => {
    setFetchLoading(true)
    try {
      let response;
      if (searchTerm) {
        response = await issueService.searchByLocation(searchTerm);
      } else if (publicView) {
        response = await issueService.getAllIssues();
      } else {
        response = await issueService.getMyIssues();
      }
      setIssues(response.data)
    } catch (error) {
      console.error('Error fetching issues:', error)
    } finally {
      setFetchLoading(false)
    }
  }

  const fetchStats = async () => {
    try {
      const response = await issueService.getStats()
      setStats(response.data)
    } catch (error) {
      console.error('Error fetching stats:', error)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    
    const data = new FormData()
    data.append('category', formData.category)
    data.append('description', formData.description)
    data.append('location', formData.location)
    if (formData.image) {
      data.append('image', formData.image)
    }

    try {
      await issueService.createIssue(data)
      setFormData({ category: 'Pothole', description: '', location: '', image: null })
      showNotification('Your issue has been reported successfully! +10 Points.')
      fetchIssues()
      fetchStats()
    } catch (error) {
      showNotification(error.response?.data || 'Failed to report issue.', 'error')
    } finally {
      setLoading(false)
    }
  }

  const getStatusClass = (status) => {
    switch (status) {
      case 'Resolved': return 'badge-resolved';
      case 'In Progress': return 'badge-in-progress';
      default: return 'badge-pending';
    }
  }

  return (
    <div style={{display: 'flex', flexDirection: 'column', gap: '2.5rem'}}>
      {notification && (
        <div style={{
          position: 'fixed', top: '20px', right: '20px', zIndex: 1000,
          backgroundColor: notification.type === 'error' ? '#ef4444' : '#10b981',
          color: 'white', padding: '1rem 1.5rem', borderRadius: '8px',
          boxShadow: '0 10px 15px -3px rgba(0,0,0,0.3)',
          animation: 'slideIn 0.3s ease-out'
        }}>
          {notification.message}
        </div>
      )}

      {/* Main Layout: Split into Form (if authenticated) and Stats */}
      <div style={{display: 'grid', gridTemplateColumns: publicView ? '1fr' : '2fr 1fr', gap: '2rem'}}>
        {!publicView && (
          <section className="card">
            <div style={{marginBottom: '1.5rem'}}>
              <h2 style={{margin: '0 0 0.5rem 0'}}>Report Issue</h2>
              <p style={{margin: 0, color: 'var(--text-secondary)', fontSize: '0.875rem'}}>Share concerns to improve community safety and quality of life.</p>
            </div>
            
            <form onSubmit={handleSubmit}>
              <div className="grid" style={{gridTemplateColumns: '1fr 1fr'}}>
                <div className="form-group">
                  <label><Tag size={14} /> Category</label>
                  <select 
                    value={formData.category} 
                    onChange={(e) => setFormData({...formData, category: e.target.value})}
                  >
                    <option>Pothole</option>
                    <option>Garbage</option>
                    <option>Streetlight</option>
                    <option>Water Leak</option>
                    <option>Road Damage</option>
                    <option>Other</option>
                  </select>
                </div>
                <div className="form-group">
                  <label><MapPin size={14} /> Location</label>
                  <input 
                    type="text" 
                    placeholder="Street name, landmark..." 
                    value={formData.location} 
                    onChange={(e) => setFormData({...formData, location: e.target.value})}
                    required
                  />
                </div>
              </div>
              
              <div className="form-group">
                <label><FileText size={14} /> Description</label>
                <textarea 
                  rows="3" 
                  placeholder="What exactly is the problem?" 
                  value={formData.description} 
                  onChange={(e) => setFormData({...formData, description: e.target.value})}
                  required
                ></textarea>
              </div>
              
              <div className="form-group">
                <label><Upload size={14} /> Attachment (Optional)</label>
                <input 
                  type="file" 
                  accept="image/*"
                  onChange={(e) => setFormData({...formData, image: e.target.files[0]})}
                  style={{marginTop: '0.5rem'}}
                />
              </div>
              
              <button type="submit" disabled={loading} style={{width: '100%', marginTop: '1rem'}}>
                {loading ? 'Submitting...' : 'Register Issue'}
              </button>
            </form>
          </section>
        )}

        {/* Impact/Stats Section */}
        <section style={{display: 'flex', flexDirection: 'column', gap: '1.5rem'}}>
          <div className="card" style={{
            background: 'linear-gradient(135deg, var(--bg-card) 0%, rgba(59, 130, 246, 0.1) 100%)',
            border: '1px solid var(--accent-blue)',
            textAlign: 'center'
          }}>
            <Trophy size={48} color="#fbbf24" style={{margin: '0 auto 1rem auto'}} />
            <h3 style={{margin: '0 0 0.5rem 0'}}>Community Impact</h3>
            <div style={{fontSize: '2.5rem', fontWeight: 800, color: 'var(--text-primary)'}}>{stats.citizenScore}</div>
            <p style={{margin: '0.5rem 0 0 0', color: 'var(--text-secondary)', fontSize: '0.8rem'}}>GLOBAL CITIZEN POINTS</p>
            <div style={{marginTop: '1rem', display: 'grid', gridTemplateColumns: '1fr 1fr', borderTop: '1px solid var(--border-color)', paddingTop: '1rem'}}>
              <div>
                <div style={{fontWeight: 700}}>{stats.total}</div>
                <div style={{fontSize: '0.6rem', color: 'var(--text-secondary)'}}>TOTAL ISSUES</div>
              </div>
              <div>
                <div style={{fontWeight: 700}}>{stats.resolved}</div>
                <div style={{fontSize: '0.6rem', color: 'var(--text-secondary)'}}>RESOLVED</div>
              </div>
            </div>
          </div>
          
          <div className="card" style={{backgroundColor: 'rgba(16, 185, 129, 0.05)', borderColor: '#10b981'}}>
            <h4 style={{margin: '0 0 0.5rem 0', display: 'flex', alignItems: 'center', gap: '8px', color: '#10b981'}}>
              <Info size={16} /> Civic Mission
            </h4>
            <p style={{margin: 0, fontSize: '0.8rem', color: 'var(--text-secondary)'}}>
              Reporting issues makes our city better. Resolved issues earn reporters <strong>+20 points</strong>!
            </p>
          </div>
        </section>
      </div>

      {/* Community Feed / My Issues Section */}
      <section>
        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem'}}>
          <h2 style={{margin: 0}}>{publicView ? 'Community Feed' : 'My Reported Issues'}</h2>
          <div style={{display: 'flex', gap: '0.5rem'}}>
             <div style={{position: 'relative'}}>
                <Search size={14} style={{position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-secondary)'}} />
                <input 
                  type="text" 
                  placeholder="Filter by location..." 
                  style={{paddingLeft: '2.2rem', marginTop: 0, width: '200px', fontSize: '0.875rem'}}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  onKeyUp={(e) => e.key === 'Enter' && fetchIssues()}
                />
             </div>
             <button className="secondary" onClick={fetchIssues} style={{padding: '0.5rem 1rem'}}>
               Refresh
             </button>
          </div>
        </div>
        
        {fetchLoading ? (
          <div style={{textAlign: 'center', padding: '5rem', color: 'var(--text-secondary)'}}>Loading reports...</div>
        ) : (
          <div className="grid">
            {issues.map((issue) => (
              <div key={issue.id} className="card" style={{display: 'flex', flexDirection: 'column'}}>
                <div className="flex justify-between" style={{marginBottom: '1rem', alignItems: 'center'}}>
                  <span className={`badge ${getStatusClass(issue.status)}`}>
                    {issue.status}
                  </span>
                  <span style={{fontSize: '0.75rem', color: 'var(--text-secondary)'}}>
                    {new Date(issue.createdAt).toLocaleDateString()}
                  </span>
                </div>
                
                {issue.imagePath && (
                  <img 
                    src={getImageUrl(issue.imagePath)} 
                    alt="Issue" 
                    style={{width: '100%', height: '180px', objectFit: 'cover', borderRadius: '8px', marginBottom: '1rem'}} 
                  />
                )}

                <h3 style={{margin: '0 0 0.5rem 0'}}>{issue.category}</h3>
                <p style={{margin: '0 0 1rem 0', fontSize: '0.875rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '4px'}}>
                  <MapPin size={12} /> {issue.location}
                </p>
                <p style={{fontSize: '0.9rem', flex: 1}}>{issue.description}</p>

                {(issue.proofStartPath || issue.proofEndPath) && (
                  <div style={{marginTop: '1.5rem', borderTop: '1px solid var(--border-color)', paddingTop: '1rem'}}>
                    <div className="proof-gallery" style={{display: 'flex', gap: '10px'}}>
                      {issue.proofStartPath && (
                        <div className="proof-item" style={{flex: 1}}>
                          <img src={getImageUrl(issue.proofStartPath)} alt="Start" style={{width: '100%', height: '60px', objectFit: 'cover', borderRadius: '4px'}} />
                          <div style={{fontSize: '0.5rem', textAlign: 'center', marginTop: '2px'}}>STARTED</div>
                        </div>
                      )}
                      {issue.proofEndPath && (
                        <div className="proof-item" style={{flex: 1}}>
                          <img src={getImageUrl(issue.proofEndPath)} alt="End" style={{width: '100%', height: '60px', objectFit: 'cover', borderRadius: '4px'}} />
                          <div style={{fontSize: '0.5rem', textAlign: 'center', marginTop: '2px'}}>RESOLVED</div>
                        </div>
                      )}
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
        {!fetchLoading && issues.length === 0 && (
          <div className="card" style={{textAlign: 'center', padding: '5rem', borderStyle: 'dashed'}}>
            <Info size={32} style={{margin: '0 auto 1rem auto', color: 'var(--text-secondary)'}} />
            <h3 style={{margin: 0, color: 'var(--text-secondary)'}}>No Issues Found</h3>
            <p style={{margin: '0.5rem 0 0 0', color: 'var(--text-secondary)', fontSize: '0.875rem'}}>
              {publicView ? 'Be the first to report an issue in this area.' : 'You haven\'t reported any issues yet.'}
            </p>
          </div>
        )}
      </section>
    </div>
  )
}

export default UserDashboard
