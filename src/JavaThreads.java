
public class JavaThreads {
    
    private BroadcastSystem broadcastSystem;
    private TokenRing tokenRing;
    protected static int testSize;
    
    public JavaThreads(int numAgents) throws Exception
    {
        testSize = numAgents;
        broadcastSystem = new BroadcastSystem();
        broadcastSystem.start();
        this.tokenRing = new TokenRing();
        for(int i = 0; i < numAgents; i++)
        {
            this.tokenRing.register(generateSubSystem(i, i));
            
        }
        tokenRing.createToken("all");
    }
    
    public static void main(String[] args)
    {
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
    
    /**
     * The generated subsystems are created and ran in individual threads.
     * The order of execution is as follows: BroadcastAgent -> DSM ->
     * TokenRingAgent -> Processor.
     * @param agentID       The agent ID given to the generated Agent
     * @param cpuID         The processor ID given to the processor
     * @return              The TokenRingAgent with all threads started
     * @throws              Exception
     */
    private TokenRingAgent generateSubSystem(int agentID, int cpuID) throws Exception
    {
        //1. Create a LocalMemory and initialize Peterson's variables
        LocalMemory localMemory = new LocalMemory();
        for(int i = 0; i < (JavaThreads.testSize); i++)
        {
            localMemory.store("flag[" + i + "]", -1);
        }
        for(int i = 0; i < (JavaThreads.testSize - 1); i++)
        {
            localMemory.store("turn[" + i + "]", 0);
        }
        
        //2. Create a BroadcastAgent
        BroadcastAgent broadcastAgent = broadcastSystem.createAgent();
        
        //3. Create DSM
        DSM dsm = new DSM(localMemory, broadcastAgent);
        broadcastAgent.setDSM(dsm);
        
        //3. Generate CPU
        Processor processor = new Processor(cpuID, dsm);
        dsm.loadLock = processor.loadLock;
        
        //4. Create token ring agent and register it
        TokenRingAgent tokenRingAgent = new TokenRingAgent(agentID, processor, 1000);
        
        //5. start the system components
        tokenRingAgent.start();
        dsm.start();
        broadcastAgent.start();
        processor.start();
        
        return tokenRingAgent;
        
    }
    

}
