# Go-Back-N Protocol Simulator

A Java-based implementation of the **Go-Back-N (GBN) Automatic Repeat Request (ARQ)** protocol that simulates reliable data transmission over UDP. The project demonstrates how reliable communication can be achieved on top of an unreliable transport protocol by implementing retransmission, acknowledgments, and error handling mechanisms.

---

## Features

- Go-Back-N sliding window protocol implementation
- Reliable data transmission over UDP sockets
- Configurable sliding window size
- Configurable number of packets
- Checksum-based error detection
- Timeout-based retransmission
- Fast retransmission using duplicate ACKs
- Client-server communication model
- Session reset and graceful connection termination

---

## Simulated Network Scenarios

The simulator supports the following scenarios:

1. Simple Transmission
2. Packet Loss
3. ACK Loss
4. Packet Corruption
5. Delayed ACK
6. Fast Retransmission
7. Custom Packet Count and Window Size

These scenarios help demonstrate how the Go-Back-N protocol recovers from common network failures.

---

## Technologies Used

- Java
- UDP Socket Programming
- DatagramSocket
- DatagramPacket
- Computer Networks
- Go-Back-N ARQ Protocol

---

## Project Structure

```
├── Client.java
├── Server.java
├── Client.class
├── Server.class
└── README.md
```

---

## How It Works

### Server
- Listens for incoming UDP packets.
- Validates packet headers and checksums.
- Sends ACK or NAK responses.
- Simulates delayed acknowledgments.
- Handles RESET and END control packets.

### Client
- Sends packets using the Go-Back-N sliding window algorithm.
- Waits for acknowledgments.
- Retransmits packets after timeout.
- Performs fast retransmission after three duplicate ACKs.
- Simulates packet loss, ACK loss, and packet corruption.

---

## Packet Format

Each packet contains a custom header:

```
<SeqNo:x><Length:y><CheckSum:z><Type:Data>|Payload
```

Header fields include:

- Sequence Number
- Payload Length
- Checksum
- Packet Type (Data, DELAYACK, RESET, END)

---

## Running the Project

### 1. Compile

```bash
javac Server.java
javac Client.java
```

### 2. Start the Server

```bash
java Server
```

### 3. Start the Client

```bash
java Client
```

### 4. Select a Simulation Scenario

The client allows you to choose one of the supported network scenarios before transmitting data.

---

## Learning Outcomes

This project demonstrates:

- Reliable Data Transfer
- Go-Back-N Sliding Window Protocol
- UDP Socket Programming
- Error Detection using Checksums
- Timeout and Retransmission Mechanisms
- Fast Retransmit
- Network Failure Simulation
- Client-Server Communication

---

## Future Improvements

- Selective Repeat ARQ implementation
- Graphical User Interface (GUI)
- Packet transmission statistics
- Performance analysis (throughput and delay)
- Multi-client support
- Logging and visualization

---

## Author

Developed as part of a Computer Networks course project to demonstrate reliable data transmission using the Go-Back-N ARQ protocol in Java.
