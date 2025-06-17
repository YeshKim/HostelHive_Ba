package com.hostelhive.hostelhive.Service;

import com.hostelhive.hostelhive.DTOs.*;
import com.hostelhive.hostelhive.models.*;
import com.hostelhive.hostelhive.repository.*;
import com.hostelhive.hostelhive.exceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {
    private final StudentRepo studentRepo;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public StudentService(StudentRepo studentRepo, PasswordEncoder passwordEncoder) {
        this.studentRepo = studentRepo;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Register a new student
     * @param registrationDto the registration details
     * @return StudentResponseDto containing the created student details
     * @throws EmailAlreadyExistsException if email is already registered
     * @throws PhoneNumberAlreadyExistsException if phone number is already registered
     * @throws PasswordMismatchException if passwords don't match
     */
    public StudentResponseDto registerStudent(StudentRegistrationDto registrationDto) {
        // Validate password confirmation
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        
        // Check if email already exists
        if (studentRepo.existsByEmail(registrationDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        
        // Check if phone number already exists
        if (studentRepo.existsByPhoneNumber(registrationDto.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistsException("Phone number is already registered");
        }
        
        // Create new student
        Student student = new Student();
        student.setFullName(registrationDto.getFullName());
        student.setEmail(registrationDto.getEmail());
        student.setPhoneNumber(registrationDto.getPhoneNumber());
        student.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        
        // Save student
        Student savedStudent = studentRepo.save(student);
        
        // Convert to response DTO
        return convertToResponseDto(savedStudent);
    }
    
    /**
     * Authenticate student login
     * @param loginDto the login credentials
     * @return StudentResponseDto if authentication successful
     * @throws ResourceNotFoundException if student not found or invalid credentials
     */
    public StudentResponseDto authenticateStudent(StudentLoginDto loginDto) {
        Optional<Student> studentOpt = studentRepo.findByEmail(loginDto.getEmail());
        
        if (studentOpt.isEmpty()) {
            throw new ResourceNotFoundException("Student not found with email: " + loginDto.getEmail());
        }
        
        Student student = studentOpt.get();
        
        // Check password
        if (!passwordEncoder.matches(loginDto.getPassword(), student.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials");
        }
        
        return convertToResponseDto(student);
    }
    
    /**
     * Get student by ID
     * @param studentId the student ID
     * @return StudentResponseDto
     * @throws ResourceNotFoundException if student not found
     */
    @Transactional(readOnly = true)
    public StudentResponseDto getStudentById(Long studentId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        return convertToResponseDto(student);
    }
    
    /**
     * Get all students
     * @return List of StudentResponseDto
     */
    @Transactional(readOnly = true)
    public List<StudentResponseDto> getAllStudents() {
        List<Student> students = studentRepo.findAll();
        return students.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Update student profile
     * @param studentId the student ID
     * @param updateDto the update details
     * @return updated StudentResponseDto
     * @throws ResourceNotFoundException if student not found
     * @throws PhoneNumberAlreadyExistsException if phone number is already taken by another student
     */
    public StudentResponseDto updateStudent(Long studentId, StudentUpdateDto updateDto) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        // Update full name if provided
        if (updateDto.getFullName() != null && !updateDto.getFullName().trim().isEmpty()) {
            student.setFullName(updateDto.getFullName());
        }
        
        // Update phone number if provided
        if (updateDto.getPhoneNumber() != null && !updateDto.getPhoneNumber().trim().isEmpty()) {
            // Check if phone number is already taken by another student
            Optional<Student> existingStudent = studentRepo.findByPhoneNumber(updateDto.getPhoneNumber());
            if (existingStudent.isPresent() && !existingStudent.get().getId().equals(studentId)) {
                throw new PhoneNumberAlreadyExistsException("Phone number is already registered");
            }
            student.setPhoneNumber(updateDto.getPhoneNumber());
        }
        
        Student updatedStudent = studentRepo.save(student);
        return convertToResponseDto(updatedStudent);
    }
    
    /**
     * Delete student
     * @param studentId the student ID
     * @throws ResourceNotFoundException if student not found
     */
    public void deleteStudent(Long studentId) {
        if (!studentRepo.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        
        studentRepo.deleteById(studentId);
    }
    
    /**
     * Search students by name
     * @param name the name to search for
     * @return List of StudentResponseDto matching the search criteria
     */
    @Transactional(readOnly = true)
    public List<StudentResponseDto> searchStudentsByName(String name) {
        List<Student> students = studentRepo.findByFullNameContainingIgnoreCase(name);
        return students.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get student count
     * @return total number of students
     */
    @Transactional(readOnly = true)
    public Long getStudentCount() {
        return studentRepo.countTotalStudents();
    }
    
    /**
     * Convert Student entity to StudentResponseDto
     * @param student the student entity
     * @return StudentResponseDto
     */
    private StudentResponseDto convertToResponseDto(Student student) {
        return new StudentResponseDto(
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                student.getPhoneNumber(),
                student.getRegisteredAt()
        );
    }
}