package org.unidata1.service;

import org.unidata1.model.Data;
import org.unidata1.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DataService {

    Data createData(Data data);

    Data updateData(Data data);

    Optional<Data> getDataById(Long id);

    List<Data> getAllData();

    void deleteData(Long id);

    List<Data> getDataByFaculty(String facultyName);

    List<String> getAllDepartments();

    Map<String, String> getAllSpecialties();

    List<User> getTeachersByDepartment(String department);

    List<User> getStudentsBySpecialty(String specialtyCode);

    void addTeacherToDepartment(User teacher, String department);

    void addStudentToSpecialty(User student, String specialtyCode);

    long countStudents();

    long countTeachers();

    void importDataFromFile(String filePath);

    void exportDataToFile(String filePath);
}