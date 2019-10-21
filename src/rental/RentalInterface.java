package rental;

import java.rmi.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;


public interface RentalInterface extends Remote {
    Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;

    Quote createQuote(ReservationConstraints constraints, String client)
            throws ReservationException, RemoteException;

    Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException;

    void cancelReservation(Reservation res) throws RemoteException;

    List<Reservation> getReservationsByRenter(String clientName) throws RemoteException;

    int getNumberOfReservationsForCarType(String carType) throws RemoteException;
}