import React, { useEffect, useState } from 'react';
import API from '../../api/api';
import { getUserEmail } from '../../utils/auth';

export default function MyRequests() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const userId = localStorage.getItem('userId');
  console.log('MyRequests userId:', userId);

    useEffect(() => {
        fetchRequests();
},[]);


  async function fetchRequests() {
    setLoading(true);
    setError('');
    const username = getUserEmail();
    const password = localStorage.getItem('userPassword');
    try {
        const credentials = btoa(`${username}:${password}`);
        const response = await API.get(`/api/requests/my/${userId}`,{
            headers: {
                'Authorization': `Basic ${credentials}`
            }
        });
      const requestsData = response.data;
      if (Array.isArray(requestsData)) {
        setRequests(requestsData);
      } else {
        console.warn('Received non-array response:', requestsData);
        setRequests([]);
      }
    } catch (err) {
      console.error('Error fetching requests:', err);
      setError(`Failed to fetch requests: ${err.response?.status === 404 ? 'Not Found' : 'Network Error'}`);
      setRequests([]); // Ensure requests is always an array
    } finally {
      setLoading(false);
    }
  }

  async function withdraw(requestId) {
    if (!window.confirm('Are you sure you want to withdraw this request?')) return;

      const username = getUserEmail();
      const password = localStorage.getItem('userPassword');
        const credentials = btoa(`${username}:${password}`);
    try {
      await API.delete(`/api/requests/${requestId}`,{
        headers: {
            'Authorization': `Basic ${credentials}`
        }
      });
      alert('Request withdrawn successfully');
      fetchRequests(); // Refresh the list
    } catch (err) {
      console.error('Error withdrawing request:', err);
      alert('Failed to withdraw request');
    }
  }

  async function requestCancel(requestId) {
    if (!window.confirm('Are you sure you want to request cancellation?')) return;

      const username = getUserEmail();
      const password = localStorage.getItem('userPassword');
      const credentials = btoa(`${username}:${password}`);

    try {
      await API.put(`/api/requests/${requestId}/cancel`,{},{
        headers: {
            'Authorization': `Basic ${credentials}`
        }
      });
      alert('Cancellation requested successfully');
      fetchRequests(); // Refresh the list
    } catch (err) {
      console.error('Error requesting cancellation:', err);
      alert('Failed to request cancellation');
    }
  }

    if (!userId) {
        return (
            <div className="my-requests">
                <div className="error-state">
                    <h3>Authentication Required</h3>
                    <p>Please log in to view your requests.</p>
                </div>
            </div>
        );
    }

  if (loading) {
    return <div>Loading your requests...</div>;
  }

  if (error) {
    return (
      <div>
        <p>Error: {error}</p>
        <button onClick={fetchRequests}>Retry</button>
      </div>
    );
  }

  return (
    <div>
      <h2>My Requests</h2>
      <button onClick={fetchRequests} disabled={loading}>
        {loading ? 'Refreshing...' : 'Refresh'}
      </button>

      {requests.length === 0 ? (
        <p>No requests found.</p>
      ) : (
        <ul>
          {requests.map((r) => (
            <li key={r.id} className="item-row">
              <div>
                <div style={{ fontWeight: 700 }}>
                  {r.id} • {r.status}
                </div>
                <div className="small">
                  Created: {r.createdDate ? new Date(r.createdDate).toLocaleString() : "—"}
                </div>
                <div className="small">
                  Manager: {r.superiorEmail}
                </div>
                <div className="small">
                  Total: ${r.totalCost || 0}
                </div>
                <div className="small">
                  Items: {r.requestDetails ? r.requestDetails.map((d) => `${d.item.itemName} x${d.quantity}`).join(", ") : "—"}
                </div>
              </div>
              <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {r.status === "PENDING" && (
                  <button className="secondary" onClick={() => withdraw(r.id)}>
                    Withdraw
                  </button>
                )}
                {r.status === "APPROVED" && (
                  <button className="secondary" onClick={() => requestCancel(r.id)}>
                    Request Cancel
                  </button>
                )}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}