import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;
//***************************************************************** */
//Implements Go-Back-N (GBN) protocol
// implemented scenarios like packet loss, ACK loss, corruption,
// delayed ACKs, and fast retransmission.
// Also implemented  connection reset, termination, and retransmission
// upon timeout.
//***************************************************************** */
public class Client{

     // implementting GBN
         static  int windowSize=4;
         static  int maxPkts=5;
         static int scenarioNo=1;
         static boolean ackLost=false;
         static int previousAckSeqNo=-1;//
         static boolean pktLostOnce=false; 
         static int duplicateAcks=0;
      public static void main(String[] args) {
        try {
             DatagramSocket clntSocket=new DatagramSocket();
             InetAddress ipAddr=InetAddress.getByName("localhost");
             Scanner sc=new Scanner(System.in);
             clntSocket.setSoTimeout(2000);
              while(true){
                    scenarioNo=selectScenerio(sc);
                    sendResetPacket(clntSocket, ipAddr);
                    if(scenarioNo==7){
                        while (true) {
                            System.out.println("Enter No of Messages >>>  : ");
                        String tempMsgNo=sc.nextLine();
                          if(isValidNumeric(tempMsgNo)){
                                maxPkts=Integer.parseInt(tempMsgNo);
                                break;
                          }
                        }
                        while (true) {
                            System.out.println("Enter window Size : ");
                            String tempWinSize=sc.nextLine();
                            if(isValidNumeric(tempWinSize)){
                                windowSize=Integer.parseInt(tempWinSize);
                                break;
                            }
                            
                        }

                    }
                    String[] msgs=new String[maxPkts];
                    int[] checkSums= new int[maxPkts];      
                    inputMessages(sc, msgs, checkSums);
                    int base=0;
                    int nextSequenceNo=0;
                    boolean[] acked=new boolean[maxPkts];
                    while(base < maxPkts){
                           while(nextSequenceNo < base + windowSize && nextSequenceNo < maxPkts){
                            if((scenarioNo==2|| scenarioNo==6) && nextSequenceNo==1 && !pktLostOnce){// simulating packet loss and Fast retransmit
                                // simulating Packet Loss 
                                System.out.println("Simulating packet loss at seq NO "+ nextSequenceNo);
                                pktLostOnce=true;
                                nextSequenceNo++;
                                continue;
                            }
                             boolean corrupt=false;
                             if(scenarioNo==4 && nextSequenceNo==2){// simulating corruption scenerio
                                corrupt=true;
                             }

                            sendPacket(clntSocket, ipAddr, msgs[nextSequenceNo], checkSums[nextSequenceNo], nextSequenceNo,corrupt);
                            nextSequenceNo++;
                           }                  
                            try{ 
                               handleResponse(clntSocket, acked, msgs, checkSums, ipAddr); 
                                while (base<maxPkts && acked[base]) {  base++;}
                                 } catch(SocketTimeoutException e){
                                System.out.println("Client: timeout! Resending from " + base);
                                timeoutReSnd(clntSocket, ipAddr, msgs, checkSums, base, nextSequenceNo);
                                }
                            }
                    String userResponse;
                    while (true) {
                     System.out.println("do you want to send more packets? yes  or no ");
                      userResponse=sc.nextLine(); 
                      if(userResponse.equalsIgnoreCase("yes") || userResponse.equalsIgnoreCase("no")) {
                        break;
                      }
                    }
                     if(!userResponse.equalsIgnoreCase("yes")){
                        sendEndPacket(clntSocket,ipAddr);
                                 break;
                            }      
                  }
               
                clntSocket.close();
                System.out.println("Client : Connection closed.");
        } catch (Exception e) {
            System.out.println("Client: Exception occured! " + e.getMessage());
        }
    
      }
static int selectScenerio(Scanner sc){
        int s;
         while (true) {
            try {
         System.out.println("\nChoose scenario No (1-7):");
        System.out.println("1. Simple Transmission");
        System.out.println("2. Packet Loss");
        System.out.println("3. ACK Loss");
        System.out.println("4. Packet Corruption");
        System.out.println("5. Delayed ACK");
        System.out.println("6.Fast Retransmitt");
        System.out.println("7.Change Packet No or Window Size (Default 5 - 4)");
         s = Integer.parseInt(sc.nextLine());
        if(s>=1 && s<=7){
            break;
        }else{
            System.out.println("Pleae Enter valid no (1-7)");
        } 
            } catch (Exception e) {
              System.out.println("Invalid Input! Pleae enter valid no 1-7");  
            }
          
         }
         resetSessionFlags();
         return s; 
        
      }
static void resetSessionFlags(){
        pktLostOnce = false;
        duplicateAcks = 0;
        ackLost = false;
        previousAckSeqNo = -1;

}
static void inputMessages(Scanner sc, String[] msgs, int[] checkSums){
        System.out.println("Enter "+maxPkts+" Msgs to send: >>>");
        for(int i=0;i<maxPkts;i++){
            while (true) {
            System.out.println("Message No  "+ i+ " : " );
            String tempMsg=sc.nextLine();
            if(!tempMsg.isEmpty()){
                     msgs[i]=tempMsg;
                     checkSums[i]=calculateCheckSum(msgs[i]);
                     break;
            } 
            else{
                System.out.println("Please Enter Non Emtpy message! ");
            }
            }      
     }
    }

static int calculateCheckSum(String msg){
      int sum=0;
        for(byte b : msg.getBytes()){
            sum+=b;
        }
        return sum;
           }  

static void sendPacket(DatagramSocket clntSocket,InetAddress ipAddr,String payload,int checkSum, int seqNO,boolean corrupt) throws Exception{
         checkSum= corrupt? (checkSum+1):checkSum;// simulating corruption by sending incorrect checksum
            String header="<SeqNo:"+seqNO + "><Length:" + payload.length()+ "><CheckSum:" + checkSum + "><Type:Data>";
         if (scenarioNo==5&& seqNO==1)// we need to req dealy in ack 
         {
            System.out.println("Simulating Ack delay for seq no: 1 ");
            header="<SeqNo:"+seqNO + "><Length:" + payload.length()+ "><CheckSum:" + checkSum + "><Type:DELAYACK>";
         }
           
            String packetToBeSent=header + "|" + payload;
            byte[] sndData=packetToBeSent.getBytes();
            DatagramPacket sndPacket=new DatagramPacket(sndData, sndData.length,ipAddr,5000);
   
            clntSocket.send(sndPacket);
            System.out.println("Client : send Packet >>>" + packetToBeSent);
        
      

      }
      
static void reSndPacket(DatagramSocket clntSocket,InetAddress ipAddr,String payload,int checkSum,int seqNo)throws Exception{
        sendPacket(clntSocket, ipAddr, payload, checkSum, seqNo,false);// while resending we are assuming no corruption
        System.out.println("Client: Resent Packet seq NO:  >>> " +seqNo);
      }

 static void handleResponse(DatagramSocket clntSocket,boolean[] acked, String[] msgs, int[] checkSums,InetAddress ipAddr)throws Exception                   {       
                             byte[]  rcvrRespone=new byte[1024];
                                DatagramPacket responsePacket=new DatagramPacket(rcvrRespone, rcvrRespone.length);
                                clntSocket.receive(responsePacket);
                                 String rcvrResponseString=new String(responsePacket.getData(),0,responsePacket.getLength());

                                 if (scenarioNo == 3 && !ackLost && rcvrResponseString.startsWith("ACK")) {
                                    System.out.println("Client:ACK loss. Ignoring received ACK.");
                                    ackLost=true;
                                    return;
                                }
                
                                 System.out.println("Client: Received Response from receiver  : "+ rcvrResponseString);
        
                                 if(rcvrResponseString.startsWith("ACK")){
                                    int ackSeqNo=Integer.parseInt(rcvrResponseString.substring(3));

                                     System.out.println("Client: Received ACK of "+ ackSeqNo);
                                     
                                     // Fast Retransmit implemented here
                                     if(ackSeqNo==previousAckSeqNo){
                                         duplicateAcks++;
                                          if (duplicateAcks==3){
                                                System.out.println("Client: 3 Duplicate ACks! Fast REtransmitting next pkt>>> "+ackSeqNo+1);
                                                if(ackSeqNo+1 < msgs.length){
                                                    reSndPacket(clntSocket, ipAddr, msgs
                                                    [ackSeqNo+1], checkSums[ackSeqNo+1], ackSeqNo+1);
                                                    duplicateAcks=0;
                                                }
                                          }
                                     }
                                     else{
                                        previousAckSeqNo=ackSeqNo;
                                        duplicateAcks=1;
                                     }
                                    for (int i = 0; i <= ackSeqNo && i < acked.length; i++) {
                                        acked[i] = true;
                                    }
                                    
                                }
                                else if(rcvrResponseString.startsWith("NAK")){
                                    int nakSeqNo=Integer.parseInt(rcvrResponseString.substring(3));
                                    System.out.println("Client: Recevived NAK for "+ nakSeqNo);
                                    reSndPacket(clntSocket, ipAddr,msgs[nakSeqNo],checkSums[nakSeqNo],nakSeqNo);
                                }
                                else{
                                    System.out.println("Client: Invalid Response!");
                                }
      }

static void timeoutReSnd(DatagramSocket clntSocket,InetAddress ipAddr,String[] msgs,int[] checkSums, int base, int nextSequenceNo) throws Exception{
    for (int j= base; j<nextSequenceNo;j++){
        reSndPacket(clntSocket, ipAddr, msgs[j], checkSums[j], j);
    }
}
// ... special packet to request termination of connection also terminates server
static void sendEndPacket(DatagramSocket clntSocket,InetAddress ipAddr){
    String header="<SeqNo:"+0 + "><Length:" + 0+ "><CheckSum:" + 0 + "><Type:END>";
    String endPktData=header + "|" + "End";
     byte[] endData = endPktData.getBytes();
    DatagramPacket endPkt = new DatagramPacket(endData, endData.length, ipAddr, 5000);
    try {
    clntSocket.send(endPkt);
    System.out.println("Client: Termination Siganl Sent."); 
    } catch (Exception e) {
    System.out.println("Termination Request Failed.");
        }

}
//... special packet to to tell receiver for new Session... 
static void sendResetPacket(DatagramSocket clntSocket,InetAddress ipAddr){
    String header="<SeqNo:"+0 + "><Length:" + 0+ "><CheckSum:" + 0 + "><Type:RESET>";
    String endPktData=header + "|" + "Reset";
    byte[] endData = endPktData.getBytes();
    DatagramPacket endPkt = new DatagramPacket(endData, endData.length, ipAddr, 5000);
    try {
    clntSocket.send(endPkt);
    } catch (Exception e) 
    {
    System.out.println("Reset Request failed.");
}


}
// Utility Fucntion to check validity of input 
public static boolean isValidNumeric(String input){
        if(input==null || input.isEmpty()){
                return false;
               }
         try {
                Integer.parseInt(input);
                return true;
               } catch (Exception e) {
                return false;
               }
}


}