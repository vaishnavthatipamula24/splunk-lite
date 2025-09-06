import React, {useState, useEffect} from 'react';
import { postLog, fetchLogs } from '../api';

export default function LogsPage(){
  const [form, setForm] = useState({ts:'', source:'', env:'dev', level:'INFO', message:'', extra:''});
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(false);

  async function load(){
    setLoading(true);
    try {
      const data = await fetchLogs({ from: '1970-01-01T00:00:00Z', to: 'now', size: 50 });
  // Ensure logs is always an array. Search API returns { hits: [...] }.
  let list = [];
  if (!data) list = [];
  else if (Array.isArray(data)) list = data;
  else if (data.hits && Array.isArray(data.hits)) list = data.hits.map(h => h.source ?? h);
  else list = [];
  setLogs(list);
    } finally { setLoading(false); }
  }

  useEffect(()=>{ load(); }, []);

  async function onSubmit(e){
    e.preventDefault();
    const payload = {
      ts: form.ts ? Number(form.ts) : undefined,
      source: form.source, env: form.env, level: form.level, message: form.message,
      extra: form.extra ? JSON.parse(form.extra) : null
    };
    const res = await postLog(payload);
    if(res.ok) {
      setForm({ts:'', source:'', env:'dev', level:'INFO', message:'', extra:''});
      load();
    } else {
      alert('Failed to post log: ' + res.status);
    }
  }

  return (
    <div>
      <div className="card">
  <h2>Ingest Logs</h2>
        <form onSubmit={onSubmit}>
          <div className="field">
            <label>ts (epoch millis, optional)</label>
            <input value={form.ts} onChange={e=>setForm({...form, ts:e.target.value})} />
          </div>
          <div className="field">
            <label>source</label>
            <input value={form.source} onChange={e=>setForm({...form, source:e.target.value})} />
          </div>
          <div className="field">
            <label>env</label>
            <input value={form.env} onChange={e=>setForm({...form, env:e.target.value})} />
          </div>
          <div className="field">
            <label>level</label>
            <input value={form.level} onChange={e=>setForm({...form, level:e.target.value})} />
          </div>
          <div className="field">
            <label>message</label>
            <textarea rows="3" value={form.message} onChange={e=>setForm({...form, message:e.target.value})}/>
          </div>
          <div className="field">
            <label>extra (JSON) </label>
            <textarea rows="2" value={form.extra} onChange={e=>setForm({...form, extra:e.target.value})}/>
          </div>
          <button className="btn" type="submit">Done</button>
        </form>
      </div>

      <div className="card">
        <h2>Ingested Logs</h2>
        {loading ? <div className="small">Loading...</div> : (
          logs.length === 0 ? <div className="small">No logs</div> :
          <div className="table-wrapper">
            <table className="logs-table">
              <thead>
                <tr>
                  <th style={{width:180}}>Time</th>
                  <th style={{width:160}}>Source</th>
                  <th style={{width:80}}>Level</th>
                  <th>Message</th>
                  <th style={{width:180}}>Extra</th>
                </tr>
              </thead>
              <tbody>
                {logs.map((h, idx) => (
                  <tr key={idx} className="logs-row">
                    <td className="small">{h.ts ? new Date(Number(h.ts)).toLocaleString() : ''}</td>
                    <td><strong>{h.source ?? h.index}</strong></td>
                    <td className="small">{h.level ?? ''}</td>
                    <td className="log-message">{h.message}</td>
                    <td className="small">{h.extra ? JSON.stringify(h.extra) : ''}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
