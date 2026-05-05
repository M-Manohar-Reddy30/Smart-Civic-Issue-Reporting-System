import { useState, useEffect } from 'react'
import { issueService } from '../services/api'
import { ShieldCheck, ArrowRight, Camera, X, CheckCircle, BarChart3, Clock, CheckCircle2, AlertCircle } from 'lucide-react'

const getImageUrl = (path) => `http://localhost:8080/uploads/${path}`;

function AdminDashboard() {
  const [issues, setIssues] = useState([])
  const [stats, setStats] = useState({ total: 0, pending: 0, inProgress: 0, resolved: 0 })
  const [loading, setLoading] = useState(false)
  const [fetchLoading, setFetchLoading] = useState(true)
  const [uploadingFor, setUploadingFor] = useState(null)
  const [proofFile, setProofFile] = useState(null)
  const [notification, setNotification] = useState(null)

  useEffect(() => {
    fetchIssues()
    fetchStats()
  }, [])

  const showNotification = (message, type = 'success') => {
    setNotification({ message, type })
    setTimeout(() => setNotification(null), 4000)
  }

  const fetchIssues = async () => {
    setFetchLoading(true)
    try {
      const response = await issueService.getAllIssues()
      setIssues(response.data)
    } catch (error) {
      console.error('Error fetching issues:', error)
      if (error.response?.status === 403) {
          showNotification('Access denied. Admin role required.', 'error')
      }
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

  const prepareUpdate = (id, currentStatus) => {
    let nextStatus = ''
    if (currentStatus === 'Pending') nextStatus = 'In Progress'
    else if (currentStatus === 'In Progress') nextStatus = 'Resolved'
    else return

    setUploadingFor({ id, nextStatus })
  }

  const handleUpdateStatus = async () => {
    if (!proofFile) return

    setLoading(true)
    try {
      await issueService.updateStatus(uploadingFor.id, uploadingFor.nextStatus, proofFile)
      showNotification(`Issue #${uploadingFor.id} updated to ${uploadingFor.nextStatus}. Points awarded.`)
      setUploadingFor(null)
      setProofFile(null)
      fetchIssues()
      fetchStats()
    } catch (error) {
      showNotification(error.response?.data || 'Failed to update status.', 'error')
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
    <div style={{display: 'flex', flexDirection: 'column', gap: '2rem'}}>
      {notification && (
        <div style={{
          position: 'fixed', top: '20px', right: '20px', zIndex: 1000,
          backgroundColor: notification.type === 'error' ? '#ef4444' : '#3b82f6',
          color: 'white', padding: '1rem 1.5rem', borderRadius: '8px',
          boxShadow: '0 10px 15px -3px rgba(0,0,0,0.3)',
          animation: 'slideIn 0.3s ease-out'
        }}>
          {notification.message}
        </div>
      )}

      <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
        <div>
          <h2 style={{margin: 0}}>Admin Management</h2>
          <p style={{margin: '0.25rem 0 0 0', color: 'var(--text-secondary)', fontSize: '0.875rem'}}>Authorized personnel only. Data integrity and transparency enforced.</p>
        </div>
        <button className="secondary" onClick={() => {fetchIssues(); fetchStats();}} disabled={fetchLoading}>
          Refresh Dashboard
        </button>
      </div>

      <div className="grid" style={{gridTemplateColumns: 'repeat(4, 1fr)'}}>
        <div className="card" style={{borderLeft: '4px solid var(--text-secondary)'}}>
          <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <span style={{fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-secondary)'}}>TOTAL REPORTS</span>
            <BarChart3 size={16} color="var(--text-secondary)" />
          </div>
          <div style={{fontSize: '1.5rem', fontWeight: 700, marginTop: '0.5rem'}}>{stats.total}</div>
        </div>
        <div className="card" style={{borderLeft: '4px solid #ef4444'}}>
          <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <span style={{fontSize: '0.75rem', fontWeight: 600, color: '#ef4444'}}>PENDING</span>
            <AlertCircle size={16} color="#ef4444" />
          </div>
          <div style={{fontSize: '1.5rem', fontWeight: 700, marginTop: '0.5rem'}}>{stats.pending}</div>
        </div>
        <div className="card" style={{borderLeft: '4px solid #f59e0b'}}>
          <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <span style={{fontSize: '0.75rem', fontWeight: 600, color: '#f59e0b'}}>IN PROGRESS</span>
            <Clock size={16} color="#f59e0b" />
          </div>
          <div style={{fontSize: '1.5rem', fontWeight: 700, marginTop: '0.5rem'}}>{stats.inProgress}</div>
        </div>
        <div className="card" style={{borderLeft: '4px solid #10b981'}}>
          <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <span style={{fontSize: '0.75rem', fontWeight: 600, color: '#10b981'}}>RESOLVED</span>
            <CheckCircle2 size={16} color="#10b981" />
          </div>
          <div style={{fontSize: '1.5rem', fontWeight: 700, marginTop: '0.5rem'}}>{stats.resolved}</div>
        </div>
      </div>
      
      {uploadingFor && (
        <div className="card" style={{
          border: '1px solid var(--accent-blue)',
          background: 'rgba(59, 130, 246, 0.05)',
          animation: 'slideIn 0.3s ease-out'
        }}>
          <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1.5rem'}}>
            <div>
              <h3 style={{margin: 0}}>Verify & Update Issue #{uploadingFor.id}</h3>
              <div style={{display: 'flex', alignItems: 'center', gap: '10px', marginTop: '0.5rem'}}>
                <span className="badge" style={{backgroundColor: 'rgba(255,255,255,0.1)'}}>Current Status</span>
                <ArrowRight size={14} />
                <span className={`badge ${getStatusClass(uploadingFor.nextStatus)}`}>{uploadingFor.nextStatus}</span>
              </div>
            </div>
            <button className="secondary" onClick={() => {setUploadingFor(null); setProofFile(null);}} style={{padding: '0.5rem'}}>
              <X size={18} />
            </button>
          </div>

          <div className="form-group" style={{marginBottom: '1.5rem'}}>
            <label><Camera size={14} /> Mandatory Verification Photo</label>
            <div style={{
              border: '2px dashed var(--border-color)',
              borderRadius: '8px',
              padding: '1.5rem',
              textAlign: 'center',
              backgroundColor: 'rgba(255,255,255,0.02)'
            }}>
              <input 
                type="file" 
                accept="image/*"
                onChange={(e) => setProofFile(e.target.files[0])}
                style={{display: 'none'}}
                id="admin-proof-upload"
              />
              <label htmlFor="admin-proof-upload" style={{cursor: 'pointer', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px'}}>
                <span style={{color: 'var(--accent-blue)', fontWeight: 600}}>
                  {proofFile ? proofFile.name : 'Choose File to Upload'}
                </span>
                <span style={{fontSize: '0.75rem', color: 'var(--text-secondary)'}}>This image will be visible to the public.</span>
              </label>
            </div>
          </div>

          <div style={{display: 'flex', gap: '1rem'}}>
            <button onClick={handleUpdateStatus} disabled={loading || !proofFile} style={{flex: 1}}>
              {loading ? 'Updating...' : `Confirm & Move to ${uploadingFor.nextStatus}`}
            </button>
            <button className="secondary" onClick={() => {setUploadingFor(null); setProofFile(null);}}>
              Cancel
            </button>
          </div>
        </div>
      )}

      <div className="card" style={{padding: 0, overflow: 'hidden'}}>
        <div style={{padding: '1.25rem 1.5rem', borderBottom: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
          <h3 style={{margin: 0}}>Official Issue Log</h3>
        </div>
        <div style={{overflowX: 'auto'}}>
          <table style={{width: '100%', borderCollapse: 'collapse'}}>
            <thead>
              <tr style={{backgroundColor: 'rgba(255,255,255,0.02)'}}>
                <th style={{textAlign: 'left', padding: '1rem 1.5rem', fontSize: '0.7rem', fontWeight: 700, color: 'var(--text-secondary)', textTransform: 'uppercase'}}>Issue Details</th>
                <th style={{textAlign: 'left', padding: '1rem 1.5rem', fontSize: '0.7rem', fontWeight: 700, color: 'var(--text-secondary)', textTransform: 'uppercase'}}>Location</th>
                <th style={{textAlign: 'left', padding: '1rem 1.5rem', fontSize: '0.7rem', fontWeight: 700, color: 'var(--text-secondary)', textTransform: 'uppercase'}}>Status</th>
                <th style={{textAlign: 'left', padding: '1rem 1.5rem', fontSize: '0.7rem', fontWeight: 700, color: 'var(--text-secondary)', textTransform: 'uppercase'}}>Verification</th>
                <th style={{textAlign: 'left', padding: '1rem 1.5rem', fontSize: '0.7rem', fontWeight: 700, color: 'var(--text-secondary)', textTransform: 'uppercase'}}>Action</th>
              </tr>
            </thead>
            <tbody>
              {issues.map((issue) => (
                <tr key={issue.id} style={{borderBottom: '1px solid var(--border-color)'}} className="table-row-hover">
                  <td style={{padding: '1rem 1.5rem'}}>
                    <div style={{fontWeight: 600, fontSize: '0.9rem'}}>{issue.category}</div>
                    <div style={{fontSize: '0.75rem', color: 'var(--text-secondary)', maxWidth: '200px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'}}>
                      {issue.description}
                    </div>
                  </td>
                  <td style={{padding: '1rem 1.5rem', fontSize: '0.85rem'}}>{issue.location}</td>
                  <td style={{padding: '1rem 1.5rem'}}>
                    <span className={`badge ${getStatusClass(issue.status)}`}>
                      {issue.status}
                    </span>
                  </td>
                  <td style={{padding: '1rem 1.5rem'}}>
                    <div style={{display: 'flex', gap: '6px'}}>
                      {issue.imagePath && <img src={getImageUrl(issue.imagePath)} alt="R" title="Report" style={{width: '28px', height: '28px', objectFit: 'cover', borderRadius: '4px', border: '1px solid var(--border-color)'}} />}
                      {issue.proofStartPath && <img src={getImageUrl(issue.proofStartPath)} alt="S" title="Started" style={{width: '28px', height: '28px', objectFit: 'cover', borderRadius: '4px', border: '1px solid #f59e0b'}} />}
                      {issue.proofEndPath && <img src={getImageUrl(issue.proofEndPath)} alt="E" title="Resolved" style={{width: '28px', height: '28px', objectFit: 'cover', borderRadius: '4px', border: '1px solid #10b981'}} />}
                    </div>
                  </td>
                  <td style={{padding: '1rem 1.5rem'}}>
                    {issue.status !== 'Resolved' ? (
                      <button 
                        onClick={() => prepareUpdate(issue.id, issue.status)}
                        className="secondary"
                        style={{fontSize: '0.7rem', padding: '0.35rem 0.7rem'}}
                        disabled={uploadingFor && uploadingFor.id === issue.id}
                      >
                        {issue.status === 'Pending' ? 'Start' : 'Resolve'}
                      </button>
                    ) : (
                      <span style={{color: '#10b981', display: 'flex', alignItems: 'center', gap: '4px', fontSize: '0.7rem', fontWeight: 700}}>
                        <CheckCircle size={12} /> VERIFIED
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {!fetchLoading && issues.length === 0 && (
            <div style={{textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)'}}>No active issues in the log.</div>
          )}
        </div>
      </div>
    </div>
  )
}

export default AdminDashboard
