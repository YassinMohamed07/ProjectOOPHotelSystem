import models.Staff;
//Interface for controllers that need to receive the currently logged-in Staff member.

public interface StaffAware {
    void setStaff(Staff staff);
}
