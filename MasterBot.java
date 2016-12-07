
/*
@author Rathna Ramesh
submitted for CMPE206 course project under Dr.Juan Gomez at SJSU
MasterBot, as per spec sheet
the system takes -p PortNo as commandline arguement 
*/
import java.net.*;
import java.io.*;
import java.lang.*;
//import java.lang.StringUtils.*;

class Listener implements Runnable //Class to connect to Host Multithreaded
{
    private int PortNo;
    private Thread t;
    private String threadName = "listener thread";
    Listener(int Port)
    {
        PortNo = Port;
        threadName = "listener thread";
    }
    
    @Override
    public void run()
    {
        try(ServerSocket master = new ServerSocket(PortNo);)
        {
            while(true)
            {
                try(Socket slave = master.accept(); //assign incoming slave connection to a new socket
                    BufferedReader in = new BufferedReader(new InputStreamReader(slave.getInputStream())); //create buffer that reads data coming in from new socket
                    FileWriter fw = new FileWriter("masterData.txt",true);) //open file to append write the incoming data                    
                {
                    String line;//weird error, won't let me initialise as while(String Line =...)
                    while((line = in.readLine())!=null )
                    {                                
                        String[] split = line.split("\\s+");
                        fw.write(line);//appends the string to the file
                        fw.write(" \n");
                        fw.close();
                    }
                    slave.close(); //job done for this slave, close socket
                }
                catch(IOException ioe)
                {
                    System.exit(-1);
                }   
            }    
        }catch(IOException ioe)
        { System.exit(-1);}
    }
    public void start()
	{
            if (t==null)
        	{
                    t = new Thread(this,threadName);
                    t.start();
		}
	}//end of start	
}
public class MasterBot
{
    //end of Listener(int)
    
    private static void listSlaves() //Displays the slave data
    {
    	try (BufferedReader br = new BufferedReader(new FileReader("masterData.txt"))) 
    	{
            String line; 
            System.out.println("Name          IP       Port No   RegDate&Time");
            while ((line = br.readLine()) != null) 
            {
       		String[] split = line.split("\\s+");//split the data read by spaces
       		System.out.println(split[0] + "   " + split[1] + "   "+split[2] + "   "+split[3]);  
            }
            br.close();
	}
	catch(IOException ioe)
        {
            System.exit(-1);
        }	
    }//end of listSlaves()
    
    //sends details to slave on connection to make 
    private static void connectCall(String IP, String Host, String PNo, String n, String feature)
    {
    	try(BufferedReader br = new BufferedReader(new FileReader("masterData.txt"));)
        {             
                //loop over all slaves on the DB!
            String line;//error persists, syntax spec?
            while ((line = br.readLine()) != null) 
            {
                    String[] split = line.split("\\s+");
                    if (( split[1].equals(IP))||(split[0].equals(IP))||"all".equals(IP))
                    {
                    int PortNo = Integer.parseInt(split[2]); //take port number
                    try
                    {   //System.out.println(split[1] +" " + PortNo);
                        Socket slave = new Socket(split[1], PortNo);
                     //create socket for slave
                    PrintWriter pout = new PrintWriter(slave.getOutputStream(),true); //writing object and output stream creation
                    pout.println("-c " + Host + " " + PNo  +" " + n + ' ' + feature);//command is passed as string
                    }
                    catch(IOException ioe)
                    {//System.out.println(ioe.getMessage());
                    }
                    }
            }
            br.close();
        }
        catch(IOException ioe)
        {//do nothing
              System.err.println("Unexpected error 4: " + ioe.getMessage());
        }          
    }//end of connectCall
 
 
    
    private static void disconnectCall(String IP, String Target, String PNo)
    {
    	try(BufferedReader br = new BufferedReader(new FileReader("masterData.txt"));)
        {             
                //loop over all slaves on the DB!
            String line;//error persists, syntax spec?
            while ((line = br.readLine()) != null) 
            {
                String[] split = line.split("\\s+");
                if (( split[1].equals(IP))||(split[0].equals(IP))||"all".equals(IP))
                {
                    int PortNo = Integer.parseInt(split[2]); //take port number
                    try
                    {   //System.out.println(split[1] +" " + PortNo);
                        Socket slave = new Socket(split[1], PortNo);
                     //create socket for slave
                    PrintWriter pout = new PrintWriter(slave.getOutputStream(),true); //writing object and output stream creation
                    pout.println("-d " + Target + " " + PNo );//command is passed as string
                    }
                    catch(IOException ioe)
                    {//System.out.println(ioe.getMessage());
                    } // do nothing 
                }
            }
            br.close();
        }
        catch(IOException ioe)
        {//do nothing
            System.exit(-1);
        }
    }
//end of disconnectCall
   
    
    public static void main(String[] args)
    {   
        while (true)
        {   
            switch (args[0]) {
            //master listening for slaves registering their names,IPs and PortNos
            case "-p":
            case "p":
                int mPortNo = Integer.parseInt(args[1]);            
                Listener listen = new Listener(mPortNo);
                listen.start();
                break;
            //list all the slave details
            case "list":
            case "-list":
                listSlaves();
                break;
            //ask a slave(or all) to connect to host n number of times/1 time
            case "connect":
            case "-connect":               
                switch (args.length) {
            //no number of connections or KeepAlive or url
                    case 4:
                    connectCall(args[1],args[2],args[3],"1","no"); //if number of connections not specified, make it 1               
                    break;
            //in this case user can either give number of connections, or KeepAlive or the url
                    case 5:
                    if ("keepalive".equals(args[4]))
                        connectCall(args[1],args[2],args[3],"1","yes");
                    else if (args[4].contains ("url="))
                        {
                            String url = args[4].replace("url=","");
                            connectCall(args[1],args[2],args[3],"1",url);
                        }
                    else
                            connectCall(args[1],args[2],args[3],args[4],"no");
                    break;
            //there surely is number of connection, and one of url or keepalive
                    case 6:
                    if ("keepalive".equals(args[5]))
                        connectCall(args[1],args[2],args[3],args[4],"yes");
                    else if (args[5].contains ("url="))
                        {
                            String url = args[5].replace("url=","");
                            connectCall(args[1],args[2],args[3],args[4],url);
                        }
                    break;
                    default:
                    break;
                    }
                break;
        //ask particular slave(or all) to disconnect from a host, established through portNo(or all connections)
            case "disconnect":
            case "-disconnect":
                //arguements - SlaveIP|all,HostIP,HostPort|all
                if(args.length!=4)
                    ;
                else
                    disconnectCall(args[1],args[2],args[3]);
                break;
            case "exit":
            case "-exit":
                System.exit(0);
                break;
            default:
                break;
            }
            
            try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    String accStr;
                    System.out.printf(">");
                    accStr = br.readLine();
                    args = accStr.split("\\s+");
                }
            catch(IOException ioe)
                    { System.err.println("Unexpected error in reading input 7:" + ioe.getMessage());
                        System.exit(0);
                    }
            }
            
        }            
}