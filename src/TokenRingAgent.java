import java.util.concurrent.ArrayBlockingQueue;

public class TokenRingAgent extends Thread {
    protected final int agentID;
    private boolean active = true;
    protected final int cpuID;
    private int predecessorID;
    private int successorID;
    protected ArrayBlockingQueue<Token> tokenBucket;
    protected Token activeToken = null;
    protected Lock lock;
    
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
        processor.lock = lock;
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
                if(token.id == lock.getID() && activeToken == null)
                {
                    activeToken = token;
                    lock.notify();
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
    protected int receiveToken()
    {
        return agentID;
    }
    
    /**
     * @param token     token sent to the successor
     */
    protected void sendToken(Token token)
    {
        
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
    
    protected void setPredecessor(int id)
    {
        predecessorID = id;
    }
    
    
    protected void setSuccessor(int id)
    {
        successorID = id;
    }
}
