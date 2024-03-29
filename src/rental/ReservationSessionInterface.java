package rental;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ReservationSessionInterface extends Remote, Serializable {
    String getCheapestCarType(Date start, Date end, String region) throws RemoteException;

    Set<CarType> checkForAvailableCarTypes(Date start, Date end) throws RemoteException;

    void addQuoteToSession(String name, Date start, Date end, String carType, String region) throws Exception;

    List<Reservation> confirmQuotes(String name) throws ReservationException, RemoteException;

    void closeReservationSession() throws RemoteException, NotBoundException;
}
