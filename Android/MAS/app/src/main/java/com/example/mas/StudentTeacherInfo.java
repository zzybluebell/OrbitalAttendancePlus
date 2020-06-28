package com.example.mas;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class StudentTeacherInfo {
    int Id;
    String ModuleName;
    String TeacherName;
    String Phone;
    String Email;
    String TeacherImage;

    public StudentTeacherInfo(int id, String moduleName, String teacherName, String phone, String email, String teacherImage) {
        Id = id;
        ModuleName = moduleName;
        TeacherName = teacherName;
        Phone = phone;
        Email = email;
        TeacherImage = teacherImage;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getModuleName() {
        return ModuleName;
    }

    public void setModuleName(String moduleName) {
        ModuleName = moduleName;
    }

    public String getTeacherName() {
        return TeacherName;
    }

    public void setTeacherName(String teacherName) {
        TeacherName = teacherName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getTeacherImage() {
        return TeacherImage;
    }

    public void setTeacherImage(String teacherImage) {
        TeacherImage = teacherImage;
    }
}
