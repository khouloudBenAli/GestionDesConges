import type { Employee, LeaveType, LeaveRequest, LeaveBalance } from '../types'

// The backend only returns employeeId/leaveTypeId (+ a few flat fields like leaveTypeName).
// These helpers attach the full Employee/LeaveType objects client-side so every page
// can consistently read req.employee / req.leaveType.
export function enrichRequests(requests: LeaveRequest[], employees: Employee[], leaveTypes: LeaveType[]): LeaveRequest[] {
  return requests.map(req => ({
    ...req,
    employee: employees.find(e => e.id === req.employeeId),
    leaveType: leaveTypes.find(lt => lt.id === req.leaveTypeId),
  }))
}

export function enrichBalances(balances: LeaveBalance[], employees: Employee[], leaveTypes: LeaveType[]): LeaveBalance[] {
  return balances.map(b => ({
    ...b,
    employee: employees.find(e => e.id === b.employeeId),
    leaveType: leaveTypes.find(lt => lt.id === b.leaveTypeId),
  }))
}
