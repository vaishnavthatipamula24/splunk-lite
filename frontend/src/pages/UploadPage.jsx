import React, {useState} from 'react';
import { fetchLogs } from '../api';

export default function UploadPage(){
  const [file, setFile] = useState(null);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [logs, setLogs] = useState([]);
  const [loadingLogs, setLoadingLogs] = useState(false);

  async function loadLogs(size = 50){
    setLoadingLogs(true);
    try{
      const r = await fetchLogs({ from: '1970-01-01T00:00:00Z', to: 'now', size });
      // r is expected to be SearchResponse { hits: [...] }
      const hits = r && r.hits ? r.hits : [];
      const list = hits.map(h => h.source ?? h);
      setLogs(list);
    }catch(e){
      console.error('failed to load logs', e);
      setLogs([]);
    }finally{ setLoadingLogs(false); }
  }

  async function onSubmit(e){
    e.preventDefault();
    if (!file) return alert('Choose a file');
    setLoading(true); setResult(null);
    const fd = new FormData(); fd.append('file', file);
    try{
      const res = await fetch('/api/upload', { method: 'POST', body: fd });
      // handle JSON and non-JSON responses (proxy errors often return HTML/text)
      const ct = (res.headers.get('content-type') || '').toLowerCase();
      let data = null;
      let text = null;
      if (ct.includes('application/json')){
        try { data = await res.json(); } catch(e) { text = await res.text(); }
      } else {
        // not JSON - read raw body so we can show proxy / server error messages
        text = await res.text();
      }
      setResult({ok: res.ok, status: res.status, data, text});
      if (res.ok){
        // fetch recent logs (size = uploaded count or default 50)
        const size = data && data.count ? data.count : 50;
        await loadLogs(size);
      }
    }catch(err){ setResult({ok:false, error: String(err)}); }
    setLoading(false);
  }

  return (
    <div>
      <div className="card">
        <h2>Upload Log File</h2>
        <form onSubmit={onSubmit}>
          <div className="field">
            <input type="file" onChange={e=>setFile(e.target.files && e.target.files[0])} />
          </div>
          <button className="btn" type="submit" disabled={loading}>{loading ? 'Uploading...' : 'Upload'}</button>
        </form>
      </div>

  {/* result is intentionally not shown per UX request; we display imported logs only */}

      <div className="card">
        <h2>Imported Logs</h2>
        {loadingLogs ? <div className="small">Loading...</div> : (
          logs.length === 0 ? <div className="small">No logs loaded yet</div> : (
            <div className="table-wrapper">
              <table className="logs-table">
                <thead>
                  <tr>
                    <th style={{width:160}}>ts</th>
                    <th style={{width:160}}>source</th>
                    <th style={{width:120}}>env</th>
                    <th style={{width:100}}>level</th>
                    <th>message</th>
                    <th style={{width:220}}>extra</th>
                  </tr>
                </thead>
                <tbody>
                  {logs.map((h, idx) => (
                    <tr key={idx} className="logs-row">
                      <td className="small">{h.ts ? new Date(Number(h.ts)).toISOString() : ''}</td>
                      <td className="small"><strong>{h.source ?? h.index}</strong></td>
                      <td className="small">{h.env ?? ''}</td>
                      <td className="small">{h.level ?? ''}</td>
                      <td className="log-message">{h.message}</td>
                      <td className="small">{h.extra ? JSON.stringify(h.extra) : ''}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )
        )}
      </div>
    </div>
  );
}
