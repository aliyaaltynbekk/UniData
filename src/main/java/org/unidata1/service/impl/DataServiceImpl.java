package org.unidata1.service.impl;

import org.springframework.stereotype.Service;
import org.unidata1.model.Data;
import org.unidata1.model.User;
import org.unidata1.service.DataService;

import java.util.*;

@Service
public class DataServiceImpl implements DataService {

    private final Map<Long, Data> dataMap = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public Data createData(Data data) {
        data.setId(nextId++);
        dataMap.put(data.getId(), data);
        return data;
    }

    @Override
    public Data updateData(Data data) {
        if (data.getId() != null && dataMap.containsKey(data.getId())) {
            dataMap.put(data.getId(), data);
            return data;
        }
        return null;
    }

    @Override
    public Optional<Data> getDataById(Long id) {
        return Optional.ofNullable(dataMap.get(id));
    }

    @Override
    public List<Data> getAllData() {
        return new ArrayList<>(dataMap.values());
    }

    @Override
    public void deleteData(Long id) {
        dataMap.remove(id);
    }

    @Override
    public List<Data> getDataByFaculty(String facultyName) {
        return dataMap.values().stream()
                .filter(data -> data.getFacultyName().equalsIgnoreCase(facultyName))
                .toList();
    }

    @Override
    public List<String> getAllDepartments() {
        Set<String> departments = new HashSet<>();
        dataMap.values().forEach(data -> departments.addAll(data.getDepartments()));
        return new ArrayList<>(departments);
    }

    @Override
    public Map<String, String> getAllSpecialties() {
        Map<String, String> allSpecialties = new HashMap<>();
        dataMap.values().forEach(data -> allSpecialties.putAll(data.getSpecialties()));
        return allSpecialties;
    }

    @Override
    public List<User> getTeachersByDepartment(String department) {
        List<User> teachers = new ArrayList<>();
        for (Data data : dataMap.values()) {
            if (data.getDepartments().contains(department)) {
                teachers.addAll(data.getTeachers());
            }
        }
        return teachers;
    }

    @Override
    public List<User> getStudentsBySpecialty(String specialtyCode) {
        List<User> students = new ArrayList<>();
        for (Data data : dataMap.values()) {
            if (data.getSpecialties().containsKey(specialtyCode)) {
                students.addAll(data.getStudents());
            }
        }
        return students;
    }

    @Override
    public void addTeacherToDepartment(User teacher, String department) {
        for (Data data : dataMap.values()) {
            if (data.getDepartments().contains(department)) {
                data.addTeacher(teacher);
                break;
            }
        }
    }

    @Override
    public void addStudentToSpecialty(User student, String specialtyCode) {
        for (Data data : dataMap.values()) {
            if (data.getSpecialties().containsKey(specialtyCode)) {
                data.addStudent(student);
                break;
            }
        }
    }

    @Override
    public long countStudents() {
        return dataMap.values().stream()
                .mapToLong(data -> data.getStudents().size())
                .sum();
    }

    @Override
    public long countTeachers() {
        return dataMap.values().stream()
                .mapToLong(data -> data.getTeachers().size())
                .sum();
    }

    @Override
    public void importDataFromFile(String filePath) {
        System.out.println("Файлдан деректерді импорттау: " + filePath);
    }

    @Override
    public void exportDataToFile(String filePath) {
        System.out.println("Файлға деректерді экспорттау: " + filePath);
    }
}