package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // យកនិស្សិតទាំងអស់
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // យកនិស្សិតទាំងអស់ជាមួយ Pagination
    @Transactional(readOnly = true)
    public Page getAllStudentsPaginated(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepository.findAll(pageable);
    }

    // យកនិស្សិតតាម ID
    @Transactional(readOnly = true)
    public Optional getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // រក្សាទុកនិស្សិតថ្មី
    public Student saveStudent(Student student) {
        // ពិនិត្យមើលថាតើអ៊ីមែលមានរួចហើយឬទេ
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new RuntimeException("អ៊ីមែលនេះមានរួចហើយ: " + student.getEmail());
        }
        return studentRepository.save(student);
    }

    // កែប្រែនិស្សិត
    public Student updateStudent(Long id, Student studentDetails) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("រកមិនឃើញនិស្សិត ID: " + id));

        // ពិនិត្យអ៊ីមែលថ្មី
        if (!student.getEmail().equals(studentDetails.getEmail()) &&
                studentRepository.existsByEmail(studentDetails.getEmail())) {
            throw new RuntimeException("អ៊ីមែលនេះមានរួចហើយ: " + studentDetails.getEmail());
        }

        student.setName(studentDetails.getName());
        student.setEmail(studentDetails.getEmail());
        student.setPhoneNumber(studentDetails.getPhoneNumber());
        student.setBirthDate(studentDetails.getBirthDate());
        student.setMajor(studentDetails.getMajor());
        student.setAddress(studentDetails.getAddress());

        return studentRepository.save(student);
    }

    // លុបនិស្សិត
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("រកមិនឃើញនិស្សិត ID: " + id));
        studentRepository.delete(student);
    }

    // លុបនិស្សិតច្រើនជាងមួយ
    public void deleteStudents(List ids) {
        List students = studentRepository.findAllById(ids);
        if (students.size() != ids.size()) {
            throw new RuntimeException("មានបញ្ហាក្នុងការលុបនិស្សិត");
        }
        studentRepository.deleteAll(students);
    }

    // ស្វែងរកនិស្សិតតាមឈ្មោះ
    @Transactional(readOnly = true)
    public List searchStudentsByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    // ស្វែងរកនិស្សិតតាមជំនាញ
    @Transactional(readOnly = true)
    public List getStudentsByMajor(String major) {
        return studentRepository.findByMajor(major);
    }

    // ស្វែងរកនិស្សិតតាមជំនាញជាមួយ Pagination
    @Transactional(readOnly = true)
    public Page getStudentsByMajorPaginated(String major, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return studentRepository.findByMajor(major, pageable);
    }

    // ស្វែងរកនិស្សិតតាមអ៊ីមែល
    @Transactional(readOnly = true)
    public Optional getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    // ស្វែងរកនិស្សិតដែលកើតក្នុងឆ្នាំកំណត់
    @Transactional(readOnly = true)
    public List getStudentsByBirthYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return studentRepository.findByBirthDateBetween(startDate, endDate);
    }

    // ស្វែងរកស្មុគស្មាញ
    @Transactional(readOnly = true)
    public Page searchStudents(String name, String major, String email,
                               int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return studentRepository.findStudentsWithFilters(name, major, email, pageable);
    }

    // រាប់ចំនួននិស្សិតទាំងអស់
    @Transactional(readOnly = true)
    public long getTotalStudents() {
        return studentRepository.count();
    }

    // រាប់ចំនួននិស្សិតតាមជំនាញ
    @Transactional(readOnly = true)
    public long getStudentCountByMajor(String major) {
        return studentRepository.countStudentsByMajor(major);
    }

    // យកនិស្សិតថ្មីៗ
    @Transactional(readOnly = true)
    public List getRecentStudents() {
        return studentRepository.findTop5ByOrderByCreatedAtDesc();
    }

    // ពិនិត្យមើលថាតើអ៊ីមែលមានរួចហើយឬទេ
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return studentRepository.existsByEmail(email);
    }
}
