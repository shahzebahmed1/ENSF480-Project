import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import flight.DatabaseConnectivity;
public class FlightManagementDAO {

    private static final String BASE_SELECT =
            "SELECT f.flightID, f.flightNumber, " +
            "       f.airlineID, a.airlineName, " +
            "       f.origin, f.destination, " +
            "       f.departureTime, f.arrivalTime, " +
            "       f.price, f.aircraftID, ac.model AS aircraftModel " +
            "FROM Flights f " +
            "JOIN Airlines a  ON f.airlineID = a.airlineID " +
            "JOIN Aircrafts ac ON f.aircraftID = ac.aircraftID ";

    public List<FlightRecord> getAllFlights() throws SQLException {
        String sql = BASE_SELECT + "ORDER BY f.departureTime";

        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<FlightRecord> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        }
    }

    public void insertFlight(FlightRecord f) throws SQLException {
        String sql = "INSERT INTO Flights " +
                     "(flightNumber, airlineID, origin, destination, " +
                     " departureTime, arrivalTime, price, aircraftID) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, f.getFlightNumber());
            ps.setInt(2, f.getAirlineId());
            ps.setString(3, f.getOrigin());
            ps.setString(4, f.getDestination());
            ps.setTimestamp(5, f.getDepartureTime());
            ps.setTimestamp(6, f.getArrivalTime());
            ps.setDouble(7, f.getPrice());
            ps.setInt(8, f.getAircraftId());

            ps.executeUpdate();
        }
    }

    public void updateFlight(FlightRecord f) throws SQLException {
        String sql = "UPDATE Flights SET " +
                     "flightNumber=?, airlineID=?, origin=?, destination=?, " +
                     "departureTime=?, arrivalTime=?, price=?, aircraftID=? " +
                     "WHERE flightID=?";

        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, f.getFlightNumber());
            ps.setInt(2, f.getAirlineId());
            ps.setString(3, f.getOrigin());
            ps.setString(4, f.getDestination());
            ps.setTimestamp(5, f.getDepartureTime());
            ps.setTimestamp(6, f.getArrivalTime());
            ps.setDouble(7, f.getPrice());
            ps.setInt(8, f.getAircraftId());
            ps.setInt(9, f.getFlightId());

            ps.executeUpdate();
        }
    }

    public void deleteFlight(int flightId) throws SQLException {
        String sql = "DELETE FROM Flights WHERE flightID=?";

        try (Connection conn = DatabaseConnectivity.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, flightId);
            ps.executeUpdate();
        }
    }

    private FlightRecord mapRow(ResultSet rs) throws SQLException {
        return new FlightRecord(
                rs.getInt("flightID"),
                rs.getString("flightNumber"),
                rs.getInt("airlineID"),
                rs.getString("airlineName"),
                rs.getString("origin"),
                rs.getString("destination"),
                rs.getTimestamp("departureTime"),
                rs.getTimestamp("arrivalTime"),
                rs.getDouble("price"),
                rs.getInt("aircraftID"),
                rs.getString("aircraftModel")
        );
    }
}
