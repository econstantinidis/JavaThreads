
public class DSM extends Thread {
    
    private LocalMemory localMemory;
    private BroadcastAgent broadcastAgent;
    
    /**
     * 
     * @param localMemory       The local cache created by the CPU
     * @param broadcastAgent    The agent created by the BroadcastSystem
     */
    protected DSM(LocalMemory localMemory, BroadcastAgent broadcastAgent)
    {
        this.localMemory = localMemory;
        this.broadcastAgent = broadcastAgent;
    }
    
    /**
     * The associated broadcast agent will add this store to the BroadcastSystem action queue
     * @param key       New keys can be added but the value of existing keys will be overwritten 
     * @param value     This parameter will be auto-boxed as an Object 
     */
    protected void store(String key, Object value)
    {
        Message<String, Object> message = new Message<String, Object>(key, value);
        localMemory.store(key, value);
        broadcastAgent.broadcast(message);
    }
    
    /**
     * 
     * @param key       This parameter is used to retrieve the associated value
     * @return          The associated value is unboxed to the specified type by cast or is null.
     */
    @SuppressWarnings("unchecked")
    protected <T> T load(String key)
    {
        return (T) localMemory.load(key);
    }
    
    @Override
    public void run()
    {
        broadcastAgent.start();
    }
}
