package rental;

import java.rmi.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface AgencyInterface extends Remote {

    ReservationSession getNewReservationSession(String name) throws RemoteException;
    ManagerSession getNewManagerSession(String name, String carRentalName) throws RemoteException;
    Set<String> getBestClients(ManagerSession ms) throws RemoteException;
    String getCheapestCarType(ReservationSession session, Date start, Date end, String region) throws RemoteException;
    CarType getMostPopularCarTypeIn(ManagerSession ms, String carRentalCompanyName, int year) throws RemoteException;
    int getNumberOfReservationsByRenter(ManagerSession ms, String clientName) throws RemoteException;
    int getNumberOfReservationsForCarType(ManagerSession ms, String carRentalName, String carType) throws RemoteException;
    void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws RemoteException;
    void addQuoteToSession(ReservationSession session, String name,
                           Date start, Date end, String carType, String region) throws RemoteException;
    List<Reservation> confirmQuotes(ReservationSession session, String name) throws RemoteException;
}