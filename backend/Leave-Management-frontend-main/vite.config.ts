import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const EMPLOYEE_SERVICE = 'http://localhost:8081'
const LEAVE_SERVICE    = 'http://localhost:8082'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api/employees':     { target: EMPLOYEE_SERVICE, changeOrigin: true },
      '/api/departments':   { target: EMPLOYEE_SERVICE, changeOrigin: true },
      '/api/leave-types':   { target: LEAVE_SERVICE,    changeOrigin: true },
      '/api/leave-requests':{ target: LEAVE_SERVICE,    changeOrigin: true },
      '/api/leave-balances':{ target: LEAVE_SERVICE,    changeOrigin: true },
    },
  },
})
