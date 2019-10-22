package rental;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

public class ManagerSession implements ManagerSessionInterface {

    public ManagerSession() {

    }

    @Override
    public Set<String> getBestClients() {
        return new HashSet<>();
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) {
        return null;
    }

    @Override
    public int getNumberOfReservationsByRenter(String clientName) {
        return 0;
    }

    @Override
    public int getNumberOfReservationsForCarType(String carRentalName, String carType) {
        return 0;
    }
}
