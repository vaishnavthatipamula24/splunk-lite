import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import '../styles/home.css'

export default function HomeLayout({ children }){
  const navigate = useNavigate()
  const username = localStorage.getItem('splunk.user') || 'Administrator'

  return (
    <div className="home-root">
      <aside className="home-sidebar">
        <div className="sidebar-brand">Splunk &gt; enterprise</div>
        <div className="sidebar-search">
          <input placeholder="Search apps by name..." />
        </div>
        <nav className="sidebar-nav">
          <Link to="/logs" className="nav-item">Logs</Link>
          <Link to="/search" className="nav-item">Search</Link>
          <Link to="/alerts" className="nav-item">Alerts</Link>
          <div className="nav-divider" />
          <a className="nav-item">Search & Reporting</a>
          <a className="nav-item">Audit Trail</a>
          <a className="nav-item">Data Management</a>
        </nav>
      </aside>

      <div className="home-main">
        <header className="home-header">
          <div className="home-title">Hello, {username}</div>
          <div className="home-actions">
            <button onClick={()=>navigate('/signup')}>Add bookmark</button>
          </div>
        </header>

        <section className="home-tabs">
          <div className="tabs-left">
            <button className="tab active">Bookmarks</button>
            <button className="tab">Dashboard</button>
            <button className="tab">Search history</button>
            <button className="tab">Recently viewed</button>
          </div>
        </section>

        <main className="home-content">
          {children}
        </main>
      </div>
    </div>
  )
}
