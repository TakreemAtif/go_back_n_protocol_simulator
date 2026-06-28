# Go-Back-N Protocol Simulator

A Java implementation of the Go-Back-N (GBN) protocol that simulates reliable data transmission over UDP using sliding window and retransmission mechanisms.

## Features

- Go-Back-N sliding window protocol
- UDP client-server communication
- Configurable window size and packet count
- Checksum-based error detection
- Timeout-based retransmission
- Fast retransmission using duplicate ACKs
- Packet loss simulation
- ACK loss simulation
- Packet corruption simulation
- Delayed ACK simulation

## Technologies

- Java
- UDP Sockets
- DatagramSocket
- DatagramPacket

## How to Run

```bash
javac Server.java
javac Client.java
java Server
java Client
```

## Project Files

- Client.java
- Server.java
