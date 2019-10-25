package rental;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CarRentalAgency implements AgencyInterface {

    private final static int REMOTE = 1;
    private final int rmiPort = 10448;
    private int agencySerialId;
    private Registry namingRegistry;

    private List<RentalInterface> carRentalCompanies;
    private List<ReservationSession> reservationSessions;

    public CarRentalAgency(List<String> carRentalCompanyNames, int localOrRemote) throws Exception {
        this.agencySerialId = 0;
        String host = localOrRemote == REMOTE ? "192.168.104.76" : "127.0.0.1";

        int port = 10447;
        if(localOrRemote == REMOTE ) {
            this.namingRegistry = LocateRegistry.getRegistry(host, port);
        }
        else {
            this.namingRegistry = LocateRegistry.getRegistry();
        }

        this.carRentalCompanies = new ArrayList<>();

        //retrieve all the carRentalInterfaces from the registry
        for (String company : carRentalCompanyNames) {
            RentalInterface carRentalCompany;
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

    //return stub identifier as String
    public String getNewReservationSession(String name) throws RemoteException {
        ReservationSession reservationSession = new ReservationSession(this, name);
        ReservationSessionInterface stub = (ReservationSessionInterface) UnicastRemoteObject.exportObject(reservationSession, this.rmiPort);
        String id = "ReservationSession" + String.valueOf(this.agencySerialId);
        this.agencySerialId++;
        try {
            namingRegistry.rebind(id, stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public ManagerSession getNewManagerSession(String name, String carRentalName) throws RemoteException {
        return null;
    }

    public Set<String> getBestClients(ManagerSession ms) throws RemoteException {
        return null;
    }

    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        /*for (RentalInterface company : this.carRentalCompanies){
            ReservationConstraints = new ReservationConstraints(start, end, region);
            company.
        }*/
        return "";
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

    public void checkForAvailableCarTypes(Date start, Date end) throws RemoteException {

    }

    /*public void addQuoteToSession(String name, Date start, Date end, String carType, String region)
            throws RemoteException {
    }*/

    protected Quote createQuote(String clientName, Date start, Date end, String carType, String region)
            throws Exception {
        ReservationConstraints constraints;
        Quote quote = null;

        //try to create constraints
        try {
            constraints = new ReservationConstraints(start, end, carType, region);
        } catch (Exception e) {
            throw new Exception("Could not create quote.");
        }
        System.out.println("Created constraints\n");

        for (RentalInterface company : carRentalCompanies) {
            try {
                quote = company.createQuote(constraints, clientName);
            } catch (ReservationException e) {
                continue;
            }
            break;
        }
        if (quote == null)
            throw new ReservationException("Didn't find an available quote for these constraints");

        return quote;
    }

    public List<Reservation> confirmQuotes(String name) throws RemoteException {
        /*List<Quote> quotes = session.getCurrentQuotes();
        for(RentalInterface company : carRentalCompanies){
            company.confirmQuote();
        }*/
        return null;
    }
}
