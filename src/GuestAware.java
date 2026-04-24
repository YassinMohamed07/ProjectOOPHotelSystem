import models.Guest;
//Interface for controllers that need to receive the currently logged-in Guest.

public interface GuestAware {
    void setGuest(Guest guest);
}
