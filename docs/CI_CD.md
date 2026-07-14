# CI/CD — GitHub → Render (Wanderlust API)

Live API: **https://wanderlust-api-dm3y.onrender.com**  
Deploy repo: [Mao-SokHun/wanderlust-api](https://github.com/Mao-SokHun/wanderlust-api)  
Service ID: `srv-d9b2gum7r5hc739mt81g`

## How it works

| Layer | Behavior |
|--------|-----------|
| **Render auto-deploy** | Push to `wanderlust-api` `main` → Render builds (`npm install`) and starts (`npm start`) |
| **GitHub Actions** | Same push → triggers an extra deploy via API/hook + optional health check |

The Android monorepo ([Wanderlust](https://github.com/Mao-SokHun/Wanderlust)) does **not** deploy the API by itself — backend lives in the nested `backend/` git repo.

## Everyday deploy

```powershell
cd backend
git add .
git commit -m "Your API change"
git push origin main
```

Then check:

1. https://github.com/Mao-SokHun/wanderlust-api/actions  
2. Render → **wanderlust-api** → Events  
3. https://wanderlust-api-dm3y.onrender.com/api/health  

## One-time secrets (wanderlust-api repo)

Already set if you used this setup script; otherwise:

**GitHub → wanderlust-api → Settings → Secrets and variables → Actions**

| Secret / Variable | Value |
|-------------------|--------|
| `RENDER_API_KEY` | Render Dashboard → Account Settings → API Keys |
| `RENDER_SERVICE_ID` | `srv-d9b2gum7r5hc739mt81g` |
| *(optional)* `RENDER_DEPLOY_HOOK` | Service → Settings → Deploy Hook URL |
| *(variable)* `RENDER_API_URL` | `https://wanderlust-api-dm3y.onrender.com` |

Prefer a **Deploy Hook** over sharing a long-lived API key when possible.

## Manual deploy

- GitHub Actions → **Deploy to Render** → **Run workflow**  
- Or Render dashboard → **Manual Deploy**  
- Or CLI: `render deploys create srv-d9b2gum7r5hc739mt81g --wait --confirm`

## Monorepo note

`FinalProject/backend` is its own git repo (`wanderlust-api`). Pushing only the parent Wanderlust repo does **not** update the live API.
