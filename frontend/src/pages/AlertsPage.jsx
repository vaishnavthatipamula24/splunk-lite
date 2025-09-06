import React, {useState, useEffect} from 'react';
import { fetchAlerts, createAlert } from '../api';

export default function AlertsPage(){
  const [alerts, setAlerts] = useState([]);
  const [form, setForm] = useState({name:'High Error Rate', query:'error', threshold: {count:5, window:'1h'}, notify:['email@example.com']});

  useEffect(()=>{ load(); }, []);
  async function load(){ const a = await fetchAlerts(); setAlerts(a ?? []); }

  async function onCreate(e){
    e.preventDefault();
    const payload = {...form};
    const res = await createAlert(payload);
    if(res.ok){ setForm({name:'', query:'', threshold:{count:1, window:'1h'}, notify:[]}); load(); }
    else alert('Failed to create alert: ' + res.status);
  }

  return (
    <div>
      <div className="card">
        <h2>Create Alert</h2>
        <form onSubmit={onCreate}>
          <div className="field"><label>Name</label><input value={form.name} onChange={e=>setForm({...form, name:e.target.value})}/></div>
          <div className="field"><label>Query</label><input value={form.query} onChange={e=>setForm({...form, query:e.target.value})}/></div>
          <div className="field"><label>Threshold Count</label><input type="number" value={form.threshold.count} onChange={e=>setForm({...form, threshold:{...form.threshold, count: Number(e.target.value)}})}/></div>
          <div className="field"><label>Notify (comma separated)</label><input value={form.notify.join(',')} onChange={e=>setForm({...form, notify: e.target.value.split(',').map(s=>s.trim())})}/></div>
          <button className="btn" type="submit">Create</button>
        </form>
      </div>

      <div className="card">
        <h2>Alerts</h2>
        {alerts.length === 0 ? <div className="small">No alerts</div> : alerts.map((a,i)=>(
          <div key={i} className="list-item">
            <div><strong>{a.name}</strong> <span className="small">{a.query}</span></div>
            <div className="small">threshold: {JSON.stringify(a.threshold)}</div>
            <div className="small">notify: {a.notify?.join(', ')}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
