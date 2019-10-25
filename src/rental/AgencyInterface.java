package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AgencyInterface extends Remote {
    String getNewReservationSession(String name) throws RemoteException;

    String getNewManagerSession(String name, String carRentalName) throws RemoteException;
}