package rental;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CarRentalCompany implements RentalInterface {

    private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());

    private List<String> regions;
    private String name;
    private List<Car> cars;
    private Map<String, CarType> carTypes = new HashMap<String, CarType>();

    /***************
     * CONSTRUCTOR *
     ***************/

    public CarRentalCompany(String name, List<String> regions, List<Car> cars) {
        super();
        logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
        setName(name);
        this.cars = cars;
        setRegions(regions);
        for (Car car : cars)
            carTypes.put(car.getType().getName(), car.getType());
        logger.log(Level.INFO, this.toString());
    }

    /********
     * NAME *
     ********/

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    /***********
     * Regions *
     **********/
    private void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public List<String> getRegions() {
        return this.regions;
    }

    public boolean operatesInRegion(String region) {
        return this.regions.contains(region);
    }

    /*************
     * CAR TYPES *
     *************/

    public Collection<CarType> getAllCarTypes() {
        return carTypes.values();
    }

    public CarType getCarType(String carTypeName) throws IllegalArgumentException {
        if (carTypes.containsKey(carTypeName))
            return carTypes.get(carTypeName);
        throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
    }

    // mark
    public boolean isAvailable(String carTypeName, Date start, Date end) throws IllegalArgumentException {
        logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
        if (carTypes.containsKey(carTypeName)) {
            return getAvailableCarTypes(start, end).contains(carTypes.get(carTypeName));
        } else {
            throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
        }
    }

    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCarTypes = new HashSet<CarType>();
        for (Car car : cars) {
            if (car.isAvailable(start, end)) {
                availableCarTypes.add(car.getType());
            }
        }
        return availableCarTypes;
    }

    public CarType getMostPopularCarTypeIn(int year) {
        Map<CarType, Integer> carTypeReservations = new HashMap<>();
        for (Car car : cars) {
            for (Reservation reservation : car.getReservations()) {
                if (reservation.getStartDate().getYear() + 1900 == year) {
                    Integer prev = carTypeReservations.getOrDefault(car.getType(), 0);
                    carTypeReservations.put(car.getType(), prev + 1);
                }
            }
        }
        if (carTypeReservations.isEmpty())
            return null;
        return Collections.max(carTypeReservations.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    /*********
     * CARS *
     *********/

    private Car getCar(int uid) {
        for (Car car : cars) {
            if (car.getId() == uid)
                return car;
        }
        throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
    }

    private List<Car> getAvailableCars(String carType, Date start, Date end) {
        List<Car> availableCars = new LinkedList<Car>();
        for (Car car : cars) {
            if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    /****************
     * RESERVATIONS *
     ****************/

    public Quote createQuote(ReservationConstraints constraints, String client)
            throws ReservationException, IllegalArgumentException {
        logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}",
                new Object[]{name, client, constraints.toString()});


        if (!operatesInRegion(constraints.getRegion()) || !isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate()))
            throw new ReservationException("<" + name
                    + "> No cars available to satisfy the given constraints.");

        CarType type = getCarType(constraints.getCarType());

        double price = calculateRentalPrice(type.getRentalPricePerDay(), constraints.getStartDate(), constraints.getEndDate());

        return new Quote(client, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
    }

    // Implementation can be subject to different pricing strategies
    private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
        return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
                / (1000 * 60 * 60 * 24D));
    }

    public synchronized Reservation confirmQuote(Quote quote) throws ReservationException {
        logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
        List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
        if (availableCars.isEmpty())
            throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
                    + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
        Car car = availableCars.get((int) (Math.random() * availableCars.size()));

        Reservation res = new Reservation(quote, car.getId());
        car.addReservation(res);
        return res;
    }

    public void cancelReservation(Reservation res) {
        logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
        getCar(res.getCarId()).removeReservation(res);
    }

    public List<Reservation> getReservationsByRenter(String clientName) {
        List<Reservation> reservations = new ArrayList<>();
        // filter car reservations with clientName
        cars.forEach(car ->
                reservations.addAll(
                        car.getReservations().stream()
                                .filter(res -> res.getCarRenter().equals(clientName))
                                .collect(Collectors.toList())
                ));
        return reservations;
    }

    public int getNoOfReservationsByRenter(String clientName) {
        return this.getReservationsByRenter(clientName).size();
    }

    public int getNumberOfReservationsForCarType(String carType) {
        int noOfReservations = 0;
        for (Car car : cars) {
            if (car.getType().getName().equals(carType)) {
                noOfReservations += car.getNoOfReservations();
            }
        }
        return noOfReservations;
    }

    public Map<String, Integer> getClientsWithReservations() {
        Map<String, Integer> reservations = new HashMap<>();
        for (Car car : cars) {
            for (Reservation reservation : car.getReservations()) {
                String client = reservation.getCarRenter();
                Integer prev = reservations.getOrDefault(client, 0);
                reservations.put(client, prev + 1);
            }
        }
        return reservations;
    }

    @Override
    public String toString() {
        return String.format("<%s> CRC is active in regions %s and serving with %d car types", name, listToString(regions), carTypes.size());
    }

    private static String listToString(List<? extends Object> input) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < input.size(); i++) {
            if (i == input.size() - 1) {
                out.append(input.get(i).toString());
            } else {
                out.append(input.get(i).toString() + ", ");
            }
        }
        return out.toString();
    }

}