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
            res.hits.map((hit, i) => (
              <div key={i} className="list-item">
                <div><strong>{hit.source?.source ?? hit.source?.index ?? hit.index}</strong> <span className="small">[{hit.source?.level}]</span></div>
                <div className="small">{hit.source?.ts ? new Date(Number(hit.source.ts)).toLocaleString() : ''}</div>
                <div>{hit.source?.message}</div>
                <div className="small">{hit.source?.extra ? JSON.stringify(hit.source.extra) : ''}</div>
              </div>
            ))
          ) : <div className="small">No results</div>
        )}
      </div>
    </div>
  );
}
