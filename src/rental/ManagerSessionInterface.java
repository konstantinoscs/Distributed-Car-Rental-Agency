package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface ManagerSessionInterface extends Remote {
    Set<String> getBestClients() throws RemoteException;

    CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws RemoteException;

    int getNumberOfReservationsByRenter(String clientName) throws RemoteException;

    int getNumberOfReservationsForCarType(String carRentalName, String carType) throws RemoteException;
}
