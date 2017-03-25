
public class TokenRingAgent extends Thread {
    private final int agentID;
    private boolean active = true;
    private final int cpuID;
    private int predecessorID;
    private int successorID;
    
    
    /**
     * An agent who holds a token should be allowed to execute privileged instructions.
     * Initially the agent is set to active but this can be toggled using the methods.
     * @param agentID   A unique identifier used by the token
     * @param cpuID     A logical identifier for the processor
     */
    protected TokenRingAgent(int agentID, Processor processor)
    {
        this.agentID = agentID;
        this.cpuID = processor.cpuID;
    }
    
    @Override
    public void run()
    {
        
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
    
    
    /**
     * Checks to see whether the predecessor id
     * is not the same as this agents id.
     * @param id    The unique predecessor agent id.
     */
    protected void setPredecessor(int id) throws IllegalArgumentException
    {
        if(id == agentID)
        {
            throw new IllegalArgumentException();
        }
        else
        {
            predecessorID = id;
        }
    }
    
    /**
     * Checks to see whether the successor id
     * is not the same as this agents id.
     * @param id    The unique successor agent id.
     */
    protected void setSuccessor(int id) throws IllegalArgumentException
    {
        if(id == agentID)
        {
            throw new IllegalArgumentException();
        }
        else
        {
            successorID = id;
        }
    }
}
