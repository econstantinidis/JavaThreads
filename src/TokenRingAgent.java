
public class TokenRingAgent extends Thread {
    protected final int agentID;
    private boolean active = true;
    protected final int cpuID;
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
    
    protected void setPredecessor(int id)
    {
        predecessorID = id;
    }
    
    
    protected void setSuccessor(int id)
    {
        successorID = id;
    }
}
