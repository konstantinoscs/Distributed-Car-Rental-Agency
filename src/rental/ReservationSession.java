package rental;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationSession implements ReservationSessionInterface {

    private CarRentalAgency carRentalAgency;
    private List<Quote> quotes;
    private String clientName;

    public ReservationSession(String clientName) {
        this.quotes = new ArrayList<>();
        this.clientName = clientName;
    }

    public void createQuote(ReservationConstraints constraints) {

    }

    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    public List<Reservation> confirmQuotes(String name) {
        return null;
    }

    public String getCheapestCarType(Date start, Date end, String region) {
        return null;
    }

    public void checkForAvailableCarTypes(Date start, Date end) {

    }

    public void addQuoteToSession(String name, Date start, Date end, String carType, String region) {

    }



}
