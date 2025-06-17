package com.hostelhive.hostelhive.repository;


import com.hostelhive.hostelhive.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long>{
	 /**
     * Find student by email address
     * @param email the email to search for
     * @return Optional containing the student if found
     */
    Optional<Student> findByEmail(String email);
    
    /**
     * Find student by phone number
     * @param phoneNumber the phone number to search for
     * @return Optional containing the student if found
     */
    Optional<Student> findByPhoneNumber(String phoneNumber);
    
    /**
     * Check if student exists by email
     * @param email the email to check
     * @return true if student exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if student exists by phone number
     * @param phoneNumber the phone number to check
     * @return true if student exists, false otherwise
     */
    boolean existsByPhoneNumber(String phoneNumber);
    
    /**
     * Find students by full name containing the search term (case insensitive)
     * @param name the name to search for
     * @return List of students matching the search criteria
     */
    @Query("SELECT s FROM Student s WHERE LOWER(s.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Student> findByFullNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find students registered after a specific date
     * @param date the date to search from
     * @return List of students registered after the specified date
     */
    @Query("SELECT s FROM Student s WHERE s.registeredAt >= :date")
    List<Student> findStudentsRegisteredAfter(@Param("date") java.time.LocalDateTime date);
    
    /**
     * Count total number of students
     * @return total count of students
     */
    @Query("SELECT COUNT(s) FROM Student s")
    Long countTotalStudents();
}


