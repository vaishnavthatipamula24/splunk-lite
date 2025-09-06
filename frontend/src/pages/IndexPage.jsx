import React from 'react';

export default function IndexPage({ onNavigate }){
  return (
    <div>
      <h2>Welcome to Splunk lite</h2>
      <p>Use the navigation to access Logs, Search, or Alerts.</p>
      <div style={{display:'flex',gap:8}}>
        <button onClick={()=>onNavigate('logs')}>Logs</button>
        <button onClick={()=>onNavigate('search')}>Search</button>
        <button onClick={()=>onNavigate('alerts')}>Alerts</button>
      </div>
    </div>
  );
}
