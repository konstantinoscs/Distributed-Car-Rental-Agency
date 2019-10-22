package rental;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CarRentalAgency implements AgencyInterface {

    private final static int LOCAL = 0;
    private final static int REMOTE = 1;

    private List<RentalInterface> carRentalCompanies;

    public CarRentalAgency(List<String> carRentalCompanyNames, int localOrRemote) throws RemoteException {
        String host = localOrRemote == REMOTE ? "192.168.104.76" : "127.0.0.1";
        Registry namingRegistry = null;
        int port = 10447;
        if(localOrRemote == REMOTE ) {
            namingRegistry =  LocateRegistry.getRegistry(host, port);
        }
        else {
            namingRegistry = LocateRegistry.getRegistry();
        }
        RentalInterface carRentalCompany;
        for (String company : carRentalCompanyNames) {
            try {
                carRentalCompany = (RentalInterface) namingRegistry.lookup(company);
            } catch (Exception e){
                e.printStackTrace();
                throw new Exception("Couldn't retrieve car rental company from registry.")
            }
            carRentalCompanies.add(carRentalCompany);
        }
        
    }

    public ReservationSession getNewReservationSession(String name) throws RemoteException {

    }

    public ManagerSession getNewManagerSession(String name, String carRentalName) throws RemoteException {
    }

    public Set<String> getBestClients(ManagerSession ms) throws RemoteException {
    }

    public String getCheapestCarType(ReservationSession session, Date start, Date end, String region) throws RemoteException {
    }

    public CarType getMostPopularCarTypeIn(ManagerSession ms, String carRentalCompanyName, int year) throws RemoteException{
    }

    public int getNumberOfReservationsByRenter(ManagerSession ms, String clientName) throws RemoteException {
    }

    public int getNumberOfReservationsForCarType(ManagerSession ms, String carRentalName, String carType)
            throws RemoteException {
    }

    public void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws RemoteException{
    }

    public void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType, String region)
            throws RemoteException {
    }

    public List<Reservation> confirmQuotes(ReservationSession session, String name) throws RemoteException {
    }
}
