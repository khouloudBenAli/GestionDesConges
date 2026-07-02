package com.leavemanagement.leave.repository;

import com.leavemanagement.leave.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    Optional<LeaveType> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<LeaveType> findByActiveTrue();

    List<LeaveType> findByActiveTrueAndPaidLeaveTrue();
}
