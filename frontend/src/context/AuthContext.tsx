'use client'
import { createContext, useState, useContext, ReactNode, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { api } from '@/services/api'  // la instancia axios que ya creaste

interface AuthContextType {
  token: string | null
  login: (email: string, pwd: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType>({} as any)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null)
  const router = useRouter()

  useEffect(() => {
    const t = localStorage.getItem('token')
    if (t) {
      setToken(t)
      api.defaults.headers.common['Authorization'] = `Bearer ${t}`
    }
  }, [])

  const login = async (email: string, pwd: string) => {
    const res = await api.post('/auth/login', { email, password: pwd })
    const t = res.data.token
    localStorage.setItem('token', t)
    api.defaults.headers.common['Authorization'] = `Bearer ${t}`
    setToken(t)
    router.push('/')
  }

  const logout = () => {
    localStorage.removeItem('token')
    delete api.defaults.headers.common['Authorization']
    setToken(null)
    router.push('/login')
  }

  return (
    <AuthContext.Provider value={{ token, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
