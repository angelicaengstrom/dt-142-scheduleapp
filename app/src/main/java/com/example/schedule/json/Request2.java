package com.example.schedule.json;

public class Request2 {
    private Shift2 shift;
    private Employee toEmployee;

    public Employee getToEmployee() {
        return toEmployee;
    }

    public void setToEmployee(Employee toEmployee) {
        this.toEmployee = toEmployee;
    }

    public Shift2 getShift() {
        return shift;
    }

    public void setShift(Shift2 shift) {
        this.shift = shift;
    }
}
