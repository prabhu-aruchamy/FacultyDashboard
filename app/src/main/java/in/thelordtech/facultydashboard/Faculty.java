package in.thelordtech.facultydashboard;

public class Faculty {

    private String facultyIconLink;
    private String facutyName;
    private String facultyUID;
    private String facultyEmail;
    private String facultyNumber;
    private String facultyBio;
    private String facultyEducation;
    private String facultyProjects;
    private String facultyResume;

    public Faculty(String facultyIconLink, String facutyName, String facultyUID, String facultyEmail, String facultyNumber, String facultyBio, String facultyEducation, String facultyProjects, String facultyResume) {
        this.facultyIconLink = facultyIconLink;
        this.facutyName = facutyName;
        this.facultyUID = facultyUID;
        this.facultyEmail = facultyEmail;
        this.facultyNumber = facultyNumber;
        this.facultyBio = facultyBio;
        this.facultyEducation = facultyEducation;
        this.facultyProjects = facultyProjects;
        this.facultyResume = facultyResume;
    }

    public String getFacultyIconLink() {
        return facultyIconLink;
    }

    public String getFacutyName() {
        return facutyName;
    }

    public String getFacultyUID() {
        return facultyUID;
    }

    public String getFacultyEmail() {
        return facultyEmail;
    }

    public String getFacultyNumber() {
        return facultyNumber;
    }

    public String getFacultyBio() {
        return facultyBio;
    }

    public String getFacultyEducation() {
        return facultyEducation;
    }

    public String getFacultyProjects() {
        return facultyProjects;
    }

    public String getFacultyResume() {
        return facultyResume;
    }
}
