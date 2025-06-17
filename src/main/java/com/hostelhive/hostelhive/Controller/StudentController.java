package com.hostelhive.hostelhive.Controller;

import com.hostelhive.hostelhive.DTOs.StudentLoginDto;
import com.hostelhive.hostelhive.DTOs.StudentRegistrationDto;
import com.hostelhive.hostelhive.DTOs.StudentResponseDto;
import com.hostelhive.hostelhive.DTOs.StudentUpdateDto;
import com.hostelhive.hostelhive.Service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Register a new student
    @PostMapping("/register")
    public ResponseEntity<StudentResponseDto> registerStudent(@Valid @RequestBody StudentRegistrationDto registrationDto) {
        StudentResponseDto response = studentService.registerStudent(registrationDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Authenticate student login
    @PostMapping("/login")
    public ResponseEntity<StudentResponseDto> loginStudent(@Valid @RequestBody StudentLoginDto loginDto) {
        StudentResponseDto response = studentService.authenticateStudent(loginDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
        StudentResponseDto response = studentService.getStudentById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get all students
    @GetMapping
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {
        List<StudentResponseDto> students = studentService.getAllStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    // Update student profile
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentUpdateDto updateDto) {
        StudentResponseDto response = studentService.updateStudent(id, updateDto);
        return new ResponseEntity<>(response, HttpStatus.OK); // Fixed the typo here
    }

    // Delete student
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Search students by name
    @GetMapping("/search")
    public ResponseEntity<List<StudentResponseDto>> searchStudentsByName(@RequestParam String name) {
        List<StudentResponseDto> students = studentService.searchStudentsByName(name);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    // Get total student count
    @GetMapping("/count")
    public ResponseEntity<Long> getStudentCount() {
        Long count = studentService.getStudentCount();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}