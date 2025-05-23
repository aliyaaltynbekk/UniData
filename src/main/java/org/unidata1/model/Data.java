package org.unidata1.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "data")
public class Data {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String facultyName;

    @ElementCollection
    @CollectionTable(name = "departments", joinColumns = @JoinColumn(name = "data_id"))
    @Column(name = "department_name")
    private List<String> departments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "data_students",
            joinColumns = @JoinColumn(name = "data_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> studentUsers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "data_teachers",
            joinColumns = @JoinColumn(name = "data_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> teacherUsers = new ArrayList<>();

    @Transient
    private List<User> teachers = new ArrayList<>();

    @Transient
    private List<User> students = new ArrayList<>();


    @ElementCollection
    @CollectionTable(name = "specialties", joinColumns = @JoinColumn(name = "data_id"))
    @MapKeyColumn(name = "code")
    @Column(name = "name")
    private Map<String, String> specialties = new HashMap<>();

    private LocalDate academicYearStart;

    private LocalDate academicYearEnd;

    private String educationForm;

    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    public void addTeacher(User teacher) {
        if (teacher != null && !teacherUsers.contains(teacher)) {
            teacherUsers.add(teacher);
        }
    }

    public void addStudent(User student) {
        if (student != null && !studentUsers.contains(student)) {
            studentUsers.add(student);
        }
    }

    public enum EducationLevel {
        BACHELOR("Бакалавриат"),
        MASTER("Магистратура"),
        PHD("Докторантура");

        private final String displayName;

        EducationLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Data() {}

    public List<User> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<User> teachers) {
        this.teachers = teachers;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }

    public void addDepartment(String department) {
        this.departments.add(department);
    }

    public List<User> getStudentUsers() {
        return studentUsers;
    }

    public void setStudentUsers(List<User> studentUsers) {
        this.studentUsers = studentUsers;
    }

    public void addStudentUser(User student) {
        this.studentUsers.add(student);
    }

    public List<User> getTeacherUsers() {
        return teacherUsers;
    }

    public void setTeacherUsers(List<User> teacherUsers) {
        this.teacherUsers = teacherUsers;
    }

    public void addTeacherUser(User teacher) {
        this.teacherUsers.add(teacher);
    }

    public Map<String, String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(Map<String, String> specialties) {
        this.specialties = specialties;
    }

    public void addSpecialty(String code, String name) {
        this.specialties.put(code, name);
    }

    public LocalDate getAcademicYearStart() {
        return academicYearStart;
    }

    public void setAcademicYearStart(LocalDate academicYearStart) {
        this.academicYearStart = academicYearStart;
    }

    public LocalDate getAcademicYearEnd() {
        return academicYearEnd;
    }

    public void setAcademicYearEnd(LocalDate academicYearEnd) {
        this.academicYearEnd = academicYearEnd;
    }

    public String getEducationForm() {
        return educationForm;
    }

    public void setEducationForm(String educationForm) {
        this.educationForm = educationForm;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", facultyName='" + facultyName + '\'' +
                ", departments=" + departments +
                ", specialties=" + specialties +
                ", studentUsers=" + studentUsers.size() +
                ", teacherUsers=" + teacherUsers.size() +
                ", academicYearStart=" + academicYearStart +
                ", academicYearEnd=" + academicYearEnd +
                ", educationForm='" + educationForm + '\'' +
                ", educationLevel=" + educationLevel +
                '}';
    }
}
