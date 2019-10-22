package rental;

import java.rmi.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface AgencyInterface extends Remote {
    ReservationSession getNewReservationSession(String name) throws RemoteException;
    ManagerSession getNewManagerSession(String name, String carRentalName) throws RemoteException;
}