package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AgencyInterface extends Remote {
    String getNewReservationSession(String name) throws RemoteException;
    ManagerSession getNewManagerSession(String name, String carRentalName) throws RemoteException;
}