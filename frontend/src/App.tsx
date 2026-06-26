import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import heroImg from './assets/hero.png'
import './App.css'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api'

type AuthResponse = {
  token: string
  userId: number
  username: string
  fullName: string
  role: string
  expiresAtEpochSeconds: number
}

type DealCard = {
  promotionId: number
  productId: number
  canonicalSku: string
  productName: string
  brand: string
  category: string
  subCategory: string
  productType: string
  imageUrl: string
  supermarket: string
  originalPrice: number
  promoPrice: number
  savings: number
  shelfLifeDays: number
  promoEnd: string
}

type ProductDeal = {
  winner: DealCard
  alternatives: DealCard[]
}

type HeroRecommendation = {
  recommended: DealCard | null
  affinitySubCategory: string | null
  fallbackMessage: string | null
}

type AuthMode = 'login' | 'register'

function money(value: number) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value)
}

function App() {
  const [auth, setAuth] = useState<AuthResponse | null>(null)
  const [mode, setMode] = useState<AuthMode>('login')
  const [form, setForm] = useState({
    fullName: '',
    username: 'demo',
    email: '',
    phoneNumber: '',
    password: 'Demo@123',
  })
  const [hero, setHero] = useState<HeroRecommendation | null>(null)
  const [deals, setDeals] = useState<ProductDeal[]>([])
  const [loading, setLoading] = useState(false)
  const [notice, setNotice] = useState('')

  const headers = useMemo(
    () => ({
      'Content-Type': 'application/json',
      ...(auth ? { Authorization: `Bearer ${auth.token}` } : {}),
    }),
    [auth],
  )

  async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
    const response = await fetch(`${API_BASE}${path}`, {
      ...options,
      headers: { ...headers, ...(options.headers ?? {}) },
    })
    if (!response.ok) {
      const message = await response.json().catch(() => null)
      throw new Error(message?.message ?? `Request failed with ${response.status}`)
    }
    return response.json()
  }

  async function loadDashboard() {
    if (!auth) return
    setLoading(true)
    setNotice('')
    try {
      const [heroResponse, dealResponse] = await Promise.all([
        api<HeroRecommendation>('/recommendations/hero'),
        api<ProductDeal[]>('/promotions/best'),
      ])
      setHero(heroResponse)
      setDeals(dealResponse)
    } catch (error) {
      setNotice(error instanceof Error ? error.message : 'Unable to load dashboard')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadDashboard()
  }, [auth])

  async function submitAuth(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setLoading(true)
    setNotice('')
    try {
      const path = mode === 'login' ? '/auth/login' : '/auth/register'
      const body =
        mode === 'login'
          ? { usernameOrEmail: form.username, password: form.password }
          : form
      const response = await api<AuthResponse>(path, {
        method: 'POST',
        body: JSON.stringify(body),
      })
      setAuth(response)
    } catch (error) {
      setNotice(error instanceof Error ? error.message : 'Authentication failed')
    } finally {
      setLoading(false)
    }
  }

  async function recordInteraction(deal: DealCard, eventType: 'VIEW' | 'CLICK' | 'CATEGORY') {
    if (!auth) return
    await api('/telemetry/interactions', {
      method: 'POST',
      body: JSON.stringify({
        productId: deal.productId,
        subCategory: deal.subCategory,
        eventType,
      }),
    })
    await loadDashboard()
  }

  if (!auth) {
    return (
      <main className="auth-shell">
        <section className="auth-panel">
          <div className="brand-lockup">
            <span className="brand-mark">P</span>
            <div>
              <p className="eyebrow">PharmaSave SmartCart</p>
              <h1>Secure OTC and cosmetic deal intelligence</h1>
            </div>
          </div>

          <form onSubmit={submitAuth} className="auth-form">
            <div className="segmented" role="tablist" aria-label="Authentication mode">
              <button type="button" className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>
                Sign in
              </button>
              <button type="button" className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>
                Register
              </button>
            </div>

            {mode === 'register' && (
              <>
                <label>
                  Full name
                  <input value={form.fullName} onChange={(event) => setForm({ ...form, fullName: event.target.value })} required />
                </label>
                <label>
                  Email
                  <input type="email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} required />
                </label>
                <label>
                  Phone
                  <input value={form.phoneNumber} onChange={(event) => setForm({ ...form, phoneNumber: event.target.value })} required />
                </label>
              </>
            )}

            <label>
              Username or email
              <input value={form.username} onChange={(event) => setForm({ ...form, username: event.target.value })} required />
            </label>
            <label>
              Password
              <input type="password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} required />
            </label>

            {notice && <p className="notice">{notice}</p>}
            <button className="primary-action" type="submit" disabled={loading}>
              {loading ? 'Working...' : mode === 'login' ? 'Enter dashboard' : 'Create account'}
            </button>
          </form>
        </section>

        <aside className="auth-visual">
          <img src={heroImg} alt="" />
          <div>
            <h2>Thursday-Wednesday promo cycle</h2>
            <p>JWT-protected recommendations rank each matched SKU by lowest price, then longest shelf life.</p>
          </div>
        </aside>
      </main>
    )
  }

  const recommended = hero?.recommended

  return (
    <main className="app-shell">
      <header className="topbar">
        <div className="brand-lockup compact">
          <span className="brand-mark">P</span>
          <div>
            <p className="eyebrow">PharmaSave</p>
            <h1>SmartCart command center</h1>
          </div>
        </div>
        <div className="user-strip">
          <span>{auth.fullName}</span>
          <button type="button" onClick={() => setAuth(null)}>Sign out</button>
        </div>
      </header>

      <section className="hero-band">
        {recommended ? (
          <>
            <div className="product-visual" style={{ background: recommended.imageUrl }}>
              <span>{recommended.brand.slice(0, 2).toUpperCase()}</span>
            </div>
            <div className="hero-copy">
              <p className="eyebrow">{hero?.affinitySubCategory ?? recommended.subCategory}</p>
              <h2>{recommended.productName}</h2>
              <p>{recommended.supermarket} has the strongest match for your current shopping behavior.</p>
            </div>
            <div className="hero-metrics" aria-label="Recommended promotion metrics">
              <span>
                Promo
                <strong>{money(recommended.promoPrice)}</strong>
              </span>
              <span>
                Savings
                <strong>{money(recommended.savings)}</strong>
              </span>
              <span>
                Shelf life
                <strong>{recommended.shelfLifeDays} days</strong>
              </span>
            </div>
          </>
        ) : (
          <div className="empty-state">{hero?.fallbackMessage ?? 'No recommendation available yet.'}</div>
        )}
      </section>

      {notice && <p className="notice dashboard">{notice}</p>}
      {hero?.fallbackMessage && <p className="hint">{hero.fallbackMessage}</p>}

      <section className="dashboard-grid">
        <div className="section-heading">
          <p className="eyebrow">Cross-supermarket matrix</p>
          <h2>Best active promotions</h2>
        </div>

        <div className="deal-list" aria-busy={loading}>
          {deals.map(({ winner, alternatives }) => (
            <article className="deal-card" key={winner.canonicalSku}>
              <div className="deal-main">
                <div className="product-visual small" style={{ background: winner.imageUrl }}>
                  <span>{winner.brand.slice(0, 2).toUpperCase()}</span>
                </div>
                <div>
                  <p className="eyebrow">{winner.subCategory}</p>
                  <h3>{winner.productName}</h3>
                  <p>{winner.brand} at {winner.supermarket}</p>
                </div>
              </div>

              <div className="price-stack">
                <span className="old-price">{money(winner.originalPrice)}</span>
                <strong>{money(winner.promoPrice)}</strong>
                <span>{money(winner.savings)} saved</span>
              </div>

              <div className="shelf-meter">
                <span>Shelf-life advantage</span>
                <strong>{winner.shelfLifeDays} days</strong>
              </div>

              <div className="alternatives">
                {alternatives.map((alternative) => (
                  <span key={alternative.promotionId}>
                    {alternative.supermarket}: {money(alternative.promoPrice)}, {alternative.shelfLifeDays} days
                  </span>
                ))}
              </div>

              <div className="deal-actions">
                <button type="button" onClick={() => recordInteraction(winner, 'VIEW')}>View</button>
                <button type="button" onClick={() => recordInteraction(winner, 'CLICK')}>Choose</button>
              </div>
            </article>
          ))}
        </div>
      </section>
    </main>
  )
}

export default App
