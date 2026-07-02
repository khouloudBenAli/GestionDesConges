import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { HiHome, HiUserGroup, HiUsers, HiTag, HiClipboardList, HiChartBar, HiCalendar, HiLogout } from 'react-icons/hi'
import toast from 'react-hot-toast'
import { useAuth } from '../context/AuthContext'

const navItems = [
  { to: '/',               label: 'Tableau de bord', icon: HiHome },
  { to: '/departments',    label: 'Départements',     icon: HiUserGroup },
  { to: '/employees',      label: 'Employés',         icon: HiUsers },
  { to: '/leave-types',    label: 'Types de Congés',  icon: HiTag },
  { to: '/leave-requests', label: 'Demandes',         icon: HiClipboardList },
  { to: '/leave-balances', label: 'Soldes',           icon: HiChartBar },
  { to: '/calendar',       label: 'Calendrier',       icon: HiCalendar },
]

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    toast.success('Déconnexion réussie')
    navigate('/login')
  }

  return (
    <div className="min-h-screen flex">
      <aside className="w-64 bg-blue-900 text-white flex flex-col">
        <div className="p-6 border-b border-blue-800">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-lg bg-blue-500 flex items-center justify-center text-white font-bold text-lg">RH</div>
            <div>
              <p className="font-bold text-sm leading-tight">Gestion des Congés</p>
              <p className="text-blue-300 text-xs">Plateforme RH</p>
            </div>
          </div>
        </div>
        <nav className="flex-1 py-4 px-3 space-y-1">
          {navItems.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/'}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive ? 'bg-blue-600 text-white' : 'text-blue-200 hover:bg-blue-800 hover:text-white'
                }`
              }
            >
              <Icon className="w-5 h-5 flex-shrink-0" />
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="p-4 border-t border-blue-800">
          {user && (
            <div className="mb-3">
              <p className="text-xs text-blue-300 truncate">{user.username}</p>
              <p className="text-xs text-blue-400">{user.role}</p>
            </div>
          )}
          <button
            onClick={handleLogout}
            className="flex items-center gap-2 text-xs text-blue-300 hover:text-white transition-colors w-full"
          >
            <HiLogout className="w-4 h-4" />
            Déconnexion
          </button>
        </div>
      </aside>
      <main className="flex-1 overflow-auto"><Outlet /></main>
    </div>
  )
}
