import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class FlightManagementAdminCLI {

    private final FlightManagementDAO flightDAO = new FlightManagementDAO();
    private final LookupDAO lookupDAO = new LookupDAO();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new FlightManagementAdminCLI().run();
    }

    private void run() {
        System.out.println("===== Flight Management (Admin Only) – TEXT MODE =====");

        while (true) {
            showMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    listFlights();
                    break;
                case "2":
                    addFlight();
                    break;
                case "3":
                    updateFlight();
                    break;
                case "4":
                    deleteFlight();
                    break;
                case "0":
                    System.out.println("Goodbye.");
                    return;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    private void showMenu() {
        System.out.println();
        System.out.println("1) List all flights");
        System.out.println("2) Add new flight");
        System.out.println("3) Update existing flight");
        System.out.println("4) Delete flight");
        System.out.println("0) Exit");
        System.out.print("Enter choice: ");
    }

    // ----------------- MENU ACTIONS -----------------

    private void listFlights() {
        try {
            List<FlightRecord> flights = flightDAO.getAllFlights();
            System.out.println("\n--- Flights ---");
            System.out.printf("%-5s %-10s %-20s %-6s %-6s %-19s %-19s %-8s %-15s%n",
                    "ID", "Flight#", "Airline", "From", "To",
                    "Departure", "Arrival", "Price", "Aircraft");

            for (FlightRecord f : flights) {
                System.out.printf("%-5d %-10s %-20s %-6s %-6s %-19s %-19s %-8.2f %-15s%n",
                        f.getFlightId(),
                        f.getFlightNumber(),
                        f.getAirlineName(),
                        f.getOrigin(),
                        f.getDestination(),
                        df.format(f.getDepartureTime()),
                        df.format(f.getArrivalTime()),
                        f.getPrice(),
                        f.getAircraftModel());
            }
        } catch (SQLException e) {
            System.out.println("Error loading flights: " + e.getMessage());
        }
    }

    private void addFlight() {
        try {
            System.out.println("\n--- Add New Flight ---");
            Airline airline = askAirline();
            Aircraft aircraft = askAircraft();

            System.out.print("Flight number: ");
            String flightNumber = scanner.nextLine().trim();

            System.out.print("Origin (IATA): ");
            String origin = scanner.nextLine().trim().toUpperCase();

            System.out.print("Destination (IATA): ");
            String destination = scanner.nextLine().trim().toUpperCase();

            Timestamp dep = askTimestamp("Departure (yyyy-MM-dd HH:mm:ss): ");
            Timestamp arr = askTimestamp("Arrival   (yyyy-MM-dd HH:mm:ss): ");

            double price = askDouble("Price: ");

            FlightRecord newFlight = new FlightRecord(
                    0,                              // new flight
                    flightNumber,
                    airline.getAirlineId(),
                    airline.getAirlineName(),
                    origin,
                    destination,
                    dep,
                    arr,
                    price,
                    aircraft.getAircraftId(),
                    aircraft.getModel()
            );

            flightDAO.insertFlight(newFlight);
            System.out.println("Flight added successfully.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void updateFlight() {
        try {
            System.out.println("\n--- Update Flight ---");
            listFlights();

            int id = (int) askDouble("Enter flight ID to update: ");

            // find existing record in list
            List<FlightRecord> flights = flightDAO.getAllFlights();
            FlightRecord existing = null;
            for (FlightRecord f : flights) {
                if (f.getFlightId() == id) {
                    existing = f;
                    break;
                }
            }
            if (existing == null) {
                System.out.println("Flight ID not found.");
                return;
            }

            System.out.println("Leave field empty to keep current value.");

            Airline airline = askAirlineOptional(existing.getAirlineId());
            Aircraft aircraft = askAircraftOptional(existing.getAircraftId());

            String flightNumber = askOptional(
                    "Flight number [" + existing.getFlightNumber() + "]: ",
                    existing.getFlightNumber());

            String origin = askOptional(
                    "Origin (IATA) [" + existing.getOrigin() + "]: ",
                    existing.getOrigin()).toUpperCase();

            String destination = askOptional(
                    "Destination (IATA) [" + existing.getDestination() + "]: ",
                    existing.getDestination()).toUpperCase();

            Timestamp dep = askTimestampOptional(
                    "Departure [" + df.format(existing.getDepartureTime()) + "]: ",
                    existing.getDepartureTime());

            Timestamp arr = askTimestampOptional(
                    "Arrival   [" + df.format(existing.getArrivalTime()) + "]: ",
                    existing.getArrivalTime());

            double price = askDoubleOptional(
                    "Price [" + existing.getPrice() + "]: ",
                    existing.getPrice());

            FlightRecord updated = new FlightRecord(
                    existing.getFlightId(),
                    flightNumber,
                    airline.getAirlineId(),
                    airline.getAirlineName(),
                    origin,
                    destination,
                    dep,
                    arr,
                    price,
                    aircraft.getAircraftId(),
                    aircraft.getModel()
            );

            flightDAO.updateFlight(updated);
            System.out.println("Flight updated.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void deleteFlight() {
        try {
            System.out.println("\n--- Delete Flight ---");
            listFlights();
            int id = (int) askDouble("Enter flight ID to delete: ");

            System.out.print("Are you sure? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (!confirm.equals("y")) {
                System.out.println("Cancelled.");
                return;
            }

            flightDAO.deleteFlight(id);
            System.out.println("Flight deleted.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // ----------------- INPUT HELPERS -----------------

    private Airline askAirline() throws SQLException {
        List<Airline> airlines = lookupDAO.getAllAirlines();
        System.out.println("\nAvailable airlines:");
        for (int i = 0; i < airlines.size(); i++) {
            Airline a = airlines.get(i);
            System.out.printf("%d) %s (%s)%n", i + 1, a.getAirlineName(), a.getIataCode());
        }
        int choice;
        while (true) {
            choice = (int) askDouble("Choose airline (1-" + airlines.size() + "): ");
            if (choice >= 1 && choice <= airlines.size()) break;
            System.out.println("Invalid choice.");
        }
        return airlines.get(choice - 1);
    }

    private Aircraft askAircraft() throws SQLException {
        List<Aircraft> aircrafts = lookupDAO.getAllAircrafts();
        System.out.println("\nAvailable aircrafts:");
        for (int i = 0; i < aircrafts.size(); i++) {
            Aircraft ac = aircrafts.get(i);
            System.out.printf("%d) %s (%d seats)%n", i + 1, ac.getModel(), ac.getCapacity());
        }
        int choice;
        while (true) {
            choice = (int) askDouble("Choose aircraft (1-" + aircrafts.size() + "): ");
            if (choice >= 1 && choice <= aircrafts.size()) break;
            System.out.println("Invalid choice.");
        }
        return aircrafts.get(choice - 1);
    }

    // Same but with default (for update)
    private Airline askAirlineOptional(int currentId) throws SQLException {
        List<Airline> airlines = lookupDAO.getAllAirlines();
        Airline current = null;
        System.out.println("\nAvailable airlines (press Enter to keep current):");
        for (int i = 0; i < airlines.size(); i++) {
            Airline a = airlines.get(i);
            String marker = (a.getAirlineId() == currentId) ? " (current)" : "";
            System.out.printf("%d) %s (%s)%s%n", i + 1, a.getAirlineName(), a.getIataCode(), marker);
            if (a.getAirlineId() == currentId) current = a;
        }

        System.out.print("Choose airline [Enter = keep current]: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return current;

        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= airlines.size()) {
                return airlines.get(choice - 1);
            }
        } catch (NumberFormatException ignored) { }
        System.out.println("Invalid choice, keeping current.");
        return current;
    }

    private Aircraft askAircraftOptional(int currentId) throws SQLException {
        List<Aircraft> aircrafts = lookupDAO.getAllAircrafts();
        Aircraft current = null;
        System.out.println("\nAvailable aircrafts (press Enter to keep current):");
        for (int i = 0; i < aircrafts.size(); i++) {
            Aircraft ac = aircrafts.get(i);
            String marker = (ac.getAircraftId() == currentId) ? " (current)" : "";
            System.out.printf("%d) %s (%d seats)%s%n", i + 1, ac.getModel(), ac.getCapacity(), marker);
            if (ac.getAircraftId() == currentId) current = ac;
        }

        System.out.print("Choose aircraft [Enter = keep current]: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return current;

        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= aircrafts.size()) {
                return aircrafts.get(choice - 1);
            }
        } catch (NumberFormatException ignored) { }
        System.out.println("Invalid choice, keeping current.");
        return current;
    }

    private Timestamp askTimestamp(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                return new Timestamp(df.parse(s).getTime());
            } catch (ParseException e) {
                System.out.println("Invalid date format. Use yyyy-MM-dd HH:mm:ss");
            }
        }
    }

    private Timestamp askTimestampOptional(String prompt, Timestamp current) {
        System.out.print(prompt);
        String s = scanner.nextLine().trim();
        if (s.isEmpty()) return current;
        try {
            return new Timestamp(df.parse(s).getTime());
        } catch (ParseException e) {
            System.out.println("Invalid date format, keeping current.");
            return current;
        }
    }

    private double askDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private double askDoubleOptional(String prompt, double current) {
        System.out.print(prompt);
        String s = scanner.nextLine().trim();
        if (s.isEmpty()) return current;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number, keeping current.");
            return current;
        }
    }

    private String askOptional(String prompt, String current) {
        System.out.print(prompt);
        String s = scanner.nextLine().trim();
        return s.isEmpty() ? current : s;
    }
}
