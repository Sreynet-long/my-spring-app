package com.ict.studentmanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_name", nullable = false, length = 100)
    @NotBlank(message = "ឈ្មោះមិនអាចទទេបានទេ")
    @Size(min = 2, max = 100, message = "ឈ្មោះត្រូវមាន 2-100 តួអក្សរ")
    private String name;

    @Column(name = "student_email", unique = true, nullable = false)
    @Email(message = "ទម្រង់អ៊ីមែលមិនត្រឹមត្រូវ")
    @NotBlank(message = "អ៊ីមែលមិនអាចទទេបានទេ")
    private String email;

    @Column(name = "phone_number", length = 15)
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "លេខទូរសព្ទមិនត្រឹមត្រូវ")
    private String phoneNumber;

    @Column(name = "birth_date")
    @Past(message = "កាលបរិច្ឆេទកំណើតត្រូវតែជាអតីតកាល")
    private LocalDate birthDate;

    @Column(name = "major", length = 50)
    @NotBlank(message = "ជំនាញមិនអាចទទេបានទេ")
    private String major;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    // Many-to-Many relationship with Course
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Student() {}

    public Student(String name, String email, String phoneNumber,
                   LocalDate birthDate, String major, String address) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.major = major;
        this.address = address;
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Set<Course> getCourses() { return courses; }
    public void setCourses(Set<Course> courses) { this.courses = courses; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", major='" + major + '\'' +
                '}';
    }

}
