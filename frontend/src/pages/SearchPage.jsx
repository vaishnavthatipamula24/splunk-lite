import React, {useState} from 'react';
import { search } from '../api';

export default function SearchPage(){
  const [req, setReq] = useState({from:'now-7d', to:'now', query:'', size:50});
  const [res, setRes] = useState(null);
  const [loading, setLoading] = useState(false);

  async function onSubmit(e){
    e.preventDefault();
    setLoading(true);
    try {
      const data = await search(req);
      setRes(data);
    } catch(err){
      alert('Search failed: ' + err);
    } finally { setLoading(false); }
  }

  return (
    <div>
      <div className="card">
        <h2>Search</h2>
        <form onSubmit={onSubmit}>
          <div className="field">
            <label>from</label>
            <input value={req.from} onChange={e=>setReq({...req, from:e.target.value})}/>
          </div>
          <div className="field">
            <label>to</label>
            <input value={req.to} onChange={e=>setReq({...req, to:e.target.value})}/>
          </div>
          <div className="field">
            <label>query</label>
            <input value={req.query} onChange={e=>setReq({...req, query:e.target.value})}/>
          </div>
          <div className="field">
            <label>size</label>
            <input type="number" value={req.size} onChange={e=>setReq({...req, size: Number(e.target.value)})}/>
          </div>
          <button className="btn" type="submit">Search</button>
        </form>
      </div>

      <div className="card">
        <h2>Results</h2>
        {loading && <div className="small">Searching...</div>}
        {!loading && res && (
          res.hits && res.hits.length > 0 ? (
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
                  {res.hits.map((hit, i) => {
                    const h = hit.source ?? hit;
                    return (
                      <tr key={i} className="logs-row">
                        <td className="small">{h.ts ? new Date(Number(h.ts)).toLocaleString() : ''}</td>
                        <td><strong>{h.source ?? h.index}</strong></td>
                        <td className="small">{h.level ?? ''}</td>
                        <td className="log-message">{h.message}</td>
                        <td className="small">{h.extra ? JSON.stringify(h.extra) : ''}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          ) : <div className="small">No results</div>
        )}
      </div>
    </div>
  );
}
