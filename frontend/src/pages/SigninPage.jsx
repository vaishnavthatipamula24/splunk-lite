import React from 'react';
import { signin } from '../api/auth';
import '../styles/auth.css';

export default function SigninPage({ onAuth, onNavigate }){
  const [username,setUsername]=React.useState('');
  const [password,setPassword]=React.useState('');
  const [msg,setMsg]=React.useState(null);
  const [loading,setLoading]=React.useState(false);

  async function onSubmit(e){
    e.preventDefault();
    setLoading(true);
    setMsg(null);
    try{
      const res = await signin({username,password});
      if (res && res.token) {
        localStorage.setItem('token', res.token);
        setMsg({type:'success', text:'Signed in'});
        if (onAuth) onAuth(res);
      } else {
        setMsg({type:'error', text: res && res.error ? res.error : 'Sign in failed'});
      }
    }catch(err){
      setMsg({type:'error', text:String(err)});
    } finally { setLoading(false); }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2 className="auth-header">Sign in</h2>
        {msg && <div className={`auth-msg ${msg.type}`}>{msg.text}</div>}
        <form className="auth-form" onSubmit={onSubmit}>
          <div className="auth-field">
            <label>Username</label>
            <input value={username} onChange={e=>setUsername(e.target.value)} required />
          </div>
          <div className="auth-field">
            <label>Password</label>
            <input type="password" value={password} onChange={e=>setPassword(e.target.value)} required />
          </div>
          <div className="auth-actions">
            <button className="auth-btn" type="submit" disabled={loading}>{loading ? 'Signing...' : 'Sign in'}</button>
            <button type="button" className="auth-link" onClick={()=>onNavigate && onNavigate('signup')}>Create an account</button>
          </div>
        </form>
      </div>
    </div>
  );
}
