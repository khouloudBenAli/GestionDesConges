import { createContext, useContext, useEffect, useState, ReactNode } from 'react'
import type { AuthUser } from '../types'

interface AuthContextValue {
  user: AuthUser | null
  loading: boolean
  login: (username: string, password: string) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

const LOCAL_USERS: Record<string, { password: string; role: AuthUser['role']; employeeId: number | null }> = {
  admin:    { password: 'admin123',    role: 'HR_ADMIN',  employeeId: null },
  manager:  { password: 'manager123',  role: 'MANAGER',   employeeId: null },
  employee: { password: 'employee123', role: 'EMPLOYEE',  employeeId: 1    },
}

const STORAGE_KEY = 'auth_user'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY)
      if (stored) setUser(JSON.parse(stored))
    } catch {
      localStorage.removeItem(STORAGE_KEY)
    }
    setLoading(false)
  }, [])

  const login = async (username: string, password: string) => {
    const found = LOCAL_USERS[username]
    if (!found || found.password !== password) {
      throw new Error('Identifiants incorrects')
    }
    const authUser: AuthUser = { username, role: found.role, employeeId: found.employeeId }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(authUser))
    setUser(authUser)
  }

  const logout = async () => {
    localStorage.removeItem(STORAGE_KEY)
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth doit être utilisé dans AuthProvider')
  return ctx
}
