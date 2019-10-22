package rental;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CarRentalAgency implements AgencyInterface {

    private final static int LOCAL = 0;
    private final static int REMOTE = 1;

    private List<RentalInterface> carRentalCompanies;
    private List<ReservationSession> reservationSessions;

    public CarRentalAgency(List<String> carRentalCompanyNames, int localOrRemote) throws Exception {
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
                throw new Exception("Couldn't retrieve car rental company from registry.");
            }
            carRentalCompanies.add(carRentalCompany);
        }

        reservationSessions = new ArrayList<>();
        
    }

    public ReservationSession getNewReservationSession(String name) throws RemoteException {
        ReservationSession reservationSession = new ReservationSession(name);
        //reservationSessions.add(reservationSession);
        return reservationSession;
    }

    public ManagerSession getNewManagerSession(String name, String carRentalName) throws RemoteException {
        return null;
    }

    public Set<String> getBestClients(ManagerSession ms) throws RemoteException {
        return null;
    }

    public String getCheapestCarType(ReservationSession session, Date start, Date end, String region) throws RemoteException {
        return null;
    }

    public CarType getMostPopularCarTypeIn(ManagerSession ms, String carRentalCompanyName, int year) throws RemoteException {
        return null;
    }

    public int getNumberOfReservationsByRenter(ManagerSession ms, String clientName) throws RemoteException {
        return 0;
    }

    public int getNumberOfReservationsForCarType(ManagerSession ms, String carRentalName, String carType)
            throws RemoteException {
        return 0;
    }

    public void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws RemoteException{
    }

    public void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType, String region)
            throws RemoteException {
    }

    public List<Reservation> confirmQuotes(ReservationSession session, String name) throws RemoteException {
        /*List<Quote> quotes = session.getCurrentQuotes();
        for(RentalInterface company : carRentalCompanies){
            company.confirmQuote();
        }*/
        return null;
    }
}
