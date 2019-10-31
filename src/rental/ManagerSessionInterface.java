package rental;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface ManagerSessionInterface extends Remote {
    Set<String> getBestClients() throws RemoteException;

    CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws Exception;

    int getNumberOfReservationsByRenter(String clientName) throws RemoteException;

    int getNumberOfReservationsForCarType(String carRentalName, String carType) throws Exception;

    void closeManagerSession() throws RemoteException, NotBoundException;
}
