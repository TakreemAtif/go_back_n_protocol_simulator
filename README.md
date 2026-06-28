# Go-Back-N Protocol Simulator

This project is a Java implementation of the Go-Back-N (GBN) Automatic Repeat Request (ARQ) protocol using UDP sockets. It demonstrates how reliable data transmission can be achieved over an unreliable network by implementing retransmission and acknowledgment mechanisms.

## Features

- Go-Back-N sliding window implementation
- Reliable data transfer over UDP
- Configurable window size and number of packets
- Checksum-based error detection
- Timeout-based retransmission
- Fast retransmission using duplicate ACKs
- Session reset and graceful connection termination

## Network Scenarios

The simulator can demonstrate:

- Normal packet transmission
- Packet loss
- ACK loss
- Packet corruption
- Delayed acknowledgments
- Fast retransmission
- Custom packet count and window size

## Technologies

- Java
- UDP Socket Programming
- DatagramSocket & DatagramPacket
- Computer Networks
- Go-Back-N ARQ Protocol

## Running the Project

Compile both files:

```bash
javac Server.java
javac Client.java
```

Start the server:

```bash
java Server
```

Start the client in another terminal:

```bash
java Client
```

Select one of the available scenarios and enter the messages you want to transmit.

## Project Structure

```
Client.java      // Sender implementing Go-Back-N
Server.java      // Receiver handling ACKs and NAKs
```

## What I Learned

Through this project, I gained practical experience with:

- Reliable data transfer protocols
- Sliding window algorithms
- UDP socket programming
- Error detection using checksums
- Timeout and retransmission strategies
- Client-server communication

## Future Improvements

- Implement Selective Repeat ARQ
- Add a graphical user interface
- Support multiple clients
- Display transmission statistics
- Measure protocol performance under different network conditions
