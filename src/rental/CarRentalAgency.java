package rental;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CarRentalAgency implements AgencyInterface {

    private final static int REMOTE = 1;
    private final int rmiPort = 10448;
    private int agencySerialId;
    private int managerSerialId;
    private Registry namingRegistry;

    private Map<String, RentalInterface> carRentalCompanies;

    public CarRentalAgency(List<String> carRentalCompanyNames, int localOrRemote) throws Exception {
        this.agencySerialId = 0;
        this.managerSerialId = 0;
        String host = localOrRemote == REMOTE ? "192.168.104.76" : "127.0.0.1";

        int port = 10447;
        if(localOrRemote == REMOTE ) {
            this.namingRegistry = LocateRegistry.getRegistry(port);
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
    }

    //return stub identifier as String
    public String getNewReservationSession(String name) throws RemoteException {
        ReservationSession reservationSession = new ReservationSession(this, name);
        ReservationSessionInterface stub = (ReservationSessionInterface) UnicastRemoteObject.exportObject(reservationSession, this.rmiPort);
        String id;
        synchronized (this) {   //ensure that serialId is unique for every remote object
            id = "ReservationSession" + String.valueOf(this.agencySerialId++);
        }
        //an exception will be thrown here if something goes wrong and we want this behavior
        namingRegistry.rebind(id, stub);
        return id;
    }

    public String getNewManagerSession(String name, String carRentalName) throws RemoteException {
        ManagerSession managerSession = new ManagerSession(this);
        ManagerSessionInterface stub = (ManagerSessionInterface) UnicastRemoteObject.exportObject(managerSession, this.rmiPort);
        String id;
        synchronized (this) {   //ensure that serialId is unique for every remote object
            id = "ManagerSession" + String.valueOf(this.managerSerialId++);
        }
        //an exception will be thrown here if something goes wrong and we want this behavior
        namingRegistry.rebind(id, stub);
        return id;
    }

    public Set<String> getBestClients() throws RemoteException {
        Map<String, Integer> reservations = new HashMap<>();
        for (RentalInterface company : carRentalCompanies.values()) {
            updateMap(reservations, company.getClientsWithReservations());
        }
        Integer max = Collections.max(reservations.entrySet(), Map.Entry.comparingByValue()).getValue();
        // after we found the max, get all clients that have max number of reservations
        return reservations.entrySet().stream()
                .filter(e -> e.getValue().equals(max))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    //update reservations with the contents of tempReservations by adding the new data
    private void updateMap(Map<String, Integer> reservations, Map<String, Integer> tempReservations) {
        for (Map.Entry<String, Integer> entry : tempReservations.entrySet()) {
            reservations.put(entry.getKey(), reservations.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
    }

    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws Exception {
        if (!this.carRentalCompanies.containsKey(carRentalCompanyName))
            throw new Exception("Requested Car Rental Company is not registered!");

        return this.carRentalCompanies.get(carRentalCompanyName).getMostPopularCarTypeIn(year);
    }

    public int getNumberOfReservationsByRenter(String clientName) throws RemoteException {
        int noOfReservations = 0;
        for (RentalInterface company : this.carRentalCompanies.values()) {
            noOfReservations += company.getNoOfReservationsByRenter(clientName);
        }
        return noOfReservations;
    }

    public int getNumberOfReservationsForCarType(String carRentalName, String carType) throws Exception {
        if (!this.carRentalCompanies.containsKey(carRentalName))
            throw new Exception("Requested Car Rental Company is not registered!");

        return this.carRentalCompanies.get(carRentalName).getNumberOfReservationsForCarType(carType);
    }

    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        double minPrice = Double.MAX_VALUE;
        String cheapestCarType = "";
        for (RentalInterface company : this.carRentalCompanies.values()) {
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
