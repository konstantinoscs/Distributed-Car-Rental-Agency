package rental;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CarRentalAgency implements AgencyInterface {

    private final static int REMOTE = 1;
    private final int rmiPort = 10448;
    private int agencySerialId;
    private int managerSerialId;
    private Registry namingRegistry;

    private Map<String, RentalInterface> carRentalCompanies;
    //private List<RentalInterface> carRentalCompanies;
    private List<ReservationSession> reservationSessions;

    public CarRentalAgency(List<String> carRentalCompanyNames, int localOrRemote) throws Exception {
        this.agencySerialId = 0;
        this.managerSerialId = 0;
        String host = localOrRemote == REMOTE ? "192.168.104.76" : "127.0.0.1";

        int port = 10447;
        if(localOrRemote == REMOTE ) {
            this.namingRegistry = LocateRegistry.getRegistry(host, port);
        }
        else {
            this.namingRegistry = LocateRegistry.getRegistry();
        }

        this.carRentalCompanies = new HashMap<>();

        //retrieve all the carRentalInterfaces from the registry
        for (String company : carRentalCompanyNames) {
            RentalInterface carRentalCompany;
            try {
                carRentalCompany = (RentalInterface) namingRegistry.lookup(company);
            } catch (Exception e){
                //e.printStackTrace();
                throw new Exception("Couldn't retrieve car rental company from registry.");
            }
            carRentalCompanies.put(company, carRentalCompany);
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
            //e.printStackTrace();
        }
        return id;
    }

    public String getNewManagerSession(String name, String carRentalName) throws RemoteException {
        ManagerSession managerSession = new ManagerSession();
        ManagerSessionInterface stub = (ManagerSessionInterface) UnicastRemoteObject.exportObject(managerSession, this.rmiPort);
        String id = "ManagerSession" + String.valueOf(this.managerSerialId);
        this.managerSerialId++;
        try {
            namingRegistry.rebind(id, stub);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return id;
    }

    public Set<String> getBestClients(ManagerSession ms) throws RemoteException {
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

    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        double minPrice = Double.MAX_VALUE;
        String cheapestCarType = "";
        for (RentalInterface company : carRentalCompanies.values()) {
            if (!company.operatesInRegion(region)) {
                continue;
            }
            Set<CarType> carTypes = company.getAvailableCarTypes(start, end);
            for (CarType carType : carTypes) {
                if (carType.getRentalPricePerDay() < minPrice) {
                    minPrice = carType.getRentalPricePerDay();
                    cheapestCarType = carType.getName();
                }
            }

        }
        return cheapestCarType;
    }

    public Set<CarType> checkForAvailableCarTypes(Date start, Date end) throws RemoteException {
        Set<CarType> carTypes = new HashSet<>();
        for (RentalInterface company : this.carRentalCompanies.values()) {
            carTypes.addAll(company.getAvailableCarTypes(start, end));
        }
        return carTypes;
    }

    /*public void addQuoteToSession(String name, Date start, Date end, String carType, String region)
            throws RemoteException {
    }*/

    public Quote createQuote(String clientName, Date start, Date end, String carType, String region)
            throws Exception {
        ReservationConstraints constraints;
        Quote quote = null;

        //try to create constraints
        try {
            constraints = new ReservationConstraints(start, end, carType, region);
        } catch (Exception e) {
            throw new Exception("Could not create quote.");
        }

        for (RentalInterface company : carRentalCompanies.values()) {
            try {
                quote = company.createQuote(constraints, clientName);
            } catch (ReservationException | IllegalArgumentException e) {
                continue;
            }
            break;
        }
        if (quote == null)
            throw new ReservationException("Didn't find an available quote for these constraints");

        return quote;
    }

    public List<Reservation> confirmQuotes(List<Quote> quotes) throws RemoteException {
        List<Reservation> reservations = new ArrayList<>();
        for (Quote quote : quotes) {
            Reservation reservation = null;
            try {
                reservation = this.carRentalCompanies.get(quote.getRentalCompany()).confirmQuote(quote);
            } catch (ReservationException e) {
                // if one reservation failed, cancel them all
                for (Reservation res : reservations) {
                    this.carRentalCompanies.get(res.getRentalCompany()).cancelReservation(res);
                }
                throw new RemoteException("Couldn't confirm quotes");
            }
            reservations.add(reservation);
        }
        return reservations;
    }
}
