  export async function signup(body){
  try{
    const res = await fetch('/api/auth/signup', {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)});
    const text = await res.text();
    // try parse json, but handle non-json (proxy/html) responses gracefully
    try{
      const data = text ? JSON.parse(text) : {};
      if (res.ok) return data;
      return { error: data && (data.error || data.message) ? (data.error || data.message) : JSON.stringify(data), status: res.status };
    }catch(e){
      return { error: text || `HTTP ${res.status}`, status: res.status };
    }
  }catch(err){
    return { error: err && err.message ? err.message : String(err) };
  }
}

export async function signin(body){
  try{
    const res = await fetch('/api/auth/signin', {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)});
    const text = await res.text();
    try{
      const data = text ? JSON.parse(text) : {};
      if (res.ok) return data;
      return { error: data && (data.error || data.message) ? (data.error || data.message) : JSON.stringify(data), status: res.status };
    }catch(e){
      return { error: text || `HTTP ${res.status}`, status: res.status };
    }
  }catch(err){
    return { error: err && err.message ? err.message : String(err) };
  }
}

export async function me(token){
  try{
    const res = await fetch('/api/auth/me', {headers:{'Authorization': 'Bearer '+token}});
    const text = await res.text();
    try{
      const data = text ? JSON.parse(text) : {};
      if (res.ok) return data;
      return { error: data && (data.error || data.message) ? (data.error || data.message) : JSON.stringify(data), status: res.status };
    }catch(e){
      return { error: text || `HTTP ${res.status}`, status: res.status };
    }
  }catch(err){
    return { error: err && err.message ? err.message : String(err) };
  }
}
