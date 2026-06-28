import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//********************************************************** */
// Implemented Go-Back-N protocol.
// Receives packets and sends ACK or NAK.
// Handles packet loss, corruption, and delayed ACKs.
// Supports reset and termination requests from client.
// Prints logs for each packet and response sent.
 //******************************************************** */
 public class Server{
        public static void main(String[] args){
            try{
                DatagramSocket srvrSocket= new DatagramSocket(5000);                
                byte[] rcvData=new byte[1024];
                System.out.println("Server: Waiting for data to receive.........");    
                int seqNo=0;
                while (true) {
                DatagramPacket rcvPacket= new DatagramPacket(rcvData, rcvData.length);
                srvrSocket.receive(rcvPacket);
                String rcvd=new String(rcvPacket.getData(),0,rcvPacket.getLength()); 
                        String[] parts= rcvd.split("\\|");
                         if(parts.length != 2){System.out.println("Packet Format is not correct! Failed to Read!");continue; 
                        }   
                        String header=parts[0];
                        String payload=parts[1];
                        int rcvdSeqNo=getIntField(header,"SeqNo:");
                        int length=getIntField(header,"Length:");
                        int checksum=getIntField(header,"CheckSum:");
                        String type=getStringField(header,"Type:");
                        if (seqNo == -1 || length == -1|| type == null || checksum == -1 )
                         {
                        System.out.println("Server: Header Reading failed.");
                            continue;
                        }
                        //...Checking for Special Requests...//
                         if(checkforTerminationREq(srvrSocket,type)){break;};
                         if(checkforResetREq(srvrSocket,type)){seqNo=0;System.out.println("Server: New Session .........................");continue;}
                        
                        int srvrRcvdChecksum=0;
                        for(byte b: payload.getBytes()){
                            srvrRcvdChecksum+=b;
                        }
                        String srvrResponse;
                        if(srvrRcvdChecksum==checksum){
                            if(seqNo==rcvdSeqNo){
                                System.out.println("Server: Packet Received Successfully. Seq NO : "+ rcvdSeqNo+"Message: "+ payload);
                                srvrResponse="ACK"+ seqNo;
                                seqNo++;// expected sequence no
                            }else{
                                System.out.println("Server: Failed to receive! seq NO - Expected: "+seqNo +"Received Seq No: "+rcvdSeqNo);
                                int effectiveSeqNo= (seqNo==0)? 0: (seqNo-1);
                                srvrResponse="ACK" + effectiveSeqNo; // here i am resending previous accepted sequence no 

                            }
                        } 
                            else{
                                System.out.println("Server: Corrupted Packet!");
                                System.out.println(".............................");
                                srvrResponse="NAK"+ (seqNo - 1);
                            }
                            byte[] sendResponse=new byte[1024];
                            sendResponse=srvrResponse.getBytes();
                            InetAddress clientAddr=rcvPacket.getAddress(); 
                            int clientPort=rcvPacket.getPort();
                            DatagramPacket responsePacket=new DatagramPacket(sendResponse, sendResponse.length,clientAddr,clientPort);
                            if("DELAYACK".equalsIgnoreCase(type)){
                                simulateDelayAck(srvrSocket,srvrResponse,clientAddr,clientPort,4000);
                              
                            }
                            else
                            {
                            srvrSocket.send(responsePacket);
                            System.out.println("Server: Response Sent to Client!"+ srvrResponse);
                            }     

                            }

            } catch(Exception e)
            { 
                System.out.println("Server : Exception occured! " + e.getMessage());
            }
        }
static int getIntField(String header,String fName){
           try {
            int s=header.indexOf("<"+fName);
            int e=header.indexOf(">",s);// it retunr -1 for invalid so need to handle them
            if (s == -1 || e == -1) return -1;

            return Integer.parseInt(header.substring(s+ fName.length()+  1,e));
           } catch (Exception e) {
            return -1;
           }
       } 
static String getStringField(String header,String fName){
        try {
            int st=header.indexOf("<"+fName);
            int en=header.indexOf(">",st);
            if (st == -1 || en == -1) return null;
            return header.substring(st+fName.length()+1, en);

        } catch (Exception e) {
           return null;
        }
       }
static boolean checkforTerminationREq(DatagramSocket srvrSocket,String type){
        if ("END".equalsIgnoreCase(type)) {
            System.out.println(".......................................");
            System.out.println("Server: Termination signal received..");
           srvrSocket.close();
           return true;     
        }
        else return false;
      } 
 static boolean checkforResetREq(DatagramSocket srvrSocket,String type){
        if ("RESET".equalsIgnoreCase(type)) {
           return true;    
        }
        else return false;
      } 
// simulating delayed Ack while server works normally for later upcoming packets.... 
 static void simulateDelayAck(DatagramSocket socket, String response, InetAddress addr, int port, int delay) {
    new Thread(() -> {
        try {
            Thread.sleep(delay);
            byte[] respData = response.getBytes();
            DatagramPacket delayedPacket = new DatagramPacket(respData, respData.length, addr, port);
            socket.send(delayedPacket);
            System.out.println("Server: Delayed ACK sent for " + response);
        } catch (Exception e) {
            System.out.println("Server: Error Occured while delaying " + e.getMessage());
        }
    }).start();
}


 }