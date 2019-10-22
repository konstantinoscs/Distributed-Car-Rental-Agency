package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import rental.*;

public class Client<ReservationSession, ManagerSession> extends AbstractTestManagement<ReservationSession, ManagerSession> {

    /********
     * MAIN *
     ********/

    private final static int LOCAL = 0;
    private final static int REMOTE = 1;

    private RentalInterface carRentalCompany;

    /**
     * The `main` method is used to launch the client application and run the test
     * script.
     */
    public static void main(String[] args) throws Exception {
        // The first argument passed to the `main` method (if present)
        // indicates whether the application is run on the remote setup or not.
        int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;

        String carRentalCompanyName = "Hertz";

        // An example reservation scenario on car rental company 'Hertz' would be...
        Client client = new Client("trips", carRentalCompanyName, localOrRemote);
        client.run();
    }

    /***************
     * CONSTRUCTOR *
     ***************/

    public Client(String scriptFile, String carRentalCompanyName, int localOrRemote) throws Exception {
        super(scriptFile);

        String host = localOrRemote == REMOTE ? "192.168.104.76" : "127.0.0.1";
        Registry namingRegistry = null;
        int port = 10447;
        if (localOrRemote == REMOTE) {
            namingRegistry = LocateRegistry.getRegistry(host, port);
        } else {
            namingRegistry = LocateRegistry.getRegistry();
        }

        try {
            carRentalCompany = (RentalInterface) namingRegistry.lookup(carRentalCompanyName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Set<String> getBestClients(ManagerSession ms) throws Exception {

    }

    @Override
    protected String getCheapestCarType(ReservationSession session, Date start,
                                        Date end, String region) throws Exception {

    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSession ms, String carRentalCompanyName, int year) throws Exception {

    }

    @Override
    protected ReservationSession getNewReservationSession(String name) throws Exception {

    }

    @Override
    protected ManagerSession getNewManagerSession(String name, String carRentalName) throws Exception {

    }

    @Override
    protected void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws Exception {

    }

    @Override
    protected void addQuoteToSession(ReservationSession session, String name,
                                     Date start, Date end, String carType, String region) throws Exception {

    }

    @Override
    protected List<Reservation> confirmQuotes(ReservationSession session, String name) throws Exception {

    }

    @Override
    protected int getNumberOfReservationsByRenter(ManagerSession ms, String clientName) throws Exception {

    }

    protected int getNumberOfReservationsForCarType(ManagerSession ms, String carRentalName, String carType) throws Exception {

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