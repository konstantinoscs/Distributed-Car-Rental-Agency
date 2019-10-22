package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Set;

import rental.AgencyInterface;
import rental.CarType;
import rental.ManagerSessionInterface;
import rental.Reservation;
import rental.ReservationSessionInterface;

public class Client<ReservationSession extends ReservationSessionInterface, ManagerSession extends ManagerSessionInterface> extends AbstractTestManagement<ReservationSession, ManagerSession> {

    /********
     * MAIN *
     ********/

    private final static int LOCAL = 0;
    private final static int REMOTE = 1;

    private AgencyInterface carAgency;

    /**
     * The `main` method is used to launch the client application and run the test
     * script.
     */
    public static void main(String[] args) throws Exception {
        // The first argument passed to the `main` method (if present)
        // indicates whether the application is run on the remote setup or not.
        int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;

        String carAgencyName = "CarRentals";

        // An example reservation scenario on car rental company 'Hertz' would be...
        Client<ReservationSessionInterface, ManagerSessionInterface> client = new Client<>("trips", carAgencyName, localOrRemote);
        client.run();
    }

    /***************
     * CONSTRUCTOR *
     ***************/

    public Client(String scriptFile, String carAgencyName, int localOrRemote) throws Exception {
        super(scriptFile);

        String host = localOrRemote == REMOTE ? "192.168.104.76" : "127.0.0.1";
        Registry namingRegistry;
        int port = 10447;
        if (localOrRemote == REMOTE) {
            namingRegistry = LocateRegistry.getRegistry(host, port);
        } else {
            namingRegistry = LocateRegistry.getRegistry();
        }

        try {
            carAgency = (AgencyInterface) namingRegistry.lookup(carAgencyName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected ReservationSession getNewReservationSession(String name) throws Exception {
        ReservationSession reservationSession;
        try {
            reservationSession = (ReservationSession) carAgency.getNewReservationSession(name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Couldn't get a new reservation session.");
        }
        return reservationSession;
    }

    @Override
    protected ManagerSession getNewManagerSession(String name, String carRentalName) throws Exception {
        ManagerSession managerSession;
        try {
            managerSession = (ManagerSession) carAgency.getNewManagerSession(name, carRentalName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Couldn't get a new manager session.");
        }
        return managerSession;
    }

    @Override
    protected Set<String> getBestClients(ManagerSession ms) throws Exception {
        Set<String> bestClients;
        try {
            bestClients = ms.getBestClients();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Couldn't get the best clients.");
        }
        return bestClients;

    }

    @Override
    protected String getCheapestCarType(ReservationSession session, Date start, Date end, String region) throws Exception {
        String cheapestCarType;
        try {
            cheapestCarType = session.getCheapestCarType(start, end, region);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Couldn't get the cheapest car type.");
        }
        return cheapestCarType;

    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSession ms, String carRentalCompanyName, int year) throws Exception {
        CarType carType;
        try {
            carType = ms.getMostPopularCarTypeIn(carRentalCompanyName, year);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Couldn't get the most popular car type.");
        }
        return carType;
    }

    @Override
    protected int getNumberOfReservationsByRenter(ManagerSession ms, String clientName) throws Exception {
        int numberOfReservationsByRenter = 0;
        try {
            numberOfReservationsByRenter = ms.getNumberOfReservationsByRenter(clientName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Couldn't get the number of reservations by renter.");
        }
        return numberOfReservationsByRenter;
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSession ms, String carRentalName, String carType) throws Exception {
        int numberOfReservationsForCarType = 0;
        try {
            numberOfReservationsForCarType = ms.getNumberOfReservationsForCarType(carRentalName, carType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Couldn't get the number of reservations for car type.");
        }
        return numberOfReservationsForCarType;
    }



    @Override
    protected void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws Exception {
        try {
            session.checkForAvailableCarTypes(start, end);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Couldn't check for available car types.");
        }
    }

    @Override
    protected void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType, String region) throws Exception {
        try {
            session.addQuoteToSession(name, start, end, carType, region);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Couldn't add quote to session.");
        }
    }

    @Override
    protected List<Reservation> confirmQuotes(ReservationSession session, String name) throws Exception {
        List<Reservation> reservations;
        try {
            reservations = session.confirmQuotes(name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Couldn't confirm quotes.");
        }
        return reservations;

    }



    /**
     * Check which car types are available in the given period (across all companies
     * and regions) and print this list of car types.
     *
     * @param start start time of the period
     * @param end   end time of the period
     * @throws Exception if things go wrong, throw exception
     */
    /*@Override
    protected void checkForAvailableCarTypes(Date start, Date end) throws Exception {
        List<CarType> carTypes;
        try {
            carTypes = carRentalCompany.getAvailableCarTypes(start, end).stream().collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Couldn't check for available car types.");
        }
        carTypes.forEach(type -> System.out.println(type.toString()));
    }

    /**
     * Retrieve a quote for a given car type (tentative reservation).
     *
     * @param clientName name of the client
     * @param start      start time for the quote
     * @param end        end time for the quote
     * @param carType    type of car to be reserved
     * @param region     region in which car must be available
     * @return the newly created quote
     * @throws Exception if things go wrong, throw exception
     */
    /*@Override
    protected Quote createQuote(String clientName, Date start, Date end, String carType, String region)
            throws Exception {
        ReservationConstraints constraints;
        try {
            constraints = new ReservationConstraints(start, end, carType, region);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not create quote.");
        }
        return carRentalCompany.createQuote(constraints, clientName);

    }

    /**
     * Confirm the given quote to receive a final reservation of a car.
     *
     * @param quote the quote to be confirmed
     * @return the final reservation of a car
     * @throws Exception if things go wrong, throw exception
     */
    /*@Override
    protected Reservation confirmQuote(Quote quote) throws Exception {
        Reservation reservation;
        try {
            reservation = carRentalCompany.confirmQuote(quote);
        } catch (Exception e) {
            throw new Exception("Could not confirm quote.");
        }
        return reservation;
    }

    /**
     * Get all reservations made by the given client.
     *
     * @param clientName name of the client
     * @return the list of reservations of the given client
     * @throws Exception if things go wrong, throw exception
     */
    /*@Override
    protected List<Reservation> getReservationsByRenter(String clientName) throws Exception {
        List<Reservation> reservations;
        try {
            reservations = carRentalCompany.getReservationsByRenter(clientName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not get the reservations by renter.");
        }
        return reservations;
    }

    /**
     * Get the number of reservations for a particular car type.
     *
     * @param carType name of the car type
     * @return number of reservations for the given car type
     * @throws Exception if things go wrong, throw exception
     */
    /*@Override
    protected int getNumberOfReservationsForCarType(String carType) throws Exception {
        int noOfReservations;
        try {
            noOfReservations = carRentalCompany.getNumberOfReservationsForCarType(carType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not get the number of reservations for the car type.");
        }
        return noOfReservations;
    }*/
}