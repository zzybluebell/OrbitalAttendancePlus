package com.example.mas;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class TeacherModule {
    int Id;
    String ModuleId;
    String ModuleCode;
    String ModuleName;
    String BeginTime;
    String EndTime;
    String Status;
    String StartStop;

    public TeacherModule(int id, String moduleId, String moduleCode, String moduleName, String beginTime, String endTime, String status, String startStop) {
        Id = id;
        ModuleId = moduleId;
        ModuleCode = moduleCode;
        ModuleName = moduleName;
        BeginTime = beginTime;
        EndTime = endTime;
        Status = status;
        StartStop = startStop;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getModuleId() {
        return ModuleId;
    }

    public void setModuleId(String moduleId) {
        ModuleId = moduleId;
    }

    public String getModuleCode() {
        return ModuleCode;
    }

    public void setModuleCode(String moduleCode) {
        ModuleCode = moduleCode;
    }

    public String getModuleName() {
        return ModuleName;
    }

    public void setModuleName(String moduleName) {
        ModuleName = moduleName;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(String beginTime) {
        BeginTime = beginTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStartStop() {
        return StartStop;
    }

    public void setStartStop(String startStop) {
        StartStop = startStop;
    }
}
