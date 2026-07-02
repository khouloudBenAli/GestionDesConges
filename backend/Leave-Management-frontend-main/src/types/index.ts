// ── Auth Types ────────────────────────────────────────────────────────────────
export interface AuthUser {
  username: string
  role: 'HR_ADMIN' | 'MANAGER' | 'EMPLOYEE'
  employeeId: number | null
}

export interface LoginRequest {
  username: string
  password: string
}

export interface AuthResponse {
  username: string
  role: string
  employeeId: number | null
  message: string
}

// ── Status ────────────────────────────────────────────────────────────────────
// Le backend microservices utilise UPPERCASE
export type LeaveStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED'

export const LEAVE_STATUS_LABELS: Record<LeaveStatus, string> = {
  PENDING: 'En attente',
  APPROVED: 'Approuvé',
  REJECTED: 'Rejeté',
  CANCELLED: 'Annulé',
}

// ── Domain Types ──────────────────────────────────────────────────────────────
export interface Department {
  id: number
  name: string
  description?: string
  employeeCount?: number
  createdAt?: string
  updatedAt?: string
}

export interface Employee {
  id: number
  firstName: string
  lastName: string
  fullName?: string
  email: string
  jobTitle?: string
  hireDate?: string
  departmentId?: number
  departmentName?: string
  // legacy compat — kept for Calendar / Leave requests display
  department?: Department
}

export interface LeaveType {
  id: number
  name: string
  description?: string
  paidLeave: boolean
  defaultDaysPerYear: number
  colorCode?: string
  requiresDocumentation: boolean
  canCarryOver: boolean
  maxCarryOverDays: number
  minNoticeDays: number
  allowHalfDay: boolean
  active?: boolean
}

export interface LeaveRequest {
  id: number
  employeeId: number
  leaveTypeId: number
  leaveTypeName?: string
  leaveTypeColor?: string
  startDate: string
  endDate: string
  halfDayStart?: boolean
  halfDayEnd?: boolean
  reason?: string
  isEmergency?: boolean
  contactInfo?: string
  status: LeaveStatus
  statusDisplay?: string
  numberOfDays?: number
  managerComment?: string
  createdAt?: string
  responseDate?: string
  // legacy compat
  employee?: Employee
  leaveType?: LeaveType
}

export interface LeaveBalance {
  id: number
  employeeId: number
  leaveTypeId: number
  leaveTypeName?: string
  leaveTypeColor?: string
  year: number
  totalDays?: number
  allocatedDays?: number
  usedDays?: number
  remainingDays?: number
  carriedOverDays?: number
  lastUpdated?: string
  // legacy compat
  employee?: Employee
  leaveType?: LeaveType
}

// ── Create / Update DTOs ──────────────────────────────────────────────────────
export interface DepartmentDto {
  name: string
  description?: string
}

export interface EmployeeDto {
  firstName: string
  lastName: string
  email: string
  jobTitle?: string
  hireDate?: string
  departmentId?: number
}

export interface LeaveTypeDto {
  name: string
  description?: string
  paidLeave: boolean
  defaultDaysPerYear: number
  colorCode?: string
  requiresDocumentation: boolean
  canCarryOver: boolean
  maxCarryOverDays: number
  minNoticeDays: number
  allowHalfDay: boolean
  active?: boolean
}

export interface LeaveRequestDto {
  employeeId: number
  leaveTypeId: number
  startDate: string
  endDate: string
  halfDayStart?: boolean
  halfDayEnd?: boolean
  reason?: string
  isEmergency?: boolean
  contactInfo?: string
}

export interface InitBalanceDto {
  employeeId: number
  leaveTypeId: number
  year: number
  allocatedDays?: number
}
