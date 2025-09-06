export async function postLog(payload){
  const res = await fetch('/api/logs', {
    method: 'POST',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify(payload)
  });
  return res;
}

export async function fetchLogs(params = {}){
  // Backend exposes a POST /api/search endpoint which returns a SearchResponse { hits: [...] }.
  // Use that endpoint to fetch logs (search with optional from/to/size/query params).
  const res = await fetch('/api/search', {
    method: 'POST',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify(params)
  });
  return res.json();
}

export async function search(req){
  const res = await fetch('/api/search', {
    method:'POST',
    headers:{'Content-Type':'application/json'},
    body: JSON.stringify(req)
  });
  return res.json();
}

export async function fetchAlerts(){
  const res = await fetch('/api/alerts');
  return res.json();
}

export async function createAlert(payload){
  const res = await fetch('/api/alerts', {
    method:'POST',
    headers:{'Content-Type':'application/json'},
    body: JSON.stringify(payload)
  });
  return res;
}
