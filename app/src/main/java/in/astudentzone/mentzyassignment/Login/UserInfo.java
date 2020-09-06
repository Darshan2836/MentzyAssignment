package in.astudentzone.mentzyassignment.Login;

public class UserInfo {
    String profilepic,name,emailid,age,city,profession,hobbies,mobile;

    public UserInfo(String profilepic) {
        this.profilepic = profilepic;
    }

    public UserInfo(String name, String emailid, String age, String city, String profession, String hobbies) {
        this.name = name;
        this.emailid = emailid;
        this.age = age;
        this.city = city;
        this.profession = profession;
        this.hobbies = hobbies;
    }


    public UserInfo() {
    }

    public String getProfilepic() {
        return profilepic;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }
}
