package com.ict.studentmanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name", nullable = false, length = 100)
    @NotBlank(message = "ឈ្មោះវគ្គសិក្សាមិនអាចទទេបានទេ")
    private String courseName;

    @Column(name = "course_code", unique = true, length = 20)
    @NotBlank(message = "កូដវគ្គសិក្សាមិនអាចទទេបានទេ")
    private String courseCode;

    @Column(name = "credits")
    @Min(value = 1, message = "ឥណទានត្រូវតែចាប់ពី 1 ឡើង")
    @Max(value = 10, message = "ឥណទានត្រូវតែតិចជាង 10")
    private Integer credits;

    @Column(name = "price", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "តម្លៃត្រូវតែធំជាង 0")
    private BigDecimal price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Many-to-Many relationship with Student
    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Course() {}

    public Course(String courseName, String courseCode, Integer credits, BigDecimal price, String description) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.credits = credits;
        this.price = price;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<Student> getStudents() { return students; }
    public void setStudents(Set<Student> students) { this.students = students; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseName='" + courseName + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", credits=" + credits +
                ", price=" + price +
                '}';
    }
}
