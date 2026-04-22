import models.Guest;
//Interface for controllers that need to receive the currently logged-in Guest.
//Implemented by all dashboard/screen controllers that require guest context.

public interface GuestAware {
    void setGuest(Guest guest);
}
