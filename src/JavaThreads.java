import java.util.ArrayList;
import java.util.List;

public class JavaThreads {
    
    private BroadcastSystem broadcastSystem;
    private TokenRing tokenRing;
    private List<Processor> processorList;
    protected static int testSize;
    
    public JavaThreads(int numAgents) throws Exception
    {
        testSize = numAgents;
        processorList = new ArrayList<Processor>();
        broadcastSystem = new BroadcastSystem();
        for(int i = 0; i < numAgents; i++)
        {
            generateSubSystem(i, i);
        }
        startSystemsRecursive();
        wait();
    }
    
    private void startSystemsRecursive()
    {
        broadcastSystem.start();
        for(Processor processor : processorList)
        {
            processor.start(); // Processor -> DSM -> BroadcastAgent
        }
    }
    
    public static void main(String[] args) {
        if(args.length != 1)
        {
            System.out.println("Error: Wrong number of argumanets");
            System.out.println("Usage: java JavaThreads.java <#Agents>");
            System.exit(0);
        }
        
        //Start test
        try 
        {
            int numAgents = Integer.parseInt(args[0]);
            JavaThreads javaThreads = new JavaThreads(numAgents);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void generateSubSystem(int agentID, int cpuID) throws Exception
    {
        //1. Create a LocalMemory and initialize Peterson's variables
        LocalMemory localMemory = new LocalMemory();
        for(int i = 0; i < (JavaThreads.testSize - 1); i++)
        {
            localMemory.store("flag[" + i + "]", -1);
        }
        for(int i = 0; i < (JavaThreads.testSize - 2); i++)
        {
            localMemory.store("turn[" + i + "]", -1);
        }
        
        //2. Create a BroadcastAgent
        BroadcastAgent broadcastAgent = broadcastSystem.createAgent(false);
        broadcastAgent.setLocalMemory(localMemory);
        
        //3. Create DSM
        DSM dsm = new DSM(localMemory, broadcastAgent);
        
        //3. Generate CPU
        Processor processor = new Processor(cpuID, dsm);
        processorList.add(processor);
        
        //4. Create token ring agent and register it
        TokenRingAgent tokenRingAgent = new TokenRingAgent(agentID, processor, 100);
        tokenRing.register(tokenRingAgent);
        
    }
    

}
