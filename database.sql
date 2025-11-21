
DROP TABLE IF EXISTS Payments; -- If you added this earlier
DROP TABLE IF EXISTS Bookings; -- If you added this earlier
DROP TABLE IF EXISTS Flights;
DROP TABLE IF EXISTS Airlines;
DROP TABLE IF EXISTS Aircrafts;

-- 2. CREATE TABLES

-- Table: Aircrafts
CREATE TABLE Aircrafts (
    aircraftID INT PRIMARY KEY AUTO_INCREMENT,
    model VARCHAR(50), 
    capacity INT
);

-- Table: Airlines
CREATE TABLE Airlines (
    airlineID INT PRIMARY KEY AUTO_INCREMENT,
    airlineName VARCHAR(50) NOT NULL,
    iataCode VARCHAR(3) NOT NULL
);

-- Table: Flights (Links to Aircrafts and Airlines)
CREATE TABLE Flights (
    flightID INT PRIMARY KEY AUTO_INCREMENT,
    flightNumber VARCHAR(10),
    airlineID INT,              -- Foreign Key
    origin VARCHAR(3),
    destination VARCHAR(3),
    departureTime DATETIME,
    arrivalTime DATETIME,
    price DECIMAL(10, 2),
    aircraftID INT,             -- Foreign Key
    FOREIGN KEY (airlineID) REFERENCES Airlines(airlineID),
    FOREIGN KEY (aircraftID) REFERENCES Aircrafts(aircraftID)
);

-- 3. INSERT DATA

-- Populate Aircrafts
INSERT INTO Aircrafts (model, capacity) VALUES 
('Boeing 737-800', 160),      -- ID 1
('Airbus A320', 150),         -- ID 2
('Boeing 777-300ER', 350),    -- ID 3
('Airbus A380', 500),         -- ID 4
('Boeing 787 Dreamliner', 240); -- ID 5

-- Populate Airlines
INSERT INTO Airlines (airlineName, iataCode) VALUES 
('Delta Air Lines', 'DL'),       -- ID 1
('American Airlines', 'AA'),     -- ID 2
('United Airlines', 'UA'),       -- ID 3
('British Airways', 'BA'),       -- ID 4
('Emirates', 'EK'),              -- ID 5
('Singapore Airlines', 'SQ'),    -- ID 6
('WestJet', 'WS'),               -- ID 7
('Air Canada', 'AC');            -- ID 8

-- Populate Flights (Using IDs for Airline and Aircraft)
INSERT INTO Flights (flightNumber, airlineID, origin, destination, departureTime, arrivalTime, price, aircraftID) VALUES 
-- Delta (ID 1): Atlanta to Orlando
('DL1042', 1, 'ATL', 'MCO', '2025-12-01 08:00:00', '2025-12-01 09:35:00', 180.00, 1),

-- American (ID 2): Dallas to Los Angeles
('AA2401', 2, 'DFW', 'LAX', '2025-12-01 14:00:00', '2025-12-01 15:30:00', 250.50, 1),

-- United (ID 3): Chicago to New York
('UA1209', 3, 'ORD', 'LGA', '2025-12-01 07:00:00', '2025-12-01 10:15:00', 145.00, 2),

-- British Airways (ID 4): London to NYC
('BA0117', 4, 'LHR', 'JFK', '2025-12-02 08:25:00', '2025-12-02 11:20:00', 850.00, 3),

-- Emirates (ID 5): Dubai to London
('EK001', 5, 'DXB', 'LHR', '2025-12-03 07:45:00', '2025-12-03 11:40:00', 900.00, 4),

-- WestJet (ID 7): Calgary to Vancouver
('WS123', 7, 'YYC', 'YVR', '2025-12-01 09:00:00', '2025-12-01 09:30:00', 120.00, 1);
-- Table: Payments
CREATE TABLE Payments (
    paymentID INT PRIMARY KEY AUTO_INCREMENT,
    bookingID INT,
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    cardLast4 VARCHAR(4),
    transactionTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bookingID) REFERENCES Bookings(bookingID)
);
-- Table: Bookings
CREATE TABLE Bookings (
    bookingID INT PRIMARY KEY AUTO_INCREMENT,
    flightID INT,
    passengerName VARCHAR(100) NOT NULL,
    passengerEmail VARCHAR(100) NOT NULL,
    bookingTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (flightID) REFERENCES Flights(flightID)
);
-- Managing flights
CREATE TABLE Flights (
    flightID INT PRIMARY KEY AUTO_INCREMENT,
    flightNumber VARCHAR(10),
    airlineID INT,
    origin VARCHAR(3),
    destination VARCHAR(3),
    departureTime DATETIME,
    arrivalTime DATETIME,
    price DECIMAL(10, 2),
    aircraftID INT,
    FOREIGN KEY (airlineID) REFERENCES Airlines(airlineID),
    FOREIGN KEY (aircraftID) REFERENCES Aircrafts(aircraftID)
);
