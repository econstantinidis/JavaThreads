import java.util.concurrent.ArrayBlockingQueue;

public class TokenRingAgent extends Thread {
    protected final int agentID;
    private boolean active = true;
    protected final int cpuID;
    protected TokenRingAgent predecessor;
    protected TokenRingAgent successor;
    protected ArrayBlockingQueue<Token> tokenBucket;
    protected Token activeToken = null;
    protected Lock lock;
    protected Processor processor;
    /**
     * An agent who holds a token should be allowed to execute privileged instructions.
     * Initially the agent is set to active but this can be toggled using the methods.
     * @param agentID   A unique identifier used by the token
     * @param cpuID     A logical identifier for the processor
     */
    protected TokenRingAgent(int agentID, Processor processor, int capacity)
    {
        this.agentID = agentID;
        this.cpuID = processor.cpuID;
        tokenBucket = new ArrayBlockingQueue<Token>(capacity);
        lock = new Lock();
        this.processor = processor;
        this.processor.storeLock = lock;
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            Token token = tokenBucket.poll();   //return head of queue or null
            if(token != null)
            {
                //Check to see if this is the token requested by the processor
                if((token.id == lock.getID() || token.id == "all") && activeToken == null)
                {
                    activeToken = token;
                    synchronized(lock)
                    {
                        lock.notify();
                    }
                }
                else
                {
                    sendToken(token);
                }
            }
            if(activeToken != null)
            {
                //Check to see if processor is done with the token
                if(activeToken.id != lock.getID())
                {
                    sendToken(activeToken);
                    activeToken = null;
                }
            }
        }
    }
    
    /**
     * @returns     The unique identifier for the token received from the predecessor
     */
    protected String receiveToken()
    {
        return activeToken.getID();
    }
    
    /**
     * Inserts the token into the tail of the successor's token queue.
     * This method will block if space is not immediately available.
     * @param token     token sent to the successor
     */
    protected void sendToken(Token token)
    {
        try 
        {
            successor.tokenBucket.put(token);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Enable the agent, if the agent is already active then
     * do nothing.
     */
    protected void enable()
    {
        if(active == false)
        {
            active = true;
        }
    }
    
    
    /**
     * Disable the agent, if the agent is already disabled then
     * do nothing.
     */
    protected void disable()
    {
        if(active == true)
        {
            active = false;
        }
    }
    
    protected int getPredecessorID()
    {
        return predecessor.getID();
    }
    
    
    protected int getSuccessorID()
    {
        return successor.getID();
    }
    
    protected int getID()
    {
        return this.agentID;
    }
}
