import React, {useState, useEffect} from 'react';
import { fetchAlerts, createAlert } from '../api';

export default function AlertsPage(){
  const [alerts, setAlerts] = useState([]);
  const [form, setForm] = useState({name:'High Error Rate', query:'error', threshold: {count:5, window:'1h'}, notify:['email@example.com']});

  useEffect(()=>{ load(); }, []);
  async function load(){ const a = await fetchAlerts(); setAlerts(a ?? []); }

  async function onCreate(e){
    e.preventDefault();
    // Build payload that matches backend AlertDefinition model
    const count = Number(form.threshold?.count || 1);
    // Build SearchRequest object for backend
    const searchReq = { from: form.queryFrom ?? 'now-5m', to: form.queryTo ?? 'now', query: form.query || '', size: form.querySize || 100 };
    // Threshold uses operator and value
    const threshold = { operator: '>=', value: count };
    // Notify: if user supplied a URL (starts with http) use webhook, otherwise use log notifier
    let notifyObj = { type: 'log' };
    if (Array.isArray(form.notify) && form.notify.length>0){
      const first = (form.notify[0]||'').trim();
      if (first.startsWith('http')) notifyObj = { type: 'webhook', url: first };
    } else if (typeof form.notify === 'string' && form.notify.trim().startsWith('http')){
      notifyObj = { type: 'webhook', url: form.notify.trim() };
    }

    const payload = { name: form.name, query: searchReq, threshold, notify: notifyObj };
    const res = await createAlert(payload);
    if(res.ok){ setForm({name:'', query:'', threshold:{count:1, window:'1h'}, notify:[]}); load(); }
    else {
      let body = null;
      try { body = await res.json(); } catch(e) { body = await res.text(); }
      alert('Failed to create alert: ' + res.status + '\n' + JSON.stringify(body));
    }
  }

  return (
    <div>
      <div className="card">
        <h2>Create Alert</h2>
        <form onSubmit={onCreate}>
          <div className="field"><label>Name</label><input value={form.name} onChange={e=>setForm({...form, name:e.target.value})}/></div>
          <div className="field"><label>Query</label><input value={form.query} onChange={e=>setForm({...form, query:e.target.value})}/></div>
          <div className="field"><label>Threshold Count</label><input type="number" value={form.threshold.count} onChange={e=>setForm({...form, threshold:{...form.threshold, count: Number(e.target.value)}})}/></div>
          <div className="field"><label>Notify mail (comma separated)</label><input value={form.notify.join(',')} onChange={e=>setForm({...form, notify: e.target.value.split(',').map(s=>s.trim())})}/></div>
          <button className="btn" type="submit">Create</button>
        </form>
      </div>

      <div className="card">
        <h2>Alerts</h2>
        {alerts.length === 0 ? <div className="small">No alerts</div> : alerts.map((a,i)=>{
          // render query safely (backend stores SearchRequest object)
          const displayQuery = (typeof a.query === 'string') ? a.query : (a.query && a.query.query) ? a.query.query : JSON.stringify(a.query);
          // render notify safely (could be array or object)
          let displayNotify = '';
          if (Array.isArray(a.notify)) displayNotify = a.notify.join(', ');
          else if (a.notify && typeof a.notify === 'object') displayNotify = a.notify.type + (a.notify.url ? (': ' + a.notify.url) : '');
          else displayNotify = String(a.notify);
          return (
            <div key={i} className="list-item">
              <div><strong>{a.name}</strong> <span className="small">{displayQuery}</span></div>
              <div className="small">threshold: {JSON.stringify(a.threshold)}</div>
              <div className="small">notify: {displayNotify}</div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
