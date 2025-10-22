package com.example.demo.controller;

//import com.example.demo.entity.Student;
import com.example.demo.entity.*;
//import com.ict.studentmanagementsystem.service.StudentService;
import com.example.demo.service.StudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*") // សម្រាប់ CORS
@Validated
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // GET - យកនិស្សិតទាំងអស់
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            if (students.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET - យកនិស្សិតជាមួយ Pagination
    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getAllStudentsPaginated(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Page<Student> pageStudents = studentService.getAllStudentsPaginated(page, size, sortBy, sortDir);

            Map<String, Object> response = new HashMap<>();
            response.put("students", pageStudents.getContent());
            response.put("currentPage", pageStudents.getNumber());
            response.put("totalItems", pageStudents.getTotalElements());
            response.put("totalPages", pageStudents.getTotalPages());
            response.put("pageSize", pageStudents.getSize());
            response.put("hasNext", pageStudents.hasNext());
            response.put("hasPrevious", pageStudents.hasPrevious());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET - យកនិស្សិតតាម ID
    @GetMapping("/{id}")
    public ResponseEntity getStudentById(@PathVariable @Min(1) Long id) {
        try {
            Optional student = studentService.getStudentById(id);
            if (student.isPresent()) {
                return new ResponseEntity<>(student.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST - បង្កើតនិស្សិតថ្មី
    @PostMapping
    public ResponseEntity<Map<String, Object>> createStudent(@Valid @RequestBody Student student) {
        try {
            Student savedStudent = studentService.saveStudent(student);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "បង្កើតនិស្សិតបានជោគជ័យ");
            response.put("student", savedStudent);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "មានបញ្ហាកើតឡើងក្នុងការបង្កើតនិស្សិត");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT - កែប្រែនិស្សិត
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody Student studentDetails) {
        try {
            Student updatedStudent = studentService.updateStudent(id, studentDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "កែប្រែនិស្សិតបានជោគជ័យ");
            response.put("student", updatedStudent);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "មានបញ្ហាកើតឡើងក្នុងការកែប្រែ");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE - លុបនិស្សិត
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable @Min(1) Long id) {
        try {
            studentService.deleteStudent(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "លុបនិស្សិតបានជោគជ័យ");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "មានបញ្ហាកើតឡើងក្នុងការលុប");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE - លុបនិស្សិតច្រើនជាងមួយ
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteStudents(@RequestBody List<Long> ids) {
        try {
            studentService.deleteStudents(ids);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "លុបនិស្សិតបានជោគជ័យ ចំនួន: " + ids.size());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "មានបញ្ហាកើតឡើងក្នុងការលុប");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // GET - ស្វែងរកនិស្សិតតាមឈ្មោះ
    @GetMapping("/search/name")
    public ResponseEntity<List<Student>> searchStudentsByName(@RequestParam String name) {
        try {
            List<Student> students = studentService.searchStudentsByName(name);
            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET - យកនិស្សិតតាមជំនាញ
    @GetMapping("/major/{major}")
    public ResponseEntity<List<Student>> getStudentsByMajor(@PathVariable String major) {
        try {
            List<Student> students = studentService.getStudentsByMajor(major);
            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET - ស្វែងរកស្មុគស្មាញ
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Page<Student> pageStudents = studentService.searchStudents(name, major, email, page, size, sortBy, sortDir);

            Map<String, Object> response = new HashMap<>();
            response.put("students", pageStudents.getContent());
            response.put("currentPage", pageStudents.getNumber());
            response.put("totalItems", pageStudents.getTotalElements());
            response.put("totalPages", pageStudents.getTotalPages());
            Map<String, String> searchCriteria = new HashMap<>();
            searchCriteria.put("name", name != null ? name : "");
            searchCriteria.put("major", major != null ? major : "");
            searchCriteria.put("email", email != null ? email : "");
            response.put("searchCriteria", searchCriteria);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET - ស្ថិតិ
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStudents", studentService.getTotalStudents());
            stats.put("recentStudents", studentService.getRecentStudents());

            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET - ពិនិត្យអ៊ីមែល
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        try {
            boolean exists = studentService.isEmailExists(email);
            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}