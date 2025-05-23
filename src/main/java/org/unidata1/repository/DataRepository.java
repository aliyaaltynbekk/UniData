package org.unidata1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.unidata1.model.Data;
import org.unidata1.model.Data.EducationLevel;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<Data, Long> {

    List<Data> findByFacultyName(String facultyName);

    List<Data> findByEducationLevel(EducationLevel educationLevel);

    @Query("SELECT d FROM Data d WHERE :department MEMBER OF d.departments")
    List<Data> findByDepartment(@Param("department") String department);

    @Query("SELECT d FROM Data d WHERE KEY(d.specialties) = :code")
    List<Data> findBySpecialtyCode(@Param("code") String code);

    List<Data> findByAcademicYearStartGreaterThanEqual(LocalDate date);

    List<Data> findByAcademicYearEndLessThanEqual(LocalDate date);

    @Query("SELECT DISTINCT d.departments FROM Data d")
    List<String> findAllDepartments();

    @Query("SELECT COUNT(s) FROM Data d JOIN d.studentUsers s")
    long countAllStudents();

    @Query("SELECT COUNT(t) FROM Data d JOIN d.teacherUsers t")
    long countAllTeachers();

    @Query("SELECT d FROM Data d WHERE d.academicYearStart <= :date AND d.academicYearEnd >= :date")
    List<Data> findCurrentAcademicYear(@Param("date") LocalDate date);
}