import React from 'react';
import LogsPage from './pages/LogsPage';
import SearchPage from './pages/SearchPage';
import AlertsPage from './pages/AlertsPage';
import IndexPage from './pages/IndexPage';
import SigninPage from './pages/SigninPage';
import SignupPage from './pages/SignupPage';
import { me } from './api/auth';

export default function App(){
  const [route, setRoute] = React.useState('index');
  const [user, setUser] = React.useState(null);
  const [loadingAuth, setLoadingAuth] = React.useState(true);

  React.useEffect(()=>{
    // try to rehydrate from stored token
    const token = localStorage.getItem('token');
    if (!token) { setLoadingAuth(false); setRoute('signin'); return; }
    (async ()=>{
      try{
        const res = await me(token);
        if (res && res.username) { setUser({username: res.username}); setRoute('index'); }
        else { localStorage.removeItem('token'); setRoute('signin'); }
      }catch(e){ localStorage.removeItem('token'); setRoute('signin'); }
      setLoadingAuth(false);
    })();
  }, []);

  function handleAuth(result){
    // result expected to contain token and username
    if (result && result.token) {
      localStorage.setItem('token', result.token);
      setUser({ username: result.username || '' });
      setRoute('index');
    }
  }

  function signout(){ localStorage.removeItem('token'); setUser(null); setRoute('signin'); }

  if (loadingAuth) return <div className="app">Checking session...</div>;

  // if not authenticated, show signin/signup flow
  if (!user) {
    return (
      <div className="app">
        <header>
          <h1>Splunk Enterprise</h1>
        </header>
        <main>
          {route === 'signup' && <SignupPage onNavigate={setRoute} />}
          {(route === 'signin' || route === 'index') && <SigninPage onAuth={handleAuth} onNavigate={setRoute} />}
        </main>
      </div>
    );
  }

  return (
    <div className="app">
      <header style={{display:'flex', alignItems:'center', justifyContent:'space-between', padding: '8px 12px'}}>
        <h1 style={{margin:0}}>Splunk Enterprise</h1>
        <div style={{display:'flex', alignItems:'center', gap:12}}>
          <span>Signed in as <strong>{user.username}</strong></span>
          <button onClick={signout}>Sign out</button>
        </div>
      </header>

      <div style={{display:'flex', height: 'calc(100vh - 56px)'}}>
        <aside style={{width:220, borderRight:'1px solid #eee', padding:12, boxSizing:'border-box'}}>
          <div style={{display:'flex', flexDirection:'column', gap:8}}>
            <button onClick={()=>setRoute('index')}>Home</button>
            <button onClick={()=>setRoute('logs')}>Ingest Logs</button>
            <button onClick={()=>setRoute('search')}>Search</button>
            <button onClick={()=>setRoute('alerts')}>Alerts</button>
          </div>
        </aside>

        <main style={{flex:1, overflow:'auto', padding:16}}>
          {route === 'index' && <IndexPage onNavigate={setRoute} />}
          {route === 'logs' && <LogsPage/>}
          {route === 'search' && <SearchPage/>}
          {route === 'alerts' && <AlertsPage/>}
        </main>
      </div>
    </div>
  );
}
