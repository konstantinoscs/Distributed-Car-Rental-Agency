package rental;

import java.rmi.RemoteException;
import java.util.Set;

public class ManagerSession implements ManagerSessionInterface {
    private CarRentalAgency carRentalAgency;

    public ManagerSession(CarRentalAgency carRentalAgency) {
        this.carRentalAgency = carRentalAgency;
    }

    @Override
    public Set<String> getBestClients() throws RemoteException {
        return this.carRentalAgency.getBestClients();
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws Exception {
        return this.carRentalAgency.getMostPopularCarTypeIn(carRentalCompanyName, year);
    }

    @Override
    public int getNumberOfReservationsByRenter(String clientName) throws RemoteException {
        return this.carRentalAgency.getNumberOfReservationsByRenter(clientName);
    }

    @Override
    public int getNumberOfReservationsForCarType(String carRentalName, String carType) throws Exception {
        return this.carRentalAgency.getNumberOfReservationsForCarType(carRentalName, carType);
    }
}
