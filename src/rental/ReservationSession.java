package rental;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ReservationSession implements ReservationSessionInterface {

    private CarRentalAgency carRentalAgency;
    private List<Quote> quotes;
    private String clientName;

    public ReservationSession(CarRentalAgency carRentalAgency, String clientName) {
        this.carRentalAgency = carRentalAgency;
        this.quotes = new ArrayList<>();
        this.clientName = clientName;
    }

    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    public List<Reservation> confirmQuotes(String name) throws ReservationException, RemoteException {
        if (!this.clientName.equals(name))
            throw new ReservationException("Wrong client name given!");
        return this.carRentalAgency.confirmQuotes(this.quotes);
    }

    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        return this.carRentalAgency.getCheapestCarType(start, end, region);
    }

    public Set<CarType> checkForAvailableCarTypes(Date start, Date end) throws RemoteException {
        return this.carRentalAgency.checkForAvailableCarTypes(start, end);
    }

    public void addQuoteToSession(String name, Date start, Date end, String carType, String region) throws Exception {
        Quote quote = this.carRentalAgency.createQuote(name, start, end, carType, region);
        this.quotes.add(quote);
    }



}
