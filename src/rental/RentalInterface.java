package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RentalInterface extends Remote {
    Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;

    Quote createQuote(ReservationConstraints constraints, String client)
            throws ReservationException, RemoteException;

    Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException;

    void cancelReservation(Reservation res) throws RemoteException;

    List<Reservation> getReservationsByRenter(String clientName) throws RemoteException;

    int getNumberOfReservationsForCarType(String carType) throws RemoteException;

    boolean operatesInRegion(String region) throws RemoteException;

    Map<String, Integer> getClientsWithReservations() throws RemoteException;

    int getNoOfReservationsByRenter(String clientName) throws RemoteException;

    CarType getMostPopularCarTypeIn(int year) throws RemoteException;
}
