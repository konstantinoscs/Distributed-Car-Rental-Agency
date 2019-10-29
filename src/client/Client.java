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
    private Registry namingRegistry;

    /**
     * The `main` method is used to launch the client application and run the test
     * script.
     */
    public static void main(String[] args) throws Exception {
        // The first argument passed to the `main` method (if present)
        // indicates whether the application is run on the remote setup or not.
        int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;
        String carAgencyName = "CarRentals";
        Client<ReservationSessionInterface, ManagerSessionInterface> client = new Client<>("trips", carAgencyName, localOrRemote);
        client.run();
    }

    /***************
     * CONSTRUCTOR *
     ***************/

    public Client(String scriptFile, String carAgencyName, int localOrRemote) throws Exception {
        super(scriptFile);

        String host = localOrRemote == REMOTE ? "192.168.104.76" : "127.0.0.1";

        int registryPort = 10447;
        this.namingRegistry = localOrRemote == REMOTE ? LocateRegistry.getRegistry(host, registryPort) : LocateRegistry.getRegistry();

        carAgency = (AgencyInterface) this.namingRegistry.lookup(carAgencyName);

    }

    @Override
    protected ReservationSession getNewReservationSession(String name) throws Exception {
        String reservationSessionId;
        try {
            reservationSessionId = carAgency.getNewReservationSession(name);
        } catch (Exception e) {
            //e.printStackTrace();
            throw new Exception("Couldn't get a new reservation session.");
        }
        return (ReservationSession) this.namingRegistry.lookup(reservationSessionId);
    }

    @Override
    protected ManagerSession getNewManagerSession(String name, String carRentalName) throws Exception {
        String managerSessionId;
        try {
            managerSessionId = carAgency.getNewManagerSession(name, carRentalName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Couldn't get a new manager session.");
        }
        return (ManagerSession) this.namingRegistry.lookup(managerSessionId);
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
    protected CarType getMostPopularCarTypeIn(ManagerSession ms, String carRentalCompanyName, int year) throws Exception {
        CarType carType;
        try {
            carType = ms.getMostPopularCarTypeIn(carRentalCompanyName, year);
        } catch (Exception e) {
            //e.printStackTrace();
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
            //e.printStackTrace();
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
            //e.printStackTrace();
            throw new Exception("Couldn't get the number of reservations for car type.");
        }
        return numberOfReservationsForCarType;
    }

    @Override
    protected String getCheapestCarType(ReservationSession session, Date start, Date end, String region) throws Exception {
        String cheapestCarType;
        try {
            cheapestCarType = session.getCheapestCarType(start, end, region);
        } catch (Exception e) {
            //e.printStackTrace();
            throw new Exception("Couldn't get the cheapest car type.");
        }
        return cheapestCarType;

    }

    @Override
    protected void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws Exception {
        Set<CarType> carTypes;
        try {
            carTypes = session.checkForAvailableCarTypes(start, end);
        } catch (Exception e) {
            //e.printStackTrace();
            throw new Exception("Couldn't check for available car types.");
        }
        carTypes.forEach(carType -> System.out.println(carType.toString()));
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
            throw new Exception("Couldn't confirm quotes.");
        }
        return reservations;

    }

}