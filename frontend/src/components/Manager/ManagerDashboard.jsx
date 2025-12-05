import React, { useState, useEffect } from "react";
import api from "../../api/api";
import { getUserFromToken } from "../../utils/auth";
import "./ManagerDashboard.css";

const ManagerDashboard = () => {
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchManagerRequests();
    }, []);

    const fetchManagerRequests = async () => {
        try {
            const token = localStorage.getItem('token');
            console.log("Token exists:", !!token);

            const user = getUserFromToken();
            console.log("Decoded user:", user);

            if (!user || !user.email) {
                console.error("User not authenticated or email missing");
                setError("Authentication required. Please log in again.");
                setLoading(false);
                setTimeout(() => {
                    window.location.href = '/login';
                }, 2000);
                return;
            }

            console.log("Fetching requests for email:", user.email);
            const response = await api.get(`/requests/manager?email=${user.email}`);
            console.log("Requests received:", response.data);

            setRequests(response.data);
            setLoading(false);
        } catch (err) {
            console.error("Full error:", err);
            console.error("Error response:", err.response?.data);

            if (err.response?.status === 401 || err.response?.status === 403) {
                setError("Authentication failed. Please log in again.");
                setTimeout(() => {
                    window.location.href = '/login';
                }, 2000);
            } else {
                setError(err.response?.data || err.message || "Failed to fetch requests");
            }
            setLoading(false);
        }
    };

    const handleApprove = async (requestId) => {
        try {
            await api.put(`/requests/${requestId}/process?action=APPROVE`);
            fetchManagerRequests();
        } catch (err) {
            console.error("Error approving request:", err);
            alert("Failed to approve request");
        }
    };

    const handleReject = async (requestId) => {
        try {
            await api.put(`/requests/${requestId}/process?action=REJECT`);
            fetchManagerRequests();
        } catch (err) {
            console.error("Error rejecting request:", err);
            alert("Failed to reject request");
        }
    };

    if (loading) {
        return <div className="loading">Loading requests...</div>;
    }

    if (error) {
        return <div className="error">Error: {error}</div>;
    }

    return (
        <div className="manager-dashboard">
            <div className="dashboard-header">
                <h2>Manager Dashboard</h2>
                <p>Pending Requests: {requests.filter(r => r.status === "PENDING").length}</p>
            </div>

            <div className="requests-section">
                {requests.length === 0 ? (
                    <p>No requests to review</p>
                ) : (
                    requests.map((request) => (
                        <div key={request.id} className="request-card">
                            <h4>Request #{request.id}</h4>
                            <p><strong>Item:</strong> {request.item?.name || "N/A"}</p>
                            <p><strong>Quantity:</strong> {request.quantity}</p>
                            <p><strong>Requested by:</strong> {request.requester?.name || "N/A"}</p>
                            <p><strong>Status:</strong> {request.status}</p>

                            {request.status === "PENDING" && (
                                <div className="action-buttons">
                                    <button
                                        className="approve-btn"
                                        onClick={() => handleApprove(request.id)}
                                    >
                                        Approve
                                    </button>
                                    <button
                                        className="reject-btn"
                                        onClick={() => handleReject(request.id)}
                                    >
                                        Reject
                                    </button>
                                </div>
                            )}
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default ManagerDashboard;