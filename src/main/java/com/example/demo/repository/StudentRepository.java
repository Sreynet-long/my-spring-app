package com.example.demo.repository;

import com.example.demo.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // ស្វែងរកនិស្សិតតាមអ៊ីមែល
    Optional<Student> findByEmail(String email);

    // ពិនិត្យមើលថាតើអ៊ីមែលមានរួចហើយឬទេ
    boolean existsByEmail(String email);

    // ស្វែងរកនិស្សិតតាមឈ្មោះ (មិនខ្វល់ពីអក្សរធំតូច)
    List<Student> findByNameContainingIgnoreCase(String name);

    // ស្វែងរកនិស្សិតតាមជំនាញ
    List<Student> findByMajor(String major);

    // ស្វែងរកនិស្សិតតាមជំនាញ (ជាមួយ Pagination)
    Page<Student> findByMajor(String major, Pageable pageable);

    // ស្វែងរកនិស្សិតដែលកើតក្នុងកម្រិតកាលបរិច្ឆេទ
    List<Student> findByBirthDateBetween(LocalDate startDate, LocalDate endDate);

    // ស្វែងរកនិស្សិតតាមលេខទូរសព្ទ
    Optional<Student> findByPhoneNumber(String phoneNumber);

    // Custom Query ដោយប្រើ JPQL
    @Query("SELECT s FROM Student s WHERE s.major = :major AND s.name LIKE %:name%")
    List<Student> findByMajorAndNameContaining(@Param("major") String major,
                                               @Param("name") String name);

    // Custom Query ដោយប្រើ Native SQL
    @Query(value = "SELECT * FROM students s WHERE s.major = ?1 ORDER BY s.student_name",
            nativeQuery = true)
    List<Student> findStudentsByMajorNative(String major);

    // រាប់ចំនួននិស្សិតតាមជំនាញ
    @Query("SELECT COUNT(s) FROM Student s WHERE s.major = :major")
    long countStudentsByMajor(@Param("major") String major);

    // ស្វែងរកនិស្សិតដែលមានឈ្មោះ និងជំនាញ
    List<Student> findByNameContainingIgnoreCaseAndMajorContainingIgnoreCase(
            String name, String major);

    // ស្វែងរកនិស្សិត top 5 តាមកាលបរិច្ឆេទបង្កើត
    List<Student> findTop5ByOrderByCreatedAtDesc();

    // Custom Query សម្រាប់ការស្វែងរកស្មុគស្មាញ
    @Query("SELECT s FROM Student s WHERE " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:major IS NULL OR LOWER(s.major) LIKE LOWER(CONCAT('%', :major, '%'))) AND " +
            "(:email IS NULL OR LOWER(s.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    Page<Student> findStudentsWithFilters(@Param("name") String name,
                                          @Param("major") String major,
                                          @Param("email") String email,
                                          Pageable pageable);
}
