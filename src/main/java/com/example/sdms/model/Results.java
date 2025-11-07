package com.example.sdms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "results")
public class Results { // Changed to singular 'Result'

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ --- THE DEFINITIVE FIX ---
    // We made this the "inverse" side by adding 'mappedBy'
    // The 'student' table now holds the 'result_id' foreign key
    @OneToOne(mappedBy = "result", fetch = FetchType.LAZY)
    @JsonBackReference
    private Student student;

    // --- Marks / Grades ---
    private int dlmMarks = 0;
    private int caoMarks = 0;
    private int evsMarks = 0;
    private int dbmsMarks = 0;
    private int psMarks = 0;  // P&S
    private int uhvMarks = 0;
    private int oopsMarks = 0;

    private int totalMarks = 0;
    private String grade; // e.g., "A+", "B", "FAIL"
    private double cgpa = 0.0;

    // --- Attendance ---
    private String attendanceRecord; // e.g., "95%"

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public int getDlmMarks() { return dlmMarks; }
    public void setDlmMarks(int dlmMarks) { this.dlmMarks = dlmMarks; }

    public int getCaoMarks() { return caoMarks; }
    public void setCaoMarks(int caoMarks) { this.caoMarks = caoMarks; }

    public int getEvsMarks() { return evsMarks; }
    public void setEvsMarks(int evsMarks) { this.evsMarks = evsMarks; }

    public int getDbmsMarks() { return dbmsMarks; }
    public void setDbmsMarks(int dbmsMarks) { this.dbmsMarks = dbmsMarks; }

    public int getPsMarks() { return psMarks; }
    public void setPsMarks(int psMarks) { this.psMarks = psMarks; }

    public int getUhvMarks() { return uhvMarks; }
    public void setUhvMarks(int uhvMarks) { this.uhvMarks = uhvMarks; }

    public int getOopsMarks() { return oopsMarks; }
    public void setOopsMarks(int oopsMarks) { this.oopsMarks = oopsMarks; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public String getAttendanceRecord() { return attendanceRecord; }
    public void setAttendanceRecord(String attendanceRecord) { this.attendanceRecord = attendanceRecord; }

    // --- Helper logic to calculate results ---
    @PreUpdate
    @PrePersist
    public void calculateResults() {
        dlmMarks = Math.max(0, dlmMarks);
        caoMarks = Math.max(0, caoMarks);
        evsMarks = Math.max(0, evsMarks);
        dbmsMarks = Math.max(0, dbmsMarks);
        psMarks = Math.max(0, psMarks);
        uhvMarks = Math.max(0, uhvMarks);
        oopsMarks = Math.max(0, oopsMarks);

        this.totalMarks = dlmMarks + caoMarks + evsMarks + dbmsMarks + psMarks + uhvMarks + oopsMarks;

        // Grade Logic
        if (dlmMarks < 50 || caoMarks < 50 || evsMarks < 50 || dbmsMarks < 50 || psMarks < 50 || uhvMarks < 50 || oopsMarks < 50) {
            this.grade = "FAIL";
        } else {
            double percentage = (double) this.totalMarks / 7.0;
            if (percentage >= 90) this.grade = "O";
            else if (percentage >= 80) this.grade = "A+";
            else if (percentage >= 70) this.grade = "A";
            else if (percentage >= 60) this.grade = "B+";
            else this.grade = "B";
        }

        // --- ADDED CGPA CALCULATION ---
        switch (this.grade != null ? this.grade : "FAIL") {
            case "O": this.cgpa = 10.0; break;
            case "A+": this.cgpa = 9.0; break;
            case "A": this.cgpa = 8.0; break;
            case "B+": this.cgpa = 7.0; break;
            case "B": this.cgpa = 6.0; break;
            default: this.cgpa = 0.0; break; // For "FAIL" or null
        }
    }
}